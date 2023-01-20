/*
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.persist.utils;

import io.ballerina.persist.BalException;
import io.ballerina.persist.PersistToolsConstants;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.EntityField;
import io.ballerina.persist.models.Relation;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sql script generator.
 *
 * @since 0.1.0
 */
public class SqlScriptGenerationUtils {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String TAB = "\t";
    private static final String EMPTY = "";
    private static final String PRIMARY_KEY_START_SCRIPT = NEW_LINE + TAB + "PRIMARY KEY(";
    private static final String UNIQUE_KEY_START_SCRIPT = NEW_LINE + TAB + "UNIQUE KEY(";
    private static final String UNIQUE = " UNIQUE";
    private static final String ON_DELETE_SYNTAX = " ON DELETE";
    private static final String ON_UPDATE_SYNTAX = " ON UPDATE";
    private static final String RESTRICT = "persist:RESTRICT";
    private static final String CASCADE = "persist:CASCADE";
    private static final String SET_NULL = "persist:SET_NULL";
    private static final String NO_ACTION = "persist:NO_ACTION";
    private static final String RESTRICT_SYNTAX = " RESTRICT";
    private static final String CASCADE_SYNTAX = " CASCADE";
    private static final String NO_ACTION_SYNTAX = " NO ACTION";
    private static final String SET_NULL_SYNTAX = " SET NULL";
    private static final String SET_DEFAULT_SYNTAX = " SET DEFAULT";

    private SqlScriptGenerationUtils(){}

    public static String[] generateSqlScript(ArrayList<Entity> entityArray) throws BalException {
        HashMap<String, List<String>> referenceTables = new HashMap<>();
        HashMap<String, List<String>> tableScripts = new HashMap<>();
        for (Entity entity : entityArray) {
            List<String> tableScript = new ArrayList<>();
            String tableName = entity.getEntityName();
            tableScript.add(generateDropTableQuery(tableName));
            tableScript.add(generateCreateTableQuery(entity, referenceTables));
            tableScripts.put(tableName, tableScript);
        }
        return rearrangeScriptsWithReference(tableScripts.keySet(), referenceTables, tableScripts);
    }

    public static void writeScriptFile(String moduleName, String[] sqlScripts, Path filePath) {
        Path path = Paths.get(String.valueOf(filePath),
                String.format(PersistToolsConstants.SQL_SCHEMA_FILE, moduleName));
        StringBuilder sqlScript = new StringBuilder();
        for (String script : sqlScripts) {
            sqlScript.append(script).append(NEW_LINE);
        }
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            Files.writeString(path, sqlScript);
        } catch (IOException e) {
            PrintStream errStream = System.err;
            errStream.println("Error while updating the SQL script file (persist_db_push.sql) in the project " +
                    "persist directory: " + e.getMessage());
        }
    }
    private static String generateDropTableQuery(String tableName) {
        return MessageFormat.format("DROP TABLE IF EXISTS {0};", tableName);
    }

    private static String generateCreateTableQuery(Entity entity,
                                             HashMap<String, List<String>> referenceTables) throws BalException {

        String fieldDefinitions = generateFieldsDefinitionSegments(entity, referenceTables);

        return MessageFormat.format("{0}CREATE TABLE {1} ({2}{3});", NEW_LINE, entity.getEntityName(),
                fieldDefinitions, NEW_LINE);
    }

    private static String generateFieldsDefinitionSegments(Entity entity,
                                                           HashMap<String, List<String>> referenceTables)
            throws BalException {
        StringBuilder sqlScript = new StringBuilder();
        sqlScript.append(getColumnsScript(entity));
        List<EntityField> relationFields = entity.getFields().stream()
                .filter(entityField -> entityField.getRelation() != null && entityField.getRelation().isOwner())
                .collect(Collectors.toList());
        for (EntityField entityField : relationFields) {
            sqlScript.append(getRelationScripts(entity.getEntityName(), entityField, referenceTables));
        }
        sqlScript.append(addPrimaryKey(entity.getKeys()));
        return sqlScript.substring(0, sqlScript.length() - 1);
    }

    private static String getColumnsScript(Entity entity) throws BalException {
        StringBuilder columnScript = new StringBuilder();
        for (EntityField entityField :entity.getFields()) {
            if (entityField.getRelation() != null) {
                continue;
            }
            String sqlType = getType(entityField);
            assert sqlType != null;
            if (sqlType.equals(PersistToolsConstants.SqlTypes.VARCHAR)) {
                sqlType += "(" + entityField.getMaxLength() + ")";
            }
            String fieldName = removeSingleQuote(entityField.getFieldName());
            columnScript.append(MessageFormat.format("{0}{1}{2} {3}{4},",
                    NEW_LINE, TAB, fieldName, sqlType, " NOT NULL"));
        }
        return columnScript.toString();
    }

    private static String getRelationScripts(String tableName, EntityField entityField,
                                             HashMap<String, List<String>> referenceTables) throws BalException {
        StringBuilder relationScripts = new StringBuilder();
        Relation relation = entityField.getRelation();
        List<Relation.Key> keyColumns = relation.getKeyColumns();
        List<String> references = relation.getReferences();
        String onDelete = relation.getOnDelete();
        String onUpdate = relation.getOnUpdate();
        String onDeleteScript = "";
        String onUpdateScript = "";
        if (onDelete != null && !onDelete.isEmpty()) {
            onDeleteScript = ON_DELETE_SYNTAX + getReferenceAction(onDelete);
        }
        if (onUpdate != null && !onUpdate.isEmpty()) {
            onUpdateScript = ON_UPDATE_SYNTAX + getReferenceAction(onUpdate);
        }
        Entity assocEntity = relation.getAssocEntity();
        for (int i = 0; i < references.size(); i++) {
            String referenceSqlType = null;
            String referenceFieldName = null;
            for (EntityField assocField : assocEntity.getFields()) {
                if (assocField.getRelation() != null) {
                    continue;
                }
                if (assocField.getFieldName().equals(references.get(i))) {
                    referenceSqlType = getType(assocField);
                    if (referenceSqlType.equals(PersistToolsConstants.SqlTypes.VARCHAR)) {
                        referenceSqlType += "(" + entityField.getMaxLength() + ")";
                    }
                    referenceFieldName = removeSingleQuote(references.get(i));
                    break;
                }
            }
            String foreignKey = keyColumns.get(i).getField();
            String unique = "";
            // TODO: check whether we need this as we remove unique keys support
//            Relation.RelationType associatedEntityRelationType = Relation.RelationType.NONE;
//            for (EntityField field: assocEntity.getFields()) {
//                if (field.getFieldType().equals(tableName)) {
//                    associatedEntityRelationType = field.getRelation().getRelationType();
//                    break;
//                }
//            }
//            if (relation.getRelationType().equals(Relation.RelationType.ONE) &&
//                    associatedEntityRelationType.equals(Relation.RelationType.ONE)) {
//                List<String> keys = assocEntity.getKeys();
//                List<List<String>> uniqueConstraints = assocEntity.getUniqueKeys();
//                if ((keys.size() == 1 && keys.get(0).equals(referenceFieldName)) ||
//                        (uniqueConstraints != null && uniqueConstraints.size() == 1 &&
//                                uniqueConstraints.get(0).size() == 1 &&
//                                uniqueConstraints.get(0).get(0).equals(referenceFieldName))) {
//                    unique = UNIQUE;
//                }
//            }
            relationScripts.append(MessageFormat.format("{0}{1}{2} {3}{4},", NEW_LINE, TAB, foreignKey,
                    referenceSqlType, unique));
            relationScripts.append(MessageFormat.format("{0}{1}CONSTRAINT FK_{2}_{3}_{4} FOREIGN KEY({5}) " +
                            "REFERENCES {6}({7}){8}{9},", NEW_LINE, TAB, tableName.toUpperCase(Locale.ENGLISH),
                    assocEntity.getEntityName().toUpperCase(Locale.ENGLISH), i, foreignKey, assocEntity.getEntityName(),
                    referenceFieldName, onDeleteScript, onUpdateScript));
            updateReferenceTable(tableName, assocEntity.getEntityName(), referenceTables);
        }
        return relationScripts.toString();
    }

    private static String removeSingleQuote(String fieldName) {
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1);
        }
        return fieldName;
    }

    private static void updateReferenceTable(String tableName, String referenceTableName,
                                             HashMap<String, List<String>> referenceTables) {
        List<String> setOfReferenceTables;
        if (referenceTables.containsKey(tableName)) {
            setOfReferenceTables = referenceTables.get(tableName);
        } else {
            setOfReferenceTables = new ArrayList<>();
        }
        setOfReferenceTables.add(referenceTableName);
        referenceTables.put(tableName, setOfReferenceTables);
    }

    private static String addPrimaryKey(List<EntityField> primaryKeys) {
        return createKeysScript(primaryKeys);
    }

    private static String createKeysScript(List<EntityField> keys) {
        StringBuilder keyScripts = new StringBuilder();
        if (keys.size() > 0) {
            keyScripts.append(MessageFormat.format("{0}", PRIMARY_KEY_START_SCRIPT));
            for (EntityField key : keys) {
                keyScripts.append(MessageFormat.format("{0},", key.getFieldName()));
            }
            keyScripts.deleteCharAt(keyScripts.length() - 1).append("),");
        }
        return keyScripts.toString();
    }

    private static String getReferenceAction(String value) {
        switch (value) {
            case RESTRICT:
                return RESTRICT_SYNTAX;
            case CASCADE:
                return CASCADE_SYNTAX;
            case NO_ACTION:
                return NO_ACTION_SYNTAX;
            case SET_NULL:
                return SET_NULL_SYNTAX;
            default:
                return SET_DEFAULT_SYNTAX;
        }
    }

    private static String getType(EntityField field) throws BalException {
        String fieldType = field.getFieldType();
        if (!field.isArrayType()) {
            switch (fieldType) {
                case PersistToolsConstants.BallerinaTypes.INT:
                    return PersistToolsConstants.SqlTypes.INT;
                case PersistToolsConstants.BallerinaTypes.BOOLEAN:
                    return PersistToolsConstants.SqlTypes.BOOLEAN;
                case PersistToolsConstants.BallerinaTypes.DECIMAL:
                    return PersistToolsConstants.SqlTypes.DECIMAL;
                case PersistToolsConstants.BallerinaTypes.FLOAT:
                    return PersistToolsConstants.SqlTypes.FLOAT;
                case PersistToolsConstants.BallerinaTypes.DATE:
                    return PersistToolsConstants.SqlTypes.DATE;
                case PersistToolsConstants.BallerinaTypes.TIME_OF_DAY:
                    return PersistToolsConstants.SqlTypes.TIME;
                case PersistToolsConstants.BallerinaTypes.UTC:
                    return PersistToolsConstants.SqlTypes.TIME_STAMP;
                case PersistToolsConstants.BallerinaTypes.CIVIL:
                    return PersistToolsConstants.SqlTypes.DATE_TIME;
                case PersistToolsConstants.BallerinaTypes.STRING:
                    return PersistToolsConstants.SqlTypes.VARCHAR;
                default:
                    throw new BalException("Couldn't find equivalent SQL type for the field type: " + fieldType);
            }
        } else {
            if (PersistToolsConstants.BallerinaTypes.BYTE.equals(field.getFieldType())) {
                return PersistToolsConstants.SqlTypes.BINARY;
            }
            throw new BalException("Couldn't find equivalent SQL type for the field type: " + fieldType);
        }
    }

    private static String[] rearrangeScriptsWithReference(Set<String> tables,
                                                          HashMap<String, List<String>> referenceTables,
                                                          HashMap<String, List<String>> tableScripts) {
        List<String> tableOrder = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : referenceTables.entrySet()) {
            if (tableOrder.isEmpty()) {
                tableOrder.add(entry.getKey());
            } else {
                int firstIndex = 0;
                List<String> referenceTableNames = referenceTables.get(entry.getKey());
                for (String referenceTableName: referenceTableNames) {
                    int index = tableOrder.indexOf(referenceTableName);
                    if ((firstIndex == 0 || index > firstIndex) && index > 0) {
                        firstIndex = index;
                    }
                }
                tableOrder.add(firstIndex, entry.getKey());
            }
        }
        for (String tableName : tables) {
            if (!tableOrder.contains(tableName)) {
                tableOrder.add(0, tableName);
            }
        }
        int length = tables.size() * 2;
        int size = tableOrder.size();
        String[] tableScriptsInOrder = new String[length];
        for (int i = 0; i <= tableOrder.size() - 1; i++) {
            List<String> script =  tableScripts.get(tableOrder.get(size - (i + 1)));
            tableScriptsInOrder[i] = script.get(0);
            tableScriptsInOrder[length - (i + 1)] = script.get(1);
        }
        return tableScriptsInOrder;
    }
}

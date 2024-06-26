/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.persist.tools;

import io.ballerina.persist.cmd.Migrate;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.GENERATED_SOURCES_DIRECTORY;
import static io.ballerina.persist.tools.utils.GeneratedSourcesTestUtils.assertMigrateGeneratedSources;

/**
 * persist tool migrate command tests.
 */
public class ToolingMigrateTest {

    private static final PrintStream errStream = System.err;

    @Test(enabled = true)
    @Description("Test first time migration with no existing migrations")
    public void testMigrateFirstTime() {
        executeCommand("tool_test_migrate_1", "firstMigration");
        assertMigrateGeneratedSources("tool_test_migrate_1");
    }

    @Test(enabled = true)
    @Description("Test third time migration with two existing migrations")
    public void testMigrateThirdTime() {
        executeCommand("tool_test_migrate_2", "thirdMigration");
        assertMigrateGeneratedSources("tool_test_migrate_2");
    }

    @Test(enabled = true)
    @Description("Create a new table and migrate")
    public void testMigrateWithNewTable() {
        executeCommand("tool_test_migrate_3", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_3");
    }

    @Test(enabled = true)
    @Description("Remove a table and migrate")
    public void testMigrateRemoveTable() {
        executeCommand("tool_test_migrate_4", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_4");
    }

    @Test(enabled = true)
    @Description("Rename a table and migrate")
    public void testMigrateRenameTable() {
        executeCommand("tool_test_migrate_5", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_5");
    }

    @Test(enabled = true)
    @Description("Add a field to an existing table and migrate")
    public void testMigrateAddField() {
        executeCommand("tool_test_migrate_6", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_6");
    }

    @Test(enabled = true)
    @Description("Remove a field from an existing table and migrate")
    public void testMigrateRemoveField() {
        executeCommand("tool_test_migrate_7", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_7");
    }

    @Test(enabled = true)
    @Description("Change ballerina type of a column and migrate")
    public void testMigrateChangeBallerinaTypeOfAColumn() {
        executeCommand("tool_test_migrate_8", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_8");
    }

    @Test(enabled = true)
    @Description("Change the simple name of a column and migrate")
    public void testMigrateChangeSimpleNameOfAColumn() {
        executeCommand("tool_test_migrate_9", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_9");
    }

    @Test(enabled = true)
    @Description("Rename a primary key and migrate")
    public void testMigrateRenamePrimaryKey() {
        executeCommand("tool_test_migrate_10", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_10");
    }

    @Test(enabled = true)
    @Description("Make an existing required column optional and migrate")
    public void testMigrateMakeRequiredColumnOptional() {
        executeCommand("tool_test_migrate_11", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_11");
    }

    @Test(enabled = true)
    @Description("Make an optional column required and migrate")
    public void testMigrateMakeOptionalColumnRequired() {
        executeCommand("tool_test_migrate_12", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_12");
    }

    @Test(enabled = true)
    @Description("Add an optional type field and migrate")
    public void testMigrateAddOptionalTypeField() {
        executeCommand("tool_test_migrate_13", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_13");
    }

    @Test(enabled = true)
    @Description("Add a primary key to an existing table and migrate")
    public void testMigrateAddPrimaryKey() {
        executeCommand("tool_test_migrate_14", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_14");
    }

    @Test(enabled = true)
    @Description("Remove a primary key from an existing table and migrate")
    public void testMigrateRemovePrimaryKey() {
        executeCommand("tool_test_migrate_15", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_15");
    }

    @Test(enabled = true)
    @Description("Test add a foreign key to an existing table and migrate")
    public void testMigrateAddFKToExistingTable() {
        executeCommand("tool_test_migrate_16", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_16");
    }

    @Test(enabled = true)
    @Description("Test add a foreign key to an existing table with a composite key and migrate")
    public void testMigrateAddFKToExistingTableWithCompositeKey() {
        executeCommand("tool_test_migrate_17", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_17");
    }

    @Test(enabled = true)
    @Description("Test add a primary key to an existing table who's referred by another table and migrate")
    // This scenario is not fully supported must change foreign keys as well
    public void testMigrateAddPrimaryKeyToExistingTableReferredByAnother() {
        executeCommand("tool_test_migrate_18", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_18");
    }

    @Test(enabled = true)
    @Description("Test add a new table having a relationship with an existing owner table and migrate")
    public void testMigrateNewTableRelatedWithExistingOwnerTable() {
        executeCommand("tool_test_migrate_19", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_19");
    }

    @Test(enabled = true)
    @Description("Test add a new owner table having a relationship with an existing table and migrate")
    public void testMigrateNewOwnerTableRelatedWithExistingTable() {
        executeCommand("tool_test_migrate_20", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_20");
    }

    @Test(enabled = true)
    @Description("Test remove the relationship between two tables and migrate")
    public void testMigrateRemoveRelationFromTable() {
        executeCommand("tool_test_migrate_21", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_21");
    }

    @Test(enabled = true)
    @Description("Test rename the foreign key field in the owner table and migrate")
    public void testMigrateRenameForeignKeyField() {
        executeCommand("tool_test_migrate_22", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_22");
    }

    @Test(enabled = true)
    @Description("Test rename the foreign key field in the non owner table and migrate")
    public void testMigrateRenameForeignKeyFieldNonOwner() {
        executeCommand("tool_test_migrate_23", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_23");
    }

    @Test(enabled = true)
    @Description("Test modify simple type of primary key")
    // This scenario is not fully supported must change foreign keys as well
    public void testMigrateModifySimpleTypeOfPK() {
        executeCommand("tool_test_migrate_24", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_24");
    }

    @Test(enabled = true)
    @Description("Test name annotation on tables")
    public void testMigrateWithNameAnnotationsOnTables() {
        executeCommand("tool_test_migrate_25", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_25");
    }

    @Test(enabled = true)
    @Description("Test name annotation on columns and primary keys")
    public void testMigrateWithNameAnnotationsOnColumns() {
        executeCommand("tool_test_migrate_26", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_26");
    }

    @Test(enabled = true)
    @Description("Test Char, Varchar and Decimal annotations on columns")
    public void testMigrateWithTypeAnnotations() {
        executeCommand("tool_test_migrate_27", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_27");
    }

    @Test(enabled = true)
    @Description("Test relation annotation with a new field")
    public void testMigrateAddRelationAnnotationToNewField() {
        executeCommand("tool_test_migrate_28", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_28");
    }

    @Test(enabled = true)
    @Description("Test relation annotation on an existing field")
    public void testMigrateAddRelationAnnotationToExistingField() {
        executeCommand("tool_test_migrate_29", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_29");
    }

    @Test(enabled = true)
    @Description("Test change of relation annotation on an existing field")
    public void testMigrateChangeRelationAnnotationOnExistingField() {
        executeCommand("tool_test_migrate_30", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_30");
    }

    @Test(enabled = true)
    @Description("Test remove relation annotation from a field")
    public void testMigrateRemoveRelationAnnotation() {
        executeCommand("tool_test_migrate_31", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_31");
    }

    @Test(enabled = true)
    @Description("Test add relation annotation referring to existing field")
    public void testMigrateRelationAnnotationReferringToExistingField() {
        executeCommand("tool_test_migrate_32", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_32");
    }

    @Test(enabled = true)
    @Description("Test change simple name of a custom foreign key field")
    public void testMigrateChangeSimpleNameOfCustomForeignKey() {
        executeCommand("tool_test_migrate_33", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_33");
    }

    @Test(enabled = true)
    @Description("Test add name annotation to custom foreign key field")
    public void testMigrateAddNameAnnotationToCustomForeignKey() {
        executeCommand("tool_test_migrate_34", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_34");
    }

    @Test(enabled = true)
    @Description("Test change name annotation in custom foreign key field")
    public void testMigrateChangeNameAnnotationOnCustomFK() {
        executeCommand("tool_test_migrate_35", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_35");
    }

    @Test(enabled = true)
    @Description("Test remove name annotation from custom foreign key field")
    public void testMigrateRemoveNameAnnotationFromCustomFK() {
        executeCommand("tool_test_migrate_36", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_36");
    }

    @Test(enabled = true)
    @Description("Test create custom foreign key with name annotation")
    public void testMigrateCreateCustomFKWithNameAnnotation() {
        executeCommand("tool_test_migrate_37", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_37");
    }

    @Test(enabled = true)
    @Description("Test add generated annotation to existing field")
    public void testMigrateAddGeneratedAnnotationToExistingField() {
        executeCommand("tool_test_migrate_38", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_38");
    }

    @Test(enabled = true)
    @Description("Test remove generated annotation from existing field")
    public void testMigrateRemoveGeneratedAnnotationFromExistingField() {
        executeCommand("tool_test_migrate_39", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_39");
    }

    @Test(enabled = true)
    @Description("Test add new primary key with generated annotation")
    public void testMigrateAddNewPrimaryKeyWithGeneratedAnnotation() {
        executeCommand("tool_test_migrate_40", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_40");
    }

    @Test(enabled = true)
    @Description("Test add new index columns")
    public void testMigrateAddNewIndexColumns() {
        executeCommand("tool_test_migrate_41", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_41");
    }

    @Test(enabled = true)
    @Description("Test modify and remove index columns")
    public void testMigrateModifyAndRemoveIndexColumns() {
        executeCommand("tool_test_migrate_42", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_42");
    }

    @Test(enabled = true)
    @Description("Test add new unique index columns")
    public void testMigrateAddNewUniqueIndexColumns() {
        executeCommand("tool_test_migrate_43", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_43");
    }

    @Test(enabled = true)
    @Description("Test modify and remove unique index columns")
    public void testMigrateModifyAndRemoveUniqueIndexColumns() {
        executeCommand("tool_test_migrate_44", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_44");
    }

    @Test(enabled = true)
    @Description("Test add table with index columns")
    public void testMigrateAddTableWithIndexColumns() {
        executeCommand("tool_test_migrate_45", "secondMigration");
        assertMigrateGeneratedSources("tool_test_migrate_45");
    }

    private void executeCommand(String subDir, String migrationLabel) {
        Class<?> persistClass;
        Path sourcePath = Paths.get(GENERATED_SOURCES_DIRECTORY, subDir);
        try {
            persistClass = Class.forName("io.ballerina.persist.cmd.Migrate");
                Migrate persistCmd = (Migrate) persistClass.getDeclaredConstructor(String.class)
                        .newInstance(sourcePath.toAbsolutePath().toString());
                new CommandLine(persistCmd).parseArgs(migrationLabel);
                persistCmd.execute();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            errStream.println(e.getMessage());
        }
    }
}

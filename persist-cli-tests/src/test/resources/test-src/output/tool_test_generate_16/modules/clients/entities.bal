// this is an autogenerated file. Please do not edit.
import ballerina/persist;

@persist:Entity {
    key: ["id"],
    tableName: "Companies"
}
public type Company record {|
    readonly int id;
    string name;
    Employee[] employees?;
|};

@persist:Entity {
    key: ["id"],
    tableName: "Employees"
}
public type Employee record {|
    readonly int id;
    string name;

    @persist:Relation {keyColumns: ["companyId"], reference: ["id"]}
    Company company?;
|};


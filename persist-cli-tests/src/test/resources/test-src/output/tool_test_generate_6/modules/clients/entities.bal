// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina.
// It should not be modified by hand.
import ballerina/persist;
import ballerina/time;

@persist:Entity {
    key: ["a"],
    tableName: "DataTypes"
}
public type DataType record {|
    @persist:AutoIncrement
    readonly int a = -1;

    string b1;
    int c1;
    boolean d1;
    float e1;
    decimal f1;
    time:Utc j1;
    time:Civil k1;
    time:Date l1;
    time:TimeOfDay m1;
|};


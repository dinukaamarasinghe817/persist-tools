// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

public type Profile record {|
    readonly int id;
    string name;
    boolean isAdult;
    float salary;
    decimal age;
|};

public type ProfileInsert Profile;

public type ProfileUpdate record {|
    string name?;
    boolean isAdult?;
    float salary?;
    decimal age?;
|};

public type User record {|
    readonly int id;
    string name;
|};

public type UserInsert User;

public type UserUpdate record {|
    string name?;
|};

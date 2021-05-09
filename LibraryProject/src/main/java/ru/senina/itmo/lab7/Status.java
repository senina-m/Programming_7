package ru.senina.itmo.lab7;

public enum Status {
    OK(1),
    PROBLEM_PROCESSED(2),
    PARSER_EXCEPTION(3),
    NULLABLE_COMMAND(4),
    REGISTRATION_FAIL(5),
    DB_EXCEPTION(6),
    ACCESS_EXCEPTION(7),
    SCRIPT_RECURSION_EXCEPTION(8),
    NETWORK_EXCEPTION(9);

    private final int val;
    Status(int val){
        this.val = val;
    }

    public int getVal(){ return val;}
}

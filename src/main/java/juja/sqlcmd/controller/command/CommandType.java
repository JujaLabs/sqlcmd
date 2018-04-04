package juja.sqlcmd.controller.command;

public enum CommandType {

    CONNECT("connect", "\tconnect"
            + System.lineSeparator()
            + "\t\tconnection with database. Require to enter credentials"),

    EXIT("exit", "\texit"
            + System.lineSeparator()
            + "\t\tto exit from this session");

    private String name;
    private String description;

    CommandType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
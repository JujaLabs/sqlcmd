package juja.sqlcmd.controller.command;

import java.util.function.Supplier;

public enum CommandType {
    CONNECT("connect", "\tconnect"
            + System.lineSeparator()
            + "\t\tConnection with database. Require to enter credentials"
            , Connect::new),
    HELP("help", "\thelp"
            + System.lineSeparator()
            + "\t\tList of available commands to execute"
            , Help::new),
    EXIT("exit", "\texit"
            + System.lineSeparator()
            + "\t\tClose active connection.", Exit::new),
    UNSUPPORTED("unsupported", "", Unsupported::new);

    private String name;
    private String description;
    private Supplier<Command> command;

    CommandType(String name, String description, Supplier<Command> command) {
        this.name = name;
        this.description = description;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Command getCommand() {
        return command.get();
    }
}

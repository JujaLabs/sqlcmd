package juja.sqlcmd.controller;

import java.util.HashMap;
import java.util.Map;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.controller.command.Command;
import juja.sqlcmd.controller.command.CommandType;
import juja.sqlcmd.controller.command.Connect;
import juja.sqlcmd.controller.command.Exit;
import juja.sqlcmd.controller.command.UnsupportedCommand;
import juja.sqlcmd.view.View;

public class CommandHandler {
    private View view;
    private Map<CommandType, Command> registeredCommands = new HashMap<>();
    private CommandType[] allCommandTypes;

    public CommandHandler(DatabaseManager databaseManager, View view) {
        this.view = view;
        register(CommandType.CONNECT, new Connect(databaseManager, view));
        register(CommandType.EXIT, new Exit(databaseManager, view));
        allCommandTypes = CommandType.values();
    }

    public void handleCommand(String strCommand) {
        if (strCommand.toLowerCase().equals("help")) {
            view.write("List of available commands to execute:");
            for (CommandType commandType : allCommandTypes) {
                view.write(commandType.getDescription());
            }
        } else {
            Command command = getCommandByName(strCommand);
            command.execute();
        }
    }

    private Command getCommandByName(String commandName) {
        for (CommandType commandType : allCommandTypes) {
            if (commandName.toLowerCase().equals(commandType.getName())) {
                return registeredCommands.get(commandType);
            }
        }
        return new UnsupportedCommand(view);
    }

    private void register(CommandType commandType, Command command) {
        registeredCommands.put(commandType, command);
    }
}
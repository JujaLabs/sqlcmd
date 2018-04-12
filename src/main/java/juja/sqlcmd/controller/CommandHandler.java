package juja.sqlcmd.controller;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.controller.command.Command;
import juja.sqlcmd.controller.command.CommandType;
import juja.sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

class CommandHandler {

    private Map<String, Command> commands = new HashMap<>();

    CommandHandler(DatabaseManager databaseManager, View view) {
        for (CommandType commandType : CommandType.values()) {
            Command command = commandType.getCommand();
            command.setView(view);
            command.setDatabaseManager(databaseManager);
            commands.put(commandType.name(), command);
        }
    }

    void handleCommand(String strCommand) {
        String[] splitedCommand = strCommand.split("\\|");
        Command command = commands.get(splitedCommand[0].toUpperCase());
        if (command != null) {
            command.execute(splitedCommand);
        } else {
            commands.get(CommandType.UNSUPPORTED.getName().toUpperCase()).execute(splitedCommand);
        }
    }
}

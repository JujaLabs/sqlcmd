package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class Connect implements Command {
    private DatabaseManager databaseManager;
    private View view;

    public Connect(DatabaseManager databaseManager, View view) {
        this.databaseManager = databaseManager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (databaseManager.hasConnection()) {
            view.write("Connection already exists. To close current connection use Disconnect or Exit command");
        } else {
            view.write("Please enter required data in format:\n" +
                    "databaseName|login|password|");
            String command = view.read();
            if (isCommandValid(command)) {
                String[] splittedCommand = command.split("\\|");
                String dbName = splittedCommand[0];
                String login = splittedCommand[1];
                String password = splittedCommand[2];
                if (databaseManager.connect(dbName, login, password)) {
                    view.write("Successful connection to database " + dbName);
                    return;
                }
            }
            view.write("Unsuccessful connection to database. Check if entered data is correct");
        }
    }

    private boolean isCommandValid(String command) {
        String[] splittedCommand = command.split("\\|");
        return splittedCommand.length == 3;
    }
}

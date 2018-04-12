package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class Connect implements Command {
    private DatabaseManager databaseManager;
    private View view;

    Connect() {

    }

    @Override
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void execute(String[] splitedCommand) {
        if (databaseManager.isConnect()) {
            view.write("Connection already established.");
            return;
        }

        if (splitedCommand.length > 3) {
            String dbName = splitedCommand[1];
            String login = splitedCommand[2];
            String password = splitedCommand[3];
            if (databaseManager.connect(dbName, login, password)) {
                view.write("Connection established.");
            } else {
                view.write("Connection is not established.");
            }
        } else {
            view.write("Connection command has an incorrect format. It must be connect|databaseName|login|password");
        }

    }
}

package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class Exit implements Command {
    private View view;
    private DatabaseManager databaseManager;

    @Override
    public void execute(String[] command) {
        databaseManager.close();
        view.write("Connection is closed.");
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}

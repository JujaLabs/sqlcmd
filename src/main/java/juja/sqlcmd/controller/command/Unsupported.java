package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class Unsupported implements Command {
    private View view;
    private DatabaseManager databaseManager;

    @Override
    public void execute(String[] command) {
        view.write("Unsupported command");
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

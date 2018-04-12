package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class Help implements Command {
    private DatabaseManager databaseManager;
    private View view;

    @Override
    public void execute(String[] command) {
        for (CommandType commandType : CommandType.values()) {
            view.write(commandType.getDescription());
        }
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

package juja.sqlcmd.controller.command;

import java.sql.SQLException;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public final class Exit implements Command {
    private DatabaseManager databaseManager;
    private View view;

    public Exit(DatabaseManager databaseManager, View view) {
        this.databaseManager = databaseManager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (databaseManager.hasConnection()) {
            try {
                databaseManager.close();
                view.write("Connection is closed.");
            } catch (SQLException e) {
                view.write("Cannot close current connection! Reason: " + e.getMessage());
            }
        } else {
            view.write("There's no connection to close.");
        }
    }
}

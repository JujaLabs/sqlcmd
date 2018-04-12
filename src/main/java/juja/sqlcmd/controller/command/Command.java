package juja.sqlcmd.controller.command;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public interface Command {

    void execute(String[] command);

    void setView(View view);

    void setDatabaseManager(DatabaseManager databaseManager);

}

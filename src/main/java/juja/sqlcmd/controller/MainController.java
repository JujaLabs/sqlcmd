package juja.sqlcmd.controller;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class MainController {

    private View view;
    private DatabaseManager databaseManager;

    public MainController(View view, DatabaseManager databaseManager) {
        this.view = view;
        this.databaseManager = databaseManager;
    }

    public void run() {
        CommandHandler handler = new CommandHandler(databaseManager, view);
        view.write("***********************************************************************************************");
        view.write("Enter connect or \"help\" to see a list of possible commands");
        view.write("***********************************************************************************************");
        while (true) {
            String command = view.read();
            handler.handleCommand(command);
        }
    }
}

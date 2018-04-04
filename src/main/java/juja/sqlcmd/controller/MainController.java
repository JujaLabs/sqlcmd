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
        view.write("Welcome to SqlCmd application!\n" +
                "Please enter help to see a list of commands");
        view.write("-----");
        while (true) {
            String command = view.read();
            handler.handleCommand(command);
            if (command.toLowerCase().equals("exit")) {
                view.write("Bye!");
                break;
            }
            view.write("-----");
            view.write("Please enter next command");
        }
    }
}

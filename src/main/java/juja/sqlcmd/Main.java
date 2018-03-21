package juja.sqlcmd;

import juja.sqlcmd.controller.MainController;
import juja.sqlcmd.view.Console;
import juja.sqlcmd.view.View;

public class Main {
    public static void main(String[] args) {

        View view = new Console(System.out, System.in);
        DatabaseManager databaseManager = new JDBCDatabaseManager();
        MainController mainController = new MainController(view, databaseManager);

        mainController.run();
    }
}

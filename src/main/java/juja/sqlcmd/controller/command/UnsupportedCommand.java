package juja.sqlcmd.controller.command;

import juja.sqlcmd.view.View;

public class UnsupportedCommand implements Command {
    private View view;

    public UnsupportedCommand(View view) {
        this.view = view;
    }

    @Override
    public void execute() {
        view.write("Entered command is not supported. Enter 'help' to see a list of available commands");
    }
}

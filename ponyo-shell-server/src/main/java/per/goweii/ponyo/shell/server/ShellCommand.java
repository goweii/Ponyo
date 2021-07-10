package per.goweii.ponyo.shell.server;

public class ShellCommand {
    private final StringBuilder cmdBuilder;

    public ShellCommand(String cmd) {
        cmdBuilder = new StringBuilder(cmd);
    }

    public ShellCommand append(String cmd) {
        if (cmdBuilder.length() > 0) {
            cmdBuilder.append(" ");
        }
        cmdBuilder.append(cmd);
        return this;
    }

    public ShellResult exec() {
        return ShellExecutor.exec(this);
    }

    @Override
    public String toString() {
        return cmdBuilder.toString().trim();
    }
}

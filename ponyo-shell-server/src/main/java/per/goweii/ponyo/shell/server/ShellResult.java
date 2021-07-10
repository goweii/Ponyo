package per.goweii.ponyo.shell.server;

import java.util.List;

public class ShellResult {
    private final int exitValue;
    private final List<String> successMsg;
    private final List<String> errorMsg;

    public ShellResult(int exitValue, List<String> successMsg, List<String> errorMsg) {
        this.exitValue = exitValue;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return exitValue == 0;
    }

    public int getExitValue() {
        return exitValue;
    }

    public List<String> getErrorMsg() {
        return errorMsg;
    }

    public List<String> getSuccessMsg() {
        return successMsg;
    }

    public List<String> getOutput() {
        if (isSuccess()) {
            return getSuccessMsg();
        } else {
            return getErrorMsg();
        }
    }

    @Override
    public String toString() {
        return "ShellResult{" +
                "exitValue=" + exitValue +
                ", successMsg='" + successMsg + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}

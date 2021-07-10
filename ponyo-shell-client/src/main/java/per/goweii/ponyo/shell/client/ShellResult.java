package per.goweii.ponyo.shell.client;

public class ShellResult {
    private final int exitValue;
    private final String successMsg;
    private final String errorMsg;

    public ShellResult(int exitValue, String successMsg, String errorMsg) {
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public String getOutput() {
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

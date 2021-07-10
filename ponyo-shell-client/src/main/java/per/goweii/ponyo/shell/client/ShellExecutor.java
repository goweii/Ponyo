package per.goweii.ponyo.shell.client;

import java.io.DataInputStream;
import java.io.IOException;

public class ShellExecutor {
    public static ShellResult exec(ShellCommand cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd.toString());
            int exitValue = process.waitFor();
            String errorMsg = readString(process.getErrorStream());
            String inputMsg = readString(process.getInputStream());
            return new ShellResult(exitValue, inputMsg, errorMsg);
        } catch (Throwable e) {
            return new ShellResult(-1, null, e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static String readString(java.io.InputStream inputStream) {
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(inputStream);
            return dataInputStream.readUTF();
        } catch (Throwable e) {
            return null;
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package per.goweii.ponyo.shell.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ShellExecutor {
    public static ShellResult exec(ShellCommand cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd.toString());
            int exitValue = process.waitFor();
            List<String> errorMsg = readString(process.getErrorStream());
            List<String> inputMsg = readString(process.getInputStream());
            return new ShellResult(exitValue, inputMsg, errorMsg);
        } catch (Throwable e) {
            List<String> errorMsg = new LinkedList<>();
            errorMsg.add(e.toString());
            return new ShellResult(-1, null, errorMsg);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static List<String> readString(InputStream inputStream) {
        BufferedReader reader = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            reader = new BufferedReader(isr);
            List<String> lines = new LinkedList<>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
            return lines;
        } catch (Throwable e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

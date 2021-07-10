package per.goweii.ponyo.shell.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ShellSocketThread extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private ShellResultCallback callback = null;

    public ShellSocketThread(Socket socket) throws IOException {
        this.socket = socket;
        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(isr);
        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
        writer = new BufferedWriter(osw);
    }

    public void setCallback(ShellResultCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("Shell client received cmd result:" + line);
                if (callback != null) {
                    callback.onResult(line);
                }
            } catch (IOException e) {
                System.out.println("Shell client error:" + e.toString());
                break;
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exec(String cmd) {
        System.out.println("Shell client received cmd:" + cmd);
        if (cmd == null) {
            return false;
        }
        if (cmd.isEmpty()) {
            return false;
        }
        try {
            writer.write(cmd);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            System.out.println("Shell client received cmd error:" + e.toString());
            return false;
        }
    }
}

package per.goweii.ponyo.shell.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class ShellSocketThread extends Thread {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public ShellSocketThread(Socket socket) throws IOException {
        this.socket = socket;
        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(isr);
        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
        writer = new BufferedWriter(osw);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("Shell server received cmd:" + line);
                if (!line.isEmpty()) {
                    ShellResult result = new ShellCommand(line).exec();
                    System.out.println("Shell server exec cmd result:" + result);
                    List<String> output = result.getOutput();
                    if (output != null && !output.isEmpty()) {
                        for (String s : output) {
                            writer.write(s);
                            writer.newLine();
                            writer.flush();
                        }
                    }
                }
            } catch (Throwable e) {
                System.out.println("Shell server client error:" + e.toString());
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
}

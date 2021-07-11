package per.goweii.ponyo.shell.client;

import java.net.InetAddress;
import java.net.Socket;

public class ShellClient {
    public static void main(String[] args) {
        ShellSocketThread thread = connectServer();
        if (thread != null) {
            thread.start();
        }
    }

    public static ShellSocketThread connectServer() {
        System.out.println("Shell client running...");
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 41142);
            System.out.println("Shell client connect server succeed");
            return new ShellSocketThread(socket);
        } catch (Throwable e) {
            System.out.println("Shell client connect server error:" + e.toString());
            return null;
        }
    }
}

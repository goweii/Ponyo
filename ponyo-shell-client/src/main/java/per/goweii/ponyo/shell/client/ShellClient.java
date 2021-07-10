package per.goweii.ponyo.shell.client;

import java.net.InetAddress;
import java.net.Socket;

public class ShellClient {
    public static void main(String[] args) {
        connectServer();
    }

    public static ShellSocketThread connectServer() {
        System.out.println("Shell client running...");
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 41142);
            System.out.println("Shell client connect server succeed");
            ShellSocketThread thread = new ShellSocketThread(socket);
            thread.start();
            return thread;
        } catch (Throwable e) {
            System.out.println("Shell client connect server error:" + e.toString());
            return null;
        }
    }
}

package per.goweii.ponyo.shell.server;

import java.net.ServerSocket;

public class ShellThread extends Thread {
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(41142);
            System.out.println("Shell server wait for connect...");
            while (true) {
                try {
                    java.net.Socket socket = serverSocket.accept();
                    System.out.println("Shell server found client connected:" + socket.toString());
                    ShellSocketThread thread = new ShellSocketThread(socket);
                    thread.start();
                } catch (Throwable e) {
                    break;
                }
            }
        } catch (Throwable e) {
            System.out.println("Shell server error:" + e.toString());
        } finally {
            System.out.println("Shell server shutdown!");
        }
    }
}

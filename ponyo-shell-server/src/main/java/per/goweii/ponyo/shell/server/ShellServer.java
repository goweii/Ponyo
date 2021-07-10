package per.goweii.ponyo.shell.server;

public class ShellServer {
    public static void main(String[] args) {
        System.out.println("Shell server running...");
        new ShellThread().start();
    }
}

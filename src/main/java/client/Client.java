package client;
import org.example.Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
        Socket serverSocket = new Socket("localhost", Server.PORT) ;
            new Thread(new ServeReader(serverSocket)).start();
            new Thread(new ServerWriter(serverSocket)).start();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подклоючится к серверу: " + e.getMessage());
        }
    }
}

class ServerWriter implements Runnable {
    private final Socket serverSocket;

    public ServerWriter(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Scanner consoleReader = new Scanner(System.in);
        try (PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true)) {
            while (true) {
                String msgFromConsole = consoleReader.nextLine();
                out.println(msgFromConsole);
                if (Objects.equals("exit", msgFromConsole)) {
                    System.out.println("Отключаемся..");
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке на сервер: " + e.getMessage());
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера: " + e.getMessage());
        }
    }
}


class ServeReader implements Runnable {
    private final Socket serverSocket;


    public ServeReader(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (Scanner in = new Scanner(serverSocket.getInputStream())) {
            while (in.hasNext()) {
                String input = in.nextLine();
                System.out.println("Сообщение от сервера: " + input);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении сервера: " + e.getMessage());
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера: " + e.getMessage());
        }

    }
}
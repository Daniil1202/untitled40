package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Server {
    public static final int PORT = 8282;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запустился на порту:" + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Подключился новый клиент: " + clientSocket.getInetAddress());
                    ClientThread clientThread = new ClientThread(clientSocket);
                    clientThread.start();


                }catch (IOException e) {
                    System.out.println("Ошибка при взамодействии с клиентами: " + e.getMessage());


                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подключится к порту: " + PORT, e);
        }
    }
}

        class ClientThread extends Thread {
            private final Socket clientSocket;

            ClientThread(Socket clientSocket) {
                this.clientSocket = clientSocket;
            }

            @Override
            public void run() {
                try (Scanner in = new Scanner(clientSocket.getInputStream());
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    out.println("Вы успешно подлкючились");

                    while (true) {
                        if (clientSocket.isClosed()) {
                            System.out.println("Клиент отключился");
                            break;

                        }
                        String input = in.nextLine();

                        out.println(" сообщение от клиента: " + input );
                        System.out.println("Получено сообщение: " + input);

                        if (Objects.equals("exit", input)) {
                            System.out.println("Клиент отключился");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка при взамодействии с клиентами: " + e.getMessage());

                }
                try {
                    clientSocket.close();

                } catch (IOException e) {
                    System.err.println("Ошибка при подключении клиента: " + e.getMessage());
                }


            }

        }

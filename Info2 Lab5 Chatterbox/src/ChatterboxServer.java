import java.io.*;
import java.net.*;

public class ChatterboxServer {

    public static void main(String[] args) {
        int port = 8000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");

                    new ClientHandler(socket).start();
                } catch (IOException ex) {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ) {

            Thread readThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = reader.readLine()) != null) {
                        System.out.println("Client: " + clientMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected.");
                }
            });
            readThread.start();


            Thread writeThread = new Thread(() -> {
                try {
                    String serverMessage;
                    System.out.println("Enter messages to send to the client:");
                    while ((serverMessage = consoleReader.readLine()) != null) {
                        writer.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writeThread.start();


            readThread.join();
            writeThread.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket");
                e.printStackTrace();
            }
        }
    }
}

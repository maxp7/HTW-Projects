import java.io.*;
import java.net.*;

public class ChatterboxClient {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8000;

        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));


            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println("Zuul: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected.");
                }
            });
            readThread.start();


            Thread writeThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = consoleReader.readLine()) != null) {
                        writer.println(clientMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writeThread.start();


            readThread.join();
            writeThread.join();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException | InterruptedException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

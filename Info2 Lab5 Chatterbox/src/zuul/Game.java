package zuul;

import zuul.commands.Command;
import zuul.commands.Parser;
import zuul.world.Room;
import zuul.world.World;
import zuul.world.persistence.WorldAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    private Parser parser;
    private GameStatus gameStatus;
    private World world ;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        this("data/zuul.yml");
    }

    public Game(String worldFileName) {
        Room.resetCounter();
        createRooms(worldFileName);
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms(String worldFileName) {
        world = new WorldAdapter().readFromFile(worldFileName);
        gameStatus = new GameStatus(world.getStartRoom());
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Waiting for client connection...");
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while (gameStatus.isPlaying() && (inputLine = in.readLine()) != null) {
                Command command = parser.getCommand(inputLine);
                String output = command.process(gameStatus);
                out.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println(world.getDescription());
        System.out.println(gameStatus.getLocationDescription());
        System.out.println();
    }

    /**
     * This is a further method added by BK to provide a clearer interface that can be tested:
     * Game processes a commandLine and returns output.
     *
     * @param commandLine - the line entered as String
     * @return output of the command
     */
    public String processCommand(String commandLine) {
        Command command = parser.getCommand(commandLine);
        return command.process(gameStatus);
    }
}

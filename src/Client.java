import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    final String HOST = "127.0.0.1";
    final int PORT = 50000;

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public Client() throws IOException {
        socket = new Socket(HOST, PORT);
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    /** Reads and returns available data from the server */
    public String read() {
        int c;
        String line = "";

        // read while available
        try {
            do {
                c = input.read();
                line += (char) c;
            } while (input.available() > 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading from socket!");
            System.exit(1);
        }

        System.out.println("RECV: " + line);

        if (line.equals("ERR")) {
            System.out.println("Received error from server, aborting!");
            this.close();
            System.exit(1);
        }

        return line;
    }

    /** Send a message to the server */
    public void send(String message) {
        try {
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("SENT: " + message);
    }

    /** Reads data from the server then responds with an OK */
    public String readWithOK() {
        String line = read();
        send("OK");
        return line;
    }

    /** Sends a message to the server then expects an OK response */
    public void sendWithOK(String message) {
        send(message);
        if (!read().equals("OK")) {
            System.out.println("Error: did not receive expected OK from server!");
            System.exit(1);
        }
    }

    /** Quit and close connection to the server */
    public void quit() {
        send("QUIT");
        read();  // RECV: QUIT
        close();
    }

    /** Close connection to the server */
    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while closing socket connection!");
            System.exit(1);
        }
    }
}

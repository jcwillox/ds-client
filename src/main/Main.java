package main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // setup initial connection to the server
        Client client = new Client();
        client.send("HELO");
        client.read();
        client.send("AUTH admin");
        client.read();
        client.send("REDY");
        client.read();

        // handle job scheduling
        // ...

        // quit and close connection
        client.quit();
    }
}

package main;

import main.algorithms.SmartQueue;
import main.client.Client;
import main.client.Messages;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // read username from environment
        String name = System.getProperty("user.name");
        if (name == null)
            name = "unknown";

        // setup initial connection to the server
        Client client = new Client();
        client.auth(name);

        // read the first job
        Messages messages = client.messages();

        // handle job scheduling
        new SmartQueue().run(client, messages);

        // quit and close connection
        client.quit();
    }
}

package main;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import main.models.Job;
import main.models.Server;

public class Main {
    public static void main(String[] args) throws IOException {
        // setup initial connection to the server
        Client client = new Client();
        client.send("HELO");
        client.read();
        client.send("AUTH keccak");
        client.read();
        client.send("REDY");
        String read = client.read();

        // get largest server
        Server server = client.getLargestServer();

        // handle job scheduling
        while (!read.equals("NONE")) {
            if (!"JCPL".equals(read.split(" ", 2)[0])) {
                Job job = Job.fromScheduleJob(client, read.split(" ", 2)[1]);
                job.schedule(server);
            }
            client.send("REDY");
            read = client.read();
        }

        // quit and close connection
        client.quit();
    }
}

package main;

import main.client.Client;
import main.client.models.Job;
import main.client.models.Server;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        // read username from environment
        String name = System.getProperty("user.name");
        if (name == null)
            name = "unknown";

        // setup initial connection to the server
        Client client = new Client();
        client.send("HELO");
        client.read();
        client.send("AUTH " + name);
        client.read();

        // read the first job
        client.send("REDY");
        String line = client.read();

        // get largest server
        Server server = getLargestServer(client);

        // handle job scheduling
        while (!line.equals("NONE")) {
            String[] splitCmd = line.split(" ", 2);
            switch (splitCmd[0]) {
                case "JOBP":
                case "JOBN":
                    Job job = Job.fromScheduleJob(client, splitCmd[1]);
                    job.schedule(server);
            }
            client.send("REDY");
            line = client.read();
        }

        // quit and close connection
        client.quit();
    }

    /** Returns the largest server comparing core, memory or disk size */
    private static Server getLargestServer(Client client) {
        Server[] servers = client.getAllServers();
        Arrays.sort(servers, (t1, t2) -> {
            if (t1.core > t2.core)
                return -1;
            if (t1.memory > t2.memory)
                return -1;
            return Integer.compare(t2.disk, t1.disk);
        });
        return servers[0];
    }
}

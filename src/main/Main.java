package main;

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
        client.send("AUTH admin");
        client.read();
        client.send("REDY");
        String line = client.read();

        // get largest server
        Server server = getLargestServer(client);

        // handle job scheduling
        while (!line.equals("NONE")) {
            String splitCmd[] = line.split(" ", 2);
            if (!"JCPL".equals(splitCmd[0])) {
                switch (splitCmd[0]) {
                case "JOBP":
                case "JOBN": {
                    Job job = Job.fromScheduleJob(client, splitCmd[1]);
                    job.schedule(server);
                }
                }
            }
            client.send("REDY");
            line = client.read();
        }

        // quit and close connection
        client.quit();
    }

    /** Returns the largest server comparing core, memory or disk size */
    private static Server getLargestServer(Client client) {
        Server[] server = client.getAllServers();
        Arrays.sort(server, (t1, t2) -> {
            if (t1.core > t2.core)
                return -1;
            if (t1.memory > t2.memory)
                return -1;
            return Integer.compare(t2.disk, t1.disk);
        });
        return server[0];
    }
}

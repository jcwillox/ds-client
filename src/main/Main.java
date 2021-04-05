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
        String read = client.read();

        Server[] serversArray = client.getAllServers(); // array sort to get the largest server
        Arrays.sort(serversArray, (t1, t2) -> {
            if (t1.core > t2.core)
                return -1;
            if (t1.memory > t2.memory)
                return -1;
            else
                return Integer.compare(t2.disk, t1.disk);
        });

        // handle job scheduling
        Job job = Job.fromScheduleJob(client, read.split(" ", 2)[1]);
        job.schedule(serversArray[0]);

        // quit and close connection
        client.quit();
    }
}

package main.algorithms;

import main.client.Client;
import main.client.Messages;
import main.client.models.Server;

import java.util.Arrays;

public class AllToLargest {
    /** Sorts servers by largest in descending order */
    public static Server[] sortServersLargest(Server[] servers) {
        Arrays.sort(servers, (t1, t2) -> {
            if (t1.core > t2.core)
                return -1;
            if (t1.memory > t2.memory)
                return -1;
            return Integer.compare(t2.disk, t1.disk);
        });
        return servers;
    }

    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        Server[] servers = sortServersLargest(client.getAllServers());
        messages.forEachJob(job -> job.schedule(servers[0]));
    }
}

package main.algorithms;

import main.client.Client;
import main.client.Messages;
import main.client.models.Server;

import static main.algorithms.AllToLargest.sortServersLargest;

public class AllToLargest2 {
    private boolean toFirst = true;

    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        Server[] servers = sortServersLargest(client.getAllServers());
        messages.forEachJob(job -> {
            if (toFirst)
                job.schedule(servers[0]);
            else
                job.schedule(servers[1]);
            toFirst = !toFirst;
        });
    }
}

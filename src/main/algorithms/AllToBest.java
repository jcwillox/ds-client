package main.algorithms;

import main.client.Client;
import main.client.Messages;
import main.client.models.Server;

import static main.algorithms.SmartQueue.getBestServer;

public class AllToBest {
    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        messages.forEachJob(job -> {
            Server[] servers = job.getAvailableServers();
            if (servers.length == 0)
                servers = job.getCapableServers();
            getBestServer(servers, job).schedule(job);
        });
    }
}

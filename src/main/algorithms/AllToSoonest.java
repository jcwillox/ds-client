package main.algorithms;

import main.client.Client;
import main.client.Messages;
import main.client.models.Server;

import static main.algorithms.SmartQueue.getBestServer;
import static main.algorithms.SmartQueue.getSoonestServer;

public class AllToSoonest {
    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        messages.forEachJob(job -> {
            Server[] servers = job.getAvailableServers();
            if (servers.length > 0)
                getBestServer(servers, job).schedule(job);
            else
                getSoonestServer(job.getCapableServers()).schedule(job);
        });
    }
}

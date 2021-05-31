package main.algorithms;

import main.client.Client;
import main.client.Commands;
import main.client.Messages;
import main.client.models.Job;
import main.client.models.JobCompletion;
import main.client.models.Server;
import main.client.models.ServerState;

import java.util.ArrayList;
import java.util.HashMap;

public class SmartQueue {
    private final HashMap<String, Integer> serverJobs = new HashMap<>();
    private int queueSize = 5;

    /** Returns a server that can run the job with the best fitness value */
    private static Server getBestServer(Server[] servers, Job job) {
        Integer fitness = null;
        Server bestServer = null;

        for (Server server : servers) {
            // server does not have enough cores to run this job
            if (!server.canRun(job))
                continue;

            // we can assume this must be greater than 0
            int diff = server.core - job.core;

            // we can never do better than 0
            if (diff == 0)
                return server;

            // find value closest to zero
            if (fitness == null || diff < fitness) {
                fitness = diff;
                bestServer = server;
            }
        }

        return bestServer;
    }

    /** Returns the server which will be available the soonest */
    private static Server getSoonestServer(Server[] servers) {
        Integer min = null;
        Server minServer = null;

        for (Server server : servers) {
            int value = server.getServerWaitTime();

            // we can never do better than 0
            if (value == 0)
                return server;

            if (min == null || value < min) {
                min = value;
                minServer = server;
            }
        }

        return minServer;
    }

    /** Returns the first inactive server */
    private static Server getInactiveServer(Server[] servers) {
        for (Server server : servers)
            if (server.state.equals(ServerState.INACTIVE))
                return server;
        return null;
    }

    /** Returns all the servers which have been started (not inactive) */
    private static Server[] getActiveServers(Server[] servers) {
        ArrayList<Server> activeServers = new ArrayList<>();

        for (Server server : servers)
            if (!server.state.equals(ServerState.INACTIVE))
                activeServers.add(server);

        return activeServers.toArray(new Server[]{});
    }

    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        messages.forEach((msg, job) -> {
            if (msg.cmd.equals(Commands.JOB_COMPLETION)) {
                String uuid = JobCompletion.fromJobCompletion(msg.content).getServerUUID();
                updateJobCount(uuid, -1);
            } else if (msg.hasJob()) {
                Server server;
                Server[] servers = job.getCapableServers();
                Server[] allowed = getFreeServers(servers);

                // increase the queue size if all capable servers are already at queue size
                if (allowed.length == 0) {
                    queueSize += 1;
                    allowed = servers;
                }

                // try find the best server that has sufficient resources to run this job and has been started
                Server[] activeServers = getActiveServers(allowed);
                server = getBestServer(activeServers, job);

                // try fetch the server with the shortest waiting time
                if (server == null)
                    server = getSoonestServer(activeServers);

                // try to startup a new server
                if (server == null)
                    server = getInactiveServer(servers);

                // final resort is to just pick the last server, should be unreachable
                if (server == null)
                    server = allowed[allowed.length - 1];

                server.schedule(job);

                // increment the number of active jobs
                updateJobCount(server.getUUID(), 1);
            }
        });
    }

    /** Update the number of jobs scheduled to a server */
    private void updateJobCount(String uuid, int change) {
        // ensure server exists in HashMap
        if (!serverJobs.containsKey(uuid)) {
            serverJobs.put(uuid, change);
        } else {
            int current = serverJobs.get(uuid);
            serverJobs.put(uuid, current + change);
        }
    }

    /** Returns the number of jobs scheduled to a server */
    private int getJobCount(String uuid) {
        // ensure server exists in HashMap
        if (!serverJobs.containsKey(uuid))
            serverJobs.put(uuid, 0);
        return serverJobs.get(uuid);
    }

    /** Returns all the servers which are not at max queue size */
    private Server[] getFreeServers(Server[] servers) {
        ArrayList<Server> freeServers = new ArrayList<>();

        for (Server server : servers)
            if (getJobCount(server.getUUID()) < queueSize)
                freeServers.add(server);

        return freeServers.toArray(new Server[]{});
    }
}

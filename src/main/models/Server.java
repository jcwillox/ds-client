package main.models;

import main.Client;
import main.Logging;

public class Server {
    public final String type;
    public final int id;
    public final String state; // inactive, booting, idle, active, unavailable
    public final int startTime;
    public final int core;
    public final int memory;
    public final int disk;
    public final int waitingJobs;
    public final int runningJobs;

    private final Client client;

    public Server(Client client, String type, int id, String state, int startTime, int core, int memory, int disk,
            int waitingJobs, int runningJobs) {
        this.type = type;
        this.id = id;
        this.state = state;
        this.startTime = startTime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
        this.waitingJobs = waitingJobs;
        this.runningJobs = runningJobs;
        this.client = client;

        Logging.yellow(this.toString());
    }

    public static Server fromGetServers(Client client, String line) {
        String[] info = line.split(" ");
        String type = info[0];
        int id = Integer.parseInt(info[1]);
        String state = info[2];
        int startTime = Integer.parseInt(info[3]);
        int core = Integer.parseInt(info[4]);
        int memory = Integer.parseInt(info[5]);
        int disk = Integer.parseInt(info[6]);
        int waitingJobs = Integer.parseInt(info[7]);
        int runningJobs = Integer.parseInt(info[8]);

        return new Server(client, type, id, state, startTime, core, memory, disk, waitingJobs, runningJobs);
    }

    public Job[] getJobs() {
        return client.getJobs(type, id);
    }

    public void terminate() {
        client.terminateServer(type, id);
    }

    @Override
    public String toString() {
        return String.format("<Server type='%s' id=%d state='%s' core=%d memory=%d, disk=%d>", type, id, state, core,
                memory, disk);
    }
}

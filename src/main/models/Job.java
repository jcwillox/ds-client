package main.models;

import main.Client;
import main.Constants;

public class Job {
    public final int submitTime;
    public final int id;
    public final int estRuntime;
    public final int core;
    public final int memory;
    public final int disk;

    private final Client client;

    // TODO: support Job representation from listJobs
    public Job(Client client, String line) {
        this.client = client;

        String[] info = line.split(" ");
        this.submitTime = Integer.parseInt(info[0]);
        this.id = Integer.parseInt(info[1]);
        this.estRuntime = Integer.parseInt(info[2]);
        this.core = Integer.parseInt(info[3]);
        this.memory = Integer.parseInt(info[4]);
        this.disk = Integer.parseInt(info[5]);

        System.out.println(Constants.YELLOW + this.toString());
    }

    public void schedule(Server server) {
        client.scheduleJob(id, server.type, server.id);
    }

    public void migrate(Server from, Server to) {
        // TODO: migrate
    }

    @Override
    public String toString() {
        return String.format("<Job id=%d core=%d memory=%d disk=%d>", id, core, memory, disk);
    }
}

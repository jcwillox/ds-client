package main.models;

import main.Client;
import main.Constants;

public class Job {
    public final int startTime;  // also called submitTime
    public final int id;
    public final int state;  // 0 is unknown, 1 is waiting, 2 is running
    public final int estRuntime;
    public final int core;
    public final int memory;
    public final int disk;

    private final Client client;

    public Job(Client client, int startTime, int id, int state, int estRuntime, int core, int memory, int disk) {
        this.client = client;
        this.startTime = startTime;
        this.id = id;
        this.state = state;
        this.estRuntime = estRuntime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;

        System.out.println(Constants.YELLOW + this.toString());
    }

    /** Deserialize job schedule request from the server (JOBP, JOBN) */
    public static Job fromScheduleJob(Client client, String line) {
        String[] info = line.split(" ");
        int startTime = Integer.parseInt(info[0]);
        int id = Integer.parseInt(info[1]);
        int state = 0;  // unknown
        int estRuntime = Integer.parseInt(info[2]);
        int core = Integer.parseInt(info[3]);
        int memory = Integer.parseInt(info[4]);
        int disk = Integer.parseInt(info[5]);
        return new Job(client, startTime, id, state, estRuntime, core, memory, disk);

    }

    /** Deserialize response from the LSTJ request */
    public static Job fromListJob(Client client, String line) {
        String[] info = line.split(" ");
        int startTime = Integer.parseInt(info[2]);
        int id = Integer.parseInt(info[0]);
        int state = Integer.parseInt(info[1]);
        int estRuntime = Integer.parseInt(info[3]);
        int core = Integer.parseInt(info[4]);
        int memory = Integer.parseInt(info[5]);
        int disk = Integer.parseInt(info[6]);
        return new Job(client, startTime, id, state, estRuntime, core, memory, disk);
    }

    public void schedule(Server server) {
        client.scheduleJob(id, server.type, server.id);
    }

    public void migrate(Server from, Server to) {
        client.migrateJob(id, from.type, from.id, to.type, to.id);
    }

    @Override
    public String toString() {
        return String.format("<Job id=%d startTime=%d state=%d estRuntime=%d core=%d memory=%d disk=%d>", id, startTime,
                state, estRuntime, core, memory, disk);
    }
}

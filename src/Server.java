public class Server {
    public final String type;
    public final int id;
    public final String state;  // inactive, booting, idle, active, unavailable
    public final int startTime;
    public final int core;
    public final int memory;
    public final int disk;
    public final int waitingJobs;
    public final int runningJobs;

    private final Client client;

    public Server(Client client, String line) {
        String[] info = line.split(" ");
        this.type = info[0];
        this.id = Integer.parseInt(info[1]);
        this.state = info[2];
        this.startTime = Integer.parseInt(info[3]);
        this.core = Integer.parseInt(info[4]);
        this.memory = Integer.parseInt(info[5]);
        this.disk = Integer.parseInt(info[6]);
        this.waitingJobs = Integer.parseInt(info[7]);
        this.runningJobs = Integer.parseInt(info[8]);
        this.client = client;

        System.out.println(this.toString());
    }

    public Job[] getJobs() {
        return client.getJobs(type, id);
    }

    public void terminate() {
        client.terminateServer(type, id);
    }

    @Override
    public String toString() {
        return String.format("<Server type='%s' id=%d state='%s' core=%d memory=%d, disk=%d>", type, id, state, core, memory, disk);
    }
}

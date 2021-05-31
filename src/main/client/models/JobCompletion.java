package main.client.models;

import main.Logging;

public class JobCompletion {
    public final int endTime;
    public final int id;
    public final String serverType;
    public final int serverId;

    public JobCompletion(int endTime, int id, String serverType, int serverId) {
        this.endTime = endTime;
        this.id = id;
        this.serverType = serverType;
        this.serverId = serverId;

        Logging.yellow(this.toString());
    }

    /** Deserialize job completion message from the server (JCPL) */
    public static JobCompletion fromJobCompletion(String line) {
        String[] info = line.split(" ");

        int endTime = Integer.parseInt(info[0]);
        int id = Integer.parseInt(info[1]);
        String serverType = info[2];
        int serverId = Integer.parseInt(info[3]);

        return new JobCompletion(endTime, id, serverType, serverId);
    }

    public String getServerUUID() {
        return serverType + serverId;
    }

    @Override
    public String toString() {
        return String.format(
                "<JobCompletion endTime=%d id=%d serverType='%s' serverId=%d>",
                endTime, id, serverType, serverId
        );
    }
}

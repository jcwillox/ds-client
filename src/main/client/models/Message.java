package main.client.models;

import main.client.Client;
import main.client.Commands;

public class Message {
    public String cmd;
    public String content;

    public Message(String cmd, String content) {
        this.cmd = cmd;
        this.content = content;
    }

    /**
     * Returns true if the current message can be parsed into a {@code Job} object
     */
    public boolean hasJob() {
        return cmd.equals(Commands.JOB) || cmd.equals(Commands.JOB_PREEMPTED);
    }

    /**
     * Attempts to parse a message into a {@code Job} object
     * 
     * @return null if the message does not contain a job
     */
    public Job toJob(Client client) {
        if (!hasJob())
            return null;
        return Job.fromScheduleJob(client, content);
    }
}

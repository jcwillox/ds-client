package main.client;

import main.Logging;
import main.client.models.Job;
import main.client.models.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Client {
    final String HOST = "127.0.0.1";
    final int PORT = 50000;

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public Client() throws IOException {
        socket = new Socket(HOST, PORT);
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    /** Reads and returns available data from the server */
    public String read() {
        int c;
        StringBuilder sb = new StringBuilder();

        // read while available
        try {
            c = input.read();
            while (c != '\n') {
                sb.append((char) c);
                c = input.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logging.error("while reading from socket!");
            System.exit(1);
        }

        String line = sb.toString();
        Logging.magenta("[RECV]", line);

        if (line.startsWith("ERR")) {
            Logging.error("received error from server, aborting!");
            this.close();
            System.exit(1);
        }

        return line;
    }

    /** Send a message to the server */
    public void send(String message) {
        try {
            output.write((message + '\n').getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Logging.blue("[SENT]", message);
    }

    /** Reads data from the server then responds with an OK */
    public String readWithOK() {
        String line = read();
        send(Commands.OK);
        return line;
    }

    /** Sends a message to the server then reads an OK response */
    public void sendWithOK(String message) {
        send(message);
        if (!read().equals(Commands.OK)) {
            Logging.error("did not receive expected OK from server!");
            System.exit(1);
        }
    }

    /** Quit and close connection to the server */
    public void quit() {
        send(Commands.QUIT);
        read(); // RECV: QUIT
        close();
    }

    /** Close connection to the server */
    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logging.error("while closing socket connection!");
            System.exit(1);
        }
    }

    /* SERVER METHODS */

    private Server[] getServers(String message) {
        send("GETS " + message);

        // RECV: DATA nRecs recLen
        int records = Integer.parseInt(readWithOK().split(" ")[1]);

        Server[] servers = new Server[records];
        for (int i = 0; i < records; i++)
            servers[i] = Server.fromGetServers(this, read());
        if (records > 0)
            send(Commands.OK);
        read(); // RECV: '.'

        return servers;
    }

    /**
     * Returns all servers that can immediately provide the resource requirements
     */
    public Server[] getAvailableServers(int core, int memory, int disk) {
        return getServers(String.format("Avail %d %d %d", core, memory, disk));
    }

    /** Returns all servers that can eventually provide the resource requirements */
    public Server[] getCapableServers(int core, int memory, int disk) {
        return getServers(String.format("Capable %d %d %d", core, memory, disk));
    }

    /** Returns all servers */
    public Server[] getAllServers() {
        return getServers("All");
    }

    /** Returns all servers of the given type */
    public Server[] getServersByType(String type) {
        return getServers("Type " + type);
    }

    /**
     * Terminates a server, killing all waiting/running jobs and switching it to an
     * inactive state.
     */
    public void terminateServer(String serverType, int serverId) {
        send(String.format("TERM %s %d", serverType, serverId));
    }

    public int getServerWaitTime(String serverType, int serverID) {
        send(String.format("EJWT %s %d", serverType, serverID));
        return Integer.parseInt(read());
    }

    /* JOB METHODS */

    public Job[] getJobs(String serverType, int serverID) {
        ArrayList<Job> jobs = new ArrayList<>();
        String response;
        send(String.format("LSTJ %s %d", serverType, serverID));
        readWithOK(); // read Data response and ignores it
        while (true) {
            response = read();
            if (response.equals("."))
                break; // check for end of DATA sequence
            jobs.add(Job.fromListJob(this, response));
            send(Commands.OK);
        }
        return jobs.toArray(new Job[0]);
    }

    public void killJob(String serverType, int serverID, int jobID) {
        sendWithOK(String.format("KILJ %s %d %d", serverType, serverID, jobID));
    }

    public void migrateJob(int jobID, String srcServerType, int srcServerID, String tgtServerType, int tgtServerID) {
        sendWithOK(String.format("MIGJ %d %s %d %s %d", jobID, srcServerType, srcServerID, tgtServerType, tgtServerID));
    }

    public void pushJob() {
        send("PSHJ");
    }

    public int getJobCount(String serverType, int serverID, int jobState) {
        send(String.format("CNTJ %s %d %d", serverType, serverID, jobState));
        return Integer.parseInt(read());
    }

    /** Submits a job to the specified server for processing */
    public void scheduleJob(int jobId, String serverType, int serverId) {
        sendWithOK(String.format("SCHD %d %s %d", jobId, serverType, serverId));
    }
}

package main;

import main.models.Job;
import main.models.Server;

import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// TODO: migrateJob [MIGJ]
// TODO: killJob [KILJ]
// TODO: getJobs [LSTJ]
// TODO: getJobCount [CNTJ]
// TODO: get server wait time [EJWT]
// TODO: pushJob [PSHJ]

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
        String line = "";

        // read while available
        try {
            do {
                c = input.read();
                line += (char) c;
            } while (input.available() > 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading from socket!");
            System.exit(1);
        }

        System.out.println(Constants.MAGENTA + "[RECV] " + line);

        if (line.equals("ERR")) {
            System.out.println("Received error from server, aborting!");
            this.close();
            System.exit(1);
        }

        return line;
    }

    /** Send a message to the server */
    public void send(String message) {
        try {
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println(Constants.BLUE + "[SENT] " + message);
    }

    /** Reads data from the server then responds with an OK */
    public String readWithOK() {
        String line = read();
        send("OK");
        return line;
    }

    /** Sends a message to the server then expects an OK response */
    public void sendWithOK(String message) {
        send(message);
        if (!read().equals("OK")) {
            System.out.println("Error: did not receive expected OK from server!");
            System.exit(1);
        }
    }

    /** Quit and close connection to the server */
    public void quit() {
        send("QUIT");
        read();  // RECV: QUIT
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
            System.err.println("Error while closing socket connection!");
            System.exit(1);
        }
    }

    /* SERVER METHODS */

    private Server[] getServers(String message) {
        send("GETS " + message);

        // RECV: DATA nRecs recLen
        int records = Integer.parseInt(readWithOK().split(" ")[1]);

        String[] data = readWithOK().split("\n");
        read();  // RECV: '.'

        Server[] servers = new Server[records];
        for (int i = 0; i < records; i++)
            servers[i] = new Server(this, data[i]);

        return servers;
    }

    /** Returns all servers that can immediately provide the resource requirements */
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
     * Terminates a server, killing all waiting/running jobs
     * and switching it to an inactive state.
     */
    public void terminateServer(String serverType, int serverId) {
        send(String.format("TERM %s %d", serverType, serverId));
    }

    /* JOB METHODS */
    public Job[] getJobs(String serverType, int serverID) {
        //throw new UnsupportedOperationException();
        ArrayList<Job> jobs = new ArrayList<Job>();
        int nRecs;
        int recLen;
        String response;
        
        send(String.format("LSTJ %s %d",serverType, serverID));

        while (true) {
            response = read();
            if (response.equals(".")){  // check for end of DATA sequence
                break;
            } else if (response.substring(0,4).equals("DATA")) {  //check if it's a "DATA" response
                String[] info = response.split(" ");
                nRecs = info[1];
                recLen = info[2];
                send("OK");
            } else {  
                jobs.add(Job.fromListJob(this, response));
                send("OK");
            }
        }

        Job[] jobArr = jobs.toArray(new Job[0]);
        return jobArr;
    }

    public void killJob(String serverType, int serverID, int jobID) {
        send(String.format("KILJ %s %d %d", serverType, serverId, jobID));
    }

    public void migrateJob(int jobID, String srcServerType, int srcServerID, String tgtServerType, int tgtServerID) {
        send(String.format("MIGJ %d %s %d %s %d", jobID, srcServerType, srcServerID, tgtServerType, tgtServerID));
    }

    public void pushJob() {
        send("PSHJ");
    }

    public int getJobCount(String serverType, int serverID, int jobState) {
        send(String.format("CNTJ %s %d %d", serverType, serverID, jobState));
        int jc = Integer.parseInt(read());
        return jc;
    }

    public int getServerWaitTime(String serverType, int serverID) {
        send(String.format("EJWT %s %d",serverType,serverID));
        int wt = Integer.parseInt(read());
        return wt;
    }

    /** Submits a job to the specified server for processing */
    public void scheduleJob(int jobId, String serverType, int serverId) {
        sendWithOK(String.format("SCHD %d %s %d", jobId, serverType, serverId));
        // TODO: handle scheduling failures
    }
}

package main.client;

import main.client.models.Job;
import main.client.models.Message;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Messages {
    private final Client client;
    private Message message;

    Messages(Client client) {
        this.client = client;
        // immediately fetch the next message from the server
        next();
    }

    /** Calls a function with each message as they are received from the server */
    public void forEach(BiConsumer<Message, Job> func) {
        while (message != null) {
            func.accept(message, message.toJob(client));
            next();
        }
    }

    /** A utility method to automatically pass all schedule jobs to a function */
    public void forEachJob(Consumer<Job> func) {
        while (message != null) {
            if (message.hasJob())
                func.accept(message.toJob(client));
            next();
        }
    }

    /** Returns the current message received from the server */
    public Message current() {
        return message;
    }

    /** Reads the next message from the server */
    public void next() {
        client.ready();
        String[] parts = client.read().split(" ", 2);

        if (parts[0].equals(Commands.NONE))
            message = null;
        else if (parts.length == 2)
            message = new Message(parts[0], parts[1]);
        else
            message = new Message(parts[0], "");
    }
}

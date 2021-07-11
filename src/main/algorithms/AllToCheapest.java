package main.algorithms;

import main.client.Client;
import main.client.Messages;
import main.client.models.Job;
import main.xml.XmlParser;
import main.xml.XmlServer;

public class AllToCheapest {
    private static XmlServer getCheapestXmlServer(XmlServer[] servers, Job job) {
        for (XmlServer server : servers)
            if (server.canRun(job))
                return server;
        return null;
    }

    /** Execute the scheduling algorithm */
    public void run(Client client, Messages messages) {
        XmlServer[] xmlServers = XmlParser.parseXmlServers();

        messages.forEachJob(job -> {
            String cheapestType = getCheapestXmlServer(xmlServers, job).type;
            client.scheduleJob(job.id, cheapestType, 0);
        });
    }
}

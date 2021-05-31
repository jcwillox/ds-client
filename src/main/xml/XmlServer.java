package main.xml;

import main.client.models.Job;
import org.w3c.dom.NamedNodeMap;

/* Representation of a server from ds-system.xml */
public class XmlServer {
    public final String type;
    public final int limit;
    public final int bootTime;
    public final float rate;
    public final int core;
    public final int memory;
    public final int disk;

    XmlServer(String type, int limit, int bootTime, float rate, int core, int memory, int disk) {
        this.type = type;
        this.limit = limit;
        this.bootTime = bootTime;
        this.rate = rate;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
    }

    public static XmlServer fromXml(NamedNodeMap attributes) {
        String type = attributes.getNamedItem("type").getTextContent();
        int limit = Integer.parseInt(attributes.getNamedItem("limit").getTextContent());
        int bootTime = Integer.parseInt(attributes.getNamedItem("bootupTime").getTextContent());
        float rate = Float.parseFloat(attributes.getNamedItem("hourlyRate").getTextContent());
        int core = Integer.parseInt(attributes.getNamedItem("coreCount").getTextContent());
        int memory = Integer.parseInt(attributes.getNamedItem("memory").getTextContent());
        int disk = Integer.parseInt(attributes.getNamedItem("disk").getTextContent());

        return new XmlServer(type, limit, bootTime, rate, core, memory, disk);
    }

    /** Returns true if the server has the resources to run the job */
    public boolean canRun(Job job) {
        return core >= job.core && memory >= job.memory && disk >= job.disk;
    }
}

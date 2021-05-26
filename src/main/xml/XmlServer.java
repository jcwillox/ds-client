package main.xml;

import org.w3c.dom.NamedNodeMap;

/* Representation of a server from ds-system.xml */
public class XmlServer {
    public final String type;
    public final int bootTime;
    public final float rate;
    public final int core;
    public final int memory;
    public final int disk;

    XmlServer(String type, int bootTime, float rate, int core, int memory, int disk) {
        this.type = type;
        this.bootTime = bootTime;
        this.rate = rate;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
    }

    public static XmlServer fromXml(NamedNodeMap attributes) {
        String type = attributes.getNamedItem("type").getTextContent();
        int bootTime = Integer.parseInt(attributes.getNamedItem("bootupTime").getTextContent());
        float rate = Float.parseFloat(attributes.getNamedItem("hourlyRate").getTextContent());
        int core = Integer.parseInt(attributes.getNamedItem("coreCount").getTextContent());
        int memory = Integer.parseInt(attributes.getNamedItem("memory").getTextContent());
        int disk = Integer.parseInt(attributes.getNamedItem("disk").getTextContent());

        return new XmlServer(type, bootTime, rate, core, memory, disk);
    }
}

package main.xml;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public interface XmlParser {
    /* Returns a list of servers parsed from ds-system.xml */
    static XmlServer[] parseXmlServers() {
        ArrayList<XmlServer> servers = new ArrayList<>();

        try {
            File inputFile = new File("ds-system.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("server");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                servers.add(XmlServer.fromXml(node.getAttributes()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return servers.toArray(new XmlServer[] {});
    }
}

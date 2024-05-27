import java.util.ArrayList;
import java.util.List;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class BayesianNetwork {

    private Map<String,Node> network;


    public BayesianNetwork() {
        network = new HashMap<>();
    }

    public void addNode(Node node) {
        this.network.put(node.getName(),node);
    }

    public Node getNode(String name) {
        return this.network.get(name);
    }

    public Collection<Node> getNodes() {
        return network.values();
    }

    public void removeNode(String name) {
        this.network.remove(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bayesian Network:\n");
        for (Node node : network.values()) {
            sb.append(node.toString()).append("\n");
        }
        return sb.toString();
    }

    public static BayesianNetwork parseXML(String filename) {
        BayesianNetwork network = new BayesianNetwork();
        try {
            File file = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("VARIABLE");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String name = element.getElementsByTagName("NAME").item(0).getTextContent();
                Node node = new Node(name);
                NodeList outcomes = element.getElementsByTagName("OUTCOME");
                for (int j = 0; j < outcomes.getLength(); j++) {
                    node.addOutcome(outcomes.item(j).getTextContent());
                }
                network.addNode(node);
            }

            NodeList definitionList = doc.getElementsByTagName("DEFINITION");
            for (int i = 0; i < definitionList.getLength(); i++) {
                Element element = (Element) definitionList.item(i);
                String name = element.getElementsByTagName("FOR").item(0).getTextContent();
                Node node = network.getNode(name);
                if (node == null) {
                    throw new IllegalArgumentException("Node " + name + " not found in the network.");
                }
                NodeList givenNodes = element.getElementsByTagName("GIVEN");
                for (int j = 0; j < givenNodes.getLength(); j++) {
                    String parentName = givenNodes.item(j).getTextContent();
                    Node parent = network.getNode(parentName);
                    if (parent == null) {
                        throw new IllegalArgumentException("Parent node " + parentName + " not found in the network.");
                    }
                    node.addParent(parent);
                    parent.addChild(node);
                }

                NodeList givenProbabilities = element.getElementsByTagName("TABLE");
                String probs = givenProbabilities.item(0).getTextContent();
                String[] probsArr = probs.split(" ");
                List<Double> probsLst = Arrays.asList(probsArr).stream().map(s -> Double.parseDouble(s)).toList();
                node.generateCPT(probsLst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return network;
    }

}

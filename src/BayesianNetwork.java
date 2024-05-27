import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * Represents a Bayesian network.
 */
public class BayesianNetwork {

    private Map<String, Node> network;

    /**
     * Constructs an empty Bayesian network.
     */
    public BayesianNetwork() {
        network = new HashMap<>();
    }

    /**
     * Constructs a copy of the given Bayesian network.
     *
     * @param network the Bayesian network to copy
     */
    public BayesianNetwork(BayesianNetwork network) {
        this.network = new HashMap<>();
        for (Node node : network.getNodes()) {
            this.network.put(node.getName(), new Node(node));
        }
    }

    /**
     * Adds a node to the Bayesian network.
     *
     * @param node the node to add
     */
    public void addNode(Node node) {
        this.network.put(node.getName(), node);
    }

    /**
     * Retrieves a node by its name.
     *
     * @param name the name of the node
     * @return the node with the given name, or null if not found
     */
    public Node getNode(String name) {
        return this.network.get(name);
    }

    /**
     * Returns a collection of all nodes in the network.
     *
     * @return a collection of all nodes
     */
    public Collection<Node> getNodes() {
        return network.values();
    }

    /**
     * Removes a node by its name.
     *
     * @param name the name of the node to remove
     */
    public void removeNode(String name) {
        this.network.remove(name);
    }

    /**
     * Returns a string representation of the Bayesian network.
     *
     * @return a string representation of the Bayesian network
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bayesian Network:\n");
        for (Node node : network.values()) {
            sb.append(node.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Parses a Bayesian network from an XML file.
     *
     * @param filename the name of the XML file
     * @return the parsed Bayesian network
     */
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
                List<Double> probsLst = Arrays.asList(probsArr).stream().map(Double::parseDouble).toList();
                node.generateCPT(probsLst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return network;
    }

    /**
     * Removes irrelevant nodes from the network based on the query.
     *
     * @param query the query to process
     * @return a new Query object with irrelevant nodes removed
     */
    public Query removeIrrelevantNodes(Query query) {
        Query newQuery = new Query(query);
        List<String> hidden = query.getHiddenVariables();
        for (String h : hidden) {
            boolean isLeaf = isLeafNode(h); // leaf nodes that are not in the query are irrelevant
            boolean isIndependent = BayesBall.isIndependent(this, query.getQueryVariable(), h, query.getEvidenceVariables()) // independent hidden nodes are irrelevant
                    && BayesBall.isIndependent(this, h, query.getQueryVariable(), query.getEvidenceVariables());  // check both directions
            if (isIndependent || isLeaf) { // if node is irrelevant
                removeSubtree(h); // remove the subtree rooted at the node
                newQuery.removeHiddenVariable(h); // remove the node from the list of hidden variables
            }
        }
        return newQuery;
    }

    /**
     * Recursively removes a subtree starting from the given root node.
     *
     * @param root the root node of the subtree to remove
     */
    private void removeSubtree(String root) {
        Node curr = getNode(root);

        for (Node child : curr.getChildren()) {
            removeSubtree(child.getName());
        }
        this.network.remove(root);
    }

    /**
     * Checks if a node is a leaf node.
     *
     * @param nodeName the name of the node
     * @return true if the node is a leaf node, false otherwise
     */
    private boolean isLeafNode(String nodeName) {
        Node node = getNode(nodeName);
        if (node == null) {
            return false;
        }
        return node.getChildren().isEmpty();
    }
}

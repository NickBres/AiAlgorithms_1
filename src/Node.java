import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in a Bayesian network.
 */
public class Node {
    private String name;
    private List<String> outcomes;
    private List<Node> parents;
    private List<Node> children;
    private Map<List<String>, Double> cpt;

    /**
     * Constructs a new Node with the given name.
     *
     * @param name the name of the node
     */
    public Node(String name) {
        this.name = name;
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.cpt = new HashMap<>();
    }

    /**
     * Constructs a copy of the given Node.
     *
     * @param node the node to copy
     */
    public Node(Node node) {
        this.name = node.getName();
        this.outcomes = new ArrayList<>(node.getOutcomes());
        this.parents = new ArrayList<>(node.getParents());
        this.children = new ArrayList<>(node.getChildren());
        this.cpt = new HashMap<>(node.getCPT());
    }

    /**
     * Returns the name of the node.
     *
     * @return the name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Adds an outcome to the node.
     *
     * @param outcome the outcome to add
     */
    public void addOutcome(String outcome) {
        this.outcomes.add(outcome);
    }

    /**
     * Returns the list of outcomes for the node.
     *
     * @return the list of outcomes
     */
    public List<String> getOutcomes() {
        return outcomes;
    }

    /**
     * Adds a parent node to the node.
     *
     * @param parent the parent node to add
     */
    public void addParent(Node parent) {
        this.parents.add(parent);
    }

    /**
     * Returns the list of parent nodes.
     *
     * @return the list of parent nodes
     */
    public List<Node> getParents() {
        return parents;
    }

    /**
     * Adds a child node to the node.
     *
     * @param child the child node to add
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Returns the list of child nodes.
     *
     * @return the list of child nodes
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Generates the Conditional Probability Table (CPT) for the node using the given probabilities.
     *
     * @param probabilities the list of probabilities for the CPT
     */
    public void generateCPT(List<Double> probabilities) {
        this.cpt = createCPT(probabilities);
    }

    /**
     * Returns the Conditional Probability Table (CPT) for the node.
     *
     * @return the CPT for the node
     */
    public Map<List<String>, Double> getCPT() {
        return cpt;
    }

    /**
     * Converts the node to a factor representation.
     *
     * @return the factor representation of the node
     */
    public Factor toFactor() {
        List<String> columnNames = new ArrayList<>();
        for (Node parent : parents) {
            columnNames.add(parent.getName());
        }
        columnNames.add(this.name);

        return new Factor(columnNames, this.cpt);
    }

    /**
     * Creates the Conditional Probability Table (CPT) using the given probabilities.
     *
     * @param probabilities the list of probabilities
     * @return the created CPT
     */
    private Map<List<String>, Double> createCPT(List<Double> probabilities) {
        Map<List<String>, Double> result = new HashMap<>();

        // Calculate combination sizes once and store them
        int[] combinationSizes = calculateCombinationSizes();

        // Populate the CPT using the index formula
        for (int i = 0; i < probabilities.size(); i++) {
            List<String> keys = generateCombinationFromIndex(i, combinationSizes);
            result.put(keys, probabilities.get(i));
        }

        return result;
    }

    /**
     * Calculates the combination sizes for generating the CPT.
     *
     * @return the combination sizes
     */
    private int[] calculateCombinationSizes() {
        int numParents = this.parents.size();
        int[] combinationSizes = new int[numParents + 1];
        int size = 1;
        for (int p = numParents; p >= 0; p--) {
            Node curr = p < numParents ? this.parents.get(p) : this;
            combinationSizes[p] = size;
            size *= curr.getOutcomes().size();
        }
        return combinationSizes;
    }

    /**
     * Generates a combination of outcomes from the given index using the combination sizes.
     *
     * @param index the index for generating the combination
     * @param combinationSizes the combination sizes
     * @return the generated combination of outcomes
     */
    private List<String> generateCombinationFromIndex(int index, int[] combinationSizes) {
        List<String> combination = new ArrayList<>();
        int numParents = this.parents.size();

        for (int p = 0; p <= numParents; p++) {
            Node curr = p < numParents ? this.parents.get(p) : this;
            int outcomeIndex = (index / combinationSizes[p]) % curr.getOutcomes().size();
            combination.add(curr.getOutcomes().get(outcomeIndex));
        }

        return combination;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node: ").append(name).append("\n");
        sb.append("Outcomes: ").append(outcomes).append("\n");
        sb.append("Parents: ");
        for (Node parent : parents) {
            sb.append(parent.getName()).append(" ");
        }
        sb.append("\n");
        sb.append("Children: ");
        for (Node child : children) {
            sb.append(child.getName()).append(" ");
        }
        sb.append("\n");
        sb.append("CPT: \n");
        for (Map.Entry<List<String>, Double> entry : cpt.entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}

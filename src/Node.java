import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private String name;
    private List<String> outcomes;
    private List<Node> parents;
    private List<Node> children;
    private Map<List<String>, Double> cpt;

    public Node(String name) {
        this.name = name;
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.cpt = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addOutcome(String outcome) {
        this.outcomes.add(outcome);
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void addParent(Node parent) {
        this.parents.add(parent);
    }

    public List<Node> getParents() {
        return parents;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public List<Node> getChildren() {
        return children;
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

    public void generateCPT(List<Double> probabilities){
        this.cpt = createCPT(probabilities);
    }

    // Method to create the CPT table
    // Simplified method to create the CPT table using the index formula
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

    // Helper method to calculate combination sizes
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

    // Helper method to generate combination from index
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




    public Map<List<String>, Double> getCPT() {
        return cpt;
    }

    public Factor toFactor() {
        List<String> columnNames = new ArrayList<>();
        for (Node parent : parents) {
            columnNames.add(parent.getName());
        }
        columnNames.add(this.name);

        return new Factor(columnNames, this.cpt);
    }
}

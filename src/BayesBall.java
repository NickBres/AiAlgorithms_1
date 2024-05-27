import java.util.*;

/**
 * Implements the Bayes Ball algorithm to determine conditional independence in a Bayesian network.
 */
public class BayesBall {
    private enum Direction {
        FORWARD, BACKWARD
    }

    /**
     * Determines if two nodes, A and B, are conditionally independent given a set of evidence nodes in a Bayesian network.
     *
     * @param network the Bayesian network
     * @param A the name of the first node
     * @param B the name of the second node
     * @param evidence the set of evidence node names
     * @return true if A and B are conditionally independent given the evidence, false otherwise
     */
    public static boolean isIndependent(BayesianNetwork network, String A, String B, Set<String> evidence) {
        Set<String> visited = new HashSet<>(); // Set to keep track of visited nodes
        Queue<Pair> queue = new LinkedList<>(); // Queue to perform traversal of the network
        queue.add(new Pair(A, Direction.FORWARD));  // Start from A and traverse forward

        while (!queue.isEmpty()) {
            Pair pair = queue.poll(); // Get the next node to visit
            String curr = pair.node;
            Direction dir = pair.direction;

            if (visited.contains(curr + dir)) { // Skip if already visited
                continue;
            }
            visited.add(curr + dir); // Mark as visited

            if (curr.equals(B)) { // If B is reached, A and B are not conditionally independent
                return false;
            }

            Node currNode = network.getNode(curr); // Get the current node

            if (dir == Direction.FORWARD) { // came from above
                for (Node child : currNode.getChildren()) { // Traverse children
                    if (evidence.contains(child.getName())) { // If child is evidence, traverse parents
                        for (Node parent : child.getParents()) {
                            queue.add(new Pair(parent.getName(), Direction.BACKWARD));
                        }
                    } else { // Otherwise, traverse children
                        queue.add(new Pair(child.getName(), Direction.FORWARD));
                    }
                }
            } else if (dir == Direction.BACKWARD) { // came from below
                for (Node parent : currNode.getParents()) { // Traverse parents
                    if (!evidence.contains(parent.getName())) { // If parent is not evidence, traverse children
                        queue.add(new Pair(parent.getName(), Direction.BACKWARD));
                        for (Node sibling : parent.getChildren()) {
                            queue.add(new Pair(sibling.getName(), Direction.FORWARD));
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Helper class to represent a node and its traversal direction.
     */
    private static class Pair {
        String node;
        Direction direction;

        Pair(String node, Direction direction) {
            this.node = node;
            this.direction = direction;
        }
    }
}

import java.util.*;

public class BayesBall {
    private enum Direction {
        FORWARD, BACKWARD
    }

    public static boolean isIndependent(BayesianNetwork network, String A, String B, Set<String> evidence) {
        Set<String> visited = new HashSet<>();
        Queue<Pair> queue = new LinkedList<>();
        queue.add(new Pair(A, Direction.FORWARD));


        while (!queue.isEmpty()) {
            Pair pair = queue.poll();
            String curr = pair.node;
            Direction dir = pair.direction;

            if (visited.contains(curr + dir)) {
                continue;
            }
            visited.add(curr + dir);

            if (curr.equals(B)) {
                return false;
            }

            Node currNode = network.getNode(curr);

            if (dir == Direction.FORWARD) {
                for (Node child : currNode.getChildren()) {
                    if (evidence.contains(child.getName())) {
                        for (Node parent : child.getParents()) {
                            queue.add(new Pair(parent.getName(), Direction.BACKWARD));
                        }
                    } else {
                        queue.add(new Pair(child.getName(), Direction.FORWARD));
                    }
                }
            } else if (dir == Direction.BACKWARD) {
                for (Node parent : currNode.getParents()) {
                    if (!evidence.contains(parent.getName())) {
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

    private static class Pair {
        String node;
        Direction direction;

        Pair(String node, Direction direction) {
            this.node = node;
            this.direction = direction;
        }
    }
}
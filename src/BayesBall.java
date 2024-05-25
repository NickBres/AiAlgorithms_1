//import java.util.*;
//
//public class BayesBall {
//    public static boolean isIndependent(BayesianNetwork network, String A, String B, List<String> evidence) {
//        Set<String> visited = new HashSet<>();
//        Queue<String> queue = new LinkedList<>();
//        queue.add(A + "|forward");
//
//        while (!queue.isEmpty()) {
//            String current = queue.poll();
//            if (visited.contains(current)) continue;
//            visited.add(current);
//
//            String[] parts = current.split("\\|");
//            String node = parts[0];
//            String direction = parts[1];
//
//            if (node.equals(B)) return false;
//
//            Node currentNode = network.getNode(node);
//            if (currentNode == null) {
//                throw new IllegalArgumentException("Node " + node + " not found in the network.");
//            }
//
//            if (direction.equals("forward")) {
//                if (!evidence.contains(node)) {
//                    for (Node child : currentNode.children) {
//                        queue.add(child.name + "|forward");
//                    }
//                    for (Node parent : currentNode.parents) {
//                        queue.add(parent.name + "|backward");
//                    }
//                }
//            } else if (direction.equals("backward")) {
//                if (!evidence.contains(node)) {
//                    for (Node parent : currentNode.parents) {
//                        queue.add(parent.name + "|backward");
//                    }
//                }
//                for (Node child : currentNode.children) {
//                    if (!evidence.contains(node) && !evidence.contains(child.name)) {
//                        queue.add(child.name + "|forward");
//                    }
//                }
//            }
//        }
//        return true;
//    }
//}

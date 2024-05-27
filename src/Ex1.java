import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Ex1 {
    public static void main(String[] args) {
        try {
            // Read input file
            List<String> lines = Files.readAllLines(Paths.get("input.txt"));
            if (lines.isEmpty()) {
                System.out.println("Input file is empty.");
                return;
            }

            // The first line is the path to the network XML file
            String xmlFilePath = lines.get(0);
            BayesianNetwork network = BayesianNetwork.parseXML(xmlFilePath);
            System.out.println("Parsed Bayesian Network:");
            System.out.println(network);

            // Prepare to write output
            PrintWriter outputWriter = new PrintWriter("output.txt");

            // Process each query
            for (int i = 1; i < lines.size(); i++) {
                String query = lines.get(i);
                if (query.startsWith("P(")) {
                    // Variable elimination query
                    System.out.println("Processing variable elimination query: " + query);
                    processVariableEliminationQuery(query,network,outputWriter);
                } else if (query.contains("-")) {
                    // Bayes Ball query
                    System.out.println("Processing Bayes Ball query: " + query);
                    boolean isIndependent = processBayesBallQuery(network, query);
                    String result = isIndependent ? "yes" : "no";
                    System.out.println("Bayes Ball query result: " + result);
                    outputWriter.println(result);
                } else {
                    System.out.println("Unknown query format: " + query);
                }
            }


            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean processBayesBallQuery(BayesianNetwork network, String query) {
        // Example query: A-B|E1=e1,E2=e2,...,Ek=ek (evidence may be empty)
        String[] parts = query.split("\\|");
        String[] nodes = parts[0].split("-");
        String A = nodes[0];
        String B = nodes[1];

        Set<String> evidence = new HashSet<>();
        if (parts.length > 1 && !parts[1].isEmpty()) {
            String[] evidenceParts = parts[1].split(",");
            for (String e : evidenceParts) {
                String[] eSplit = e.split("=");
                evidence.add(eSplit[0]);
            }
        }

        return BayesBall.isIndependent(network, A, B, evidence);
    }

    private static void processVariableEliminationQuery(String query, BayesianNetwork network, PrintWriter writer) throws IOException {
        // Example query: P(Q=q|E1=e1, E2=e2, …, Ek=ek) H1-H2-…-Hj
        String[] parts = query.split("\\|");
        String[] queryPart = parts[0].substring(2).split("=");  // Remove "P(" and split by "="
        String queryVariable = queryPart[0];
        String queryValue = queryPart[1];

        // Split the evidence and hidden parts correctly
        String[] evidenceAndHidden = parts[1].split("\\) ");
        String evidenceString = evidenceAndHidden[0].substring(0, evidenceAndHidden[0].length() - 1); // Remove the trailing ')'
        String[] evidencePart = evidenceString.split(", ");

        List<SimpleEntry<String, String>> evidence = new ArrayList<>();
        for (String ev : evidencePart) {
            String[] evParts = ev.split("=");
            evidence.add(new SimpleEntry<>(evParts[0], evParts[1]));
        }

        List<String> hiddenVariables = evidenceAndHidden.length > 1 ? Arrays.asList(evidenceAndHidden[1].split("-")) : new ArrayList<>();

        System.out.println("Parsed Query: " + queryVariable + "=" + queryValue);
        System.out.println("Parsed Evidence: " + evidence);
        System.out.println("Parsed Hidden Variables: " + hiddenVariables);

        VariableElimination ve = new VariableElimination();
        FactorOperationResult result = ve.runVariableElimination(network, evidence, hiddenVariables);

        if (result == null) {
            writer.write("Could not process query: " + query);
        } else {
            // Normalize the result
            Factor finalFactor = result.getFactor();
            System.out.println(finalFactor);

            double normalizedProbability = ve.normalizeFactor(finalFactor, queryValue, evidence);

            writer.write(String.format("Query: %s\n", query));
            writer.write(String.format("Result: %.5f\n", normalizedProbability));
            writer.write(String.format("Multiplications: %d\n", result.getMultiplications()));
            writer.write(String.format("Additions: %d\n", result.getAdditions()));
            writer.write("\n");
        }
    }

}

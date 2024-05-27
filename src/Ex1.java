import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Main class for assignment 1 in Ai Algorithms course.
 * Reads input from a file, processes queries, and writes output to a file.
 */
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
            BayesianNetwork network = BayesianNetwork.parseXML(xmlFilePath); // Parse the Bayesian Network from the XML file
            System.out.println("Parsed Bayesian Network:");
            System.out.println(network);

            // Prepare to write output
            PrintWriter outputWriter = new PrintWriter("output.txt");

            // Process each query
            for (int i = 1; i < lines.size(); i++) {
                Query query = new Query(lines.get(i));
                String result;

                if (query.getType() == Query.QueryType.BAYES_BALL) {
                    result = processBayesBallQuery(network, query);
                } else {
                    result = processVariableEliminationQuery(network, query);
                }
                outputWriter.write(String.format(result + "\n"));
            }

            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a Bayes Ball query.
     *
     * @param network the Bayesian network
     * @param query the query to process
     * @return "yes" if the variables are independent, "no" otherwise
     */
    private static String processBayesBallQuery(BayesianNetwork network, Query query) {
        if (BayesBall.isIndependent(network, query.getQueryVariable(), query.getQueryValue(), query.getEvidenceVariables())) {
            return "yes";
        } else {
            return "no";
        }
    }

    /**
     * Processes a variable elimination query.
     *
     * @param network the Bayesian network
     * @param query the query to process
     * @return the result of the variable elimination query, including the normalized probability and the number of additions and multiplications
     * @throws IOException if an I/O error occurs
     */
    private static String processVariableEliminationQuery(BayesianNetwork network, Query query) throws IOException {
        BayesianNetwork networkCopy = new BayesianNetwork(network); // copy the network to avoid modifying the original
        Query cleanQuery = networkCopy.removeIrrelevantNodes(query); // remove irrelevant nodes from the query and network

        VariableElimination ve = new VariableElimination();
        FactorOperationResult result = ve.runVariableElimination(networkCopy, cleanQuery.getEvidence(), cleanQuery.getHiddenVariables()); // run variable elimination

        if (result == null) {
            return "Query could not be answered.";
        }

        Factor finalFactor = result.getFactor(); // get the final factor after variable elimination
        System.out.println("With evidence " + cleanQuery.getEvidence());
        System.out.println("With query " + cleanQuery.getQueryVariable() + " = " + cleanQuery.getQueryValue());
        System.out.println("Normalize " + finalFactor);

        // Step 1: Calculate normalization constant by summing all entries in the final factor
        double normalizationConstant = 0.0;
        for (double value : finalFactor.getTable().values()) {
            normalizationConstant += value;
        }

        int normalizationAdditions = finalFactor.getTable().size() - 1; // (n-1) additions for n values

        if (normalizationConstant == 0.0) {
            throw new ArithmeticException("Normalization constant is zero, indicating no matching evidence.");
        }

        // Step 2: Find query probability by looking up the query value in the final factor
        double queryProbability = 0.0;
        // Get the key to find in the final factor
        List<String> keyToFind = new ArrayList<>();
        for (String column : finalFactor.getColumnNames()) {
            if (column.equals(cleanQuery.getQueryVariable())) {
                keyToFind.add(cleanQuery.getQueryValue());
            } else {
                for (SimpleEntry<String, String> entry : cleanQuery.getEvidence()) {
                    if (entry.getKey().equals(column)) {
                        keyToFind.add(entry.getValue());
                        break;
                    }
                }
            }
        }
        // Find the query probability in the final factor using the key
        for (Map.Entry<List<String>, Double> entry : finalFactor.getTable().entrySet()) {
            if (entry.getKey().equals(keyToFind)) {
                queryProbability = entry.getValue();
                break;
            }
        }

        // Normalize the query probability
        double normalizedProbability = queryProbability / normalizationConstant;
        // Round the probability to 5 decimal places
        BigDecimal roundedProbability = BigDecimal.valueOf(normalizedProbability).setScale(5, RoundingMode.HALF_UP);

        int additions = result.getAdditions() + normalizationAdditions; // Add the normalization additions to the total additions of the variable elimination
        int multiplications = result.getMultiplications(); // Get the total multiplications of the variable elimination

        return roundedProbability + "," + additions + "," + multiplications;
    }
}

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.sql.SQLOutput;
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

                Query query = new Query(lines.get(i));
                String result;

                if(query.getType() == Query.QueryType.BAYES_BALL) {
                    result = processBayesBallQuery(network, query);
                } else {
                    result = processVariableEliminationQuery(network,query);
                }
                outputWriter.write(String.format(result + "\n"));
            }

            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processBayesBallQuery(BayesianNetwork network, Query query) {
        if (BayesBall.isIndependent(network, query.getQueryVariable(), query.getQueryValue(), query.getEvidenceVariables())){
            return "yes";
        } else {
            return "No";
        }
    }

    private static String processVariableEliminationQuery(BayesianNetwork network, Query query) throws IOException {
        VariableElimination ve = new VariableElimination();
        FactorOperationResult result = ve.runVariableElimination(network, query.getEvidence(), query.getHiddenVariables());

        if (result == null) {
            return "Query could not be answered.";
        }

        Factor finalFactor = result.getFactor();
        System.out.println("With evidence " + query.getEvidence());
        System.out.println("With query " + query.getQueryVariable() + " = " + query.getQueryValue());
        System.out.println("Normalize " + finalFactor);

        double normalizationConstant = 0.0;
        double queryProbability = 0.0;

        // Step 1: Find query probability
        List<String> keyToFind = new ArrayList<>();
        for (SimpleEntry<String, String> entry : query.getEvidence()) {
            keyToFind.add(entry.getValue()); // Add evidence values
        }
        keyToFind.add(query.getQueryValue()); // Add query value

        for (Map.Entry<List<String>, Double> entry : finalFactor.getTable().entrySet()) {
            if (entry.getKey().equals(keyToFind)) {
                queryProbability = entry.getValue();
                break;
            }
        }

        // Step 2: Find normalization constant by eliminating the query variable
        FactorOperationResult eliminationResult = finalFactor.eliminate(query.getQueryVariable());
        Factor eliminationFactor = eliminationResult.getFactor();
        keyToFind = new ArrayList<>();
        for (SimpleEntry<String, String> entry : query.getEvidence()) {
            keyToFind.add(entry.getValue()); // Add evidence values
        }

        for (Map.Entry<List<String>, Double> entry : eliminationFactor.getTable().entrySet()) {
            if (entry.getKey().equals(keyToFind)) {
                normalizationConstant = entry.getValue();
                break;
            }
        }

        if (normalizationConstant == 0.0) {
            throw new ArithmeticException("Normalization constant is zero, indicating no matching evidence.");
        }

        // Normalize up to 5 decimal places
        double normalizedProbability = queryProbability / normalizationConstant;
        BigDecimal roundedProbability = BigDecimal.valueOf(normalizedProbability).setScale(5, RoundingMode.HALF_UP);

        int additions = result.getAdditions() + eliminationResult.getAdditions();
        int multiplications = result.getMultiplications() + eliminationResult.getMultiplications();

        return roundedProbability + "," + additions + "," + multiplications;
    }

}

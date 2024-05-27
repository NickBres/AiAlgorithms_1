import org.junit.jupiter.api.Test;
import java.util.AbstractMap.SimpleEntry;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class VariableEliminationTest {
    @Test
    void initializeFactorsTest() {
        BayesianNetwork network = BayesianNetwork.parseXML("alarm_net.xml");
        VariableElimination ve = new VariableElimination();
        ve.initializeFactors(network);

        // Print the initialized factors
        for (Factor factor : ve.getFactors()) {
            System.out.println(factor);
        }
    }

    @Test
    void normalizeFactorTest() {
        List<String> columnNames = Arrays.asList("B", "J", "M");
        Map<List<String>, Double> cpt = new HashMap<>();
        cpt.put(Arrays.asList("T", "F", "T"), 7.0E-5);
        cpt.put(Arrays.asList("F", "F", "T"), 0.00958);
        cpt.put(Arrays.asList("T", "T", "T"), 5.9E-4);
        cpt.put(Arrays.asList("T", "F", "F"), 8.0E-5);
        cpt.put(Arrays.asList("F", "F", "F"), 0.93812);
        cpt.put(Arrays.asList("F", "T", "T"), 0.0015);
        cpt.put(Arrays.asList("T", "T", "F"), 2.6E-4);
        cpt.put(Arrays.asList("F", "T", "F"), 0.0498);

        Factor finalFactor = new Factor(columnNames, cpt);

        String queryValue = "T";
        List<SimpleEntry<String, String>> evidence = Arrays.asList(
                new SimpleEntry<>("J", "T"),
                new SimpleEntry<>("M", "T")
        );

        VariableElimination ve = new VariableElimination();
        double normalizedProbability = ve.normalizeFactor(finalFactor, queryValue, evidence);

        System.out.println("Normalized Probability: " + normalizedProbability);

        double expectedProbability = 5.9E-4 / (5.9E-4 + 0.0015);
        assertEquals(expectedProbability, normalizedProbability, 1e-5, "The normalized probability should match the expected value.");
    }
}

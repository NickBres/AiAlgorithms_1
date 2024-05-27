import org.junit.jupiter.api.Test;
import java.util.*;

public class FactorTest {
    @Test
    void joinSimpleTest() {
        // Define column names and CPT for the first factor f4(A)
        List<String> columnNames1 = Arrays.asList("A");
        Map<List<String>, Double> cpt1 = new HashMap<>();
        cpt1.put(Arrays.asList("T"), 0.90);
        cpt1.put(Arrays.asList("F"), 0.05);

        // Create the first factor
        Factor factor1 = new Factor(columnNames1, cpt1);

        // Define column names and CPT for the second factor f5(A)
        List<String> columnNames2 = Arrays.asList("A");
        Map<List<String>, Double> cpt2 = new HashMap<>();
        cpt2.put(Arrays.asList("T"), 0.70);
        cpt2.put(Arrays.asList("F"), 0.01);

        // Create the second factor
        Factor factor2 = new Factor(columnNames2, cpt2);

        // Print the original factors
        System.out.println("Factor f4(A):");
        System.out.println(factor1);

        System.out.println("Factor f5(A):");
        System.out.println(factor2);

        // Join the factors
        FactorOperationResult joinResult = factor1.join(factor2);
        Factor joinedFactor = joinResult.getFactor();

        // Print the factor after join
        System.out.println("Joined Factor f6(A):");
        System.out.println(joinedFactor);
        System.out.println("Multiplications: " + joinResult.getMultiplications());
        System.out.println("Additions: " + joinResult.getAdditions());

        // Check the correctness of the join
        Map<List<String>, Double> expectedCpt = new HashMap<>();
        expectedCpt.put(Arrays.asList("T"), 0.63);  // 0.90 * 0.70
        expectedCpt.put(Arrays.asList("F"), 0.0005);  // 0.05 * 0.01

        Factor expectedFactor = new Factor(columnNames1, expectedCpt);

        assert(joinedFactor.equals(expectedFactor)) : "The join operation did not produce the expected results.";
    }

    @Test
    void eliminateSimpleTest() {
        // Define column names and CPT for the factor
        List<String> columnNames = Arrays.asList("A", "B");
        Map<List<String>, Double> cpt = new HashMap<>();
        cpt.put(Arrays.asList("T", "T"), 0.9);
        cpt.put(Arrays.asList("T", "F"), 0.1);
        cpt.put(Arrays.asList("F", "T"), 0.4);
        cpt.put(Arrays.asList("F", "F"), 0.6);

        // Create the factor
        Factor factor = new Factor(columnNames, cpt);

        // Print the original factor
        System.out.println("Original Factor:");
        System.out.println(factor);

        // Eliminate variable "B"
        FactorOperationResult eliminateResult = factor.eliminate("B");
        Factor eliminatedFactor = eliminateResult.getFactor();

        // Print the factor after elimination
        System.out.println("Factor after eliminating 'B':");
        System.out.println(eliminatedFactor);
        System.out.println("Multiplications: " + eliminateResult.getMultiplications());
        System.out.println("Additions: " + eliminateResult.getAdditions());

        // Check the correctness of the elimination
        Map<List<String>, Double> expectedCpt = new HashMap<>();
        expectedCpt.put(Arrays.asList("T"), 1.0);  // 0.9 + 0.1
        expectedCpt.put(Arrays.asList("F"), 1.0);  // 0.4 + 0.6

        Factor expectedFactor = new Factor(Arrays.asList("A"), expectedCpt);

        assert(eliminatedFactor.equals(expectedFactor)) : "The eliminate operation did not produce the expected results.";
    }

    @Test
    void compareToTest() {
        // Define column names and CPT for the first factor
        List<String> columnNames1 = Arrays.asList("A", "B");
        Map<List<String>, Double> cpt1 = new HashMap<>();
        cpt1.put(Arrays.asList("T", "T"), 0.9);
        cpt1.put(Arrays.asList("T", "F"), 0.1);
        cpt1.put(Arrays.asList("F", "T"), 0.4);
        cpt1.put(Arrays.asList("F", "F"), 0.6);

        // Create the first factor
        Factor factor1 = new Factor(columnNames1, cpt1);

        // Define column names and CPT for the second factor
        List<String> columnNames2 = Arrays.asList("A");
        Map<List<String>, Double> cpt2 = new HashMap<>();
        cpt2.put(Arrays.asList("T"), 0.9);
        cpt2.put(Arrays.asList("F"), 0.1);

        // Create the second factor
        Factor factor2 = new Factor(columnNames2, cpt2);

        // Define column names and CPT for the third factor
        List<String> columnNames3 = Arrays.asList("A", "C");
        Map<List<String>, Double> cpt3 = new HashMap<>();
        cpt3.put(Arrays.asList("T", "T"), 0.5);
        cpt3.put(Arrays.asList("T", "F"), 0.5);
        cpt3.put(Arrays.asList("F", "T"), 0.5);
        cpt3.put(Arrays.asList("F", "F"), 0.5);

        // Create the third factor
        Factor factor3 = new Factor(columnNames3, cpt3);

        // Create a list of factors and sort them
        List<Factor> factors = Arrays.asList(factor1, factor2, factor3);
        Collections.sort(factors);

        // Print the sorted factors
        System.out.println("Sorted Factors:");
        for (Factor factor : factors) {
            System.out.println(factor);
        }

        // Check the correctness of the sorting
        assert factors.get(0).equals(factor2) : "The first factor should be the one with the smallest size.";
        assert factors.get(1).equals(factor1) : "The second factor should be the one with the second smallest size.";
        assert factors.get(2).equals(factor3) : "The third factor should be the one with the largest size.";
    }
}

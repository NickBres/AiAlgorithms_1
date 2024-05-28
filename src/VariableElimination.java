import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Implements the variable elimination algorithm for exact inference in Bayesian networks.
 */
public class VariableElimination {
    private List<Factor> factors;

    /**
     * Constructs a VariableElimination instance with an empty list of factors.
     */
    public VariableElimination() {
        this.factors = new ArrayList<>();
    }

    /**
     * Initializes the factors of the Bayesian network by creating factors for each node and instantiating them with evidence.
     *
     * @param network the Bayesian network
     * @param evidence the list of evidence variables and their values
     */
    public void initializeFactors(BayesianNetwork network, List<SimpleEntry<String, String>> evidence) {
        for (Node node : network.getNodes()) {
            Factor factor = node.toFactor();
            for (SimpleEntry<String, String> e : evidence) { // instantiate evidence variables
                if (factor.containsVariable(e.getKey())) {
                    factor = factor.instantiate(e.getKey(), e.getValue());
                }
            }
            if(!factor.canBeDiscarded()) // don't add factors that can be discarded (contain one variable only)
                factors.add(factor);
        }
        factors.sort(Comparator.naturalOrder()); // sort factors by their size
    }

    /**
     * Runs the variable elimination algorithm on the Bayesian network.
     *
     * @param network the Bayesian network
     * @param evidence the list of evidence variables and their values
     * @param hiddenVariables the list of hidden variables to eliminate
     * @return the result of the variable elimination, including the final factor and the number of multiplications and additions performed
     */
    public FactorOperationResult runVariableElimination(BayesianNetwork network, List<SimpleEntry<String, String>> evidence, List<String> hiddenVariables) {
        int multiplications = 0; // number of multiplications performed
        int additions = 0; // number of additions performed

        initializeFactors(network, evidence); // initialize factors with evidence

        for (int i = 0; i < hiddenVariables.size(); i++) { // iterate over hidden variables
            String hidden = hiddenVariables.get(i);
            int[] indexes = findFactorsWithVariable(hidden); // find two first factors that contain the hidden variable
            if (indexes[0] == -1) { // if not found, return null
                System.out.println("Didn't find factor with hidden value: " + hidden);
                return null;
            }
            while (indexes[1] != -1) { // while there are still two factors that contain the hidden variable
                Factor first = factors.get(indexes[0]);
                Factor second = factors.get(indexes[1]);
                FactorOperationResult joinRes = first.join(second); // join the two factors
                factors.remove(first); // remove the factors from the list
                factors.remove(second);
                factors.add(joinRes.getFactor()); // add the joined factor to the list
                factors.sort(Comparator.naturalOrder()); // sort the factors by their size
                multiplications += joinRes.getMultiplications(); // join uses only multiplications
                indexes = findFactorsWithVariable(hidden); // find the next two factors that contain the hidden variable
            }

            // last factor that contains the specific hidden variable
            Factor toElim = factors.get(indexes[0]);
            FactorOperationResult elimRes = toElim.eliminate(hidden); // eliminate the hidden variable from the factor
            additions += elimRes.getAdditions(); // elimination uses only additions
            factors.remove(toElim); // remove the factor from the list
            if (!elimRes.getFactor().canBeDiscarded()) { // dont add the factor if it can be discarded (contains one variable only)
                factors.add(elimRes.getFactor()); // add the factor to the list
                factors.sort(Comparator.naturalOrder()); // sort the factors by their size
            }
        }

        while (factors.size() > 1) { // join the remaining factors
            Factor first = factors.get(0);
            Factor second = factors.get(1);
            FactorOperationResult joinRes = first.join(second); // join the two factors
            factors.remove(first);  // remove the factors from the list
            factors.remove(second);
            factors.add(joinRes.getFactor()); // add the joined factor to the list
            factors.sort(Comparator.naturalOrder()); // sort the factors by their size
            multiplications += joinRes.getMultiplications(); // join uses only multiplications
        }
        Factor finalFactor = factors.get(0); // the final factor is the only one left in the list
        FactorOperationResult finalResult = new FactorOperationResult(finalFactor, multiplications, additions); // return the final factor and the number of multiplications and additions performed

        return finalResult;
    }

    /**
     * Finds the indices of the factors that contain the given variable.
     *
     * @param variable the variable to find in the factors
     * @return an array containing the indices of the first two factors that contain the variable, or -1 if not found
     */
    public int[] findFactorsWithVariable(String variable) {
        int firstIndex = -1;
        int secondIndex = -1;

        for (int i = 0; i < factors.size(); i++) { // iterate over factors
            if (factors.get(i).containsVariable(variable)) { // if the factor contains the variable
                if (firstIndex == -1) { // if the first factor is not found yet
                    firstIndex = i;
                } else {
                    secondIndex = i;
                    break;
                }
            }
        }
        return new int[] {firstIndex, secondIndex};
    }

    /**
     * Returns the list of factors currently in the variable elimination process.
     *
     * @return the list of factors
     */
    public List<Factor> getFactors() {
        return factors;
    }
}

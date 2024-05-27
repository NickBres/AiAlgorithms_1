import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class VariableElimination {
    private List<Factor> factors;

    public VariableElimination() {
        this.factors = new ArrayList<>();
    }

    public void initializeFactors(BayesianNetwork network) {
        for (Node node : network.getNodes()) {
            Factor factor = node.toFactor();
            factors.add(factor);
        }
        factors.sort(Comparator.naturalOrder());
    }

    // Placeholder for steps 2 and 3
    public FactorOperationResult runVariableElimination(BayesianNetwork network, List<SimpleEntry<String, String>> evidence, List<String> hiddenVariables) {
        int multiplications = 0;
        int additions = 0;

        initializeFactors(network);

        for(int i = 0; i< hiddenVariables.size(); i++){
            String hidden = hiddenVariables.get(i);
            int [] indexes = findFactorsWithVariable(hidden);
            if(indexes[0] == -1){
                System.out.println("Didnt found factor with hidden value:" + hidden);
                return null;
            }
            while (indexes[1] != -1){
                Factor first = factors.get(indexes[0]);
                Factor second = factors.get(indexes[1]);
                FactorOperationResult joinRes = first.join(second);
                factors.remove(first);
                factors.remove(second);
                factors.add(joinRes.getFactor());
                multiplications += joinRes.getMultiplications();
                additions += joinRes.getAdditions();
                indexes = findFactorsWithVariable(hidden);
            }

            Factor toElim = factors.get(indexes[0]);
            FactorOperationResult elimRes = toElim.eliminate(hidden);
            multiplications += elimRes.getMultiplications();
            additions += elimRes.getAdditions();
            factors.remove(toElim);
            if(!elimRes.getFactor().canBeDiscarded())
                factors.add(elimRes.getFactor());
        }

        while(factors.size() > 1){
            Factor first = factors.get(0);
            Factor second = factors.get(1);
            FactorOperationResult joinRes = first.join(second);
            factors.remove(first);
            factors.remove(second);
            factors.add(joinRes.getFactor());
            multiplications += joinRes.getMultiplications();
            additions += joinRes.getAdditions();
        }
        Factor finalFactor = factors.get(0);
        FactorOperationResult finalResult = new FactorOperationResult(finalFactor,multiplications,additions);

        return finalResult;
    }

    public int[] findFactorsWithVariable(String variable) {
        int firstIndex = -1;
        int secondIndex = -1;

        for (int i = 0; i < factors.size(); i++) {
            if (factors.get(i).containsVariable(variable)) {
                if (firstIndex == -1) {
                    firstIndex = i;
                } else {
                    secondIndex = i;
                    break;
                }
            }
        }
        return new int[] {firstIndex, secondIndex};
    }

    public double normalizeFactor(Factor finalFactor, String queryValue, List<SimpleEntry<String, String>> evidence) {
        double normalizationConstant = 0;
        double queryProbability = 0;

        System.out.println(evidence);
        return 0;
    }



    public List<Factor> getFactors() {
        return factors;
    }
}

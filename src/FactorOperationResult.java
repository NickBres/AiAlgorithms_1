public class FactorOperationResult {
    private Factor factor;
    private int multiplications;
    private int additions;

    public FactorOperationResult(Factor factor, int multiplications, int additions) {
        this.factor = factor;
        this.multiplications = multiplications;
        this.additions = additions;
    }

    public Factor getFactor() {
        return factor;
    }

    public int getMultiplications() {
        return multiplications;
    }

    public int getAdditions() {
        return additions;
    }
}

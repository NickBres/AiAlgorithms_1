/**
 * Represents the result of a factor operation in variable elimination, including the resulting factor and the counts of multiplications and additions.
 */
public class FactorOperationResult {
    private Factor factor;
    private int multiplications;
    private int additions;

    /**
     * Constructs a FactorOperationResult with the given factor, multiplications, and additions.
     *
     * @param factor the resulting factor
     * @param multiplications the number of multiplications performed
     * @param additions the number of additions performed
     */
    public FactorOperationResult(Factor factor, int multiplications, int additions) {
        this.factor = factor;
        this.multiplications = multiplications;
        this.additions = additions;
    }

    /**
     * Returns the resulting factor from the operation.
     *
     * @return the resulting factor
     */
    public Factor getFactor() {
        return factor;
    }

    /**
     * Returns the number of multiplications performed during the operation.
     *
     * @return the number of multiplications
     */
    public int getMultiplications() {
        return multiplications;
    }

    /**
     * Returns the number of additions performed during the operation.
     *
     * @return the number of additions
     */
    public int getAdditions() {
        return additions;
    }
}

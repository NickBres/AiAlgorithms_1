import java.util.*;
import java.math.BigDecimal;

/**
 * Represents a factor in a Bayesian network, used in variable elimination.
 */
public class Factor implements Comparable<Factor> {
    private Map<List<String>, Double> table; // Table of the factor with keys as assignments and values as probabilities
    private List<String> columnNames; // Column names of the factor

    /**
     * Constructs a new Factor with the given column names and table.
     *
     * @param columnNames the column names of the factor
     * @param table the table of the factor
     */
    public Factor(List<String> columnNames, Map<List<String>, Double> table) {
        this.columnNames = new ArrayList<>(columnNames);
        this.table = new HashMap<>(table);
    }

    /**
     * Returns the column names of the factor.
     *
     * @return the column names
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Returns the table of the factor.
     *
     * @return the table
     */
    public Map<List<String>, Double> getTable() {
        return table;
    }

    /**
     * Eliminates a variable from the factor by summing out the variable.
     *
     * @param variable the variable to eliminate
     * @return the result of the elimination, including the new factor and the number of additions
     */
    public FactorOperationResult eliminate(String variable) {
        int index = columnNames.indexOf(variable);
        if (index == -1) {
            throw new IllegalArgumentException("Variable not found in factor");
        }

        Map<List<String>, Double> newTable = new HashMap<>();
        int[] additions = {0}; // Counter for additions using an array to pass by reference

        for (Map.Entry<List<String>, Double> entry : table.entrySet()) { // Iterate through table entries
            List<String> key = new ArrayList<>(entry.getKey());
            key.remove(index); // Remove the variable from the key
            newTable.merge(key, entry.getValue(), (v1, v2) -> { // Merge the entry into the new table
                additions[0]++; // Increment the counter
                return v1 + v2; // Sum the values
            });
        }

        List<String> newColumnNames = new ArrayList<>(columnNames); // Create new column names without the variable
        newColumnNames.remove(variable);

        Factor newFactor = new Factor(newColumnNames, newTable); // Create the new factor
        return new FactorOperationResult(newFactor, 0, additions[0]); // Return the new factor and the number of additions
    }

    /**
     * Instantiates the factor with a specific value for a variable, reducing its size.
     *
     * @param variable the variable to instantiate
     * @param value the value to instantiate the variable with
     * @return the new instantiated factor
     */
    public Factor instantiate(String variable, String value) {
        int index = columnNames.indexOf(variable);
        if (index == -1) {
            return this; // Variable not found, return the factor as-is
        }

        Map<List<String>, Double> newTable = new HashMap<>();
        for (Map.Entry<List<String>, Double> entry : table.entrySet()) { // Iterate through table entries
            if (entry.getKey().get(index).equals(value)) { // Check if the variable has the specified value
                List<String> newKey = new ArrayList<>(entry.getKey()); // Create a new key without the variable
                newKey.remove(index);
                newTable.put(newKey, entry.getValue()); // Add the entry to the new table
            }
        }

        List<String> newColumnNames = new ArrayList<>(columnNames); // Create new column names without the variable
        newColumnNames.remove(variable);

        return new Factor(newColumnNames, newTable);
    }

    /**
     * Joins this factor with another factor, combining their tables.
     *
     * @param other the factor to join with
     * @return the result of the join, including the new factor and the number of multiplications
     */
    public FactorOperationResult join(Factor other) {
        List<String> newColumnNames = new ArrayList<>(this.columnNames); // Create new column names with the column names of this factor
        for (String col : other.getColumnNames()) { // Add column names from the other factor that are not already in the new column names
            if (!newColumnNames.contains(col)) {
                newColumnNames.add(col);
            }
        }

        Map<List<String>, Double> newTable = new HashMap<>(); // Create a new table for the new factor
        int multiplications = 0;

        // Create index maps for quick lookup
        Map<String, Integer> thisColIndex = new HashMap<>();
        for (int i = 0; i < this.columnNames.size(); i++) { // Create a map of column names to indices for this factor
            thisColIndex.put(this.columnNames.get(i), i);
        }

        Map<String, Integer> otherColIndex = new HashMap<>(); // Create a map of column names to indices for the other factor
        for (int i = 0; i < other.getColumnNames().size(); i++) {
            otherColIndex.put(other.getColumnNames().get(i), i);
        }

        // Iterate through tables and combine entries
        for (Map.Entry<List<String>, Double> entry1 : this.table.entrySet()) {
            for (Map.Entry<List<String>, Double> entry2 : other.getTable().entrySet()) {
                if (isJoinable(entry1.getKey(), entry2.getKey(), thisColIndex, otherColIndex)) { // Check if the entries are joinable
                    List<String> newKey = new ArrayList<>(entry1.getKey()); // Create a new key with the keys of this factor
                    for (int i = 0; i < other.getColumnNames().size(); i++) { // Add keys from the other factor that are not already in the new key
                        String col = other.getColumnNames().get(i);
                        if (!this.columnNames.contains(col)) {
                            newKey.add(entry2.getKey().get(i));
                        }
                    }
                    double newValue = entry1.getValue() * entry2.getValue(); // Multiply the values
                    multiplications++; // Increment the counter
                    newTable.merge(newKey, newValue, Double::sum); // Merge the entry into the new table
                }
            }
        }

        Factor newFactor = new Factor(newColumnNames, newTable); // Create the new factor
        return new FactorOperationResult(newFactor, multiplications, 0); // Return the new factor and the number of multiplications
    }

    /**
     * Checks if two entries are joinable based on their keys and column indices.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @param colIndex1 the column indices of the first factor
     * @param colIndex2 the column indices of the second factor
     * @return true if the entries are joinable, false otherwise
     */
    private boolean isJoinable(List<String> key1, List<String> key2, Map<String, Integer> colIndex1, Map<String, Integer> colIndex2) {
        for (String col : colIndex1.keySet()) { // Iterate through column names of the first factor
            if (colIndex2.containsKey(col)) { // Check if the column name is in the second factor
                if (!key1.get(colIndex1.get(col)).equals(key2.get(colIndex2.get(col)))) { // Check if the keys are different
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the factor contains a specific variable.
     *
     * @param variable the variable to check
     * @return true if the factor contains the variable, false otherwise
     */
    public boolean containsVariable(String variable) {
        return columnNames.contains(variable);
    }

    /**
     * Checks if the factor can be discarded, i.e., if it only has one entry.
     *
     * @return true if the factor can be discarded, false otherwise
     */
    public boolean canBeDiscarded() {
        return table.size() == 1;
    }

    /**
     * Compares this factor with another factor based on their column names and sizes.
     *
     * @param other the factor to compare with
     * @return a negative integer, zero, or a positive integer as this factor is less than, equal to, or greater than the specified factor
     */
    @Override
    public int compareTo(Factor other) {
        int sizeComparison = Integer.compare(this.columnNames.size(), other.columnNames.size());
        if (sizeComparison != 0) { // if sizes are different, return the comparison
            return sizeComparison;
        }
        for (int i = 0; i < this.columnNames.size(); i++) { // if sizes are the same, compare by ascii
            int cmp = this.columnNames.get(i).compareTo(other.columnNames.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    /**
     * Checks if this factor is equal to another object.
     *
     * @param o the object to compare with
     * @return true if this factor is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factor factor = (Factor) o;
        return Objects.equals(table, factor.table) &&
                Objects.equals(columnNames, factor.columnNames);
    }

    /**
     * Returns the hash code value for this factor.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(table, columnNames);
    }

    /**
     * Returns a string representation of this factor.
     *
     * @return a string representation of the factor
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Factor:\n");
        sb.append(columnNames).append("\n");
        for (Map.Entry<List<String>, Double> entry : table.entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}

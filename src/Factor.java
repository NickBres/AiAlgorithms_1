import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Factor implements Comparable<Factor> {
    private Map<List<String>, Double> table;
    private List<String> columnNames;

    public Factor(List<String> columnNames, Map<List<String>, Double> table) {
        this.columnNames = new ArrayList<>(columnNames);
        this.table = new HashMap<>(table);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public Map<List<String>, Double> getTable() {
        return table;
    }

    public FactorOperationResult eliminate(String variable) {
        int index = columnNames.indexOf(variable);
        if (index == -1) {
            throw new IllegalArgumentException("Variable not found in factor");
        }

        Map<List<String>, Double> newTable = new HashMap<>();
        int[] additions = {0};

        for (Map.Entry<List<String>, Double> entry : table.entrySet()) {
            List<String> key = new ArrayList<>(entry.getKey());
            key.remove(index);
            newTable.merge(key, entry.getValue(), (v1, v2) -> {
                additions[0]++;
                return v1 + v2;
            });
        }

        List<String> newColumnNames = new ArrayList<>(columnNames);
        newColumnNames.remove(variable);

        Factor newFactor = new Factor(newColumnNames, newTable);
        return new FactorOperationResult(newFactor, 0, additions[0]);
    }

    public FactorOperationResult join(Factor other) {
        List<String> newColumnNames = new ArrayList<>(this.columnNames);
        for (String col : other.getColumnNames()) {
            if (!newColumnNames.contains(col)) {
                newColumnNames.add(col);
            }
        }

        Map<List<String>, Double> newTable = new HashMap<>();
        int multiplications = 0;

        for (Map.Entry<List<String>, Double> entry1 : this.table.entrySet()) {
            for (Map.Entry<List<String>, Double> entry2 : other.getTable().entrySet()) {
                if (isJoinable(entry1.getKey(), entry2.getKey(), this.columnNames, other.getColumnNames())) {
                    List<String> newKey = new ArrayList<>(entry1.getKey());
                    for (int i = 0; i < other.getColumnNames().size(); i++) {
                        String col = other.getColumnNames().get(i);
                        if (!this.columnNames.contains(col)) {
                            newKey.add(entry2.getKey().get(i));
                        }
                    }
                    double newValue = entry1.getValue() * entry2.getValue();
                    multiplications++;
                    newTable.put(newKey, newValue);
                }
            }
        }

        Factor newFactor = new Factor(newColumnNames, newTable);
        return new FactorOperationResult(newFactor, multiplications, 0);
    }

    private boolean isJoinable(List<String> key1, List<String> key2, List<String> columns1, List<String> columns2) {
        for (int i = 0; i < columns1.size(); i++) {
            if (columns2.contains(columns1.get(i))) {
                int index = columns2.indexOf(columns1.get(i));
                if (!key1.get(i).equals(key2.get(index))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean containsVariable(String variable) {
        return columnNames.contains(variable);
    }

    public boolean canBeDiscarded() {
        return table.size() == 1;
    }

    @Override
    public int compareTo(Factor other) {
        int sizeComparison = Integer.compare(this.columnNames.size(), other.columnNames.size());
        if (sizeComparison != 0) {
            return sizeComparison;
        }
        for (int i = 0; i < this.columnNames.size(); i++) {
            int cmp = this.columnNames.get(i).compareTo(other.columnNames.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factor factor = (Factor) o;
        return Objects.equals(table, factor.table) &&
                Objects.equals(columnNames, factor.columnNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, columnNames);
    }

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

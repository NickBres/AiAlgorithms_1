import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Query {

    public enum QueryType {
        BAYES_BALL,
        ELIMINATION
    }

    private final QueryType type;
    private final String queryVariable;
    private final String queryValue;
    private final List<SimpleEntry<String, String>> evidence;
    private final List<String> hiddenVariables;

    public Query(String queryString) {
        Query parsedQuery = parseQuery(queryString);
        this.type = parsedQuery.type;
        this.queryVariable = parsedQuery.queryVariable;
        this.queryValue = parsedQuery.queryValue;
        this.evidence = parsedQuery.evidence;
        this.hiddenVariables = parsedQuery.hiddenVariables;
    }

    private Query(QueryType type, String queryVariable, String queryValue, List<SimpleEntry<String, String>> evidence, List<String> hiddenVariables) {
        this.type = type;
        this.queryVariable = queryVariable;
        this.queryValue = queryValue;
        this.evidence = evidence;
        this.hiddenVariables = hiddenVariables;
    }

    private Query parseQuery(String queryString) {
        if (queryString.startsWith("P(")) {
            return parseVariableEliminationQuery(queryString);
        } else {
            return parseBayesBallQuery(queryString);
        }
    }

    private Query parseVariableEliminationQuery(String query) {
        // Example query: P(Q=q|E1=e1, E2=e2, …, Ek=ek) H1-H2-…-Hj
        String[] parts = query.split("\\|");
        String[] queryPart = parts[0].substring(2).split("=");  // Remove "P(" and split by "="
        String queryVariable = queryPart[0];
        String queryValue = queryPart[1];

        // Split the evidence and hidden parts correctly
        String[] evidenceAndHidden = parts[1].split("\\) ");
        String evidenceString = evidenceAndHidden[0].substring(0, evidenceAndHidden[0].length()); // Remove the trailing ')'

        List<SimpleEntry<String, String>> evidence = new ArrayList<>();
        String[] evidencePart = evidenceString.split(",");
        for (String ev : evidencePart) {
            String[] evParts = ev.split("=");
            evidence.add(new SimpleEntry<>(evParts[0].trim(), evParts[1].trim()));
        }

        List<String> hiddenVariables = evidenceAndHidden.length > 1 ? Arrays.asList(evidenceAndHidden[1].split("-")) : new ArrayList<>();

        return new Query(QueryType.ELIMINATION, queryVariable, queryValue, evidence, hiddenVariables);
    }

    private Query parseBayesBallQuery(String query) {
        // Example query: A-B|E1=e1,E2=e2,…,Ek=ek (evidence may be empty)
        String[] parts = query.split("\\|");
        String[] nodes = parts[0].split("-");
        String nodeA = nodes[0];
        String nodeB = nodes[1];

        String queryVariable = nodeA;
        String queryValue = nodeB;

        List<SimpleEntry<String, String>> evidence = new ArrayList<>();
        if (parts.length > 1 && !parts[1].isEmpty()) {
            String[] evidencePart = parts[1].split(",");
            for (String ev : evidencePart) {
                String[] evParts = ev.split("=");
                evidence.add(new SimpleEntry<>(evParts[0].trim(), evParts[1].trim()));
            }
        }

        List<String> hiddenVariables = new ArrayList<>(); // Bayes Ball query does not have hidden variables

        return new Query(QueryType.BAYES_BALL, queryVariable, queryValue, evidence, hiddenVariables);
    }

    public QueryType getType() {
        return type;
    }

    public String getQueryVariable() {
        return queryVariable;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public List<SimpleEntry<String, String>> getEvidence() {
        return evidence;
    }

    public Set<String> getEvidenceVariables(){
        Set<String> evidenceVariables = new HashSet<>();
        for(SimpleEntry<String, String> entry : evidence){
            evidenceVariables.add(entry.getKey());
        }
        return evidenceVariables;
    }

    public List<String> getHiddenVariables() {
        return hiddenVariables;
    }

    @Override
    public String toString() {
        return "Query{" +
                "type=" + type +
                ", queryVariable='" + queryVariable + '\'' +
                ", queryValue='" + queryValue + '\'' +
                ", evidence=" + evidence +
                ", hiddenVariables=" + hiddenVariables +
                '}';
    }
}

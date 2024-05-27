import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Represents a query to be processed in a Bayesian network.
 */
public class Query {

    /**
     * Enumeration for the type of query.
     */
    public enum QueryType {
        BAYES_BALL,
        ELIMINATION
    }

    private QueryType type;
    private String queryVariable;
    private String queryValue;
    private List<SimpleEntry<String, String>> evidence;
    private List<String> hiddenVariables;

    /**
     * Constructs a Query from a query string.
     *
     * @param queryString the query string
     */
    public Query(String queryString) {
        Query parsedQuery = parseQuery(queryString);
        this.type = parsedQuery.type;
        this.queryVariable = parsedQuery.queryVariable;
        this.queryValue = parsedQuery.queryValue;
        this.evidence = parsedQuery.evidence;
        this.hiddenVariables = new ArrayList<>(parsedQuery.hiddenVariables); // Ensure mutable list
    }
    /**
     * Constructs a Query
     *
     * @param type the query type
     * @param queryVariable the query variable
     * @param queryValue the query value
     * @param evidence the list of evidence
     * @param hiddenVariables the list of hidden variables
     */

    private Query(QueryType type, String queryVariable, String queryValue, List<SimpleEntry<String, String>> evidence, List<String> hiddenVariables) {
        this.type = type;
        this.queryVariable = queryVariable;
        this.queryValue = queryValue;
        this.evidence = evidence;
        this.hiddenVariables = hiddenVariables;
    }

    /**
     * Constructs a copy of the given Query.
     *
     * @param query the query to copy
     */
    public Query(Query query){
        this.type = query.type;
        this.queryVariable = query.queryVariable;
        this.queryValue = query.queryValue;
        this.evidence = new ArrayList<>(query.evidence);
        this.hiddenVariables = new ArrayList<>(query.hiddenVariables);
    }

    /**
     * Parses the query string to determine the type of query and its components.
     *
     * @param queryString the query string
     * @return the parsed Query object
     */
    private Query parseQuery(String queryString) {
        if (queryString.startsWith("P(")) { // Variable elimination query
            return parseVariableEliminationQuery(queryString);
        } else { // Bayes Ball query
            return parseBayesBallQuery(queryString);
        }
    }

    /**
     * Parses a variable elimination query string.
     *
     * @param query the query string
     * @return the parsed Query object
     */
    private Query parseVariableEliminationQuery(String query) {
        // Example query: P(Q=q|E1=e1, E2=e2, …, Ek=ek) H1-H2-…-Hj
        String[] parts = query.split("\\|");
        String[] queryPart = parts[0].substring(2).split("=");  // Remove "P(" and split by "="
        String queryVariable = queryPart[0];
        String queryValue = queryPart[1];

        // Split the evidence and hidden parts correctly
        String[] evidenceAndHidden = parts[1].split("\\) ");
        String evidenceString = evidenceAndHidden[0]; // Remove the trailing ')'

        List<SimpleEntry<String, String>> evidence = new ArrayList<>();
        String[] evidencePart = evidenceString.split(",");
        for (String ev : evidencePart) {
            String[] evParts = ev.split("=");
            evidence.add(new SimpleEntry<>(evParts[0].trim(), evParts[1].trim()));
        }

        List<String> hiddenVariables = evidenceAndHidden.length > 1
                ? new ArrayList<>(Arrays.asList(evidenceAndHidden[1].split("-")))  // Ensure mutable list
                : new ArrayList<>();

        return new Query(QueryType.ELIMINATION, queryVariable, queryValue, evidence, hiddenVariables);
    }

    /**
     * Parses a Bayes Ball query string.
     *
     * @param query the query string
     * @return the parsed Query object
     */
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

    /**
     * Returns the type of the query.
     *
     * @return the query type
     */
    public QueryType getType() {
        return type;
    }

    /**
     * Returns the query variable.
     *
     * @return the query variable
     */
    public String getQueryVariable() {
        return queryVariable;
    }

    /**
     * Returns the query value.
     *
     * @return the query value
     */
    public String getQueryValue() {
        return queryValue;
    }

    /**
     * Removes a hidden variable from the list of hidden variables.
     *
     * @param hiddenVariable the hidden variable to remove
     */
    public void removeHiddenVariable(String hiddenVariable){
        hiddenVariables.remove(hiddenVariable);
    }

    /**
     * Returns the list of evidence for the query.
     *
     * @return the list of evidence
     */
    public List<SimpleEntry<String, String>> getEvidence() {
        return evidence;
    }

    /**
     * Returns a set of evidence variables (keys only) for the query.
     *
     * @return the set of evidence variables
     */
    public Set<String> getEvidenceVariables(){
        Set<String> evidenceVariables = new HashSet<>();
        for(SimpleEntry<String, String> entry : evidence){
            evidenceVariables.add(entry.getKey());
        }
        return evidenceVariables;
    }

    /**
     * Returns the list of hidden variables for the query.
     *
     * @return the list of hidden variables
     */
    public List<String> getHiddenVariables() {
        return hiddenVariables;
    }

    /**
     * Returns a string representation of the query.
     *
     * @return a string representation of the query
     */
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

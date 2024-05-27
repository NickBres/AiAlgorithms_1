import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest {

    @Test
    void testParseVariableEliminationQuery() {
        String queryString = "P(Q=q|E1=e1, E2=e2) H1-H2-H3";

        Query parsedQuery = new Query(queryString);

        assertEquals(Query.QueryType.ELIMINATION, parsedQuery.getType());
        assertEquals("Q", parsedQuery.getQueryVariable());
        assertEquals("q", parsedQuery.getQueryValue());

        List<SimpleEntry<String, String>> expectedEvidence = List.of(
                new SimpleEntry<>("E1", "e1"),
                new SimpleEntry<>("E2", "e2")
        );
        assertEquals(expectedEvidence, parsedQuery.getEvidence());

        List<String> expectedHiddenVariables = List.of("H1", "H2", "H3");
        assertEquals(expectedHiddenVariables, parsedQuery.getHiddenVariables());
    }

    @Test
    void testParseBayesBallQuery() {
        String queryString = "A-B|E1=e1,E2=e2";

        Query parsedQuery = new Query(queryString);

        assertEquals(Query.QueryType.BAYES_BALL, parsedQuery.getType());
        assertEquals("A", parsedQuery.getQueryVariable());
        assertEquals("B", parsedQuery.getQueryValue());

        List<SimpleEntry<String, String>> expectedEvidence = List.of(
                new SimpleEntry<>("E1", "e1"),
                new SimpleEntry<>("E2", "e2")
        );
        assertEquals(expectedEvidence, parsedQuery.getEvidence());

        List<String> expectedHiddenVariables = Collections.emptyList();
        assertEquals(expectedHiddenVariables, parsedQuery.getHiddenVariables());
    }
}

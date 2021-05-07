package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;


class QueryTest {

    Query query = new Query(50, 0, Arrays.asList("123","456","789"));

    @Test
    void should_return_page_size() {
        assertEquals(50,query.getPageSize());
    }

    @Test
    void should_return_from_value() {
        assertEquals(0,query.getFromValue());
    }

    @Test
    void should_return_ccd_numbers() {
        assertEquals(Arrays.asList("123", "456", "789"),query.getCcdNumbers());
    }

    @Test
    void should_build_query() {
        String numbers = query.getCcdNumbers()
            .stream().map(s -> "\"" + s + "\"")
            .collect(Collectors.joining(", "));

        String expected = "{"
                          + "\"size\": " + query.getPageSize() + ","
                          + "\"from\": " + query.getFromValue() + ","
                          + "\"sort\": " + "[\n"
                          + "    { \"created_date\" : {\"order\" : \"desc\"}}\n"
                          + "    ]" + ","
                          + "\"query\": " + "{\n"
                          + "  \"terms\": " + "{\n"
                          + "      \"reference\": " + "[" + numbers + "]"
                          + "    }"
                          + "  }"
                          + '}';

        assertEquals(expected,query.toString());
    }
}

package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients;

import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode
public class Query {

    private final int pageSize;
    private final int fromValue;
    private final List<String> ccdNumbers;

    public Query(int pageSize, int fromValue, List<String> ccdNumbers) {
        this.pageSize = pageSize;
        this.fromValue = fromValue;
        this.ccdNumbers = ccdNumbers;
    }

    @Override
    public String toString() {
        String caseIds = ccdNumbers.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
        return "{"
               + "\"size\": " + pageSize + ","
               + "\"from\": " + fromValue + ","
               + "\"sort\": " + "[\n"
               + "    { \"created_date\" : {\"order\" : \"desc\"}}\n"
               + "    ]" + ","
               + "\"query\": " + "{\n"
               + "  \"terms\": " + "{\n"
               + "      \"reference\": " + "[" + caseIds + "]"
               + "    }"
               + "  }"
               + '}';
    }
}

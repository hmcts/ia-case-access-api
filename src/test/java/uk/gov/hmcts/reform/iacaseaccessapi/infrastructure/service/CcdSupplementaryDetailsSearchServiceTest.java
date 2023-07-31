package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.CoreCaseDataApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.SearchResult;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.SystemTokenGenerator;

@ExtendWith(MockitoExtension.class)
class CcdSupplementaryDetailsSearchServiceTest {

    @Mock
    private CoreCaseDataApi coreCaseDataApi;
    @Mock
    private SystemTokenGenerator systemTokenGenerator;
    @Mock
    private AuthTokenGenerator s2sAuthTokenGenerator;
    @Mock
    private CaseDetails caseDetails;
    @Mock
    SearchResult searchResult;

    private CcdSupplementaryDetailsSearchService ccdSupplementaryDetailsSearchService;
    private static final int MAX_RECORDS = 100;
    private static final int THREAD_POOL_SIZE = 10;
    private final String authorisation = "Bearer token";
    private final String serviceToken = "Bearer serviceToken";

    private final String caseType = "Asylum";
    private final long caseId = 1234;
    private final List<String> ccdCaseNumberList = Arrays.asList("11111111111111", "22222222222222", "99999999999999");

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    private Map<String, Object> data = new HashMap<>();
    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        data = new HashMap<>();
        data.put("appellantFamilyName", "Johnson");

        TermsQueryBuilder termQueryBuilder = QueryBuilders.termsQuery("reference", ccdCaseNumberList);
        searchSourceBuilder.size(MAX_RECORDS);
        searchSourceBuilder.from(0);
        searchSourceBuilder.sort("created_date", SortOrder.DESC);
        searchSourceBuilder.query(termQueryBuilder);

        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        when(systemTokenGenerator.generate()).thenReturn("token");
        when(s2sAuthTokenGenerator.generate()).thenReturn(serviceToken);

        when(coreCaseDataApi.searchCases(authorisation, serviceToken, caseType,
                                         searchSourceBuilder.toString()
        )).thenReturn(searchResult);

        ccdSupplementaryDetailsSearchService = new CcdSupplementaryDetailsSearchService(
            systemTokenGenerator,
            coreCaseDataApi,
            s2sAuthTokenGenerator,
            executorService,
            MAX_RECORDS
        );
    }

    @Test
    void should_return_supplementary_details() {
        when(searchResult.getCases()).thenReturn(Arrays.asList(caseDetails));
        when(caseDetails.getId()).thenReturn(caseId);
        when(caseDetails.getCaseData()).thenReturn(data);

        List<SupplementaryInfo> supplementaryInfoList =
            ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList);

        assertEquals(1, supplementaryInfoList.size());
        assertEquals("1234", supplementaryInfoList.get(0).getCcdCaseNumber());
        assertEquals("Johnson", supplementaryInfoList.get(0).getSupplementaryDetails().getSurname());

        verify(systemTokenGenerator).generate();
        verify(coreCaseDataApi).searchCases(authorisation, serviceToken, caseType, searchSourceBuilder.toString());
    }

    @Test
    void should_handle_error() {

        List<SupplementaryInfo> supplementaryInfoList =
            ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList);

        assertEquals(0, supplementaryInfoList.size());
        verify(systemTokenGenerator).generate();
        verify(coreCaseDataApi).searchCases(authorisation, serviceToken, caseType, searchSourceBuilder.toString());
    }
}

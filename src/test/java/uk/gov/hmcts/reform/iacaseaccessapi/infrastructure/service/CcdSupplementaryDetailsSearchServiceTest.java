package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.CoreCaseDataApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.Query;
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
    private CaseDetails caseDetails;

    @Mock
    SearchResult searchResult;

    private CcdSupplementaryDetailsSearchService ccdSupplementaryDetailsSearchService;
    private final String authorisation = "Bearer token";
    private final String serviceToken = "Bearer serviceToken";

    private final String caseType = "Asylum";
    private final long caseId = 1234;
    private final List<String> ccdCaseNumberList = Arrays.asList("11111111111111","22222222222222","99999999999999");

    private final Query query = new Query(100,0,Arrays.asList("11111111111111","22222222222222","99999999999999"));
    private Map<String,Object> data = new HashMap<>();

    @BeforeEach
    public void setUp() {
        data = new HashMap<>();
        data.put("appellantFamilyName","Johnson");

        when(systemTokenGenerator.generate()).thenReturn("token");
        when(coreCaseDataApi.searchCases(authorisation, serviceToken, caseType,
                                         query.toString())).thenReturn(searchResult);

        ccdSupplementaryDetailsSearchService = new CcdSupplementaryDetailsSearchService(systemTokenGenerator, coreCaseDataApi);
    }

    @Test
    void should_return_supplementary_details() {
        when(searchResult.getCases()).thenReturn(Arrays.asList(caseDetails));
        when(caseDetails.getId()).thenReturn(caseId);
        when(caseDetails.getCaseData()).thenReturn(data);

        List<SupplementaryInfo> supplementaryInfoList =
            ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList, serviceToken);

        assertEquals(1, supplementaryInfoList.size());
        assertEquals("1234", supplementaryInfoList.get(0).getCcdCaseNumber());
        assertEquals("Johnson", supplementaryInfoList.get(0).getSupplementaryDetails().getSurname());

        verify(systemTokenGenerator).generate();
        verify(coreCaseDataApi).searchCases(authorisation, serviceToken, caseType, query.toString());

    }

    @Test
    void should_handle_error() {

        List<SupplementaryInfo> supplementaryInfoList =
            ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList, serviceToken);

        assertEquals(0, supplementaryInfoList.size());
        verify(systemTokenGenerator).generate();
        verify(coreCaseDataApi).searchCases(authorisation, serviceToken, caseType, query.toString());

    }
}

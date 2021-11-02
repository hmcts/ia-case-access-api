package uk.gov.hmcts.reform.iacaseaccessapi.provider;

import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import java.io.IOException;
import java.util.*;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.services.SupplementaryDetailsService;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.CoreCaseDataApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.SearchResult;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.SupplementaryDetailsController;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.SystemTokenGenerator;


@ExtendWith(SpringExtension.class)
@Provider("ia_caseAccessApi")
@PactBroker(scheme = "${PACT_BROKER_SCHEME:http}", host = "${PACT_BROKER_URL:localhost}", port = "${PACT_BROKER_PORT:80}")
@Import(SupplementaryDetailsProviderTestConfiguration.class)
public class SupplementaryDetailsProviderTest {

    @Autowired
    SupplementaryDetailsService supplementaryDetailsService;

    @Autowired
    CoreCaseDataApi coreCaseDataApiMock;

    @Autowired
    SystemTokenGenerator systemTokenGeneratorMock;

    @Autowired
    AuthTokenGenerator s2sAuthTokenGeneratorMock;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        System.getProperties().setProperty("pact.verifier.publishResults", "true");
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(new SupplementaryDetailsController(supplementaryDetailsService));
        context.setTarget(testTarget);
    }

    @State({"Supplementary details are requested for a single, existing case"})
    public void toGetSupplementaryDetailsForSingleExistingCase() throws IOException, JSONException {

        String searchString = "{\"from\":0,\"size\":100,\"query\":{\"terms\":{"
                              + "\"reference\":[\"6666661111111111\"],"
                              + "\"boost\":1.0}},\"sort\":[{\"created_date\":{\"order\":\"desc\"}}]}";

        CaseDetails caseDetails = new CaseDetails(6666661111111111L, "some state", new HashMap<>());
        SearchResult searchResult = new SearchResult(1, Arrays.asList(caseDetails));
        setupMockInteractions(searchString, searchResult);
    }

    @State({"Supplementary details are requested for multiple, existing cases"})
    public void toGetSupplementaryDetailsForMultipleExistingCases() throws IOException, JSONException {

        String searchString = "{\"from\":0,\"size\":100,\"query\":{\"terms\":{"
                              + "\"reference\":[\"6666661111111111\",\"6666662222222222\"],"
                              + "\"boost\":1.0}},\"sort\":[{\"created_date\":{\"order\":\"desc\"}}]}";

        CaseDetails caseDetails1 = new CaseDetails(6666661111111111L, "some state", new HashMap<>());
        CaseDetails caseDetails2 = new CaseDetails(6666662222222222L, "some state", new HashMap<>());
        SearchResult searchResult = new SearchResult(2, Arrays.asList(caseDetails1, caseDetails2));
        setupMockInteractions(searchString, searchResult);
    }

    @State({"Supplementary details are requested for multiple cases, including a missing case"})
    public void toGetSupplementaryDetailsForPartialContent() throws IOException, JSONException {

        String searchString = "{\"from\":0,\"size\":100,\"query\":{\"terms\":{"
                              + "\"reference\":[\"6666661111111111\",\"6666660000000000\"],"
                              + "\"boost\":1.0}},\"sort\":[{\"created_date\":{\"order\":\"desc\"}}]}";

        CaseDetails caseDetails = new CaseDetails(6666661111111111L, "some state", new HashMap<>());
        SearchResult searchResult = new SearchResult(1, Arrays.asList(caseDetails));
        setupMockInteractions(searchString, searchResult);
    }

    @State({"Supplementary details are requested for an unknown case"})
    public void toGetSupplementaryDetailsForUnknownCase() throws IOException, JSONException {

        String searchString = "{\"from\":0,\"size\":100,\"query\":{\"terms\":{"
                              + "\"reference\":[\"6666660000000000\"],"
                              + "\"boost\":1.0}},\"sort\":[{\"created_date\":{\"order\":\"desc\"}}]}";

        SearchResult searchResult = new SearchResult(0, new ArrayList<>());
        setupMockInteractions(searchString, searchResult);
    }

    private void setupMockInteractions(String searchString, SearchResult searchResult) {

        when(systemTokenGeneratorMock.generate()).thenReturn("token");
        when(s2sAuthTokenGeneratorMock.generate()).thenReturn("Bearer serviceToken");
        when(coreCaseDataApiMock.searchCases(
            "Bearer token",
            "Bearer serviceToken",
            "Asylum",
            searchString
        )).thenReturn(searchResult);
    }
}

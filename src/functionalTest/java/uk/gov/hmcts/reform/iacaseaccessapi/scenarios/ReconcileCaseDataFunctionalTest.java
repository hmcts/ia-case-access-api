package uk.gov.hmcts.reform.iacaseaccessapi.scenarios;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.restassured.http.Header;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.FunctionalTest;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.data.CaseDataFixture;

@RunWith(SpringIntegrationSerenityRunner.class)
@SpringBootTest
@ActiveProfiles("functional")
public class ReconcileCaseDataFunctionalTest extends FunctionalTest {

    private String jurisdiction = "IA";
    private String caseType = "Asylum";

    private String legalRepUserToken;
    private String legalRepUserId;

    private CaseDataFixture caseDataFixture;

    private List<String> ccdCaseNumbers;
    private String cases;

    @Before
    public void setUp() {
        createCase();
    }

    @Test
    public void should_return_correct_status_codes_for_supplementary_details_request() {

        // 400 status code for bad request
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add(String.valueOf(caseDataFixture.getCaseId()));
        ccdCaseNumbers.add("1234567890123457");
        ccdCaseNumbers.add("1234567890123458");
        cases = caseListAsString(ccdCaseNumbers, "-");

        Response response = supplementaryDetails(
            cases,
            caseDataFixture.getS2sToken()
        );
        assertThat(response.getStatusCode()).isEqualTo(400);

        // 401 status code for missing s2s token
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add(String.valueOf(caseDataFixture.getCaseId()));
        cases = caseListAsString(ccdCaseNumbers, ",");

        response = supplementaryDetails(
            cases,
            null
        );
        assertThat(response.getStatusCode()).isEqualTo(401);

        // 401 status code for invalid s2s token
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add(String.valueOf(caseDataFixture.getCaseId()));
        cases = caseListAsString(ccdCaseNumbers, ",");

        response = supplementaryDetails(
            cases,
            "eyJhbGciOiJIUzUxMi.invalid"
        );
        assertThat(response.getStatusCode()).isEqualTo(401);

        // 404 status code when supplementary details not found for given case numbers
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add("1234567890123457");
        ccdCaseNumbers.add("1234567890123458");
        cases = caseListAsString(ccdCaseNumbers, ",");

        response = supplementaryDetails(
            cases,
            caseDataFixture.getS2sToken()
        );
        assertThat(response.getStatusCode()).isEqualTo(404);

        // 200 status code when surname retrieved for given case number
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add(String.valueOf(caseDataFixture.getCaseId()));
        cases = caseListAsString(ccdCaseNumbers, ",");

        response = supplementaryDetails(
            cases,
            caseDataFixture.getS2sToken()
        );
        assertThat(response.getStatusCode()).isEqualTo(200);

        // 206 status code for partial match found for given case numbers
        ccdCaseNumbers.clear();
        ccdCaseNumbers.add(String.valueOf(caseDataFixture.getCaseId()));
        ccdCaseNumbers.add("1234567890123457");
        ccdCaseNumbers.add("1234567890123458");
        cases = caseListAsString(ccdCaseNumbers, ",");

        response = supplementaryDetails(
            cases,
            caseDataFixture.getS2sToken()
        );
        assertThat(response.getStatusCode()).isEqualTo(206);
    }

    private void createCase() {

        ccdCaseNumbers = new ArrayList<String>();
        cases = "";

        legalRepUserToken = idamAuthProvider.getLegalRepToken();
        legalRepUserId = idamApi.userInfo(legalRepUserToken).getUid();

        caseDataFixture = new CaseDataFixture(
            ccdApi,
            objectMapper,
            s2sAuthTokenGenerator,
            minimalAppealStarted,
            idamAuthProvider,
            mapValueExpander
        );

        caseDataFixture.startAppeal();
        caseDataFixture.submitAppeal();
    }

    private Response supplementaryDetails(String cases, String serviceToken) {

        return given(requestSpecification)
            .when()
            .header(new Header("Authorization", caseDataFixture.getLegalRepToken()))
            .header(new Header("ServiceAuthorization", serviceToken))
            .contentType("application/json")
            .body("{\"ccd_case_numbers\":["
                  + cases
                  + "]}")
            .post("/supplementary-details")
            .then()
            .extract().response();
    }

    private String caseListAsString(List<String> ccdCaseNumbers, String delimiter) {

        String casesToString = "";
        String cases = "";

        if (!ccdCaseNumbers.isEmpty()) {
            for (String ccdCaseNumber : ccdCaseNumbers) {
                casesToString += "\"" + ccdCaseNumber + "\"" + delimiter;
            }
            cases = casesToString.substring(0, casesToString.length() - 1);
        }

        return cases;
    }

    private void assertThatCaseIsInState(long caseId, String state) {

        await().pollInterval(2, SECONDS).atMost(60, SECONDS).until(() ->
                                                                       ccdApi.get(
                                                                           legalRepUserToken,
                                                                           caseDataFixture.getS2sToken(),
                                                                           legalRepUserId,
                                                                           jurisdiction,
                                                                           caseType,
                                                                           String.valueOf(caseId)
                                                                       ).getState().equals(state)
        );
    }
}

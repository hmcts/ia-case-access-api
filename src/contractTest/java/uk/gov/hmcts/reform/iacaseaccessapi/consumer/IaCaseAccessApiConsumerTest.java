package uk.gov.hmcts.reform.iacaseaccessapi.consumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsResponse;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.IaCaseAccessApi;

@ExtendWith(SpringExtension.class)
@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactFolder("pacts")
@PactTestFor(providerName = "ia_caseAccessApi", port = "8089")
@ContextConfiguration(classes = {IaCaseAccessApiConsumerApplication.class})
@TestPropertySource(
    properties = {"iaCaseAccessApi.baseUrl=localhost:8089"}
)
public class IaCaseAccessApiConsumerTest {

    static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";
    static final String SERVICE_AUTH_TOKEN = "someServiceAuthToken";

    @Autowired
    IaCaseAccessApi iaCaseAccessApi;

    @Pact(provider = "ia_caseAccessApi", consumer = "liberata")
    public RequestResponsePact generatePactSingleSupplementaryDetails(PactDslWithProvider builder) throws JSONException {
        Map<String, String> requestHeaders = ImmutableMap.<String, String>builder()
            .put(SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN)
            .put("Content-Type", "application/json")
            .build();
        return builder
            .given("Supplementary details are requested for a single, existing case")
            .uponReceiving(
                "Provider receives a POST /supplementary-details request from Liberata API for a single, existing case")
            .path("/supplementary-details")
            .method(HttpMethod.POST.toString())
            .headers(requestHeaders)
            .body(createSingleSupplementaryDetailsRequest())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(singleCaseSupplementaryDetailsResponse())
            .toPact();
    }

    @Pact(provider = "ia_caseAccessApi", consumer = "liberata")
    public RequestResponsePact generatePactMultipleSupplementaryDetails(PactDslWithProvider builder) throws JSONException {
        Map<String, String> requestHeaders = ImmutableMap.<String, String>builder()
            .put(SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN)
            .put("Content-Type", "application/json")
            .build();
        return builder
            .given("Supplementary details are requested for multiple, existing cases")
            .uponReceiving(
                "Provider receives a POST /supplementary-details request from Liberata API for multiple, existing cases")
            .path("/supplementary-details")
            .method(HttpMethod.POST.toString())
            .headers(requestHeaders)
            .body(createMultipleSupplementaryDetailsRequest())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(multipleCaseSupplementaryDetailsResponse())
            .toPact();
    }

    @Pact(provider = "ia_caseAccessApi", consumer = "liberata")
    public RequestResponsePact generatePactMissingSupplementaryDetails(PactDslWithProvider builder) throws JSONException {
        Map<String, String> requestHeaders = ImmutableMap.<String, String>builder()
            .put(SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN)
            .put("Content-Type", "application/json")
            .build();
        return builder
            .given("Supplementary details are requested for multiple cases, including a missing case")
            .uponReceiving(
                "Provider receives a POST /supplementary-details request from Liberata API for multiple cases, including a missing case")
            .path("/supplementary-details")
            .method(HttpMethod.POST.toString())
            .headers(requestHeaders)
            .body(createMissingSupplementaryDetailsRequest())
            .willRespondWith()
            .status(HttpStatus.PARTIAL_CONTENT.value())
            .body(missingCaseSupplementaryDetailsResponse())
            .toPact();
    }

    @Pact(provider = "ia_caseAccessApi", consumer = "liberata")
    public RequestResponsePact generatePactNotFoundSupplementaryDetails(PactDslWithProvider builder) throws JSONException {
        Map<String, String> requestHeaders = ImmutableMap.<String, String>builder()
            .put(SERVICE_AUTHORIZATION_HEADER, SERVICE_AUTH_TOKEN)
            .put("Content-Type", "application/json")
            .build();
        return builder
            .given("Supplementary details are requested for an unknown case")
            .uponReceiving(
                "Provider receives a POST /supplementary-details request from Liberata API for an unknown case")
            .path("/supplementary-details")
            .method(HttpMethod.POST.toString())
            .headers(requestHeaders)
            .body(createNotFoundSupplementaryDetailsRequest())
            .willRespondWith()
            //.status(HttpStatus.NOT_FOUND.value())
            .status(HttpStatus.OK.value())
            .body(notFoundSupplementaryDetailsResponse())
            .toPact();
    }

    private DslPart createSingleSupplementaryDetailsRequest() {
        return new PactDslJsonBody()
            .array("ccd_case_numbers")
            .stringValue("6666661111111111")
            .closeArray();
    }

    private DslPart createMultipleSupplementaryDetailsRequest() {
        return new PactDslJsonBody()
            .array("ccd_case_numbers")
            .stringValue("6666661111111111")
            .stringValue("6666662222222222")
            .closeArray();
    }

    private DslPart createMissingSupplementaryDetailsRequest() {
        return new PactDslJsonBody()
            .array("ccd_case_numbers")
            .stringValue("6666661111111111")
            .stringValue("6666660000000000")
            .closeArray();
    }

    private DslPart createNotFoundSupplementaryDetailsRequest() {
        return new PactDslJsonBody()
            .array("ccd_case_numbers")
            .stringValue("6666660000000000")
            .closeArray();
    }

    private DslPart singleCaseSupplementaryDetailsResponse() {
        return new PactDslJsonBody()
            .array("supplementary_info")
            .object()
            .stringType("ccd_case_number", "6666661111111111")
            .object("supplementary_details")
            .stringType("surname", "Smith")
            .closeObject()
            .closeObject()
            .closeArray();
    }

    private DslPart multipleCaseSupplementaryDetailsResponse() {
        return new PactDslJsonBody()
            .array("supplementary_info")
            .object()
            .stringType("ccd_case_number", "6666661111111111")
            .object("supplementary_details")
            .stringType("surname", "Smith")
            .closeObject()
            .closeObject()
            .object()
            .stringType("ccd_case_number", "6666662222222222")
            .object("supplementary_details")
            .stringType("surname", "Gomez")
            .closeObject()
            .closeObject()
            .closeArray();
    }

    private DslPart missingCaseSupplementaryDetailsResponse() {
        return new PactDslJsonBody()
            .array("supplementary_info")
            .object()
            .stringType("ccd_case_number", "6666661111111111")
            .object("supplementary_details")
            .stringType("surname", "Smith")
            .closeObject()
            .closeObject()
            .closeArray()
            .object("missing_supplementary_info")
            .array("ccd_case_numbers")
            .stringValue("6666660000000000")
            .closeArray()
            .closeObject();
    }

    private DslPart notFoundSupplementaryDetailsResponse() {
        return new PactDslJsonBody()
            .array("supplementary_info")
            .closeArray()
            .object("missing_supplementary_info")
            .array("ccd_case_numbers")
            .stringValue("6666660000000000")
            .closeArray()
            .closeObject();
    }

    @Test
    @PactTestFor(pactMethod = "generatePactSingleSupplementaryDetails")
    public void verifySingleSupplementaryDetailsPactResponse() {

        SupplementaryDetailsResponse response = iaCaseAccessApi.supplementaryDetails(
            SERVICE_AUTH_TOKEN,
            createSingleSupplementaryDetailsRequest().toString()
        );

        Assertions.assertEquals(1, response.getSupplementaryInfo().size());
        Assertions.assertEquals("6666661111111111", response.getSupplementaryInfo().get(0).getCcdCaseNumber());
        Assertions.assertEquals("Smith", response.getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname());
    }

    @Test
    @PactTestFor(pactMethod = "generatePactMultipleSupplementaryDetails")
    public void verifyMultipleSupplementaryDetailsPactResponse() {

        SupplementaryDetailsResponse response = iaCaseAccessApi.supplementaryDetails(
            SERVICE_AUTH_TOKEN,
            createMultipleSupplementaryDetailsRequest().toString()
        );

        Assertions.assertEquals(2, response.getSupplementaryInfo().size());
        Assertions.assertEquals("6666661111111111", response.getSupplementaryInfo().get(0).getCcdCaseNumber());
        Assertions.assertEquals("Smith", response.getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname());
        Assertions.assertEquals("6666662222222222", response.getSupplementaryInfo().get(1).getCcdCaseNumber());
        Assertions.assertEquals("Gomez", response.getSupplementaryInfo().get(1).getSupplementaryDetails().getSurname());
    }

    @Test
    @PactTestFor(pactMethod = "generatePactMissingSupplementaryDetails")
    public void verifyMissingSupplementaryDetailsPactResponse() {

        SupplementaryDetailsResponse response = iaCaseAccessApi.supplementaryDetails(
            SERVICE_AUTH_TOKEN,
            createMissingSupplementaryDetailsRequest().toString()
        );

        Assertions.assertEquals(1, response.getSupplementaryInfo().size());
        Assertions.assertEquals("6666661111111111", response.getSupplementaryInfo().get(0).getCcdCaseNumber());
        Assertions.assertEquals("Smith", response.getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname());
        Assertions.assertEquals(1, response.getMissingSupplementaryInfo().getCcdCaseNumbers().size());
        Assertions.assertEquals("6666660000000000", response.getMissingSupplementaryInfo().getCcdCaseNumbers().get(0));
    }

    @Test
    @PactTestFor(pactMethod = "generatePactNotFoundSupplementaryDetails")
    public void verifyNotFoundSupplementaryDetailsPactResponse() {

        SupplementaryDetailsResponse response = iaCaseAccessApi.supplementaryDetails(
            SERVICE_AUTH_TOKEN,
            createNotFoundSupplementaryDetailsRequest().toString()
        );

        Assertions.assertEquals(0, response.getSupplementaryInfo().size());
        Assertions.assertEquals(1, response.getMissingSupplementaryInfo().getCcdCaseNumbers().size());
        Assertions.assertEquals("6666660000000000", response.getMissingSupplementaryInfo().getCcdCaseNumbers().get(0));
    }
}


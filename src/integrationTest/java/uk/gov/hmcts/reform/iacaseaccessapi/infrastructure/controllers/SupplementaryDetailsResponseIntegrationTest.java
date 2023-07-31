package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.IntegrationConstants;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.SpringBootIntegrationTest;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.WithCoreCaseApiStub;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.WithIdamStub;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.WithServiceAuthStub;

public class SupplementaryDetailsResponseIntegrationTest
    extends SpringBootIntegrationTest
    implements WithServiceAuthStub, WithCoreCaseApiStub, WithIdamStub {

    @org.springframework.beans.factory.annotation.Value("classpath:ccd-search-result-response.json")
    private Resource resourceFile;

    @org.springframework.beans.factory.annotation.Value("classpath:ccd-search-result-empty-surname-response.json")
    private Resource emptySurnameResourceFile;

    @org.springframework.beans.factory.annotation.Value("classpath:ccd-search-result-missing-surname-response.json")
    private Resource missingSurnameResourceFile;

    @org.springframework.beans.factory.annotation.Value("classpath:ccd-search-result-empty-response.json")
    private Resource emptyResponseResourceFile;

    @org.springframework.beans.factory.annotation.Value("classpath:ccd-search-result-error-response.json")
    private Resource errorResponseResourceFile;

    private final String fullResponseRequest = "{\"ccd_case_numbers\":["
                                               + "\"1619513189387098\"]}";

    private final String partialResponseRequest = "{\"ccd_case_numbers\":["
                                                  + "\"1619513189387098\","
                                                  + "\"22222222222222\","
                                                  + "\"99999999999999\"]}";

    private final String emptyResponseRequest = "{\"ccd_case_numbers\":["
                                                + "\"1619513189387090\","
                                                + "\"22222222222222\","
                                                + "\"99999999999999\"]}";

    private final String emptySurnameResponseRequest = "{\"ccd_case_numbers\":["
                                                         + "\"1619513189387099\"]}";

    private final String missingSurnameResponseRequest = "{\"ccd_case_numbers\":["
                                                         + "\"1619513189387099\"]}";

    private final String fullResponse = "{\"supplementary_info\":["
                                        + "{\"ccd_case_number\":\"1619513189387098\","
                                        + "\"supplementary_details\":{\"surname\":\"Johnson\"}}]}";

    private final String partialResponse = "{\"supplementary_info\":["
                                           + "{\"ccd_case_number\":\"1619513189387098\","
                                           + "\"supplementary_details\":{\"surname\":\"Johnson\"}}],"
                                           + "\"missing_supplementary_info\":"
                                           + "{\"ccd_case_numbers\":[\"22222222222222\",\"99999999999999\"]}}";

    private final String emptyResponse =
        "{\"supplementary_info\":[],"
            + "\"missing_supplementary_info\":"
            + "{\"ccd_case_numbers\":[\"1619513189387090\",\"22222222222222\",\"99999999999999\"]}}";

    private final String emptySurnameResponse = "{\"supplementary_info\":["
                                                + "{\"ccd_case_number\":\"1619513189387099\","
                                                + "\"supplementary_details\":{\"surname\":\"\"}}]}";

    private final String missingSurnameResponse = "{\"supplementary_info\":["
                                                  + "{\"ccd_case_number\":\"1619513189387099\","
                                                  + "\"supplementary_details\":{\"surname\":\"null\"}}]}";

    void setup(WireMockServer server) {
        addUserInfoStub(server);
        addIdamTokenStub(server);
        addServiceAuthStub(server);

    }

    @Test
    public void should_return_200_status_code_for_full_response() throws Exception {
        setup(server);
        addSearchStub(server, resourceFile);

        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(fullResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(fullResponse, postResponse.getResponse().getContentAsString());
    }

    @Test
    public void should_return_206_status_code_for_partial_response() throws Exception {

        setup(server);
        addSearchStub(server, resourceFile);

        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(partialResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().isPartialContent())
            .andReturn();

        assertEquals(partialResponse, postResponse.getResponse().getContentAsString());
    }

    @Test
    public void should_return_404_status_code_for_not_found_response() throws Exception {

        setup(server);
        addSearchStub(server, emptyResponseResourceFile);
        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(emptyResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().isNotFound())
            .andReturn();

        assertEquals(emptyResponse, postResponse.getResponse().getContentAsString());
    }

    @Test
    public void should_return_400_status_code_for_error_response() throws Exception {

        setup(server);
        addSearchStub(server, errorResponseResourceFile);
        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(emptyResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().is4xxClientError())
            .andReturn();

    }

    @Test
    public void should_return_200_status_code_for_empty_surname() throws Exception {
        setup(server);
        addSearchStub(server, emptySurnameResourceFile);

        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(emptySurnameResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(emptySurnameResponse, postResponse.getResponse().getContentAsString());

    }

    @Test
    public void should_return_200_status_code_for_missing_surname() throws Exception {
        setup(server);
        addSearchStub(server, missingSurnameResourceFile);

        MvcResult postResponse = mockMvc
            .perform(
                post("/supplementary-details")
                    .content(missingSurnameResponseRequest)
                    .contentType("application/json")
                    .header("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .header("ServiceAuthorization", equalTo("Bearer " + IntegrationConstants.SERVICE_TOKEN))
            )
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(missingSurnameResponse, postResponse.getResponse().getContentAsString());

    }

}

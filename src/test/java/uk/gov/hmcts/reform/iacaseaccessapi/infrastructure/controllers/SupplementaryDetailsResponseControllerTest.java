package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.idam.UserInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsRequest;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsResponse;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service.CcdSupplementaryDetailsSearchService;

@ExtendWith(MockitoExtension.class)
class SupplementaryDetailsResponseControllerTest {

    @Mock
    private CcdSupplementaryDetailsSearchService ccdSupplementaryDetailsSearchService;

    private final String serviceAuthorization = "authorisation";
    private SupplementaryDetailsController supplementaryDetailsController;
    private ArrayList<String> ccdCaseNumberList = new ArrayList<String>();
    private UserInfo userInfo = new UserInfo(
        "ia-system-user@fake.hmcts.net",
        "id",
        newArrayList("caseworker-ia", "caseworker-ia-system-user"),
        "System User",
        "System",
        "User"
    );

    @BeforeEach
    public void setUp() {

        supplementaryDetailsController
            = new SupplementaryDetailsController(ccdSupplementaryDetailsSearchService);

        ccdCaseNumberList.add("11111111111111");
        ccdCaseNumberList.add("22222222222222");
        ccdCaseNumberList.add("99999999999999");
    }

    @Test
    void should_return_supplementary_details_complete_on_request() {

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<SupplementaryInfo>();

        SupplementaryDetails supplementaryDetails = new SupplementaryDetails("Johnson");

        ccdCaseNumberList.forEach((ccdCaseNumber) -> {
            SupplementaryInfo supplementaryInformation = new SupplementaryInfo(ccdCaseNumber, supplementaryDetails);
            supplementaryInfo.add(supplementaryInformation);
        });

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList, serviceAuthorization)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest, serviceAuthorization);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ccdCaseNumberList.size(), response.getBody().getSupplementaryInfo().size());
        assertEquals("11111111111111", response.getBody().getSupplementaryInfo().get(0).getCcdCaseNumber());
        assertEquals("Johnson", response.getBody().getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname());
        assertTrue(response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().isEmpty());
    }

    @Test
    void should_return_supplementary_details_partial_on_request() {

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<SupplementaryInfo>();

        SupplementaryDetails supplementaryDetails = new SupplementaryDetails("Johnson");

        SupplementaryInfo supplementaryInformation = new SupplementaryInfo("11111111111111", supplementaryDetails);
        supplementaryInfo.add(supplementaryInformation);

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList, serviceAuthorization)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest, serviceAuthorization);

        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(1, response.getBody().getSupplementaryInfo().size());
        assertEquals("11111111111111", response.getBody().getSupplementaryInfo().get(0).getCcdCaseNumber());
        assertEquals("Johnson", response.getBody().getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname());
        assertEquals(2, response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().size());
        assertTrue(response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().contains("22222222222222"));
        assertTrue(response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().contains("99999999999999"));
    }

    @Test
    void should_return_no_supplementary_details_on_request() {

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<>();

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList, serviceAuthorization)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest, serviceAuthorization);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, response.getBody().getSupplementaryInfo().size());
    }

    @Test
    void should_return_forbidden_on_request() {

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList,
                                                                          serviceAuthorization)).thenReturn(null);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest, serviceAuthorization);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}

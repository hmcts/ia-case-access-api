package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsRequest;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsResponse;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service.CcdSupplementaryDetailsSearchService;

@ExtendWith(MockitoExtension.class)
class SupplementaryDetailsResponseControllerTest {

    @Mock
    private CcdSupplementaryDetailsSearchService ccdSupplementaryDetailsSearchService;
    private SupplementaryDetailsController supplementaryDetailsController;
    private final ArrayList<String> ccdCaseNumberList = new ArrayList<>();

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

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<>();

        SupplementaryDetails supplementaryDetails = new SupplementaryDetails("Johnson", "EU/12345/2024");

        ccdCaseNumberList.forEach((ccdCaseNumber) -> {
            SupplementaryInfo supplementaryInformation = new SupplementaryInfo(ccdCaseNumber, supplementaryDetails);
            supplementaryInfo.add(supplementaryInformation);
        });

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ccdCaseNumberList.size(), response.getBody().getSupplementaryInfo().size());
        assertEquals("11111111111111", response.getBody().getSupplementaryInfo().get(0).getCcdCaseNumber());
        assertEquals(
            "Johnson",
            response.getBody().getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname()
        );
        assertNull(response.getBody().getMissingSupplementaryInfo());
    }

    @Test
    void should_return_supplementary_details_complete_on_request_duplicated_ccc_ids() {

        ArrayList<String> ccdCaseNumberList = new ArrayList<>();
        ccdCaseNumberList.add("11111111111111");
        ccdCaseNumberList.add("11111111111111");
        ccdCaseNumberList.add("11111111111111");
        ccdCaseNumberList.add("22222222222222");
        ccdCaseNumberList.add("99999999999999");

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<>();

        SupplementaryDetails supplementaryDetails = new SupplementaryDetails("Johnson", "EU/12345/2024");

        ccdCaseNumberList.stream().distinct().forEach((ccdCaseNumber) -> {
            SupplementaryInfo supplementaryInformation = new SupplementaryInfo(ccdCaseNumber, supplementaryDetails);
            supplementaryInfo.add(supplementaryInformation);
        });

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(
            ccdCaseNumberList
                .stream()
                .distinct()
                .collect(Collectors.toList()))
        ).thenReturn(supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // after distinct it is 3 element list
        assertEquals(3, response.getBody().getSupplementaryInfo().size());
        assertEquals(
            "11111111111111",
            response.getBody().getSupplementaryInfo().get(0).getCcdCaseNumber()
        );
        assertEquals(
            "Johnson",
            response.getBody().getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname()
        );
        assertNull(response.getBody().getMissingSupplementaryInfo());
    }

    @Test
    void should_return_supplementary_details_partial_on_request() {

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<>();

        SupplementaryDetails supplementaryDetails = new SupplementaryDetails("Johnson", "EU/12345/2024");

        SupplementaryInfo supplementaryInformation = new SupplementaryInfo("11111111111111", supplementaryDetails);
        supplementaryInfo.add(supplementaryInformation);

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
        assertEquals(1, response.getBody().getSupplementaryInfo().size());
        assertEquals("11111111111111", response.getBody().getSupplementaryInfo().get(0).getCcdCaseNumber());
        assertEquals(
            "Johnson",
            response.getBody().getSupplementaryInfo().get(0).getSupplementaryDetails().getSurname()
        );
        assertEquals(2, response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().size());
        assertTrue(response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().contains("22222222222222"));
        assertTrue(response.getBody().getMissingSupplementaryInfo().getCcdCaseNumbers().contains("99999999999999"));
    }

    @Test
    void should_return_no_supplementary_details_on_request() {

        List<SupplementaryInfo> supplementaryInfo = new ArrayList<>();

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList)).thenReturn(
            supplementaryInfo);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, response.getBody().getSupplementaryInfo().size());
    }

    @Test
    void should_return_forbidden_on_request() {

        when(ccdSupplementaryDetailsSearchService.getSupplementaryDetails(ccdCaseNumberList)).thenReturn(null);

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(ccdCaseNumberList);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void should_return_ok_on_empty_list_on_request() {

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(emptyList());

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getSupplementaryInfo().size());
    }

    @Test
    void should_return_bad_request_when_supplementary_details_request_is_null() {

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void should_return_bad_request_when_ccd_case_number_list_is_null() {

        SupplementaryDetailsRequest supplementaryDetailsRequest = new SupplementaryDetailsRequest(null);

        ResponseEntity<SupplementaryDetailsResponse> response
            = supplementaryDetailsController.post(supplementaryDetailsRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

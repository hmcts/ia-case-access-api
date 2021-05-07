package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.services.SupplementaryDetailsService;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.CoreCaseDataApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.Query;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.oauth2.IdentityManagerResponseException;

@Service
@Slf4j
public class CcdSupplementaryDetailsSearchService implements SupplementaryDetailsService {
    private static final String CASE_TYPE_ID = "Asylum";
    private static final String APPELLANT_FAMILY_NAME = "appellantFamilyName";
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CoreCaseDataApi coreCaseDataApi;
    private final SystemTokenGenerator systemTokenGenerator;


    public CcdSupplementaryDetailsSearchService(SystemTokenGenerator systemTokenGenerator,
                                                CoreCaseDataApi coreCaseDataApi) {
        this.systemTokenGenerator = systemTokenGenerator;
        this.coreCaseDataApi = coreCaseDataApi;
    }

    @Override
    public List<SupplementaryInfo> getSupplementaryDetails(List<String>  ccdCaseNumberList,String authorisation) {

        logger.info("CcdSupplementaryDetailsSearchService : getSupplementaryDetails fetch data at {} for the case ids  {} ",
                    LocalDateTime.now(), ccdCaseNumberList);

        String userToken;

        try {
            userToken = "Bearer " + systemTokenGenerator.generate();
            log.info("CcdSupplementaryDetailsSearchService : getSupplementaryDetails System user token has been generated.");

        } catch (Exception e) {
            throw new IdentityManagerResponseException(e.getMessage(), e);
        }

        var query = new Query(100, 0, ccdCaseNumberList);

        return search(userToken, authorisation, query);
    }

    private List<SupplementaryInfo> search(String userAuthorisation, String serviceAuthToken, Query query) {

        var searchResult = coreCaseDataApi.searchCases(
            userAuthorisation,
            serviceAuthToken,
            CASE_TYPE_ID,
            query.toString()
        );

        if (searchResult.getCases() == null) {
            return Collections.emptyList();
        }
        return  searchResult.getCases()
            .stream()
            .filter(p -> p.getCaseData() != null && p.getId() != null)
            .map(this::extractSupplementaryInfo)
            .collect(Collectors.toList());
    }

    private SupplementaryInfo extractSupplementaryInfo(CaseDetails caseDetails) {
        Map<String, Object> tempData = new HashMap<>(caseDetails.getCaseData());
        return new SupplementaryInfo(String.valueOf(caseDetails.getId()),
                                     new SupplementaryDetails(tempData.get(APPELLANT_FAMILY_NAME).toString()));
    }
}

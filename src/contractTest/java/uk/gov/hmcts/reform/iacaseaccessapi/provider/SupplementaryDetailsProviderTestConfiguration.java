package uk.gov.hmcts.reform.iacaseaccessapi.provider;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.CoreCaseDataApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.SystemTokenGenerator;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.service.CcdSupplementaryDetailsSearchService;

@TestConfiguration
public class SupplementaryDetailsProviderTestConfiguration {

    @Bean
    @Primary
    public CcdSupplementaryDetailsSearchService ccdSupplementaryDetailsSearchService() {
        return new CcdSupplementaryDetailsSearchService(systemTokenGenerator, coreCaseDataApi, s2sAuthTokenGenerator);
    }

    @MockBean
    CoreCaseDataApi coreCaseDataApi;

    @MockBean
    SystemTokenGenerator systemTokenGenerator;

    @MockBean
    AuthTokenGenerator s2sAuthTokenGenerator;
}

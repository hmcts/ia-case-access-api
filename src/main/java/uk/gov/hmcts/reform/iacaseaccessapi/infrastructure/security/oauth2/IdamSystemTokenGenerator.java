package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.oauth2;

import feign.FeignException;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.services.IdamService;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.idam.Token;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.SystemTokenGenerator;

@Component
public class IdamSystemTokenGenerator implements SystemTokenGenerator {

    private final IdamService idamService;

    public IdamSystemTokenGenerator(IdamService idamService) {
        this.idamService = idamService;
    }

    @Override
    public String generate() {
        try {
            Token tokenResponse = idamService.getServiceUserToken();
            return tokenResponse.getAccessToken();
        } catch (FeignException ex) {
            throw new IdentityManagerResponseException("Could not get system user token from IDAM", ex);
        }
    }
}

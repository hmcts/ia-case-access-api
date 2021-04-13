package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.services;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidIdentityProvider implements IdentityProvider {

    public String identity() {
        return UUID.randomUUID().toString();
    }
}

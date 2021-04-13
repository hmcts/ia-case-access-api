package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security;

import java.util.Set;

public interface AuthorizedRolesProvider {

    Set<String> getRoles();

}

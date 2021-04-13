package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security;

public interface SystemUserProvider {

    String getSystemUserId(String userToken);
}

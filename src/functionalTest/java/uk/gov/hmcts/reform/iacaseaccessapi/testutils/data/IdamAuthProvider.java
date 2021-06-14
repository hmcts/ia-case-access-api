package uk.gov.hmcts.reform.iacaseaccessapi.testutils.data;

import feign.FeignException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.idam.Token;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.idam.UserInfo;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.security.oauth2.IdentityManagerResponseException;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.ExtendedIdamApi;

public class IdamAuthProvider {

    private final ExtendedIdamApi idamApi;
    private final String idamRedirectUrl;
    private final String userScope;
    private final String idamClientId;
    private final String idamClientSecret;

    public IdamAuthProvider(
        ExtendedIdamApi idamApi,
        String idamRedirectUrl,
        String userScope,
        String idamClientId,
        String idamClientSecret
    ) {
        this.idamApi = idamApi;
        this.idamRedirectUrl = idamRedirectUrl;
        this.userScope = userScope;
        this.idamClientId = idamClientId;
        this.idamClientSecret = idamClientSecret;
    }

    public String getLegalRepToken() {
        return getUserToken(
            System.getenv("TEST_LAW_FIRM_SHARE_CASE_A_USERNAME"),
            System.getenv("TEST_LAW_FIRM_SHARE_CASE_A_PASSWORD")
        );
    }

    public String getUserId(String token) {

        try {
            UserInfo userInfo = idamApi.userInfo(token);
            return userInfo.getUid();

        } catch (FeignException ex) {
            throw new IdentityManagerResponseException("Could not get system user token from IDAM", ex);
        }
    }

    private String getUserToken(String userName, String password) {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("redirect_uri", idamRedirectUrl);
        map.add("client_id", idamClientId);
        map.add("client_secret", idamClientSecret);
        map.add("username", userName);
        map.add("password", password);
        map.add("scope", userScope);

        try {
            Token tokenResponse = idamApi.token(map);
            return "Bearer " + tokenResponse.getAccessToken();

        } catch (FeignException ex) {
            throw new IdentityManagerResponseException("Could not get system user token from IDAM", ex);
        }
    }
}

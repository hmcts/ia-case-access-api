package uk.gov.hmcts.reform.iacaseaccessapi.testutils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public interface WithIdamStub {

    default void addIdamTokenStub(WireMockServer server) {
        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.POST, urlEqualTo("/idam/o/token"))
                    .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded;charset=UTF-8"))
                    //.withRequestBody(
                    //    equalTo("grant_type=password"
                    //            + "&redirect_uri=http%3A%2F%2Flocalhost%3A3002%2Foauth2%2Fcallback"
                    //            + "&client_id=ia"
                    //            + "&client_secret=something"
                    //            + "&username=ia-system-user%40fake.hmcts.net"
                    //            + "&password=London05"
                    //            + "&scope=openid+profile+roles"
                    //    )
                    //)
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"access_token\": \"" + IntegrationConstants.USER_TOKEN + "\"}")
                    .build()
            )
        );
    }

    default void addUserInfoStub(WireMockServer server) {
        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.GET, urlEqualTo("/idam/o/userinfo"))
                    .withHeader("Authorization", equalTo("Bearer " + IntegrationConstants.USER_TOKEN))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"uid\": \"" + IntegrationConstants.USER_ID + "\"}")
                    .build()
            )
        );
    }
}

package uk.gov.hmcts.reform.iacaseaccessapi.testutils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;


public interface WithCoreCaseApiStub {

    default void addSearchStub(WireMockServer server, Resource resourceFile) throws IOException {

        String ccdDataResponseJson =
            new String(Files.readAllBytes(Paths.get(resourceFile.getURI())));

        server.addStubMapping(
            new StubMapping(
                newRequestPattern(RequestMethod.POST, urlEqualTo("/searchCases?ctid=Asylum"))
                    .build(),
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(ccdDataResponseJson)
                    .build()
            )
        );
    }

}


package uk.gov.hmcts.reform.iacaseaccessapi.testutils;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.iacaseaccessapi.Application;

@SpringBootTest(classes = {
    Application.class
})
@TestPropertySource(properties = {
    "CCD_URL=http://127.0.0.1:8990/",
    "S2S_URL=http://127.0.0.1:8990/s2s",
    "IDAM_URL=http://127.0.0.1:8990/idam",
    "IA_IDAM_CLIENT_ID=ia",
    "IA_IDAM_SECRET=something",
    "IA_S2S_SECRET=AAAAAAAAAAAAAAAC",
    "IA_S2S_MICROSERVICE=ia"
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpringBootIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected static WireMockServer server;

    @BeforeAll
    public void spinUp() {
        server = new WireMockServer(WireMockConfiguration.options()
                                        .notifier(new Slf4jNotifier(true))
                                        .port(8990));
        server.start();
    }

    @AfterEach
    public void reset() {
        server.resetAll();
    }

    @AfterAll
    public void shutDown() {
        server.stop();
    }

}

package uk.gov.hmcts.reform.iacaseaccessapi.testutils;

import static uk.gov.hmcts.reform.iacaseaccessapi.testutils.StaticPortWiremockFactory.WIREMOCK_PORT;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.lanwen.wiremock.ext.WiremockResolver;
import uk.gov.hmcts.reform.iacaseaccessapi.Application;

@SpringBootTest(classes = {
    Application.class
})
@TestPropertySource(properties = {
    "CCD_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/",
    "S2S_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/s2s",
    "IDAM_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/idam",
    "IA_IDAM_CLIENT_ID=ia",
    "IA_IDAM_SECRET=something",
    "IA_S2S_SECRET=AAAAAAAAAAAAAAAC",
    "IA_S2S_MICROSERVICE=ia"
})
@ExtendWith({
    WiremockResolver.class
})
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration
@ActiveProfiles("integration")
public class SpringBootIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

}

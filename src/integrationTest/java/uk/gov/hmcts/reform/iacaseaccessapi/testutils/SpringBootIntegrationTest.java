package uk.gov.hmcts.reform.iacaseaccessapi.testutils;

import static uk.gov.hmcts.reform.iacaseaccessapi.testutils.StaticPortWiremockFactory.WIREMOCK_PORT;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.lanwen.wiremock.ext.WiremockResolver;
import uk.gov.hmcts.reform.iacaseaccessapi.Application;

@SpringBootTest(classes = {
    TestConfiguration.class,
    Application.class
})
@TestPropertySource(properties = {
    "CCD_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/ccd",
    "S2S_URL=http://127.0.0.1:" + WIREMOCK_PORT + "/s2s"
})
@ExtendWith({
    WiremockResolver.class
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integration")
public class SpringBootIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

}

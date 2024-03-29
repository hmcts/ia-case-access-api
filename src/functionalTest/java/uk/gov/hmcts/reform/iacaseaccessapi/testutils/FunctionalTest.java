package uk.gov.hmcts.reform.iacaseaccessapi.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config.ServiceTokenGeneratorConfiguration;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.ExtendedCcdApi;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.ExtendedIdamApi;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.data.DocumentManagementFilesFixture;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.data.DocumentManagementUploader;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.data.IdamAuthProvider;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.data.MapValueExpander;


@SpringBootTest(classes = {
    DocumentUploadClientApiConfiguration.class,
    ServiceTokenGeneratorConfiguration.class,
    FunctionalSpringContext.class
})
@ActiveProfiles("functional")
public class FunctionalTest {

    @Value("${idam.redirectUrl}") protected String idamRedirectUrl;
    @Value("${idam.system.scope}") protected String userScope;
    @Value("${idam.system.client-id}") protected String idamClientId;
    @Value("${idam.system.client-secret}") protected String idamClientSecret;

    @Value("classpath:templates/minimal-appeal-started.json")
    protected Resource minimalAppealStarted;

    @Autowired
    protected AuthTokenGenerator s2sAuthTokenGenerator;

    protected IdamAuthProvider idamAuthProvider;

    @Autowired
    protected ExtendedCcdApi ccdApi;

    @Autowired
    protected ExtendedIdamApi idamApi;

    @Autowired
    protected DocumentUploadClientApi documentUploadClientApi;

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:8089"
        );

    protected RequestSpecification requestSpecification;

    protected MapValueExpander mapValueExpander;

    @BeforeEach
    public void setup() throws IOException {
        requestSpecification = new RequestSpecBuilder()
            .setBaseUri(targetInstance)
            .setRelaxedHTTPSValidation()
            .build();

        idamAuthProvider = new IdamAuthProvider(
            idamApi,
            idamRedirectUrl,
            userScope,
            idamClientId,
            idamClientSecret
        );

        DocumentManagementUploader documentManagementUploader = new DocumentManagementUploader(
            documentUploadClientApi,
            idamAuthProvider,
            s2sAuthTokenGenerator
        );

        DocumentManagementFilesFixture documentManagementFilesFixture = new DocumentManagementFilesFixture(
            documentManagementUploader);
        documentManagementFilesFixture.prepare();

        mapValueExpander = new MapValueExpander(documentManagementFilesFixture);
    }
}

package uk.gov.hmcts.reform.iacaseaccessapi;

import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.FunctionalTest;

@ActiveProfiles("functional")
@DirtiesContext
public class SupplementaryDetailsResponseFunctionTest extends FunctionalTest {

    @Test
    public void should_allow_unauthorized_requests_and_return_401_response_code() {

        SerenityRest
            .given()
            .when()
            .get("/supplementary-details")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}

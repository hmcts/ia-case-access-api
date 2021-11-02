package uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients;

import static uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config.ServiceTokenGeneratorConfiguration.SERVICE_AUTHORIZATION;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config.FeignConfiguration;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers.model.SupplementaryDetailsResponse;

@FeignClient(
    name = "ia-case-access-api",
    url = "localhost:8089",
    configuration = FeignConfiguration.class
)
public interface IaCaseAccessApi {

    @PostMapping(value = "/supplementary-details", produces = "application/json", consumes = "application/json")
    SupplementaryDetailsResponse supplementaryDetails(
        @RequestHeader(SERVICE_AUTHORIZATION) String s2sToken,
        @RequestBody String searchString
    );
}

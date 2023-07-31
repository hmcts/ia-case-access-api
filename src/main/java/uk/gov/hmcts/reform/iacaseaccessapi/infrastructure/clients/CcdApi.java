package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config.ServiceTokenGeneratorConfiguration.SERVICE_AUTHORIZATION;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.CaseDataContent;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.clients.model.ccd.StartEventTrigger;
import uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.config.FeignConfiguration;

@FeignClient(
    name = "ccd-data-store-api",
    url = "${ccd.case-data-api.url}",
    configuration = FeignConfiguration.class
)
public interface CcdApi {

    @GetMapping(
        value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/event-triggers/{etid}/token"
            + "?ignore-warning=true",
        produces = "application/json",
        consumes = "application/json")
    StartEventTrigger startEvent(
        @RequestHeader(AUTHORIZATION) String userToken,
        @RequestHeader(SERVICE_AUTHORIZATION) String s2sToken,
        @PathVariable("uid") String userId,
        @PathVariable("jid") String jurisdiction,
        @PathVariable("ctid") String caseType,
        @PathVariable("cid") String id,
        @PathVariable("etid") String eventId
    );

    @PostMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/events",
        produces = "application/json",
        consumes = "application/json")
    CaseDetails submitEvent(
        @RequestHeader(AUTHORIZATION) String userToken,
        @RequestHeader(SERVICE_AUTHORIZATION) String s2sToken,
        @PathVariable("uid") String userId,
        @PathVariable("jid") String jurisdiction,
        @PathVariable("ctid") String caseType,
        @PathVariable("cid") String id,
        @RequestBody CaseDataContent content
    );

}

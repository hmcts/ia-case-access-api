package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.services;

import java.time.ZonedDateTime;

public interface DateTimeProvider {

    ZonedDateTime now();
}

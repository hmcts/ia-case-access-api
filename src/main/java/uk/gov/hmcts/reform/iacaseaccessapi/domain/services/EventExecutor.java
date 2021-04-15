package uk.gov.hmcts.reform.iacaseaccessapi.domain.services;

import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.EventExecution;

public interface EventExecutor {

    void execute(EventExecution eventExecution);
}

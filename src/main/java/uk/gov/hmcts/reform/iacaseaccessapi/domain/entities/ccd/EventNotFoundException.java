package uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.ccd;

public class EventNotFoundException extends IllegalArgumentException {

    public EventNotFoundException(String message) {
        super(message);
    }
}

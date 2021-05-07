package uk.gov.hmcts.reform.iacaseaccessapi.domain.services;

import java.util.List;
import uk.gov.hmcts.reform.iacaseaccessapi.domain.entities.SupplementaryInfo;

public interface SupplementaryDetailsService {

    List<SupplementaryInfo> getSupplementaryDetails(List<String>  ccdCaseNumberList,String authorisation);
}

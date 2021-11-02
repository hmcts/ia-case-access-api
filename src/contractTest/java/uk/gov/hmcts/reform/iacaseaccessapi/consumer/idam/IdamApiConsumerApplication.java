package uk.gov.hmcts.reform.iacaseaccessapi.consumer.idam;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.ExtendedIdamApi;

@SpringBootApplication
@EnableFeignClients(clients = {
    ExtendedIdamApi.class
})
public class IdamApiConsumerApplication {
}


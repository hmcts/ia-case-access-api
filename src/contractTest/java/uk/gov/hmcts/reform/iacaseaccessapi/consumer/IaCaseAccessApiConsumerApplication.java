package uk.gov.hmcts.reform.iacaseaccessapi.consumer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.reform.iacaseaccessapi.testutils.clients.IaCaseAccessApi;

@SpringBootApplication
@EnableFeignClients(clients = {
    IaCaseAccessApi.class
})
public class IaCaseAccessApiConsumerApplication {
}

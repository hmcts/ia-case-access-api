# ia-case-access-api
Service to access CCD case data for IAC case types

### Background
There is a business need to query CCD case data for a reconciliation purpose.
The reconciliation process is requested by Liberata where there is a need to provide additional information (surname) by IAC Team.
There is a designed solution where IAC application will request case data querying CCD Elastic Search endpoint.
Exposed IAC endpoint will be generic and easy to extend if additional information is needed in the future.
`ia-case-access-api` allows accessing CCD case data information.

### Testing application
Unit tests and code style checks:
```
./gradlew build
```

Integration tests use Wiremock and Spring MockMvc framework:
```
./gradlew integration
```

Functional tests use started application instance:
```
./gradlew functional
```

### Running application

`ia-case-access-api` is common Spring Boot application. Command to run:
```
./gradlew bootRun
```

### Usage
API details about usages and error statuses are placed in [ UI](http://ia-case-access-api-aat.service.core-compute-aat.internal/swagger-ui.html)


### Implementation
`ia-case-access-api` has a single POST endpoint called `supplementary-details`. The payload is a list of CCD Case Ids.

Authentication is defined as S2S `ServiceAuthorization` token.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

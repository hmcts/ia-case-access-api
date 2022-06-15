package uk.gov.hmcts.reform.iacaseaccessapi.infrastructure.controllers;

import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @Operation(summary = "Welcome message for the Immigration & Asylum Case Access Api",
        responses =
            {
                @ApiResponse(
                    responseCode = "200",
                    description = "Welcome message",
                    content = @Content(schema = @Schema(implementation = String.class)))
            })
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ok("Welcome to IA Case Access Api");
    }
}

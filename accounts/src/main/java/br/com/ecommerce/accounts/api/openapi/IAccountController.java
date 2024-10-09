package br.com.ecommerce.accounts.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.ecommerce.accounts.api.dto.DataUserDTO;
import br.com.ecommerce.common.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "account-controller",
    description = "Controller common to all types of users, regardless of roles.")
@SecurityRequirement(name = "bearer-key")
public interface IAccountController {

    @Operation(
        summary = "Get current user data",
        description = "Endpoint for retrieving the data of the logged-in user.",
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataUserDTO.class)
            )), 
            @ApiResponse(
                description = "Forbidden", 
                responseCode = "403",
                content = @Content())
        })
    public ResponseEntity<DataUserDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl user);
}

package br.com.ecommerce.accounts.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.ecommerce.accounts.api.dto.CreateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.DataUserClientDTO;
import br.com.ecommerce.accounts.api.dto.UpdateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.exception.ResponseError;
import br.com.ecommerce.common.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "client-account-controller",
    description = """
        Controller dedicated to clients. It has a public route for registration, while all other routes are 
        exclusive to users with the CLIENT role.
        """)
public interface IClientAccountController {

    @Operation(
        summary = "Create user client",
        description = """
            Endpoint to create a UserClient.

            - Public route
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataUserClientDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 400,
                                "error": "Bad Request",
                                "message": {
                                    "username": "Size must be between 8 and 20 characters",
                                    "password": "The password must contain at least one letter, one special character and be at least 8 characters long",
                                    "name": "must not be blank",
                                    "phone_number": "Size must be between 11 and 19 characters",
                                    "cpf": "invalid Brazilian individual taxpayer registry number (CPF)",
                                    "address.street": "must not be blank",
                                    "address.postal_code": "must not be blank",
                                    "address.complement": "must not be blank",
                                    "address.neighborhood": "must not be blank",
                                    "address.city": "must not be blank",
                                    "address.state": "must not be blank"
                                }
                            }
                        """
                    )
                ))
        })
    public ResponseEntity<DataUserClientDTO> create(@RequestBody @Valid CreateUserClientDTO dto);

    @Operation(
        summary = "Get current user client data",
        description = """
            Endpoint to retrieve all data of the logged-in user (UserClient specific properties).

            - Only UserClient users with the CLIENT role can access this endpoint.
            """,
        security = { @SecurityRequirement(name = "bearer-key") },
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataUserClientDTO.class)
            )), 
            @ApiResponse(
                description = "Forbidden", 
                responseCode = "403",
                content = @Content(
                ))
        })
    public ResponseEntity<DataUserClientDTO> getCurrentUserClientData(
        @AuthenticationPrincipal UserDetailsImpl currentUser
    );

    @Operation(
        summary = "Update current user client data",
        description = """
            Endpoint to update the logged-in user's client data.

            - Updates only the user's own data.
            - Only UserClient users with the CLIENT role can access this endpoint.
            - All fields are optional.
            """,
        security = { @SecurityRequirement(name = "bearer-key") },
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataUserClientDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
                )),
            @ApiResponse(
                description = "Forbidden", 
                responseCode = "403",
                content = @Content())
        })
    public ResponseEntity<DataUserClientDTO> updateCurrentClientData(
        @RequestBody UpdateUserClientDTO dto,
        @AuthenticationPrincipal UserDetailsImpl currentUser
    );
}
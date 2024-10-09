package br.com.ecommerce.accounts.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.ecommerce.accounts.api.dto.CreateUserEmployeeDTO;
import br.com.ecommerce.accounts.api.dto.UserEmployeeCreatedDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "admin-account-controller",
    description = "Controller accessible only to users with the ADMIN role.")
@SecurityRequirement(name = "bearer-key")
public interface IAdminAccountController {

    @Operation(
        summary = "Create admin user",
        description = 
            """
            Endpoint to create a user with ADMIN role.
    
            - Accessible only to authenticated users with ADMIN role.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserEmployeeCreatedDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = 
                            """
                            {
                                "status": 400,
                                "error": "Bad Request",
                                "message": {
                                    "password": "The password must contain at least one letter, one special character and be at least 8 characters long",
                                    "name": "must not be blank",
                                    "username": "size must be 3 or more characters"
                                }
                            }
                            """
                    )
                )),
            @ApiResponse(
                description = "Forbidden", 
                responseCode = "403",
                content = @Content())
        })
    public ResponseEntity<UserEmployeeCreatedDTO> createAdminUser(@RequestBody @Valid CreateUserEmployeeDTO dto);

    @Operation(
        summary = "Create employee user",
        description = 
            """
            Endpoint to create a user with the EMPLOYEE role.
    
            - Accessible only to users with ADMIN or EMPLOYEE role.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserEmployeeCreatedDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = 
                            """
                            {
                                "status": 400,
                                "error": "Bad Request",
                                "message": {
                                    "password": "The password must contain at least one letter, one special character and be at least 8 characters long",
                                    "name": "must not be blank",
                                    "username": "size must be 3 or more characters"
                                }
                            }
                            """
                    )
                )),
            @ApiResponse(
                description = "Forbidden", 
                responseCode = "403",
                content = @Content())
        })
    public ResponseEntity<UserEmployeeCreatedDTO> createEmployeeUser(@RequestBody @Valid CreateUserEmployeeDTO dto);
}
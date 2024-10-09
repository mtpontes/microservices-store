package br.com.ecommerce.accounts.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.ecommerce.accounts.api.dto.SignInDTO;
import br.com.ecommerce.accounts.api.dto.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "sign-in-controller",
    description = "Public controller.")
public interface ISignInController {

    @Operation(
        summary = "Sign in user",
        description = "Endpoint for authenticating the user and retrieving their data along with an access token.",
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TokenDTO.class)
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
                                    "password": "The password must contain at least one letter, one special character and be at least 8 characters long",
                                    "username": "Cannot be less than 3 characters"
                                }
                            }
                        """
                    )
                )),
            @ApiResponse(
                description = "Invalid credentials", 
                responseCode = "401",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 401,
                                "error": "Unauthorized",
                                "message": "User not found"
                            }
                        """
                    )
                ))
        })
    public ResponseEntity<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto);
}
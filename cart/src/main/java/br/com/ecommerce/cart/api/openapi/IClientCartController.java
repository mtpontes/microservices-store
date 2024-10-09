package br.com.ecommerce.cart.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.cart.api.dto.cart.AnonCartRefereceDTO;
import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.dto.exception.ResponseError;
import br.com.ecommerce.cart.api.dto.exception.ResponseErrorWithoutMessage;
import br.com.ecommerce.common.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "client-cart-controller", 
    description = "Controller intended for authenticated CLIENT users \n")
@SecurityRequirement(name = "bearer-key")
public interface IClientCartController {

    @Operation(
        summary = "Create cart for authenticated user",
        description = 
            """
            Creates a shopping cart exclusively for the authenticated user, where the **cart ID is the same as the user ID**. 
            If the user already has an active cart, it will be returned, preventing the creation of multiple carts. 
            The cart is used to manage products before checkout.

            - **Authenticated user**: The cart is linked to the user currently logged in.
            - **Product management**: Products can be added, removed, or updated via specific endpoints.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "201",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CartDTO.class)
            )), 
            @ApiResponse(
                description = "Empty fields", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )), 
            @ApiResponse(
                description = "Product not found", 
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseErrorWithoutMessage.class)
            )), 
        })
    ResponseEntity<CartDTO> create(
        @AuthenticationPrincipal UserDetailsImpl user,
        UriComponentsBuilder uriBuilder);

    @Operation(
        summary = "Get cart",
        description = 
            """
            Retrieves the shopping cart associated with the authenticated user. 
            If the cart does not exist or the authentication header is missing, the request will fail.
            
            - **Authentication required**: The cart is retrieved based on the authenticated user's ID.
            - **Cart not found**: If the associated cart is not located, a 404 response will be returned.
            """,
        responses = {
            @ApiResponse(
                description = "Missing 'X-anon-cart-id' header ", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )), 
            @ApiResponse(
                description = "Cart not found", 
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseErrorWithoutMessage.class)
            )), 
        })
    ResponseEntity<CartDTO> get(@AuthenticationPrincipal UserDetailsImpl user);

    @Operation(
        summary = "Merge anonymous cart with authenticated user's cart",
        description = 
            """
            Merges the anonymous cart with the authenticated user's cart after authentication.
    
            - If the user does not yet have a cart linked to their ID, one will be created during the process.
            - The anonymous cart will be deleted at the end of the operation.
            - If the product exists in both carts, quantities will be summed.
            - Products not existing in the authenticated user's cart will be transferred from the anonymous cart.
            - Merging is not possible if the source is not an anonymous cart.
            """,
        responses = {
            @ApiResponse(
                description = "Empty fields", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )), 
            @ApiResponse(
                description = "Cart not found", 
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseErrorWithoutMessage.class)
            )), 
        })
    ResponseEntity<CartDTO> mergeCarts(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid AnonCartRefereceDTO requestBody
    );

    @Operation(
        summary = "Manage products in cart",
        description = 
            """
            Allows adding, removing, and updating product quantities in the cart.
    
            - **Add products**: New products are added to the cart.
            - **Remove products**: Removes products from the cart.
            - **Increase quantities**: Any specified quantity is accepted.
            - **Decrease quantities**: If the quantity reaches 0, the product is removed from the cart.
            """,
        responses = {
            @ApiResponse(
                description = "Empty fields", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )), 
            @ApiResponse(
                description = "Product not found", 
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseErrorWithoutMessage.class)
            )), 
        })
    ResponseEntity<CartDTO> updateUnit(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    );
}
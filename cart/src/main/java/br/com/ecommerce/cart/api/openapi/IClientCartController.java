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
        description = "Endpoint for creating an anonymous cart.",
        summary = "Create anonymous cart",
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
        description = "Endpoint to retrieve cart data.",
        summary = "Get cart data",
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
        summary = "Endpoint to merge an anonymous cart with the authenticated user's cart.",
        description = 
            "Merge the carts when the user authenticates \n" +
            "- If the user does not yet have a cart linked to their ID, one will be created in the process \n" +
            "- The anonymous cart is deleted at the end of the process \n" +
            "- If the product exists in both carts, the quantities will be summed \n" +
            "- If the product does not exist in the cart, it will be transferred to the authenticated user's cart" +
            "- It is not possible to merge if the target is not an anonymous cart",
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
        summary = "Endpoint to add, remove, and change quantities of products.",
        description = 
            "- Add products \n" + 
            "- Remove products \n" + 
            "- Increase quantities: accepts any amount \n" + 
            "- Decrease quantity: when reaching quantity 0, the product is removed from the cart",
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
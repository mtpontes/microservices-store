package br.com.ecommerce.cart.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.dto.exception.ResponseError;
import br.com.ecommerce.cart.api.dto.exception.ResponseErrorWithoutMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "anonymous-cart-controller",
    description = 
        "- Intended for users who have not yet been authenticated, so they can have a cart even " + 
        "without logging into an account. \n" +
        "- For it to work, I recommend storing the cart ID locally in the browser, and in the " + 
        "subsequent HTTP operations, send this ID in the header __'X-anon-cart-id'__")
public interface IAnonymousCartController {

    @Operation(
        summary = "Create anonymous cart",
        description = 
            """
            Endpoint for creating an anonymous cart. This endpoint allows users 
            to create a cart without requiring authentication. The cart can then 
            be populated with products for later checkout.
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
        @RequestBody @Valid UpdateCartProductDTO requestBody,
        UriComponentsBuilder uriBuilder);


    @Operation(
        summary = "Get cart",
        description = 
            """
            Endpoint for retrieving cart data using the anonymous cart ID.
            This endpoint allows clients to fetch the contents of their 
            anonymous cart, enabling them to view items before 
            proceeding to checkout.
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
    ResponseEntity<CartDTO> get(
        @Parameter(
            name = "X-anon-cart-id", 
            description = "Anonymous cart ID.",
            required = true,
            schema = @Schema(type = "string"))
        @RequestHeader(name = "X-anon-cart-id") String anonCartId);

        @Operation(
            summary = "Manage cart products",
            description = 
                """
                Endpoint for adding, removing, and changing product quantities in an anonymous cart.
                This endpoint allows clients to manage their shopping cart in a flexible manner:
                - **Add products**: Clients can specify products to add to the cart along with the desired quantities.
                - **Remove products**: Clients can specify which products to remove from the cart entirely.
                - **Increase quantities**: Accepts any positive integer to increase the quantity of existing products in the cart.
                - **Decrease quantity**: When the quantity of a product reaches 0, it is automatically removed from the cart.
                
                The cart is identified by the 'X-anon-cart-id' header, which is mandatory for accessing this endpoint. Ensure that the provided cart ID is valid to avoid errors.
                """,
        responses = {
            @ApiResponse(
                description = "Empty fields", responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )), 
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
    ResponseEntity<CartDTO> updateUnit(
        @Parameter(
            name = "X-anon-cart-id", 
            description = "Anonymous cart ID.",
            required = true,
            schema = @Schema(type = "string"))
        @RequestHeader(name = "X-anon-cart-id") String anonCartId,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    );
}
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
        @RequestBody @Valid UpdateCartProductDTO requestBody,
        UriComponentsBuilder uriBuilder);


    @Operation(
        description = "Endpoint for retrieving cart data.",
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
    ResponseEntity<CartDTO> get(
        @Parameter(
            name = "X-anon-cart-id", 
            description = "Anonymous cart ID.",
            required = true,
            schema = @Schema(type = "string"))
        @RequestHeader(name = "X-anon-cart-id") String anonCartId);


    @Operation(
        summary = "Endpoint for adding, removing, and changing product quantities.",
        description = 
            "- Add products \n" + 
            "- Remove products \n" + 
            "- Increase quantities: accepts any quantity \n" + 
            "- Decrease quantity: when the quantity reaches 0, the product is removed from the cart",
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
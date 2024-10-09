package br.com.ecommerce.cart.api.openapi;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.exception.ResponseError;
import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.common.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Tag(
    name = "cart-to-order-controller",
    description = "This controller is aimed at requesting the creation of an order from the data of a cart.")
@SecurityRequirement(name = "bearer-key")
public interface ICartToOrderController {

    @Operation(
        description = 
            "Endpoint for creating an anonymous cart \n" +
            "- All product IDs must be different from null \n" +
            "- Anonymous carts cannot generate orders \n" +
            "- It is not possible to generate orders with an empty cart \n" +
            "- Only the IDs of the products present in the cart must be passed \n" +
            "- The quantities are not adjustable here; the quantity of the product will be that which is in the cart",
        summary = "Create anonymous cart",
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CartDTO.class)
            )), 
            @ApiResponse(
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            )),  
        })
    ResponseEntity<OrderDataDTO> create(
        @AuthenticationPrincipal @Valid @NotNull UserDetailsImpl user,
        @RequestBody @Valid @NotEmpty Set<String> requestBody);
}
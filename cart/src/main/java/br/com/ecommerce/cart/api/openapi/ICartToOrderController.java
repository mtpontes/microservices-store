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
        summary = "Create an order from the cart",
        description = 
            """
            Endpoint for creating an order based on the contents of an anonymous cart.
            
            - **Fixed quantity**: The product quantities are determined by what is registered in the cart at the time the order is created. No quantity adjustments are allowed at this stage.
            - **User identification**: The order will be linked to the authenticated user, and the cart must contain the products that will be included in the order.
            - **Product IDs**: The set of product IDs being purchased must be sent in the request body. These IDs correspond to the products previously added to the cart.
            - **Cart restrictions**: The cart must be complete and valid to create the order. If there are invalid products or the cart is empty, the order cannot be created.
            - The selected products are removed from the cart at the end of the process.
            
            This endpoint is essential for finalizing the purchase process, transforming the cart's contents into a concrete order.
            """,
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
        @RequestBody @Valid @NotEmpty Set<String> productIds);
}
package br.com.ecommerce.orders.api.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.ecommerce.common.user.UserDetailsImpl;
import br.com.ecommerce.orders.api.dto.exception.ResponseError;
import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.openapi.schemas.PagedOrderBasicInfDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "client-order-controller",
    description = "Controller intended for users with CLIENT role"
)
@SecurityRequirement(name = "bearer-key")
public interface IClientOrderController {

    @Operation(
        summary = "Get all basic information of all orders of a customer",
        description = "Shows all orders of the customer in a paginated manner, with reduced data",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedOrderBasicInfDTO.class)
                )
            ),
        }
    )
    public ResponseEntity<Page<OrderBasicInfDTO>> getAllBasicsInfoOrdersByUser(
        @AuthenticationPrincipal UserDetailsImpl user,
        @PageableDefault(size = 10) Pageable pageable
    );

    @Operation(
        summary = "Get all the data from a customer's order",
        description = "Shows all the data of the customer's order",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderDTO.class)
                )
            ),
            @ApiResponse(
                description = "Order not found",
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
                )
            )
        }
    )
    public ResponseEntity<OrderDTO> getOrderByIdAndUserId(
        @PathVariable String orderId,
        @AuthenticationPrincipal UserDetailsImpl user,
        @PageableDefault(size = 10) Pageable pageable
    );

    @Operation(
        summary = "Cancel customer's order",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "204",
                content = @Content()
            ),
            @ApiResponse(
                description = "Order not found",
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
                )
            )
        }
    )
    public ResponseEntity<Void> cancelOrder(
        @PathVariable String orderId, 
        @AuthenticationPrincipal UserDetailsImpl user
    );
}
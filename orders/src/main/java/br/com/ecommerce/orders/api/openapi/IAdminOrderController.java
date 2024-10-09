package br.com.ecommerce.orders.api.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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
    name = "admin-order-controller",
    description = "Controller intended for users with ADMIN or EMPLOYEE role"
)
@SecurityRequirement(name = "bearer-key")
public interface IAdminOrderController {

    @Operation(
        summary = "Get all basic information of all orders of a customer",
        description = 
            """
            Returns all basic information of all orders of a customer in a paginated format.
    
            - The order data is presented in a reduced format to enhance performance and user experience.
            """,
        responses = {
        	@ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedOrderBasicInfDTO.class)
                )
            ),
            @ApiResponse(
                description = "When no orders are found for the provided user ID",
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
                )
            )
        }
    )
    public ResponseEntity<Page<OrderBasicInfDTO>> getAllBasicsInfoOrdersByUser(
        @PathVariable String userId,
        @PageableDefault(size = 10) Pageable pageable
    );

    @Operation(
        summary = "Get all the data from a customer's order",
        description = 
            """
            Returns all the data of a customer's order.
    
            - This endpoint provides complete details about the order specified by the ID.
            """,
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
        @PathVariable String userId
    );

    @Operation(
        summary = "Cancel customer's order",
        description = 
            """
            Cancels the customer's order specified by the ID.
    
            - If the order is not found, it will return a 404 error.
            """,
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
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId);
}
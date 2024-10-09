package br.com.ecommerce.products.api.openapi;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "product-controller",
    description = "Public controller")
public interface IProductController {

    @Operation(
        summary = "Get all products",
        description = 
            """
            Returns a paginated list of all products.
    
            All query parameters are optional and can be used to filter the results:
    
            - `name`: Filters products by name.
            - `category`: Filters products by category.
            - `minPrice`: Filters products with the specified minimum price.
            - `maxPrice`: Filters products with the specified maximum price.
            - `manufacturer`: Filters products by manufacturer.
    
            ### Note:
            If no parameters are provided, all products will be returned.
            """
    )
    public ResponseEntity<Page<DataProductDTO>> getAll(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) String manufacturer
    );

    @Operation(
        summary = "Get product",
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataProductDTO.class)
            )), 
            @ApiResponse(
                description = "Not found", 
                responseCode = "404",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 404,
                                "error": "Not found",
                                "message": "Product not found"
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<DataProductDTO> getProduct(@PathVariable Long productId);
}
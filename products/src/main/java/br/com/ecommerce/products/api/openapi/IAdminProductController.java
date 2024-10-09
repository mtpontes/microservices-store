package br.com.ecommerce.products.api.openapi;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.api.dto.exception.ResponseError;
import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductStockDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.EndOfPromotionDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductImagesResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductPriceResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "admin-product-controller",
    description = "Controller accessible only by users with ADMIN or EMPLOYEE role")
@SecurityRequirement(name = "bearer-key")
public interface IAdminProductController {

    @Operation(
        summary = "Create product",
        description = 
            """
            Creates a product.
    
            ### Rules:
    
            1. **Unique name**: The product name must be unique within the system.
            2. **Prices, stock, and images are handled in other endpoints**: The product must be initially created 
            **without price, stock, or images**.
    
            
            ### Optional Fields (allowed as null):
    
            - `description`: Product description.
            - `specs`: Technical specifications.
    
    
            ### Dependencies:
    
            - **Category**: The product must be associated with an existing category.
            - **Manufacturer**: The product must be linked to an existing manufacturer.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "201",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataProductDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<DataProductDTO> createProduct(
        @RequestBody @Valid CreateProductDTO dto, 
        UriComponentsBuilder uriBuilder
    );
    
    @Operation(
        summary = "Update product [name, description, specs]",
        description = 
            """
            This endpoint allows updating the name, description, or specifications of an existing product.
        
            ### Rules:
        
            1. **Partial update**: Any combination of `name`, `description`, or `specs` can be updated. 
            Fields with a value of `null` will be ignored and remain unchanged.
            2. **Unique name**: The product name must be unique and **cannot be the same as another existing product**.
        
            ### Notes:
            - If the `name` field is provided, it will be validated to ensure it is unique within the system.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "When the name is already in use", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = 
                            """
                            {
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Invalid department name"
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<UpdateProductResponseDTO> updateProduct(
        @PathVariable Long productId, 
        @RequestBody UpdateProductDTO dto
    );
    
    @Operation(
        summary = "Update product stocks",
        description = 
            """
            Updates the stock of a product.
        
            ### Validation rules:
            
            - The `unit` field must be an integer value.
            - Accepts positive and negative values.
            - The updated stock value will be added or subtracted based on the input value.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataProductStockDTO.class)
            )),
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        }
    )
    public ResponseEntity<DataProductStockDTO> updateStock(
        @PathVariable Long productId, 
        @RequestBody @Valid DataStockDTO dto
    );
    
    @Operation(
        summary = "Update product prices",
        description = 
            """
            Updates the prices of a product.
    
            ### Validations:
    
            1. **Negative values are not allowed**: All prices must be positive numbers.
            2. **'promotionalPrice' must be less than 'originalPrice'**: The promotional price, if present, must 
            always be less than the original price.
    
            ### Optional Fields:
            - `promotionalPrice`: Can be null.
    
            ### Notes:
            - `currentPrice`: This is considered the final price of the product. By default, the 
            `currentPrice` is equal to the `originalPrice`. If you want the promotional price to be considered 
            the current price, use the endpoint `/products/{productId}/prices/switch-to-promotional` to create 
            a promotion period for the product. The promotion sets the `currentPrice` to the value of `promotionalPrice` for a 
            determined period.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<UpdateProductPriceResponseDTO> updatePrice(
        @PathVariable Long productId, 
        @RequestBody UpdatePriceDTO dto
    );
    
    @Operation(
        summary = "Start a promotion",
        description = 
            """
            Applies the promotional state to a product, changing the `currentPrice` to the value 
            defined in `promotionalPrice`. The promotion must have an end date, and upon reaching this date, the product 
            automatically exits the promotion, returning to the original price (`originalPrice`).
        
            ### Requirements:
            - The `promotionalPrice` must be set on the product before using this endpoint.
            - An expiration date (`endOfPromotion`) for the promotion must be provided.
        
            ### Transition Rules:
            - **During the promotion**: The `currentPrice` reflects the value of `promotionalPrice`.
            - **After the promotion ends**: The `currentPrice` reverts to the value of `originalPrice`.
        
            ### Validations:
            1. **Past dates are not allowed**: The expiration date must be in the future.
            2. **The promotional price must be less than the original price**.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<UpdateProductPriceResponseDTO> switchCurrentPriceToPromotionalPrice(
        @PathVariable Long productId,
        @RequestBody @Valid EndOfPromotionDTO requestBody
    );
    
    @Operation(
        summary = "End promotion",
        description = 
            """
            Returns the product to its normal state, restoring the `currentPrice` to the value of `originalPrice`.
    
            - The final price of the product will be updated to the original value that was configured before the promotion.
            - No additional information needs to be provided for this operation.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            ))
        })
    public ResponseEntity<UpdateProductPriceResponseDTO> switchCurrentPriceToOriginalPrice(@PathVariable Long productId);
    
    @Operation(
        summary = "Add main product image",
        description = 
            """
            Directly sets the main image of a product using an image link.
    
            - If the product already has a list of images and no main image has been manually defined, one of 
            the images from the list will be randomly chosen as the main image.
    
            ### Note:
            It is recommended to always explicitly set the main image to avoid random selections.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field value", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<UpdateProductImagesResponseDTO> addMainImage(
        @PathVariable Long productId, @RequestParam String imageLink
    );    

    @Operation(
        summary = "Add images to product",
        description = 
            """
            Adds new images to the product's image list.
        
            - If the product already has images in the list and does not have a main image set, one of the images will 
            be randomly chosen as the main image for the product.
        
            ### Note:
            To ensure a better user experience, it is recommended to explicitly set a main image after adding new images.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field value", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<UpdateProductImagesResponseDTO> addImages(
        @PathVariable Long productId, @RequestBody Set<String> newImages
    );
    
    @Operation(
        summary = "Remove images from product",
        description = 
            """
            Removes specific images from the product's image list.
        
            - It is necessary to provide a list with the links of the images to be removed.
            - After removal, the image list will be updated, and the product will no longer have the specified images.
        
            ### Note:
            Ensure that the provided links correspond to existing images in the product's list.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field value", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<UpdateProductImagesResponseDTO> removeImages(
        @PathVariable Long productId, @RequestBody Set<String> imagesToRemove
    );
}
package br.com.ecommerce.products.api.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.exception.ResponseError;
import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.UpdateManufacturerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "admin-manufacturer-controller",
    description = "Controller accessible only by users with ADMIN or EMPLOYEE role")
@SecurityRequirement(name = "bearer-key")
public interface IAdminManufacturerController {

    @Operation(
        summary = "Create manufacturer",
        description = 
            """
            Creates a new manufacturer in the database.
            
            - The `name` field must be unique and cannot be the same as another existing manufacturer.
            - Only the `name` field is required; all other fields are optional and can be updated later.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataManufacturerDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
                ))
        })
    public ResponseEntity<DataManufacturerDTO> create(
        @RequestBody @Valid CreateManufacturerDTO dto, 
        UriComponentsBuilder uriBuilder
    );

    @Operation(
        summary = "Get manufacturer",
        description = 
            """
            Retrieves the details of a specific manufacturer based on its unique identifier (ID). 
            
            If the manufacturer with the provided ID is not found, an error response will be returned.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataManufacturerDTO.class)
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
                                "message": "Manufacturer not found"
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<DataManufacturerDTO> getOne(@PathVariable Long id);

    @Operation(
        summary = "Get all manufacturers",
        description = 
            """
            Retrieves a list of all manufacturers registered in the database. All query parameters are optional and can be used to filter the results:
            
            - `name`: Filters manufacturers by name.
            - `phone`: Filters manufacturers by phone.
            - `email`: Filters manufacturers by email.
            - `contactPerson`: Filters manufacturers by contact person.
            
            If no parameters are provided, all manufacturers will be returned with a default pagination of 10 items per page.
            """
    )
    public ResponseEntity<Page<DataManufacturerDTO>> getAllWithDiverseParams(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String contactPerson,
        @PageableDefault(size = 10) Pageable pageable
    );

    @Operation(
        summary = "Update manufacturer",
        description = 
            """
            Updates the details of a manufacturer. All fields are optional, but the name must be unique and cannot be the same as another manufacturer.
            
            If the provided name is already in use, a validation error will be returned.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataManufacturerDTO.class)
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
                                "message": "Invalid manufacturer name" 
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<DataManufacturerDTO> update(
        @PathVariable Long id, 
        @RequestBody @Valid UpdateManufacturerDTO dto
    );
}
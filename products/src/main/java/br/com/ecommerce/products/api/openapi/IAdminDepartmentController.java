package br.com.ecommerce.products.api.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.category.CreateCategoryDTO;
import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.category.UpdateCategoryDTO;
import br.com.ecommerce.products.api.dto.department.CreateDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.UpdateDepartmentoDTO;
import br.com.ecommerce.products.api.dto.exception.ResponseError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "admin-department-controller",
    description = "Controller accessible only by users with ADMIN or EMPLOYEE role")
@SecurityRequirement(name = "bearer-key")
public interface IAdminDepartmentController {

    @Operation(
        summary = "Create department",
        description = 
            """
            Creates a new department in the database.
    
            - The `name` field must be unique and cannot be the same as another existing department.
            - A department may contain a collection of categories, which are added to the department during its creation.
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
    public ResponseEntity<SimpleDataDepartmentDTO> createDepartment(
        @RequestBody @Valid CreateDepartmentDTO dto, 
        UriComponentsBuilder uriBuilder
    );

    @Operation(
        summary = "Update department",
        description = 
            """
            Updates the name of an existing department.
    
            - The department name must be unique and cannot be the same as another registered department.
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
                        name = "When the name is already in use",
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
    public ResponseEntity<SimpleDataDepartmentDTO> updateDepartment(
        @PathVariable Long departmentId, 
        @RequestBody UpdateDepartmentoDTO dto
    );

    @Operation(
        summary = "Create category",
        description = 
            """
            Creates a new category associated with an existing department.
    
            - A department must be created beforehand, as the category directly depends on it.
            - The category name must be unique and cannot be the same as another registered category.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataCategoryDTO.class)
            )), 
            @ApiResponse(
                description = "Invalid field values", 
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseError.class)
            ))
        })
    public ResponseEntity<SimpleDataCategoryDTO> createCategory(
        @RequestBody @Valid CreateCategoryDTO dto, 
        UriComponentsBuilder uriBuilder
    );

    @Operation(
        summary = "Update category",
        description = 
            """
            Updates the details of an existing category.
    
            - The category name must be unique and cannot be the same as another registered category.
            - Make sure to provide the ID of the category to be updated.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SimpleDataCategoryDTO.class)
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
    public ResponseEntity<SimpleDataCategoryDTO> updateCategory(
        @PathVariable Long categoryId, 
        @RequestBody UpdateCategoryDTO dto
    );
}
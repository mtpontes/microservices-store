package br.com.ecommerce.products.api.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.department.DataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "department-controller",
    description = "Public controller")
public interface IDepartmentController {

    @Operation(
        summary = "Get department",
        description = 
            """
            Retrieves the details of a specific department by its ID.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Not found", 
                responseCode = "404",
                content = @Content(
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 404,
                                "error": "Not found",
                                "message": "Department not found"
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<DataDepartmentDTO> getDepartment(@PathVariable Long departmentId);


    @Operation(
        summary = "Get all departments",
        description = 
            """
            Retrieves a list of all departments registered in the database.
            
            It is possible to filter the results by department name. The query is paginated, with a 
            default size of 10 items per page.
            """
    )
    public ResponseEntity<Page<SimpleDataDepartmentDTO>> getAllDepartments(
        @RequestParam(required = false) String name,
        Pageable pageable
    );

    @Operation(
        summary = "Get department",
        description = 
            """
            Retrieves the details of a specific department based on its ID.
            """,
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataDepartmentDTO.class)
            )), 
            @ApiResponse(
                description = "Not found", 
                responseCode = "404",
                content = @Content(
                    examples = @ExampleObject(
                        value = """
                            {
                                "status": 404,
                                "error": "Not found",
                                "message": "Department not found"
                            }
                            """
                    )
                ))
        })
    public ResponseEntity<SimpleDataCategoryDTO> getCategory(@PathVariable Long categoryId);

    @Operation(
        summary = "Get all categories",
        description = 
            """
            Returns all available categories.
    
            - It is possible to filter categories by name.
            - Categories are presented in a paginated manner.
            """)
    public ResponseEntity<Page<SimpleDataCategoryDTO>> getAllCategories(
        @RequestParam(required = false) String name,
        @PageableDefault(size = 10) Pageable pageable
    );
}
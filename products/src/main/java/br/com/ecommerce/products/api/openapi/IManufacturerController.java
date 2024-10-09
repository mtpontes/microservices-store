package br.com.ecommerce.products.api.openapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "manufacturer-controller",
    description = "Public controller")
public interface IManufacturerController {

    @Operation(
        summary = "Get all manufacturers",
        description = 
            """
            Retrieves a list of all manufacturers registered in the database.
            
            All query parameters are optional and can be used to filter the results:
            - `name`: Filters manufacturers by name.
            - `contactPerson`: Filters manufacturers by contact person.
            
            If no parameters are provided, all manufacturers will be returned with a default pagination of 10 items per page.
            """
    )
    public ResponseEntity<Page<SimpleDataManufacturerDTO>> getAll(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String contactPerson,
        @PageableDefault(size = 10) Pageable pageable
    );
}
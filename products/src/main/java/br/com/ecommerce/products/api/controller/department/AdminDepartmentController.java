package br.com.ecommerce.products.api.controller.department;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.category.CreateCategoryDTO;
import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.category.UpdateCategoryDTO;
import br.com.ecommerce.products.api.dto.department.CreateDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.UpdateDepartmentoDTO;
import br.com.ecommerce.products.business.service.CategoryService;
import br.com.ecommerce.products.business.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/departments")
public class AdminDepartmentController {

	private final DepartmentService departmentService;
	private final CategoryService categoryService;


	@PostMapping
	public ResponseEntity<SimpleDataDepartmentDTO> createDepartment(
		@RequestBody @Valid CreateDepartmentDTO dto, 
		UriComponentsBuilder uriBuilder
	) {
		SimpleDataDepartmentDTO responseBody = departmentService.createDepartment(dto);
		var uri = uriBuilder
			.path("/departments/{departmentId}")
			.buildAndExpand(responseBody.getId())
			.toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}

	@PutMapping("/{departmentId}")
	public ResponseEntity<SimpleDataDepartmentDTO> updateDepartment(
		@PathVariable Long departmentId, 
		@RequestBody UpdateDepartmentoDTO dto
	) {
		return ResponseEntity.ok(departmentService.updateDepartment(departmentId, dto));
	}

	@PostMapping("/categories")
	public ResponseEntity<SimpleDataCategoryDTO> createCategory(
		@RequestBody @Valid CreateCategoryDTO dto, 
		UriComponentsBuilder uriBuilder
	) {
		SimpleDataCategoryDTO responseBody = categoryService.create(dto);
		var uri = uriBuilder
			.path("/departments/categories/{categoryId}")
			.buildAndExpand(responseBody.getId())
			.toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}

	@PutMapping("/categories/{categoryId}")
	public ResponseEntity<SimpleDataCategoryDTO> updateCategory(
		@PathVariable Long categoryId, 
		@RequestBody UpdateCategoryDTO dto
	) {
		return ResponseEntity.ok(categoryService.update(categoryId, dto));
	}
}
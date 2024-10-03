package br.com.ecommerce.products.api.controller.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.department.DataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.business.service.CategoryService;
import br.com.ecommerce.products.business.service.DepartmentService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/departments")
public class DepartmentController {

	private final DepartmentService departmentService;
	private final CategoryService categoryService;


	@GetMapping("/{departmentId}")
	public ResponseEntity<DataDepartmentDTO> getDepartment(@PathVariable Long departmentId) {
		return ResponseEntity.ok(departmentService.getOneDepartment(departmentId));
	}

	@GetMapping
	public ResponseEntity<Page<SimpleDataDepartmentDTO>> getAllDepartments(
		@RequestParam(required = false) String name,
		Pageable pageable
	) {
		return ResponseEntity.ok(departmentService.getAllDepartments(name, pageable));
	}

	@GetMapping("/categories/{categoryId}")
	public ResponseEntity<SimpleDataCategoryDTO> getCategory(@PathVariable Long categoryId) {
		return ResponseEntity.ok(categoryService.getOne(categoryId));
	}

	@GetMapping("/categories")
	public ResponseEntity<Page<SimpleDataCategoryDTO>> getAllCategories(
		@RequestParam(required = false) String name,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(categoryService.getAllByParams(name, pageable));
	}
}
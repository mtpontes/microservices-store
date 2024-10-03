package br.com.ecommerce.products.api.controller.manufacturer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.business.service.ManufacturerService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/manufacturers")
public class ManufacturerController {

	private final ManufacturerService service;


	@GetMapping
	public ResponseEntity<Page<SimpleDataManufacturerDTO>> getAll(
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String contactPerson,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(service.getAllSimpleDataManufacturers(name, contactPerson, pageable));
	}
}
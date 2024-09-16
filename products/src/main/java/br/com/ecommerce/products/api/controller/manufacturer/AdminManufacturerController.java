package br.com.ecommerce.products.api.controller.manufacturer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.UpdateManufacturerDTO;
import br.com.ecommerce.products.business.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/manufacturers")
public class AdminManufacturerController {

	private final ManufacturerService service;


	@PostMapping
	public ResponseEntity<DataManufacturerDTO> create(
		@RequestBody @Valid CreateManufacturerDTO dto, 
		UriComponentsBuilder uriBuilder
	) {
		DataManufacturerDTO responseBody = service.createManufacturer(dto);
		var uri = uriBuilder
			.path("/manufacturers/{id}")
			.buildAndExpand(responseBody.getId())
			.toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DataManufacturerDTO> getOne(@PathVariable Long id) {
		return ResponseEntity.ok(service.getManufacturer(id));
	}

	@GetMapping
	public ResponseEntity<Page<DataManufacturerDTO>> getAllWithDiverseParams(
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String phone,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String contactPerson,
		@PageableDefault(size = 10) Pageable pageable
	) {
		Page<DataManufacturerDTO> dto = service.getAllManufacturers(
			name,
			phone,
			email,
			contactPerson,
			pageable);
		return ResponseEntity.ok(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DataManufacturerDTO> update(
		@PathVariable Long id, 
		@RequestBody @Valid UpdateManufacturerDTO dto
	) {
		return ResponseEntity.ok(service.updateManufacturer(id, dto));
	}
}
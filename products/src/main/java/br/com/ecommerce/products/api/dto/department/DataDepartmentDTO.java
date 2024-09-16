package br.com.ecommerce.products.api.dto.department;

import java.util.List;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataDepartmentDTO {

	private Long id;
	private String name;
	private List<SimpleDataCategoryDTO> categories;
}
package br.com.ecommerce.products.api.mapper;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.department.CreateDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.DataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.tools.factory.DepartmentFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DepartmentMapper {

    private final DepartmentFactory factory;


    public Department toDepartment(CreateDepartmentDTO data) {
        return Optional.ofNullable(data)
            .map(d -> factory.createDepartment(d.getName()))
            .orElse(null);
    }

    public DataDepartmentDTO toDataDepartmentDTO(Department data, List<SimpleDataCategoryDTO> categoriesData) {
        return new DataDepartmentDTO(
            data.getId(),
            data.getName(),
            categoriesData
        );
    }

    public SimpleDataDepartmentDTO toSimpleDataDepartmentDTO(Department data) {
        return Optional.ofNullable(data)
            .map(d -> new SimpleDataDepartmentDTO(d.getId(), d.getName()))
            .orElse(null);
    }
}
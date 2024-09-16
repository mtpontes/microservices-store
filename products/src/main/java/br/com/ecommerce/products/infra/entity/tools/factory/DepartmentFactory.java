package br.com.ecommerce.products.infra.entity.tools.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.department.Department;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DepartmentFactory {

    public Department createDepartment(String name) {
        return new Department(name);
    }
}
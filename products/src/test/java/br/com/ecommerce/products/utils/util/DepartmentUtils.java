package br.com.ecommerce.products.utils.util;

import java.util.List;

import org.springframework.boot.test.context.TestComponent;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.utils.factory.DepartmentTestFactory;

@TestComponent
public class DepartmentUtils {

    private RandomUtils utils = new RandomUtils();
    private DepartmentTestFactory factory = new DepartmentTestFactory();


    public Department getDepartmentInstance() {
        return factory.createDepartment(
            null,
            utils.getRandomString()
        );
    }

    public Department getDepartmentInstance(List<Category> categories) {
        return factory.createDepartment(
            null,
            utils.getRandomString(),
            categories
        );
    }

    public Department getDepartmentInstanceWithId() {
        return factory.createDepartment(
            utils.getRandomLong(),
            utils.getRandomString()
        );
    }

    public Department getDepartmentInstanceWithId(List<Category> categories) {
        return factory.createDepartment(
            utils.getRandomLong(),
            utils.getRandomString(),
            categories
        );
    }
}
package br.com.ecommerce.products.integration.api.controller.department;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;

@ControllerIntegrationTest
class DepartmentControllerIntegrationTest {

    private static List<Department> departmentsPersisted = new ArrayList<>();
    private static List<Category> categoriesPersisted = new ArrayList<>();
    private final String basePath = "/departments";
    private final String baseCategoryPath = basePath + "/categories";

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository
    ) {
        IntStream.range(0, 3)
            .mapToObj(flux -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                Category category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                categoryRepository.save(category);
                departmentRepository.save(department);

                departmentsPersisted.add(department);
                categoriesPersisted.add(category);
                return flux;
            })
            .toList();
    }


    @Test
    void getDepartmentTest01() throws Exception {
        // arrange
        Long departmentId = 1L;
        String path = String.format("%s/%s", basePath, departmentId);

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.categories").exists());
    }

    @Test
    void getAllDepartmentsTest01_withoutParams() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(departmentsPersisted.size())))
            .andExpect(jsonPath("$.content[0].id").isNumber())
            .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    void getAllDepartmentsTest02_withNameParam() throws Exception {
        // arrange
        String path = basePath;

        // act
        String paramName = departmentsPersisted.get(0).getName();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", paramName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @Test
    void getCategoryTest01() throws Exception {
        // arrange
        Long categoryId = 1L;
        String path = baseCategoryPath + "/" + categoryId;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void getAllCategoriesTest01_withoutParams() throws Exception {
        // arrange
        String path = baseCategoryPath;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(departmentsPersisted.size())))
            .andExpect(jsonPath("$.content[0].id").isNumber())
            .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    void getAllCategoriesTest02_withNameParam() throws Exception {
        // arrange
        String path = baseCategoryPath;

        // act
        String paramName = categoriesPersisted.get(0).getName();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", paramName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }
}
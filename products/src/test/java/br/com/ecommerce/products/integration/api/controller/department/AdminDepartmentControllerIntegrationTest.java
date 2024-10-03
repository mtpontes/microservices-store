package br.com.ecommerce.products.integration.api.controller.department;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
import br.com.ecommerce.products.api.dto.category.CreateCategoryDTO;
import br.com.ecommerce.products.api.dto.category.UpdateCategoryDTO;
import br.com.ecommerce.products.api.dto.department.CreateDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.UpdateDepartmentoDTO;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import br.com.ecommerce.products.utils.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerIntegrationTest
class AdminDepartmentControllerIntegrationTest {

    private static List<Department> departmentsPersisted = new ArrayList<>();
    private static List<Category> categoriesPersisted = new ArrayList<>();
    private final String basePath = "/admin/departments";
    private final String baseCategoryPath = basePath + "/categories";

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private RandomUtils randomUtils;

    @Autowired
    private JacksonTester<CreateDepartmentDTO> createDepartmentDTOJson;
    @Autowired
    private JacksonTester<UpdateDepartmentoDTO> updateDepartmentDTOJson;
    @Autowired
    private JacksonTester<CreateCategoryDTO> createCategoryDTOJson;
    @Autowired
    private JacksonTester<UpdateCategoryDTO> updateCategoryDTOJson;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository
    ) {
        IntStream.range(0, 3)
            .forEach(flux -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                Category category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                categoryRepository.save(category);
                departmentRepository.save(department);

                departmentsPersisted.add(department);
                categoriesPersisted.add(category);
                System.out.println("DEPARTAMENTOS: " + departmentRepository.count());
            });
    }


    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createDepartmentTest01_withValidData() throws Exception {
        // arrange
        String path = basePath;
        var requestBody = new CreateDepartmentDTO(randomUtils.getRandomString());

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createDepartmentDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createDepartmentTest02_withNameNull() throws Exception {
        // arrange
        String path = basePath;
        var requestBodyWithNameNull = new CreateDepartmentDTO(null);
        var requestBodyWithNameBlank = new CreateDepartmentDTO("");

        // act
        var requestMockWithNameNull = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createDepartmentDTOJson.write(requestBodyWithNameNull).getJson());
        ResultActions actForNameNull = mvc.perform(requestMockWithNameNull);
        var requestMockWithNameBlank = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createDepartmentDTOJson.write(requestBodyWithNameBlank).getJson());
        ResultActions actForNameBlank = mvc.perform(requestMockWithNameBlank);

        // assert
        actForNameNull
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.name").exists());
        actForNameBlank
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.name").exists());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void createDepartmentTest03_withUnauthorizedRoles() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMockWithNameNull = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMockWithNameNull);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateDepartmentTest01() throws Exception {
        // arrange
        System.out.println("AQUI: " + departmentsPersisted);
        System.out.println("AQUI: " + categoriesPersisted);

        Long departmentId = 1L;
        String path = basePath + "/" + departmentId;
        var requestBody = new UpdateDepartmentoDTO(randomUtils.getRandomString());

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateDepartmentDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updateDepartmentTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Long departmentId = 1L;
        String path = baseCategoryPath + "/" + departmentId;

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createCategoryTest01_withValidData() throws Exception {
        // arrange
        String path = baseCategoryPath;
        Long departmentId = departmentsPersisted.get(0).getId();
        var requestBody = new CreateCategoryDTO(departmentId, randomUtils.getRandomString());

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createCategoryDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createCategoryTest02_withInvalidValues() throws Exception {
        // arrange
        String path = baseCategoryPath;
        
        Long departmentId = null;
        var requestBodyWithNameNull = new CreateCategoryDTO(departmentId, null);
        var requestBodyWithNameBlank = new CreateCategoryDTO(departmentId, "");

        // act
        var requestMockWithNameNull = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createCategoryDTOJson.write(requestBodyWithNameNull).getJson());
        ResultActions actForNameNull = mvc.perform(requestMockWithNameNull);
        var requestMockWithNameBlank = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createCategoryDTOJson.write(requestBodyWithNameBlank).getJson());
        ResultActions actForNameBlank = mvc.perform(requestMockWithNameBlank);

        // assert
        actForNameNull
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.departmentId").exists())
            .andExpect(jsonPath("$.message.name").exists());
        actForNameBlank
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.departmentId").exists())
            .andExpect(jsonPath("$.message.name").exists());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void createCategoryTest03_withUnauthorizedRoles() throws Exception {
        // arrange
        String path = baseCategoryPath;

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateCategoryTest01() throws Exception {
        // arrange
        Long manufacturerId = 1L;
        String path = baseCategoryPath + "/" + manufacturerId;
        var requestBody = new UpdateCategoryDTO(randomUtils.getRandomString());

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCategoryDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updateCategoryTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Long manufacturerId = 1L;
        String path = baseCategoryPath + "/" + manufacturerId;

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }
}
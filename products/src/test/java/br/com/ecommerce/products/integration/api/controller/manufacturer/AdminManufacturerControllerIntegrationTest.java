package br.com.ecommerce.products.integration.api.controller.manufacturer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataAddressDTO;
import br.com.ecommerce.products.api.dto.manufacturer.UpdateManufacturerDTO;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;
import br.com.ecommerce.products.utils.util.RandomUtils;

@ControllerIntegrationTest
class AdminManufacturerControllerIntegrationTest {

    private static List<Manufacturer> manufacturersPersisted;
    private final String basePath = "/admin/manufacturers";

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private RandomUtils randomUtils;
    @Autowired
    private PhoneUtils phoneUtils;

    @Autowired
    private JacksonTester<CreateManufacturerDTO> createManufacturerDTOJson;
    @Autowired
    private JacksonTester<UpdateManufacturerDTO> updateManufacturerDTOJson;

    @BeforeAll
    static void setup(
        @Autowired ManufacturerRepository repository,
        @Autowired ManufacturerUtils manufacturerUtils,
        @Autowired PhoneUtils phoneUtils,
        @Autowired AddressUtils addressUtils
    ) {
        manufacturersPersisted = Stream.generate(() -> {
                Phone phone = phoneUtils.getPhoneInstance();
                Address address = addressUtils.getAddressInstance();
                return manufacturerUtils.getManufacturerInstance(phone, address);
            })
            .limit(3)
            .collect(Collectors.collectingAndThen(Collectors.toList(), result -> repository.saveAll(result)));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createTest01_withValidData() throws Exception {
        // arrange
        String path = basePath;
        var requestBody = new CreateManufacturerDTO(
            randomUtils.getRandomString(),
            phoneUtils.getRandomPhoneString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            new DataAddressDTO()
        );


        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createManufacturerDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()))
            .andExpect(jsonPath("$.phone").value(requestBody.getPhone()))
            .andExpect(jsonPath("$.email").value(requestBody.getEmail()))
            .andExpect(jsonPath("$.contactPerson").value(requestBody.getContactPerson()))
            .andExpect(jsonPath("$.address").isNotEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createTest02_withValidData() throws Exception {
        // arrange
        String path = basePath;
        var requestBody = new CreateManufacturerDTO(
            randomUtils.getRandomString(),
            null,
            null,
            null,
            null
        );


        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createManufacturerDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()))
            .andExpect(jsonPath("$.phone").isEmpty())
            .andExpect(jsonPath("$.email").isEmpty())
            .andExpect(jsonPath("$.contactPerson").isEmpty())
            .andExpect(jsonPath("$.address").isNotEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createTest03_withInvalidNameInput() throws Exception {
        // arrange
        String path = basePath;
        var requestBodyWithNameNull = new CreateManufacturerDTO(
            null,
            phoneUtils.getRandomPhoneString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            new DataAddressDTO()
        );
        var requestBodyWithNameBlank = new CreateManufacturerDTO(
            "",
            phoneUtils.getRandomPhoneString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            new DataAddressDTO()
        );

        // act
        var requestMockWithNameNull = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createManufacturerDTOJson.write(requestBodyWithNameNull).getJson());
        ResultActions actForNameNull = mvc.perform(requestMockWithNameNull);
        var requestMockWithNameBlank = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createManufacturerDTOJson.write(requestBodyWithNameBlank).getJson());
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
    void createTest01_withUnauthorizedRoles() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getOneTest() throws Exception {
        // arrange
        Long manufacturerId = manufacturersPersisted.get(0).getId();
        String path = String.format("%s/%s", basePath, manufacturerId);

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.phone").isNotEmpty())
            .andExpect(jsonPath("$.email").isNotEmpty())
            .andExpect(jsonPath("$.contactPerson").isNotEmpty())
            .andExpect(jsonPath("$.address").isNotEmpty());
    }

    @TestCustomWithMockUser(roles = {"CLIENT"})
    void getOneTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Long manufacturerId = manufacturersPersisted.get(0).getId();
        String path = String.format("%s/%s", basePath, manufacturerId);

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest01_withoutParams() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(manufacturersPersisted.size())))
            .andExpect(jsonPath("$.content[0].name").exists())
            .andExpect(jsonPath("$.content[0].phone").exists())
            .andExpect(jsonPath("$.content[0].email").exists())
            .andExpect(jsonPath("$.content[0].contactPerson").exists())
            .andExpect(jsonPath("$.content[0].address").exists());
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest02_withNameParam() throws Exception {
        // arrange
        String path = basePath;

        // act
        String paramName = manufacturersPersisted.get(0).getName();
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

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest03_withPhoneParam() throws Exception {
        // arrange
        String path = basePath;

        // act
        String phoneParam = manufacturersPersisted.get(0).getPhone();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("phone", phoneParam);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest04_withEmailParam() throws Exception {
        // arrange
        String path = basePath;

        // act
        String emailParam = manufacturersPersisted.get(0).getEmail();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("email", emailParam);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest05_withContactPersonParam() throws Exception {
        // arrange
        String path = basePath;

        // act
        String paramContactPerson = manufacturersPersisted.get(0).getContactPerson();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("contactPerson", paramContactPerson);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getAllWithDiverseParamsTest06_withAllParams() throws Exception {
        // arrange
        String path = basePath;

        // act
        String paramName = manufacturersPersisted.get(0).getName();
        String paramPhone = manufacturersPersisted.get(0).getPhone();
        String paramEmail = manufacturersPersisted.get(0).getEmail();
        String paramContactPerson = manufacturersPersisted.get(0).getContactPerson();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", paramName)
            .param("phone", paramPhone)
            .param("email", paramEmail)
            .param("contactPerson", paramContactPerson);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @TestCustomWithMockUser(roles = {"CLIENT"})
    void getAllWithDiverseParamsTest07_withUnauthorizedRoles() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateTest01_withValidData() throws Exception {
        // arrange
        Long manufacturerId = 1L;
        String path = String.format("%s/%s", basePath, manufacturerId);
        var requestBody = new UpdateManufacturerDTO(
            randomUtils.getRandomString(),
            phoneUtils.getRandomPhoneString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            new DataAddressDTO()
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateManufacturerDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName().toUpperCase()))
            .andExpect(jsonPath("$.phone").value(requestBody.getPhone()))
            .andExpect(jsonPath("$.email").value(requestBody.getEmail()))
            .andExpect(jsonPath("$.contactPerson").value(requestBody.getContactPerson()))
            .andExpect(jsonPath("$.address").isNotEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updateTest02_withInvalidData() throws Exception {
        // arrange
        Long manufacturerId = 1L;
        String path = String.format("%s/%s", basePath, manufacturerId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }
}
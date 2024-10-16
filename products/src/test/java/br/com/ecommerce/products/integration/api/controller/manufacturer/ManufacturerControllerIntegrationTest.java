package br.com.ecommerce.products.integration.api.controller.manufacturer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;

@ControllerIntegrationTest
class ManufacturerControllerIntegrationTest {

    private static List<Manufacturer> manufacturersPersisted;
    private final String basePath = "/manufacturers";

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;

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


    @Test
    void getAllTest01_withoutParams() throws Exception {
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
            .andExpect(jsonPath("$.content[0].id").isNumber())
            .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    void getAllTest02_withNameParam() throws Exception {
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

    @Test
    void getAllTest03_withContactPersonParam() throws Exception {
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

    @Test
    void getAllTest04_withAllParams() throws Exception {
        // arrange
        String path = basePath;

        // act
        String paramName = manufacturersPersisted.get(0).getName();
        String paramContactPerson = manufacturersPersisted.get(0).getContactPerson();
        int expectedResultSize = 1;
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", paramName)
            .param("contactPerson", paramContactPerson);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }
}
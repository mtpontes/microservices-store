package br.com.ecommerce.accounts.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.api.dto.AddressDTO;
import br.com.ecommerce.accounts.api.dto.CreateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.UpdateUserClientDTO;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;
import br.com.ecommerce.common.annotations.IdRolePair;
import br.com.ecommerce.common.utils.MockUserUtils;
import jakarta.transaction.Transactional;

/**
* Integration test for the {@link UserClient} controller.
* <p>
* The API persists a default user with ID {@code 1L} during initialization.
* The test persists an additional user with ID {@code 2L} in the {@code @BeforeAll} method.
* In the {@link IdRolePair} annotation, the ID {@code 2L} is used to reference the user
* persisted during test initialization.
* </p>
*/
@Transactional
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ClientControllerIntegrationTest {

    private static User clientPersisted = null;
    private final String basePath = "/client/account";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserClientDTO> userClientDTOJson;
    @Autowired
    private JacksonTester<UpdateUserClientDTO> updateUserClientDTOJson;

    @BeforeAll
    static void setup(
        @Autowired PasswordEncoder encoder,
        @Autowired UserRepository userRepository
    ) {
        clientPersisted = new UserBuilderTestUtils()
            .username("seed-san")
            .password(encoder.encode("password-seed-san!@123"))
            .name("Client-seed-san")
            .email("clientseed@email.com")
            .phoneNumber("+55 11 99999-9999")
            .cpf("653.037.960-60")
            .address(new Address(
                "street",
                "neighborhood",
                "postal",
                "number",
                "complement",
                "city",
                "SC"))
            .buildUserClient();
        userRepository.save(clientPersisted);
    }


    @Rollback
    @Test
    @DisplayName("Integration - create - Must return status 200 and user data")
    void createTest01() throws IOException, Exception {
        // arrange
        CreateUserClientDTO userClientDTO = new CreateUserClientDTO(
            "userclient-second",
            "password-san!@123",
            "Client-san",
            "client@email.com",
            "+55 11 99999-9998",
            "245.620.010-11",
            new AddressDTO(
                "street",
                "neighborhood",
                "postal",
                "number",
                "complement",
                "city",
                "SC"));

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userClientDTOJson.write(userClientDTO).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").isNotEmpty())
        .andExpect(jsonPath("$.email").isNotEmpty())
        .andExpect(jsonPath("$.phone_number").isNotEmpty())
        .andExpect(jsonPath("$.cpf").isNotEmpty());
    }

    @Test
    @Rollback
    @DisplayName("Integration - create with invalid data - Must return status 400 and fields with error")
    void createTest02() throws IOException, Exception {
        // arrange
        CreateUserClientDTO userClientDTO = new CreateUserClientDTO(
            "us",
            "password", // invalid format
            "",
            "clientemail.com", // invalid format
            "99999999", // invalid format
            "524.323.760-40", // invalid format
            new AddressDTO(
                "",
                "",
                "",
                "",
                "",
                "",
                ""));

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userClientDTOJson.write(userClientDTO).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message.username").exists())
        .andExpect(jsonPath("$.message.password").exists())
        .andExpect(jsonPath("$.message.name").exists())
        .andExpect(jsonPath("$.message.email").exists())
        .andExpect(jsonPath("$.message.phone_number").exists())
        .andExpect(jsonPath("$.message.['address.street']").exists())
        .andExpect(jsonPath("$.message.['address.neighborhood']").exists())
        .andExpect(jsonPath("$.message.['address.postal_code']").exists())
        .andExpect(jsonPath("$.message.['address.number']").exists())
        .andExpect(jsonPath("$.message.['address.complement']").exists())
        .andExpect(jsonPath("$.message.['address.city']").exists())
        .andExpect(jsonPath("$.message.['address.state']").exists());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "CLIENT")
    void updateCurrentClientDataTest01() throws IOException, Exception {
        // arrange
        MockUserUtils.mockUser(clientPersisted.getId().toString());

        var requestBody = new UpdateUserClientDTO(
            "email@email.com",
            "+55 47 98888-8888",
            new AddressDTO(
                "new street",
                "new neighborhood",
                "postalll",
                "123",
                "new complement",
                "new city",
                "SP")
            );

        // act
        mvc.perform(
            put(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserClientDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.email").value(requestBody.getEmail()))
        .andExpect(jsonPath("$.phone_number").value(requestBody.getPhone_number()))
        .andExpect(jsonPath("$.address.street").value(requestBody.getAddress().getStreet()))
        .andExpect(jsonPath("$.address.neighborhood").value(requestBody.getAddress().getNeighborhood()))
        .andExpect(jsonPath("$.address.postal_code").value(requestBody.getAddress().getPostal_code()))
        .andExpect(jsonPath("$.address.number").value(requestBody.getAddress().getNumber()))
        .andExpect(jsonPath("$.address.complement").value(requestBody.getAddress().getComplement()))
        .andExpect(jsonPath("$.address.city").value(requestBody.getAddress().getCity()))
        .andExpect(jsonPath("$.address.state").value(requestBody.getAddress().getState()));
    }

    @Test
    @Rollback
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Integration - updateCurrentClientData - Must return status 200 and non-updated user data")
    void updateCurrentClientDataTest02() throws IOException, Exception {
        // arrange
        MockUserUtils.mockUser(clientPersisted.getId().toString());

        var requestBody = new UpdateUserClientDTO(
            "",
            "",
            new AddressDTO(
                "",
                "",
                "",
                "",
                "",
                "",
                "")
            );

        // act
        mvc.perform(
            put(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserClientDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.email").isNotEmpty())
        .andExpect(jsonPath("$.phone_number").isNotEmpty())
        .andExpect(jsonPath("$.address.street").isNotEmpty())
        .andExpect(jsonPath("$.address.neighborhood").isNotEmpty())
        .andExpect(jsonPath("$.address.postal_code").isNotEmpty())
        .andExpect(jsonPath("$.address.number").isNotEmpty())
        .andExpect(jsonPath("$.address.complement").isNotEmpty())
        .andExpect(jsonPath("$.address.city").isNotEmpty())
        .andExpect(jsonPath("$.address.state").isNotEmpty());
    }

    @Test
    @Rollback
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateCurrentClientDataTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            put(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUserClientDataTest01_withoutAuthorization() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getCurrentUserClientDataTest01_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }
}
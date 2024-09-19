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
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;
import br.com.ecommerce.common.annotations.TestWithRoles;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ClientControllerIntegrationTest {

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
        User client = new UserBuilderTestUtils()
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
        userRepository.save(client);
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

    @Rollback
    @Test
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
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.password").exists())
        .andExpect(jsonPath("$.fields.name").exists())
        .andExpect(jsonPath("$.fields.email").exists())
        .andExpect(jsonPath("$.fields.phone_number").exists())
        .andExpect(jsonPath("$.fields.['address.street']").exists())
        .andExpect(jsonPath("$.fields.['address.neighborhood']").exists())
        .andExpect(jsonPath("$.fields.['address.postal_code']").exists())
        .andExpect(jsonPath("$.fields.['address.number']").exists())
        .andExpect(jsonPath("$.fields.['address.complement']").exists())
        .andExpect(jsonPath("$.fields.['address.city']").exists())
        .andExpect(jsonPath("$.fields.['address.state']").exists());
    }

    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Integration - updateCurrentClientData - Must return status 200 and user data")
    void updateCurrentClientDataTest01() throws IOException, Exception {
        // arrange
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
                .header("X-auth-user-id", "2")
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

    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Integration - updateCurrentClientData - Must return status 200 and non-updated user data")
    void updateCurrentClientDataTest02() throws IOException, Exception {
        // arrange
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
        String currentUserID = "2";
        mvc.perform(
            put(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateUserClientDTOJson.write(requestBody).getJson())
                .header("X-auth-user-id", currentUserID)
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

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void updateCurrentClientDataTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        String currentUserID = "2";
        mvc.perform(
            put(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", currentUserID)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void getCurrentUserClientDataTest01() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "")
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void getCurrentUserClientDataTest01_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "")
        )
        // assert
        .andExpect(status().isForbidden());
    }
}
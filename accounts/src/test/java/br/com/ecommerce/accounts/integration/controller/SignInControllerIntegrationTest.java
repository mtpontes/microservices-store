package br.com.ecommerce.accounts.integration.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.api.dto.SignInDTO;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.utils.TokenFormatValidatorUtils;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class SignInControllerIntegrationTest {

    private final String basePath = "/auth";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JacksonTester<SignInDTO> loginDTOJson;


    @Test
    @DisplayName("Integration - signIn - Must return status 200 and a valid token")
    void signInTest01() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = new UserBuilderTestUtils()
            .username(USERNAME)
            .password(encoder.encode(PASSWORD))
            .buildUser();
        repository.save(user);

        SignInDTO requestBody = new SignInDTO(USERNAME, PASSWORD);

        // act
        var result = mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn().getResponse();

        String responseBody = result.getContentAsString()
            .replaceAll(".*\"token\":\\s*\"(.*?)\".*", "$1");

        // assert
        assertTrue(TokenFormatValidatorUtils.isValidTokenFormat(responseBody));
    }

    @Test
    @DisplayName("Integration - signIn - Should return status 400 when username and password do not meet the expected format")
    void signInTest02() throws IOException, Exception {
        // arrange
        String USERNAME = "te";
        String PASSWORD = "te";
        User user = new UserBuilderTestUtils()
            .username(USERNAME)
            .password(encoder.encode(PASSWORD))
            .buildUser();
        repository.save(user);

        SignInDTO requestBody = new SignInDTO(USERNAME, PASSWORD);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.token").doesNotExist())
        .andExpect(jsonPath("$.fields.username").exists())
        .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    @DisplayName("Integration - signIn - Should return status 400 when user is not found")
    void signInTest03() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = new UserBuilderTestUtils()
            .username(USERNAME)
            .password(encoder.encode(PASSWORD))
            .buildUser();
        repository.save(user);
        
        String INVALID_USERNAME = "non-existent";
        SignInDTO requestBody = new SignInDTO(INVALID_USERNAME, PASSWORD);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("Integration - signIn - Should return 401 status when password does not match")
    void signInTest04() throws IOException, Exception {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "test@123";
        User user = new UserBuilderTestUtils()
            .username(USERNAME)
            .password(encoder.encode(PASSWORD))
            .buildUser();
        repository.save(user);
        
        SignInDTO requestBody = new SignInDTO(USERNAME, PASSWORD + "invalidatingPassword");

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.token").doesNotExist());
    }
}
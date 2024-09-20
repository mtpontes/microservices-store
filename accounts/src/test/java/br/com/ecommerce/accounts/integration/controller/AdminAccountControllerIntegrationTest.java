package br.com.ecommerce.accounts.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.api.dto.CreateUserEmployeeDTO;
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
class AdminAccountControllerIntegrationTest {

    private final String basePath = "/admin/account";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserEmployeeDTO> userEmployeeDTOJson;


    @Rollback
    @TestWithRoles(roles = {"ADMIN"})
    @DisplayName("Integration - createAdminUser - must return status 200 and user data")
    void createAdminUserTest01() throws IOException, Exception {
        // arrange
        CreateUserEmployeeDTO userEmployee = new CreateUserEmployeeDTO(
            "admin-san",
            "password-san!@123",
            "Admin-san");

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.username").isNotEmpty())
        .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN"})
    @DisplayName("Integration - createAdminUser - Must return status 400 and fields with error")
    void createAdminUserTest02() throws IOException, Exception {
        // arrange
        CreateUserEmployeeDTO userEmployee = new CreateUserEmployeeDTO(
            "ab",
            "password",
            "");

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message.username").exists())
        .andExpect(jsonPath("$.message.password").exists())
        .andExpect(jsonPath("$.message.name").exists());
    }

    @Rollback
    @TestWithRoles(roles = {"EMPLOYEE", "CLIENT"})
    void createAdminUserTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    @DisplayName("Integration - createEmployeeUser - must return status 200 and user data")
    void createEmployeeUserTest01() throws IOException, Exception {
        // arrange
        CreateUserEmployeeDTO userEmployee = new CreateUserEmployeeDTO(
            "userclient-san",
            "password-san!@123",
            "Client-san");

        // act
        mvc.perform(
            post(basePath + "/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.username").isNotEmpty())
        .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    @DisplayName("Integration - createEmployeeUser - Must return status 400 and fields with error")
    void createEmployeeUserTest02() throws IOException, Exception {
        // arrange
        CreateUserEmployeeDTO userEmployee = new CreateUserEmployeeDTO(
            "ab",
            "password",
            "");

        // act
        mvc.perform(
            post(basePath + "/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userEmployeeDTOJson.write(userEmployee).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message.username").exists())
        .andExpect(jsonPath("$.message.password").exists())
        .andExpect(jsonPath("$.message.name").exists());
    }

    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    void createEmployeeUserTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            post(basePath + "/employee")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }
}
package br.com.ecommerce.accounts.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.config.ContextualizeUserTypeWithRoles;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class AccountControllerIntegrationTest {

    private final String basePath = "/account";

    @Autowired
    private MockMvc mvc;


    @TestTemplate
    @ContextualizeUserTypeWithRoles(roles = {"CLIENT"})
    @DisplayName("Integration - getCurrentUser - Must return status 400 and fields with error")
    void getCurrentUserTest01() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "")
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestTemplate
    @ContextualizeUserTypeWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void getCurrentUserTest02_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "")
        )
        // assert
        .andExpect(status().isUnauthorized());
    }
}
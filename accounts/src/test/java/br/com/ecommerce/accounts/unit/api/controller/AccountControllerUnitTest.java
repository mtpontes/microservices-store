package br.com.ecommerce.accounts.unit.api.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.accounts.api.controller.AccountController;
import br.com.ecommerce.accounts.api.dto.DataUserDTO;
import br.com.ecommerce.accounts.business.service.UserService;
import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.common.utils.MockUserUtils;

@ActiveProfiles("test")
@AutoConfigureJsonTesters
@WebMvcTest(AccountController.class)
class AccountControllerUnitTest {

    private final String basePath = "/account";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    private final DataUserDTO userData = new DataUserDTO(1L, "Name", "username");


    @Test
    void getCurrentUserTest_withouAuthorization() throws IOException, Exception {
        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestCustomWithMockUser(roles = {"CLIENT", "EMPLOYEE", "ADMIN"})
    void getCurrentUserTest_allRolesIsAuthorized() throws IOException, Exception {
        // arrange
        MockUserUtils.mockUser("1");

        when(service.getCurrentUserData(anyLong()))
            .thenReturn(userData);

        // act
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isOk());
    }
}
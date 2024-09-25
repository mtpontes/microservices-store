package br.com.ecommerce.accounts.unit.business.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.service.TokenService;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.utils.TokenFormatValidatorUtils;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenServiceUnitTest {
	
    @InjectMocks
    private TokenService tokenService;

    private String secret = "testSecret";
    private final User user = new UserBuilderTestUtils()
        .username("default")
        .password("default")
        .buildUser();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }


    @Test
    void generateTokenTest() {
        // act
        var result = tokenService.generateToken(user.getUsername());

        // assert
        assertTrue(TokenFormatValidatorUtils.isValidTokenFormat(result));
    }
}
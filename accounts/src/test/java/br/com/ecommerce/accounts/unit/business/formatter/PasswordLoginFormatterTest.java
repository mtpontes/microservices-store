package br.com.ecommerce.accounts.unit.business.formatter;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.formatter.PasswordLoginFormatter;
import br.com.ecommerce.accounts.model.valueobjects.Login;

public class PasswordLoginFormatterTest {
    
    private PasswordEncoder encoder = new BCryptPasswordEncoder();
    private PasswordLoginFormatter formatter;

    @BeforeEach
    void setup() {
        formatter = new PasswordLoginFormatter(encoder);
    }

    
    @Test
    @DisplayName("Unit - It should format as expected")
    void formatTest() {
        // arrange
        String input = "123456789";
        Login login = new Login();
        ReflectionTestUtils.setField(login, "password", input);

        // act
        String result = formatter.format(login);

        // assert
        assertNotEquals(input, result);
        assertTrue(encoder.matches(input, result));
    }

    @Test
    @DisplayName("Unit - Should throw exception when String is blank")
    void formatTest02() {
        // arrange
        String input = "";
        Login login = new Login();
        ReflectionTestUtils.setField(login, "username", input);

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> formatter.format(login));
    }

    @Test
    @DisplayName("Unit - Should throw exception when String is null")
    void formatTest03() {
        // arrange
        Login login = new Login();

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> formatter.format(login));
    }
}
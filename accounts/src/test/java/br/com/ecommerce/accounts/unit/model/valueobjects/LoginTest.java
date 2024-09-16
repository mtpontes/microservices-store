package br.com.ecommerce.accounts.unit.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.ecommerce.accounts.business.formatter.PasswordLoginFormatter;
import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Login;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    
    @Mock
    private List<Validator<Login>> validators;
    @Mock
    private Validator<Login> validator;
    @Mock
    private Formatter<Login> formatter;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        validators = List.of(validator);
        formatter = new PasswordLoginFormatter(encoder);
    }


    @Test
    void testCreateLogin() {
        assertDoesNotThrow(() -> new Login());
        assertDoesNotThrow(() -> new Login(USERNAME, PASSWORD, validators, formatter));
    }

    @Test
    void testCreateLoginWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Login(null, PASSWORD, validators, formatter));

        assertThrows(
            IllegalArgumentException.class, 
            () -> new Login("", PASSWORD, validators, formatter));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Login(USERNAME, null, validators, formatter));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Login(USERNAME, "", validators, formatter));
        
        assertThrows(
            RuntimeException.class, 
            () -> new Login(USERNAME, PASSWORD, null, formatter));
        assertThrows(
            RuntimeException.class, 
            () -> new Login(USERNAME, PASSWORD, List.of(), formatter));

        assertThrows(
            RuntimeException.class, 
            () -> new Login(USERNAME, PASSWORD, validators, null));
    }

    @Test
    void testUpdatePassword() {
        Login login = new Login(USERNAME, PASSWORD, validators, formatter);
        String originalPassword = login.getPassword();

        login.updatePassword(PASSWORD, formatter);

        assertNotEquals(originalPassword, login.getPassword());
    }
}
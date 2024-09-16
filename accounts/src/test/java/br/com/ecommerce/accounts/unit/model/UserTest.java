package br.com.ecommerce.accounts.unit.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Login;

public class UserTest {

    private static final Login LOGIN = new Login();
    private static final String NAME = "default";
    private static final UserRole ROLE = UserRole.ADMIN;

    @Test
    void createUser() {
        assertDoesNotThrow(() -> new User());
        assertDoesNotThrow(() -> new User(LOGIN, NAME, UserRole.ADMIN));
    }

    @Test
    void createUserWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(null, NAME, ROLE));

        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(LOGIN, null, ROLE));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(LOGIN, "", ROLE));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(LOGIN, NAME, null));
    }
}
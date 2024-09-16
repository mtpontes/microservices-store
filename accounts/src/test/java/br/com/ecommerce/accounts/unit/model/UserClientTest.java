package br.com.ecommerce.accounts.unit.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;

public class UserClientTest {

    private static final String NAME = "default";
    private static final UserRole ROLE = UserRole.ADMIN;

    private static final CPF CPF = new CPF();
    private static final Login LOGIN = new Login();
    private static final Email EMAIL = new Email();
    private static final Address ADDRESS = new Address();
    private static final PhoneNumber PHONE_NUMBER = new PhoneNumber();


    @Test
    void createUser() {
        assertDoesNotThrow(() -> new User());
        assertDoesNotThrow(() -> new User(LOGIN, NAME, ROLE));
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

        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(null, null, null));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new User(null, "", null));
    }

    @Test
    void createUserClient() {
        assertDoesNotThrow(() -> new UserClient());
        assertDoesNotThrow(() -> new UserClient(LOGIN, NAME, EMAIL, PHONE_NUMBER, CPF, ADDRESS));
    }

    @Test
    void createUserClientWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(null, null, null, null, null, null));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(null, "", null, null, null, null));

        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(null, NAME, EMAIL, PHONE_NUMBER, CPF, ADDRESS));

        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, null, EMAIL, PHONE_NUMBER, CPF, ADDRESS));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, "", EMAIL, PHONE_NUMBER, CPF, ADDRESS));

        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, NAME, null, PHONE_NUMBER, CPF, ADDRESS));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, NAME, EMAIL, null, CPF, ADDRESS));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, NAME, EMAIL, PHONE_NUMBER, null, ADDRESS));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new UserClient(LOGIN, NAME, EMAIL, PHONE_NUMBER, CPF, null));
    }
}
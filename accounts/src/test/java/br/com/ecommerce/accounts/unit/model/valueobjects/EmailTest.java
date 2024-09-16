package br.com.ecommerce.accounts.unit.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Email;

@ExtendWith(MockitoExtension.class)
public class EmailTest {

    private static final String VALUE = "email";

    @Mock
    private List<Validator<Email>> validators;
    @Mock
    private Validator<Email> validator;

    @BeforeEach
    void setup() {
        validators = List.of(validator);
    }


    @Test
    void testCreateLogin() {
        assertDoesNotThrow(() -> new Email());
        assertDoesNotThrow(() -> new Email(VALUE, validators));
    }

    @Test
    void testCreateLoginWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Email(null, validators));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Email("", validators));

        assertThrows(
            RuntimeException.class, 
            () -> new Email(VALUE, null));
        assertThrows(
            RuntimeException.class, 
            () -> new Email(VALUE, List.of()));
    }
}
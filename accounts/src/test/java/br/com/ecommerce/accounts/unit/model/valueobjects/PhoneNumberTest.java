package br.com.ecommerce.accounts.unit.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;

@ExtendWith(MockitoExtension.class)
public class PhoneNumberTest {

    private static final String VALUE = "phone";

    @Mock
    private List<Validator<PhoneNumber>> validators;
    @Mock
    private Validator<PhoneNumber> validator;
    @Mock
    private Formatter<PhoneNumber> formatter;

    @BeforeEach
    void setup() {
        validators = List.of(validator);
    }


    @Test
    void testCreateLogin() {
        assertDoesNotThrow(() -> new PhoneNumber());
        assertDoesNotThrow(() -> new PhoneNumber(VALUE, validators, formatter));
    }

    @Test
    void testCreateLoginWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new PhoneNumber(null, validators, formatter));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new PhoneNumber("", validators, formatter));

        assertThrows(
            RuntimeException.class, 
            () -> new PhoneNumber(VALUE, null, formatter));
        assertThrows(
            RuntimeException.class, 
            () -> new PhoneNumber(VALUE, List.of(), formatter));
        
        assertThrows(
            RuntimeException.class, 
            () -> new PhoneNumber(VALUE, validators, null));
    }
}
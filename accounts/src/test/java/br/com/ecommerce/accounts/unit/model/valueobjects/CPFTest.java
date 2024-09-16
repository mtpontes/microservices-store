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
import br.com.ecommerce.accounts.model.valueobjects.CPF;

@ExtendWith(MockitoExtension.class)
public class CPFTest {

    private static final String VALUE = "cpf";

    @Mock
    private List<Validator<CPF>> validators;
    @Mock
    private Validator<CPF> validator;
    @Mock
    private Formatter<CPF> formatter;

    @BeforeEach
    void setup() {
        validators = List.of(validator);
    }


    @Test
    void testCreateLogin() {
        assertDoesNotThrow(() -> new CPF());
        assertDoesNotThrow(() -> new CPF(VALUE, validators, formatter));
    }

    @Test
    void testCreateLoginWithConstructor() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new CPF(null, validators, formatter));
        assertThrows(
            IllegalArgumentException.class, 
            () -> new CPF("", validators, formatter));

        assertThrows(
            RuntimeException.class, 
            () -> new CPF(VALUE, null, formatter));
        assertThrows(
            RuntimeException.class, 
            () -> new CPF(VALUE, List.of(), formatter));
        
        assertThrows(
            RuntimeException.class, 
            () -> new CPF(VALUE, validators, null));
    }
}
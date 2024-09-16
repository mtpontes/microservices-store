package br.com.ecommerce.accounts.integration.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.validator.UniqueCPFValidator;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UniqueCPFValidatorTest {

    @Autowired
    private UniqueCPFValidator validator;

    private static final String EXISTENT_CPF = "123.456.789-00";
    private static final String UNEXISTENT_CPF = "789.456.123-11";

    @BeforeAll
    static void setup(@Autowired UserRepository repository) {
        UserClient client = new UserBuilderTestUtils()
            .username("userclient-sasn")
            .password("password-san!")
            .phoneNumber("(47) 99999-9999")
            .email("client@email.com")
            .cpf(EXISTENT_CPF)
            .buildUserClient();

        repository.save(client);
    }


    @Test
    void validateTest01() {
        // arrange
        CPF cpf = new CPF();
        ReflectionTestUtils.setField(cpf, "value", EXISTENT_CPF);
 
        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> validator.validate(cpf));
    }

    @Test
    void validateTest02() {
        // arrange
        CPF cpf = new CPF();
        ReflectionTestUtils.setField(cpf, "value", UNEXISTENT_CPF);
 
        // act and assert
        assertDoesNotThrow(() -> validator.validate(cpf));
    }
}
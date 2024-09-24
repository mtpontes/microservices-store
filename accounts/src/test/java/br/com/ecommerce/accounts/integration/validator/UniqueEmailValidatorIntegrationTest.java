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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.validator.UniqueEmailValidator;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UniqueEmailValidatorIntegrationTest {

    @Autowired
    private UniqueEmailValidator validator;

    private static final String EXISTENT_EMAIL = "existent@email.com";
    private static final String UNEXISTENT_EMAIL = "unexistent@email.com";

    @BeforeAll
    static void setup(@Autowired UserRepository repository) {
        UserClient client = new UserBuilderTestUtils()
            .username("userclient-sasn")
            .password("password-san!")
            .phoneNumber("(47) 99999-9999")
            .email(EXISTENT_EMAIL)
            .cpf("123.456.789-00")
            .buildUserClient();

        repository.save(client);
    }


    @Test
    void validateTest01() {
        // arrange
        Email email = new Email();
        ReflectionTestUtils.setField(email, "value", EXISTENT_EMAIL);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> validator.validate(email));
    }

    @Test
    void validateTest02() {
        // arrange
        Email email = new Email();
        ReflectionTestUtils.setField(email, "value", UNEXISTENT_EMAIL);
 
        // act and assert
        assertDoesNotThrow(() -> validator.validate(email));
    }
}
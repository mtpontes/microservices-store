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

import br.com.ecommerce.accounts.business.validator.UniqueUsernameLoginValidator;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UniqueUsernameLoginValidatorIntegrationTest {

    @Autowired
    private UniqueUsernameLoginValidator validator;

    private static final String EXISTENT_USERNAME = "existent";
    private static final String UNEXISTENT_USERNAME = "unexistent";

    @BeforeAll
    static void setup(@Autowired UserRepository repository) {
        UserClient client = new UserBuilderTestUtils()
            .username(EXISTENT_USERNAME)
            .password("password-san!")
            .phoneNumber("(47) 99999-9999")
            .email("example@email.com")
            .cpf("123.456.789-00")
            .buildUserClient();

        repository.save(client);
    }


    @Test
    void validateTest01() {
        // arrange
        Login login = new Login();
        ReflectionTestUtils.setField(login, "username", EXISTENT_USERNAME);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> validator.validate(login));
    }

    @Test
    void validateTest02() {
        // arrange
        Login login = new Login();
        ReflectionTestUtils.setField(login, "username", UNEXISTENT_USERNAME);
 
        // act and assert
        assertDoesNotThrow(() -> validator.validate(login));
    }
}
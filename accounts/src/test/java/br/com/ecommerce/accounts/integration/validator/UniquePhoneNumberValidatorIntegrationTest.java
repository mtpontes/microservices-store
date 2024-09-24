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

import br.com.ecommerce.accounts.business.validator.UniquePhoneNumberValidator;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UniquePhoneNumberValidatorIntegrationTest {

    @Autowired
    private UniquePhoneNumberValidator validator;

    private static final String EXISTENT_PHONE = "(47) 99999-9999";
    private static final String UNEXISTENT_PHONE = "(48) 88888-8888";

    @BeforeAll
    static void setup(@Autowired UserRepository repository) {
        UserClient client = new UserBuilderTestUtils()
            .username("userclient-sasn")
            .password("password-san!")
            .phoneNumber(EXISTENT_PHONE)
            .email("client@email.com")
            .cpf("123.456.789-00")
            .buildUserClient();

        repository.save(client);
    }


    @Test
    void validateTest01() {
        // arrange
        PhoneNumber phone = new PhoneNumber();
        ReflectionTestUtils.setField(phone, "value", EXISTENT_PHONE);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> validator.validate(phone));
    }

    @Test
    void validateTest02() {
        // arrange
        PhoneNumber phone = new PhoneNumber();
        ReflectionTestUtils.setField(phone, "value", UNEXISTENT_PHONE);
 
        // act and assert
        assertDoesNotThrow(() -> validator.validate(phone));
    }
}
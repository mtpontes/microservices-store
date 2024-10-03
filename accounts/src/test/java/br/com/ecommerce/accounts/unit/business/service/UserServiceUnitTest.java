package br.com.ecommerce.accounts.unit.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.ecommerce.accounts.api.dto.AddressDTO;
import br.com.ecommerce.accounts.api.dto.CreateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.CreateUserEmployeeDTO;
import br.com.ecommerce.accounts.api.factory.UserFactory;
import br.com.ecommerce.accounts.api.mapper.AddressMapper;
import br.com.ecommerce.accounts.business.service.TokenProducer;
import br.com.ecommerce.accounts.business.service.UserService;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.utils.UserBuilderTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private PasswordEncoder encoder;
	@Mock
	private UserRepository repository;
    @Mock
    private UserFactory factory;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private TokenProducer tokenService;
	@InjectMocks
	private UserService service;


	@Test
    @DisplayName("Unit - createUserEmployee - Must create a user client")
    void saveUserCLientTest01() {
        // arrange
        UserClient client = new UserBuilderTestUtils()
            .username("userclient-san")
            .password("password-san")
            .name("Client-san")
            .email("client@email.com")
            .phoneNumber("(11) 99999-9999")
            .cpf("123.456.789-00")
            .id(1L)
            .role(UserRole.CLIENT)
            .buildUserClient();
        CreateUserClientDTO input = new CreateUserClientDTO(
            client.getUsername(),
            client.getPassword(),
            client.getName(),
            client.getEmail(),
            client.getPhone_number(),
            client.getCPF(),
            new AddressDTO());
        when(factory.createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
            .thenReturn(client);
        when(addressMapper.toAddress(any())).thenReturn(new Address());
        
        // act
        var result = service.saveClientUser(input);
        
        // assert
        assertFalse(result.getPhone_number().isBlank());
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getEmail(), result.getEmail());
        assertEquals(input.getCpf(), result.getCpf());
    }

    @Test
    @DisplayName("Unit - createUserEmployee - Must create a user employee")
    void createUserEmployeeTest01() {
        // arrange
        User user = new UserBuilderTestUtils()
            .username("useremployee-san")
            .password("password-san!")
            .name("Employee-san")
            .buildUser();
        CreateUserEmployeeDTO input = new CreateUserEmployeeDTO(
            user.getUsername(),
            user.getPassword(),
            user.getName());
        when(factory.createEmployee(anyString(), anyString(), anyString()))
            .thenReturn(user);
        when(repository.save(any())).thenReturn(user);

        // act
        var result = service.saveEmployeeUser(input);
        
        // assert
        assertEquals(input.getUsername(), result.getUsername());
        assertEquals(input.getName(), result.getName());
    }
}
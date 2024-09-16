package br.com.ecommerce.accounts.utils;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class UserBuilderTestUtils {

    private Long id;
    private String name;
    private String username;
    private String password;
    private UserRole role;
    private String email;
    private String phoneNumber;
    private String cpf;
    private Address address;

    public UserBuilderTestUtils id(Long id) {
        this.id = id;
        return this;
    }
    
    public UserBuilderTestUtils name(String nome) {
        this.name = nome;
        return this;
    }

    public UserBuilderTestUtils username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilderTestUtils password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilderTestUtils role(UserRole role) {
        this.role = role;
        return this;
    }
    
    public UserBuilderTestUtils email(String value) {
        this.email = value;
        return this;
    }

    public UserBuilderTestUtils phoneNumber(String value) {
        this.phoneNumber = value;
        return this;
    }

    public UserBuilderTestUtils cpf(String value) {
        this.cpf = value;
        return this;
    }

    public UserBuilderTestUtils address(Address address) {
        this.address = address;
        return this;
    }

    public UserClient buildUserClient() {
        Login loginvo = new Login();
        ReflectionTestUtils.setField(loginvo, "username", this.username);
        ReflectionTestUtils.setField(loginvo, "password", this.password);

        Email emailvo = new Email();
        ReflectionTestUtils.setField(emailvo, "value", this.email);

        PhoneNumber phonevo = new PhoneNumber();
        ReflectionTestUtils.setField(phonevo, "value", this.phoneNumber);

        CPF cpfvo = new CPF();
        ReflectionTestUtils.setField(cpfvo, "value", this.cpf);

        UserClient user = new UserClient();
        ReflectionTestUtils.setField(user, "id", this.id);
        ReflectionTestUtils.setField(user, "name", this.name);
        ReflectionTestUtils.setField(user, "login", loginvo);
        ReflectionTestUtils.setField(user, "role", this.role);
        ReflectionTestUtils.setField(user, "email", emailvo);
        ReflectionTestUtils.setField(user, "phone_number", phonevo);
        ReflectionTestUtils.setField(user, "cpf", cpfvo);
        ReflectionTestUtils.setField(user, "address", this.address);

        return user;
    }

    public User buildUser() {
        Login loginvo = new Login();
        ReflectionTestUtils.setField(loginvo, "username", this.username);
        ReflectionTestUtils.setField(loginvo, "password", this.password);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", this.id);
        ReflectionTestUtils.setField(user, "name", this.name);
        ReflectionTestUtils.setField(user, "login", loginvo);
        ReflectionTestUtils.setField(user, "role", this.role);

        return user;
    }
}
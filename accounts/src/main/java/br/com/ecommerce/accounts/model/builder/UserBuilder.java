package br.com.ecommerce.accounts.model.builder;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Component
public class UserBuilder {
    
    public UserClient createUserClient(
        Login login, 
        String name, 
        Email email, 
        PhoneNumber phoneNumber, 
        CPF cpf, 
        Address address
    ) {
        return new UserClient(
            login, 
            name, 
            email,
            phoneNumber,
            cpf,
            address
        );
    }

    public User createUserEmployee(Login login, String name) {
        return new User(login, name, UserRole.EMPLOYEE);
    }
    
    public User createUserAdmin(Login login, String name) {
        return new User(login, name, UserRole.ADMIN);
    }
}
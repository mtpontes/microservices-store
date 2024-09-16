package br.com.ecommerce.accounts.api.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.builder.UserBuilder;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserFactory {

    private UserBuilder builder;
    private CPFFactory cpfFactory;
    private PhoneNumberFactory phoneNumberFactory;
    private LoginFactory loginFactory;
    private EmailFactory emailFactory;


    public UserClient createClient(
        String username, 
        String password, 
        String name,
        String emailValue, 
        String phoneNumberValue, 
        String cpfValue, 
        Address address
    ) {
        return builder.createUserClient(
            this.getLogin(username, password), 
            name,
            this.getEmail(emailValue), 
            this.getPhoneNumber(phoneNumberValue),
            this.getCpf(cpfValue), 
            address);
    }

    public User createEmployee(String username, String password, String name) {
        return builder.createUserEmployee(
            this.getLogin(username, password), name);
    }

    public User createAdmin(String username, String password, String name) {
        return builder.createUserAdmin(
            this.getLogin(username, password), name);
    }


    private Login getLogin(String username, String password) {
        return loginFactory.createLogin(username, password);
    }
    private Email getEmail(String emailValue) {
        return emailFactory.createEmail(emailValue);
    }
    private PhoneNumber getPhoneNumber(String phoneNumberValue) {
        return phoneNumberFactory.createPhoneNumber(phoneNumberValue);
    }
    private CPF getCpf(String cpfValue) {
        return cpfFactory.createCPF(cpfValue);
    }
}
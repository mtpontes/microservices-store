package br.com.ecommerce.accounts.api.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class LoginFactory {

    private List<Validator<Login>> loginValidators;
    private Formatter<Login> loginFormatter;


    public Login createLogin(String username, String password) {
        return new Login(
            username, 
            password, 
            loginValidators, 
            loginFormatter);
    }
}
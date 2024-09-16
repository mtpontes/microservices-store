package br.com.ecommerce.accounts.api.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class EmailFactory {

	private List<Validator<Email>> emailValidators;


    public Email createEmail(String emailValue) {
        return new Email(emailValue, emailValidators);
    }
}
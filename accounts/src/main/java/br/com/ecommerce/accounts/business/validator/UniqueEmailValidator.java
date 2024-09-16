package br.com.ecommerce.accounts.business.validator;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UniqueEmailValidator implements Validator<Email> {
    
    private final UserRepository repository;

    @Override
    public void validate(Email email) {
        if (repository.existsByEmailValue(email.getValue()))
            throw new IllegalArgumentException("Email unavailable");
    }
}
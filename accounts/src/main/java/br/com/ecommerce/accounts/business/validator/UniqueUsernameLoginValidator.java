package br.com.ecommerce.accounts.business.validator;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UniqueUsernameLoginValidator implements Validator<Login> {
    
    private final UserRepository repository;

    public void validate(Login login) {
        if (repository.existsByLoginUsername(login.getUsername()))
            throw new IllegalArgumentException("Username unavailable");
    }
}
package br.com.ecommerce.accounts.business.validator;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UniquePhoneNumberValidator implements Validator<PhoneNumber> {

    private final UserRepository repository;


    @Override
    public void validate(PhoneNumber phone) {
        if (repository.existsByPhoneNumberValue(phone.getValue()))
            throw new IllegalArgumentException("Phone number unavailable");
    }
}
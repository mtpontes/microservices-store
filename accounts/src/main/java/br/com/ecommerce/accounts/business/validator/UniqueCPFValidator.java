package br.com.ecommerce.accounts.business.validator;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UniqueCPFValidator implements Validator<CPF> {

    private final UserRepository repository;


    @Override
    public void validate(CPF cpf) {
        if (repository.existsByCpfValue(cpf.getValue()))
            throw new IllegalArgumentException("CPF unavailable");
    }
}
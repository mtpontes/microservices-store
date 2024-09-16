package br.com.ecommerce.accounts.model.valueobjects;

import java.util.List;
import java.util.Optional;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Embeddable
public class CPF {
	
	private String value;

    public CPF(String value, List<Validator<CPF>> validators, Formatter<CPF> formatter) {
        value = Optional.ofNullable(value)
            .filter(v -> !v.isBlank())
            .orElseThrow(() -> new IllegalArgumentException("Cannot be blank/null: cpf"));
        
        Optional.ofNullable(validators)
            .filter(v -> !v.isEmpty())
            .orElseThrow(() -> new RuntimeException("Must provide one or more validators"));

        Optional.ofNullable(formatter)
            .orElseThrow(() -> new RuntimeException("Must provide one formatter"));

        this.value = value;

        validators.forEach(validator -> validator.validate(this));
        this.value = formatter.format(this);
    }
}
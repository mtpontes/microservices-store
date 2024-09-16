package br.com.ecommerce.accounts.model.valueobjects;

import java.util.List;
import java.util.Optional;

import br.com.ecommerce.accounts.model.interfaces.Validator;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Email {
	
	private String value;

    public Email(String value, List<Validator<Email>> validators) {
        value = Optional.ofNullable(value)
            .orElseThrow(() -> new IllegalArgumentException("Cannot be null: email"));
        
        if (value.isBlank()) 
            throw new IllegalArgumentException("Cannot be blank: email");
            
        if (validators == null || validators.isEmpty())
            throw new RuntimeException(
                "Must provide one or more validators");
        
        this.value = value;
        validators.forEach(validator -> validator.validate(this));
    }
}
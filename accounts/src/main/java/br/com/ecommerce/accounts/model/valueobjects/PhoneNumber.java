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
public class PhoneNumber {

	private String value;

    public PhoneNumber(String value, List<Validator<PhoneNumber>> validators, Formatter<PhoneNumber> formatter) {
        value = Optional.ofNullable(value)
            .orElseThrow(() -> new IllegalArgumentException("Cannot be null: phoneNumber"));
        if (value.isBlank()) 
            throw new IllegalArgumentException("Cannot be blank: phoneNumber");
        if (validators == null || validators.isEmpty())
            throw new RuntimeException("Must provide one or more validators");
        if (formatter == null)
            throw new RuntimeException("Must provide one formatter");

        this.value = value;

        validators.forEach(validator -> validator.validate(this));
        this.value = formatter.format(this);
    }
}
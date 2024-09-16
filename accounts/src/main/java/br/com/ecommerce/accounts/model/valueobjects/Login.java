package br.com.ecommerce.accounts.model.valueobjects;

import java.util.List;
import java.util.Optional;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Embeddable
public class Login {

	@Column(unique = true, nullable = false, length = 20)
	private String username;

	@Column(nullable = false)
	private String password;

    public Login(String username, String password, List<Validator<Login>> validators, Formatter<Login> formatter) {
        username = Optional.ofNullable(username)
            .orElseThrow(() -> new IllegalArgumentException("Cannot be null: username"));
        password = Optional.ofNullable(password)
            .orElseThrow(() -> new IllegalArgumentException("Cannot be null: password"));
        
        if (username.isBlank()) 
            throw new IllegalArgumentException("Cannot be blank: username");
        if (password.isBlank()) 
            throw new IllegalArgumentException("Cannot be blank: password");

        if (validators == null || validators.isEmpty())
            throw new RuntimeException("Must provide one or more validators");
        if (formatter == null)
            throw new RuntimeException("Must provide one formatter");

        this.password = password;
        this.username = username;

        this.password = formatter.format(this);
        validators.forEach(validator -> validator.validate(this));
    }

    public void updatePassword(String newPassword, Formatter<Login> formatter) {
        Optional.ofNullable(newPassword)
            .filter(p -> !p.isBlank())
            .ifPresent(p -> {
                this.password = p;
                this.password = formatter.format(this);
            });
    }
}
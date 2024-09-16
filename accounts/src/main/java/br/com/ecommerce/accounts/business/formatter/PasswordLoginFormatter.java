package br.com.ecommerce.accounts.business.formatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PasswordLoginFormatter implements Formatter<Login> {

    private final PasswordEncoder encoder;
    

    @Override
    public String format(Login login) {
        return encoder.encode(login.getPassword());
    }
}

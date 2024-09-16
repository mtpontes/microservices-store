package br.com.ecommerce.products.infra.entity.tools.factory;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.tools.interfaces.Formatter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PhoneFactory {

    private final Formatter<Phone> formatter;


    public Phone createPhone(String phone) {
        return Optional.ofNullable(phone)
            .filter(p -> !p.isBlank())
            .map(p -> new Phone(p, formatter))
            .orElse(new Phone());
    }
}
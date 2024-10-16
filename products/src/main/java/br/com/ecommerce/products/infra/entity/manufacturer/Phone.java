package br.com.ecommerce.products.infra.entity.manufacturer;

import java.io.Serializable;
import java.util.Optional;

import br.com.ecommerce.products.infra.entity.tools.interfaces.Formatter;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Embeddable
public class Phone implements Serializable {

    private String value;


    public Phone() {
        this.value = "";
    }

    public Phone(String phone, Formatter<Phone> formatter) {
        this.value = Optional.ofNullable(phone)
            .filter(p -> !p.isBlank())
            .orElseThrow(() -> new IllegalArgumentException("Cannot be null: phone"));

        this.value = Optional.ofNullable(formatter)
            .orElseThrow(() -> new IllegalArgumentException("Must provide a formatter"))
            .format(this);
    }
}
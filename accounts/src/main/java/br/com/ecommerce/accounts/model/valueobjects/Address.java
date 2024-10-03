package br.com.ecommerce.accounts.model.valueobjects;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Embeddable
public class Address {

    @Column(length = 100)
    private String street;

    @Column(length = 50)
    private String neighborhood;

    @Column(length = 10)
    private String postal_code;

    @Column(length = 10)
    private String number;

    @Column(length = 50)
    private String complement;

    @Column(length = 50)
    private String city;

    @Column(length = 2)
    private String state;

    public Address(
        String street, 
        String neighborhood, 
        String postal_code, 
        String number, 
        String complement, 
        String city, 
        String state
    ) {
        this.street = validateField(street, "street");
        this.neighborhood = validateField(neighborhood, "neighborhood");
        this.postal_code = validateField(postal_code, "postal_code");
        this.number = validateField(number, "number");
        this.complement = validateField(complement, "complement");
        this.city = validateField(city, "city");
        this.state = validateField(state, "state");
    }

    
    private String validateField(String field, String fieldName) {
        return Optional.ofNullable(field)
            .filter(f -> !f.isBlank())
            .orElseThrow(() -> new IllegalArgumentException("Cannot be blank: " + fieldName));
    }
}
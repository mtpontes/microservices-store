package br.com.ecommerce.products.infra.entity.manufacturer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Embeddable
public class Address {

    @Column(length = 100)
    private String street;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 50)
    private String country;

    @Column(length = 255)
    private String additionalInfo;

    public Address() {
        this.street = "";
        this.city = "";
        this.state = "";
        this.postalCode = "";
        this.country = "";
        this.additionalInfo = "";
    }
}
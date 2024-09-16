package br.com.ecommerce.products.unit.infra.entity.manufacturer;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;

class AddressTest {

    @Test
    void createAddress() {
        // arrange
        String street = "stree";
        String city = "city";
        String state = "state";
        String postalCode = "postalCode";
        String country = "country";
        String additionalInfo = "additionalInfo";

        // act
        Address result = new Address(
            street,
            city,
            state,
            postalCode,
            country,
            additionalInfo
        );

        // assert
        assertEquals(street, result.getStreet());
        assertEquals(city, result.getCity());
        assertEquals(state, result.getState());
        assertEquals(postalCode, result.getPostalCode());
        assertEquals(country, result.getCountry());
        assertEquals(additionalInfo, result.getAdditionalInfo());
    }
}
package br.com.ecommerce.products.utils.util;

import org.springframework.boot.test.context.TestComponent;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;

@TestComponent
public class AddressUtils {

    private RandomUtils utils = new RandomUtils();

    public Address getAddressInstance() {
        String street = utils.getRandomString();
        String city = utils.getRandomString(50);
        String state = utils.getRandomString(50);
        String postalCode = utils.getRandomString(20);
        String country = utils.getRandomString(50);
        String additionalInfo = utils.getRandomString(10);

        // act
        return new Address(
            street,
            city,
            state,
            postalCode,
            country,
            additionalInfo
        );
    }
}
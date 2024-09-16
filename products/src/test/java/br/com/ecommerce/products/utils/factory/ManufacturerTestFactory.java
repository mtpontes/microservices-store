package br.com.ecommerce.products.utils.factory;

import java.util.List;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.utils.builder.ManufacturerTestBuilder;

public class ManufacturerTestFactory {

    private ManufacturerTestBuilder builder = new ManufacturerTestBuilder();


    public Manufacturer createManufacturer(
        Long id,
        String name,
        Phone phone,
        String email,
        String contactPerson,
        Address address
    ) {
        return builder
            .id(id)
            .name(name)
            .phone(phone)
            .email(email)
            .contactPerson(contactPerson)
            .address(address)
            .build();
    }

    public Manufacturer createManufacturer(
        Long id,
        String name,
        Phone phone,
        String email,
        String contactPerson,
        Address address,
        List<Product> products
    ) {
        return builder
            .id(id)
            .name(name)
            .phone(phone)
            .email(email)
            .contactPerson(contactPerson)
            .address(address)
            .products(products)
            .build();
    }
}
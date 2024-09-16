package br.com.ecommerce.products.utils.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.product.Product;

public class ManufacturerTestBuilder {

    private Long id;
    private String name;
    private Phone phone;
    private String email;
    private String contactPerson;
    private Address address;
    private List<Product> products = new ArrayList<>();


    public ManufacturerTestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ManufacturerTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ManufacturerTestBuilder phone(Phone phone) {
        this.phone = phone;
        return this;
    }

    public ManufacturerTestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ManufacturerTestBuilder contactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        return this;
    }

    public ManufacturerTestBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public ManufacturerTestBuilder products(Product product) {
        this.products.add(product);
        return this;
    }

    public ManufacturerTestBuilder products(List<Product> products) {
        this.products.addAll(products);
        return this;
    }

    public Manufacturer build() {
        Manufacturer manufacturer = new Manufacturer();
        ReflectionTestUtils.setField(manufacturer, "id", this.id);
        ReflectionTestUtils.setField(manufacturer, "name", this.name);
        ReflectionTestUtils.setField(manufacturer, "phone", this.phone);
        ReflectionTestUtils.setField(manufacturer, "email", this.email);
        ReflectionTestUtils.setField(manufacturer, "contactPerson", this.contactPerson);
        ReflectionTestUtils.setField(manufacturer, "address", this.address);
        ReflectionTestUtils.setField(manufacturer, "products", this.products);

        return manufacturer;
    }
}
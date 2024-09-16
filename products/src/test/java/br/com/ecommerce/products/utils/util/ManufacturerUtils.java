package br.com.ecommerce.products.utils.util;

import org.springframework.boot.test.context.TestComponent;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.utils.factory.ManufacturerTestFactory;

@TestComponent
public class ManufacturerUtils {

    private RandomUtils utils = new RandomUtils();
    private ManufacturerTestFactory factory = new ManufacturerTestFactory();


    public Manufacturer getManufacturerInstance() {
        return factory.createManufacturer(
            null,
            utils.getRandomString(), 
            null, 
            utils.getRandomString(), 
            utils.getRandomString(), 
            null);
    }

    public Manufacturer getManufacturerInstance(Phone phone) {
        return factory.createManufacturer(
            null,
            utils.getRandomString(), 
            phone, 
            utils.getRandomString(), 
            utils.getRandomString(), 
            null);
    }

    public Manufacturer getManufacturerInstance(Phone phone, Address address) {
        return factory.createManufacturer(
            null,
            utils.getRandomString(), 
            phone, 
            utils.getRandomString(), 
            utils.getRandomString(), 
            address);
    }
}
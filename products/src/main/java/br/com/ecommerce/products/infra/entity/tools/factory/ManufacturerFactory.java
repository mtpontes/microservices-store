package br.com.ecommerce.products.infra.entity.tools.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ManufacturerFactory {

    private final PhoneFactory phoneFactory;


    public Manufacturer createManufacturer(		
        String name,
        String phone,
        String email,
        String contactPerson,
        Address address
    ) {
        return new Manufacturer(
            name, 
            phoneFactory.createPhone(phone), 
            email, 
            contactPerson, 
            address);
    }
}
package br.com.ecommerce.products.api.mapper;

import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataAddressDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.tools.factory.ManufacturerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class ManufacturerMapper {

    private final ManufacturerFactory factory;


    public Manufacturer toManufacturer(CreateManufacturerDTO data, Address address) {
        return factory.createManufacturer(
            data.getName(), 
            data.getPhone(),
            data.getEmail(), 
            data.getContactPerson(), 
            address
        );
    }

    public DataManufacturerDTO toDataManufacturerDTO(Manufacturer data, DataAddressDTO address) {
        return Optional.ofNullable(data)
            .map(m -> new DataManufacturerDTO(
                m.getId(), 
                m.getName(),
                m.getPhone(),
                m.getEmail(),
                m.getContactPerson(),
                address
            ))
            .orElse(null);
    }

    public SimpleDataManufacturerDTO toSimpleDataManufacturerDTO(
        Manufacturer data
    ) {
        return Optional.ofNullable(data)
            .map(m -> new SimpleDataManufacturerDTO(m.getId(), m.getName()))
            .orElse(null);
    }
}
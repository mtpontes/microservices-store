package br.com.ecommerce.products.api.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.manufacturer.DataAddressDTO;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;

@Component
public class AddressMapper {

    public Address toAddress(DataAddressDTO data) {
        return Optional.ofNullable(data)
            .map(a -> new Address(
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountry(),
                a.getAdditionalInfo()
            ))
            .orElse(new Address());
    }

    public DataAddressDTO toDataAddressDTO(Address data) {
        return Optional.ofNullable(data)
            .map(a -> new DataAddressDTO(
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountry(),
                a.getAdditionalInfo()
            ))
            .orElse(null);
    }
}
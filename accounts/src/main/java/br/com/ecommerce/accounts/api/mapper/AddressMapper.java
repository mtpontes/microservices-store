package br.com.ecommerce.accounts.api.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.api.dto.AddressDTO;
import br.com.ecommerce.accounts.model.valueobjects.Address;

@Component
public class AddressMapper {

    public Address toAddress(AddressDTO dto) {
		return Optional.ofNullable(dto)
			.map(data -> new Address(
				dto.getStreet(),
				dto.getNeighborhood(),
				dto.getPostal_code(),
				dto.getNumber(),
				dto.getComplement(),
				dto.getCity(),
				dto.getState()))
			.orElse(null);
    }

    public AddressDTO toAddressDTO(Address data) {
		return Optional.ofNullable(data)
			.map(d -> new AddressDTO(
				d.getStreet(),
				d.getNeighborhood(),
				d.getPostal_code(),
				d.getNumber(),
				d.getComplement(),
				d.getCity(),
				d.getState()))
			.orElse(null);
    }
}

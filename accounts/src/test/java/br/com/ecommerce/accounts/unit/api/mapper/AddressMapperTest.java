package br.com.ecommerce.accounts.unit.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.accounts.api.dto.AddressDTO;
import br.com.ecommerce.accounts.api.mapper.AddressMapper;

public class AddressMapperTest {

	private final AddressMapper mapper = new AddressMapper();

	
	@Test
	void toAdressTest() {
		// arrange
		var input = new AddressDTO(
            "null", 
            "null", 
            "null", 
            "null", 
            "null", 
            "null", 
            "sc");

		// act
		var result = mapper.toAddress(input);

		assertEquals(input.getStreet(), result.getStreet());
		assertEquals(input.getNeighborhood(), result.getNeighborhood());
		assertEquals(input.getPostal_code(), result.getPostal_code());
		assertEquals(input.getNumber(), result.getNumber());
		assertEquals(input.getComplement(), result.getComplement());
		assertEquals(input.getCity(), result.getCity());
		assertEquals(input.getState(), result.getState());
	}
}

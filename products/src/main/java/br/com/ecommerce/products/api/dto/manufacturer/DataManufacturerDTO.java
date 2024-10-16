package br.com.ecommerce.products.api.dto.manufacturer;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataManufacturerDTO implements Serializable {

	private Long id;
	private String name;
	private String phone;
	private String email;
	private String contactPerson;
	private DataAddressDTO address;
}
package br.com.ecommerce.accounts.api.dto;

import br.com.ecommerce.accounts.model.valueobjects.Address;
import jakarta.validation.constraints.NotBlank;
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
public class AddressDTO {

    @NotBlank
    String street;
    @NotBlank
    String neighborhood;
    @NotBlank
    String postal_code;
    @NotBlank
    String number;
    @NotBlank
    String complement;
    @NotBlank
    String city;
    @NotBlank
    String state;

    public AddressDTO(Address address) {
        this.street = address.getStreet();
        this.neighborhood = address.getNeighborhood();
        this.postal_code = address.getPostal_code();
        this.number = address.getNumber();
        this.complement = address.getComplement();
        this.city = address.getCity();
        this.state = address.getState();
    }
}
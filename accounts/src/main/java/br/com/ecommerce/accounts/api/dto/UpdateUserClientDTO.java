package br.com.ecommerce.accounts.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserClientDTO {

    private String email;
    private String phone_number;
    private AddressDTO address;
}
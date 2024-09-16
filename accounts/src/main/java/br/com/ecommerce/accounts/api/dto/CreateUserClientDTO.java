package br.com.ecommerce.accounts.api.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserClientDTO {

    @Size(min = 3, max = 20, message = "Size must be between 8 and 20 characters")
    private String username;

    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "The password must contain at least one letter, one special character and be at least 8 characters long")
    private String password;

    @NotBlank
    private String name;

    @Email
    private String email;

    @Size(min = 11, max = 19, message = "Size must be between 11 and 19 characters")
    private String phone_number;

    @CPF
    private String cpf;

    @NotNull
    @Valid
    private AddressDTO address;
}
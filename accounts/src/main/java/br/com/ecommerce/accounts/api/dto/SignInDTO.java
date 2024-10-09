package br.com.ecommerce.accounts.api.dto;

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
public class SignInDTO {

    @Size(min = 3, message = "Cannot be less than 3 characters")
    private String username;

    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "The password must contain at least one letter, one special character and be at least 8 characters long")
    private String password;
}
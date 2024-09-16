package br.com.ecommerce.accounts.api.dto;

import br.com.ecommerce.accounts.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployeeCreatedDTO {

    private Long id;
    private String username;
    private String name;

    public UserEmployeeCreatedDTO(User user) {
        this(user.getId(), user.getUsername(), user.getName());
    }
}
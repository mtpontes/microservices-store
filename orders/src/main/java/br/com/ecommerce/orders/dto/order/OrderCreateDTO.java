package br.com.ecommerce.orders.dto.order;

import java.util.List;

import br.com.ecommerce.orders.dto.product.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    @NotEmpty
    @Valid
    private List<ProductDTO> products;
}
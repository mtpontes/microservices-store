package br.com.ecommerce.products.api.dto.category;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDataCategoryDTO implements Serializable {

    private Long id;
    private String name;
}
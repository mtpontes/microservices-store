package br.com.ecommerce.products.business.validator;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.tools.interfaces.Validator;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UniqueNameCategoryValidator implements Validator<String> {

    private final CategoryRepository repository;


    @Override
    public void validate(String param) {
        if (repository.existsByName(param))
            throw new IllegalArgumentException("Invalid category name");
    }
}
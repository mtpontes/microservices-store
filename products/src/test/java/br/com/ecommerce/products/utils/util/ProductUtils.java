package br.com.ecommerce.products.utils.util;

import org.springframework.boot.test.context.TestComponent;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;
import br.com.ecommerce.products.utils.factory.ProductTestFactory;

@TestComponent
public class ProductUtils {

    private RandomUtils utils = new RandomUtils();
    private ProductTestFactory factory = new ProductTestFactory();


    public Product getProductInstance() {
        return factory.createProduct(
            null,
            utils.getRandomString(),
            utils.getRandomString(),
            utils.getRandomString(),
            null,
            null,
            null,
            null
        );
    }

    public Product getProductInstance(
        Stock stock,
        Category category,
        Manufacturer mf
    ) {
        return factory.createProduct(
            null,
            utils.getRandomString(),
            utils.getRandomString(),
            utils.getRandomString(),
            null,
            category,
            stock,
            mf
        );
    }

    public Product getProductInstance(
        Price price,
        Stock stock,
        Category category,
        Manufacturer mf
    ) {
        return factory.createProduct(
            null,
            utils.getRandomString(),
            utils.getRandomString(),
            utils.getRandomString(),
            price,
            category,
            stock,
            mf
        );
    }

    public Product getProductInstanceWithId(
        Price price,
        Stock stock,
        Category category,
        Manufacturer mf
    ) {
        return factory.createProduct(
            utils.getRandomLong(),
            utils.getRandomString(),
            utils.getRandomString(),
            utils.getRandomString(),
            price,
            category,
            stock,
            mf
        );
    }
}
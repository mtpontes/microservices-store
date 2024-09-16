package br.com.ecommerce.products.utils.builder;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;

public class ProductTestBuilder {

    private Long id;
    private String name;
    private String description;
    private String specs;
    private Price price;
    private Stock stock;
    private Category category;
    private Manufacturer manufacturer;


    public ProductTestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ProductTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductTestBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductTestBuilder specs(String specs) {
        this.specs = specs;
        return this;
    }

    public ProductTestBuilder price(Price price) {
        this.price = price;
        return this;
    }

    public ProductTestBuilder stock(Stock stock) {
        this.stock = stock;
        return this;
    }

    public ProductTestBuilder category(Category category) {
        this.category = category;
        return this;
    }

    public ProductTestBuilder manufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public Product build() {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", this.id);
        ReflectionTestUtils.setField(product, "name", this.name);
        ReflectionTestUtils.setField(product, "description", this.description);
        ReflectionTestUtils.setField(product, "specs", this.specs);
        ReflectionTestUtils.setField(product, "price", this.price);
        ReflectionTestUtils.setField(product, "stock", this.stock);
        ReflectionTestUtils.setField(product, "category", this.category);
        ReflectionTestUtils.setField(product, "manufacturer", this.manufacturer);
        return product;
    }
}
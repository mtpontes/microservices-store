package br.com.ecommerce.products.utils.factory;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;
import br.com.ecommerce.products.utils.builder.ProductTestBuilder;

public class ProductTestFactory {

	private ProductTestBuilder builder = new ProductTestBuilder();


	public Product createProduct(
		Long id,
		String name, 
		String description, 
		String specs,
		Price price, 
		Category category, 
		Stock stock, 
		Manufacturer manufacturer
	) {
		return builder
			.id(id)
			.name(name)
			.description(description)
			.specs(specs)
			.price(price)
			.stock(stock)
			.category(category)
			.manufacturer(manufacturer)
			.build();
	}
}
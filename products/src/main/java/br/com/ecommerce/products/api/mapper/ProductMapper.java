package br.com.ecommerce.products.api.mapper;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.api.dto.product.*;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.tools.factory.ProductFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class ProductMapper {

    private final ProductFactory factory;


    public Product toProduct(CreateProductDTO dto, Category category, Manufacturer manufacturer) {
        return factory.createProduct(
            dto.getName(),
            dto.getDescription(),
            dto.getSpecs(),
            category,
            manufacturer
        );
    }

    public DataProductDTO toDataProductDTO(
            Product data,
            SimplePriceDataDTO priceData,
            DataStockDTO stockData,
            SimpleDataCategoryDTO categoryData,
            SimpleDataManufacturerDTO manufacturerData) {
        return Optional.ofNullable(data)
            .map(p -> new DataProductDTO(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getSpecs(),
                priceData,
                stockData,
                categoryData,
                manufacturerData))
            .orElse(null);
    }

    public UpdateProductResponseDTO toProductUpdateResponseDTO(Product data) {
        return Optional.ofNullable(data)
            .map(p -> new UpdateProductResponseDTO(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getSpecs()
            ))
            .orElse(null);
    }

    public UpdateProductPriceResponseDTO toUpdateProductPriceResponseDTO(Product data, CompletePriceDataDTO priceData) {
        return Optional.ofNullable(data)
            .map(p -> new UpdateProductPriceResponseDTO(
                data.getId(),
                data.getName(),
                priceData
            ))
            .orElse(null);
    }

    public DataProductPriceDTO toProductPriceDTO(Product data) {
        return Optional.ofNullable(data)
            .map(p -> new DataProductPriceDTO(p.getId(), p.getPrice().getCurrentPrice()))
            .orElse(null);
    }
}
package br.com.ecommerce.products.api.mapper.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.SimplePriceDataDTO;
import br.com.ecommerce.products.api.mapper.CategoryMapper;
import br.com.ecommerce.products.api.mapper.ManufacturerMapper;
import br.com.ecommerce.products.api.mapper.PriceMapper;
import br.com.ecommerce.products.api.mapper.StockMapper;
import br.com.ecommerce.products.infra.entity.product.Product;

@Component
public class ProductDTOFactory {

    private final PriceMapper priceMapper;
    private final StockMapper stockMapper;
    private final CategoryMapper categoryMapper;
    private final ManufacturerMapper manufacturerMapper;

    public ProductDTOFactory(PriceMapper priceMapper, StockMapper stockMapper, CategoryMapper categoryMapper, ManufacturerMapper manufacturerMapper) {
        this.priceMapper = priceMapper;
        this.stockMapper = stockMapper;
        this.categoryMapper = categoryMapper;
        this.manufacturerMapper = manufacturerMapper;
    }

    public DataProductDTO createDataProductDTO(Product product) {
        SimplePriceDataDTO priceData = priceMapper.toSimplePriceDataDTO(product.getPrice());
        DataStockDTO stockData = stockMapper.toDataStockDTO(product.getStock());
        SimpleDataCategoryDTO categoryData = categoryMapper.toSimpleDataCategoryDTO(product.getCategory());
        SimpleDataManufacturerDTO manufacturerData = manufacturerMapper.toSimpleDataManufacturerDTO(product.getManufacturer());

        return new DataProductDTO(
            product.getId(), 
            product.getName(), 
            product.getDescription(), 
            product.getSpecs(), 
            priceData, 
            stockData, 
            product.getImages(),
            categoryData, 
            manufacturerData);
    }
}
package br.com.ecommerce.products.api.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.product.DataProductStockDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;

@Component
public class StockMapper {

    public Stock toStock(DataStockDTO data) {
        return Optional.ofNullable(data)
            .map(s -> new Stock(data.getUnit()))
            .orElse(null);
    }

    public DataStockDTO toDataStockDTO(Stock data) {
        return Optional.ofNullable(data)
            .map(s -> new DataStockDTO(s.getUnit()))
            .orElse(null);
    }

    public DataProductStockDTO toDataProductStock(Product data) {
        return Optional.ofNullable(data)
            .map(p -> new DataProductStockDTO(
                data.getId(), 
                data.getName(), 
                data.getStock().getUnit()))
            .orElse(null);
    }
}
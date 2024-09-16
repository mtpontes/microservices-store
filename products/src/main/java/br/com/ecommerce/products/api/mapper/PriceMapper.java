package br.com.ecommerce.products.api.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.product.CompletePriceDataDTO;
import br.com.ecommerce.products.api.dto.product.SimplePriceDataDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.infra.entity.product.Price;

@Component
public class PriceMapper {

    public Price toPrice(UpdatePriceDTO data) {
        return Optional.ofNullable(data)
            .map(p -> new Price(p.getOriginalPrice(), data.getPromotionalPrice()))
            .orElse(null);
    }

    public CompletePriceDataDTO toCompletePriceDataDTO(Price data) {
        return Optional.ofNullable(data)
            .map(p -> new CompletePriceDataDTO(
                p.getCurrentPrice(),
                p.getOriginalPrice(), 
                p.getPromotionalPrice(), 
                p.isOnPromotion(),
                p.getEndOfPromotion()))
            .orElse(null);
    }

    public SimplePriceDataDTO toSimplePriceDataDTO(Price data) {
        return Optional.ofNullable(data)
            .map(p -> new SimplePriceDataDTO(
                p.getCurrentPrice(),
                p.getOriginalPrice(), 
                data.isOnPromotion(),
                data.getEndOfPromotion()))
            .orElse(null);
    }
}
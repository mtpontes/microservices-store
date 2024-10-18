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
        return new Price(data.getPrice());
    }

    public Price toPriceWithPromotionalPrice(Price origin, UpdatePriceDTO data) {
        Price price = new Price(origin.getOriginalPrice());
        price.setPromotionalPrice(data.getPrice());
        return price;
    }

    public CompletePriceDataDTO toCompletePriceDataDTO(Price data) {
        return Optional.ofNullable(data)
            .map(p -> new CompletePriceDataDTO(
                p.getCurrentPrice(),
                p.getOriginalPrice(), 
                p.getPromotionalPrice(), 
                p.isOnPromotion(),
                p.getStartPromotion(),
                p.getEndPromotion()))
            .orElse(null);
    }

    public SimplePriceDataDTO toSimplePriceDataDTO(Price data) {
        return Optional.ofNullable(data)
            .map(p -> new SimplePriceDataDTO(
                p.getCurrentPrice(),
                p.getOriginalPrice(), 
                data.isOnPromotion(),
                data.getStartPromotion(),
                data.getEndPromotion()))
            .orElse(null);
    }
}
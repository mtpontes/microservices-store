package br.com.ecommerce.products.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimplePriceDataDTO {

    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private boolean isOnPromotion;
    private LocalDateTime endOfPromotion;
}
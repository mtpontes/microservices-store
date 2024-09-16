package br.com.ecommerce.products.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompletePriceDataDTO {

    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private BigDecimal promotionalPrice;
    private boolean onPromotion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endOfPromotion;
}
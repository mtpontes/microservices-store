package br.com.ecommerce.products.api.dto.product;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EndOfPromotionDTO {

    @NotNull
    private LocalDateTime endOfPromotion;
}
package br.com.ecommerce.products.utils.util;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.product.Price;

@TestComponent
public class PriceUtils {

    private Random random = new Random();


    public Price getPriceInstance() {
        BigDecimal originalPrice = this.getRandomPrice();
        BigDecimal promotionalPrice = originalPrice.subtract(BigDecimal.ONE);
        Price price = new Price();
        ReflectionTestUtils.setField(price, "originalPrice", originalPrice);
        ReflectionTestUtils.setField(price, "promotionalPrice", promotionalPrice);
        ReflectionTestUtils.setField(price, "currentPrice", originalPrice);
        return price;
    }

    private BigDecimal getRandomPrice() {
        return BigDecimal.valueOf(random.nextInt(99) + 2);
    }
}
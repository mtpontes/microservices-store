package br.com.ecommerce.cart.infra.entity;

import java.util.Objects;
import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Product {
    
	private static final int MINIMAL_UNIT = 1;

	private String id;
	private Integer unit;

	public Product(String id, Integer unit) {
		this.id = this.checkNotBlank(id, "product ID");
		this.unit = this.checkUnit(unit);
	}

	
	private String checkNotBlank(String name, String filedName) {
		return Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + filedName));
	}

	private Integer checkUnit(Integer unit) {
		return Optional.ofNullable(unit)
			.filter(units -> units.compareTo(MINIMAL_UNIT) >= 0)
			.orElseThrow(() -> new IllegalArgumentException("Unit cannot be lower than: " + MINIMAL_UNIT));
	}

	public void addUnit(Integer unit) {
		if ((this.unit + unit) < 0) {
			this.unit = 0;
			return;
		}
		this.unit += unit;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
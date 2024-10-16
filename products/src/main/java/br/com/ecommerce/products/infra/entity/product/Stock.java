package br.com.ecommerce.products.infra.entity.product;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
@Embeddable
public class Stock implements Serializable {

	@Column(nullable = false)
	private Integer unit;

	public Stock() {
		this.unit = 0;
	}

	public Stock(Integer quantity) {
		if (quantity == null || quantity < 0) 
			throw new IllegalArgumentException("The quantity sold must be positive");
		this.unit = quantity;
	}


	public void update(Integer quantity) {
		if (quantity == null) return;

		if ((this.unit + quantity) < 0) {
			this.unit = 0;
			return;
		}
		this.unit += quantity;
	}
}
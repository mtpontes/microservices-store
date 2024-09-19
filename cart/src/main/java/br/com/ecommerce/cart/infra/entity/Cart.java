package br.com.ecommerce.cart.infra.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document
@Getter
@ToString
@NoArgsConstructor
public class Cart {

    @Id
    private String userId;
    private Set<Product> products = new HashSet<>();
    private boolean isAnon;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Cart(String userId) {
        this.userId = this.checkId(userId);
        this.createdAt = LocalDateTime.now();
        this.updateModifiedAt();
        this.isAnon = false;
    }

    public Cart(Product product) {
        this.userId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updateModifiedAt();
        this.isAnon = true;
        this.addProduct(product);
    }


    public void addProduct(Product product) {
        if (this.products.contains(product)) {
            this.products.stream()
                .filter(p -> p.equals(product))
                .findFirst().get()
                .addUnit(product.getUnit());
            this.updateModifiedAt();
            return;
        }
        this.products.add(product);
        this.updateModifiedAt();
    }
    
    public void addProducts(Set<Product> products) {
        Optional.ofNullable(products)
            .filter(set -> !set.isEmpty())
            .ifPresent(set -> this.products.addAll(products));
        this.updateModifiedAt();
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        this.updateModifiedAt();
    }

    private void updateModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }

    private String checkId(String userId) {
        return Optional.ofNullable(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart ID cannot be null"));
    }
}
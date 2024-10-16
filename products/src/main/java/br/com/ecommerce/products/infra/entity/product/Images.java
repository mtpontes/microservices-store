package br.com.ecommerce.products.infra.entity.product;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Images implements Serializable {

    private String mainImage;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> additionalImages = new HashSet<>();

    public Images(String mainImagem, Set<String> images) {
        this.addAdditionalImages(images);
        this.setMainImage(mainImagem);
    }


    public void setMainImage(String mainImage) {
        this.mainImage = Optional.ofNullable(mainImage)
            .filter(image -> !image.isBlank())
            .or(() -> this.additionalImages.stream().findFirst())
            .filter(set -> !set.isBlank())
            .orElse(null);
    }

    public void addAdditionalImages(Set<String> images) {
        images.stream()
            .filter(link -> !link.isBlank())
            .collect(Collectors.collectingAndThen(
                Collectors.toSet(), this.additionalImages::addAll));

        if (this.mainImage == null) this.updateMainImage();
    }

    public void remove(Set<String> imageLinks) {
        this.additionalImages.removeAll(imageLinks);
        if (imageLinks.contains(this.mainImage)) this.updateMainImage();
    }

    private void updateMainImage() {
        this.mainImage = this.additionalImages.stream().findFirst().orElse(null);
    }
}
package br.com.ecommerce.products.business.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.api.dto.product.CompletePriceDataDTO;
import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductStockDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.products.api.dto.product.ProductUnitsRequestedDTO;
import br.com.ecommerce.products.api.dto.product.SimplePriceDataDTO;
import br.com.ecommerce.products.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductImagesResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductPriceResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductResponseDTO;
import br.com.ecommerce.products.api.mapper.CategoryMapper;
import br.com.ecommerce.products.api.mapper.ManufacturerMapper;
import br.com.ecommerce.products.api.mapper.PriceMapper;
import br.com.ecommerce.products.api.mapper.ProductMapper;
import br.com.ecommerce.products.api.mapper.StockMapper;
import br.com.ecommerce.products.business.validator.UniqueNameProductValidator;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.exception.exceptions.ProductNotFoundException;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ManufacturerRepository manufacturerRepository;

	private final PriceMapper priceMapper;
	private final StockMapper stockMapper;
	private final ProductMapper productMapper;
	private final CategoryMapper categoryMapper;
	private final ManufacturerMapper manufacturerMapper;

	private final UniqueNameProductValidator uniqueNameValidator;

	private final PriceJobService scheduler;


	public DataProductDTO getProduct(Long id) {
		return productRepository.findById(id)
			.map(this::createDataProductDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	public InternalProductDataDTO getProductPriceInternal(Long id) {
		return productRepository.findById(id)
			.map(productMapper::toInternalProductDataDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	public Page<DataProductDTO> getAllProductWithParams(
		String name, 
		String categoryName, 
		BigDecimal minPrice, 
		BigDecimal maxPrice,
		String manufacturer,
		Pageable pageable 
	) {
		return productRepository.findAllByParams(
			name, 
			categoryName, 
			minPrice, 
			maxPrice, 
			manufacturer,
			pageable 
		)
		.map(this::createDataProductDTO);
	}

	public List<Product> checkWichProductsExceedsStock(List<ProductUnitsRequestedDTO> productsRequest) {
		Map<Long, Integer> unitiesRequested = productsRequest.stream()
			.collect(Collectors.toMap(i -> i.getId(), u -> u.getUnit()));

		List<Long> listOfProductIds = productsRequest.stream()
			.map(p -> p.getId())
			.toList();
		
		return productRepository.findAllById(listOfProductIds).stream()
			.filter(product -> product != null && product.getStock().getUnit() < unitiesRequested.get(product.getId())) 
			.collect(Collectors.toList());
	}

	@Transactional
	public UpdateProductResponseDTO updateProductData(Long id, UpdateProductDTO dto) {
		uniqueNameValidator.validate(dto.getName());
		return productRepository.findById(id)
			.map(product -> {
				product.update(dto.getName(), dto.getDescription(), dto.getSpecs());
				return productRepository.save(product);
			})
			.map(productMapper::toProductUpdateResponseDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	@Transactional
	public UpdateProductPriceResponseDTO updateProductPrice(Long id, UpdatePriceDTO dto) {
		return productRepository.findById(id)
			.map(p -> {
				Price newPrice = priceMapper.toPrice(dto);
				p.updatePrice(newPrice);
				return productRepository.save(p);
			})
			.map(this::createUpdateProductPriceResponseDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	@Transactional
	public UpdateProductPriceResponseDTO switchCurrentPriceToOriginal(Long id) {
		return productRepository.findById(id)
			.map(p -> {
				p.switchPriceToOriginal();
				return productRepository.save(p);
			})
			.stream()
				.peek(product -> scheduler.removeRedundantSchedulePromotion(product.getId()))
				.findFirst()
			.map(this::createUpdateProductPriceResponseDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	@Transactional
	public UpdateProductPriceResponseDTO switchCurrentPriceToPromotional(Long productId, LocalDateTime endOfPromotion) {
		return productRepository.findById(productId)
			.map(product -> {
				product.switchPriceToPromotional(endOfPromotion);
				return productRepository.save(product);
			})
			.stream()
				.peek(p -> scheduler.createScheduleForEndOfPromotion(productId, endOfPromotion))
				.findFirst()
			.map(this::createUpdateProductPriceResponseDTO)
			.orElseThrow(ProductNotFoundException::new);
	}

	@Transactional
	public DataProductStockDTO updateStockByProductId(Long productId, DataStockDTO dto) {
		Product target = productRepository.getReferenceById(productId);
		target.updateStock(dto.getUnit());
		return stockMapper.toDataProductStock(target);
	}

	@Transactional
	public void updateStocks(List<StockWriteOffDTO> dto) {
		Map<Long, Integer> writeOffValueMap = dto.stream()
			.collect(Collectors.toMap(
				StockWriteOffDTO::getProductId, StockWriteOffDTO::getUnit));
		
		productRepository.findAllById(dto.stream()
			.map(StockWriteOffDTO::getProductId)
			.toList())
				.forEach(p -> p.updateStock(writeOffValueMap.get(p.getId())));
	}

	@Transactional
	public DataProductDTO createProduct(CreateProductDTO dto) {
		uniqueNameValidator.validate(dto.getName());

		Category category = categoryRepository.getReferenceById(dto.getCategoryId());
		Manufacturer manufacturer = manufacturerRepository.getReferenceById(dto.getManufacturerId());
		Product product = productMapper.toProduct(dto, category, manufacturer);

		category.addProduct(product);
		manufacturer.addProduct(product);

		productRepository.save(product);
		categoryRepository.save(category);
		manufacturerRepository.save(manufacturer);
		
		return this.createDataProductDTO(product);
	}

	public Map<String, InternalProductDataDTO> getAllProductsByListOfIds(Set<Long> productsIds) {
		return productRepository.findAllById(productsIds).stream()
			.collect(Collectors.toMap(
				product -> String.valueOf(product.getId()), 
				productMapper::toInternalProductDataDTO));
	}

	@Transactional
	public UpdateProductImagesResponseDTO addMainImage(Long productId, String imageLink) {
		return productRepository.findById(productId)
			.stream()
			.peek(product -> product.getImages().setMainImage(imageLink))
			.map(productRepository::save)
			.map(productMapper::toUpdateProductImagesResponseDTO)
			.findFirst()
			.orElseThrow(ProductNotFoundException::new);
    }

	@Transactional
	public UpdateProductImagesResponseDTO addImages(Long productId, Set<String> newImages) {
		return productRepository.findById(productId)
			.stream()
			.peek(product -> product.getImages().addAdditionalImages(newImages))
			.map(productRepository::save)
			.map(productMapper::toUpdateProductImagesResponseDTO)
			.findFirst()
			.orElseThrow(ProductNotFoundException::new);
    }

	@Transactional
	public UpdateProductImagesResponseDTO removeImages(Long productId, Set<String> newImages) {
		return productRepository.findById(productId)
			.stream()
			.peek(product -> product.getImages().remove(newImages))
			.map(productRepository::save)
			.map(productMapper::toUpdateProductImagesResponseDTO)
			.findFirst()
			.orElseThrow(ProductNotFoundException::new);
    }

	private DataProductDTO createDataProductDTO(Product product) {
		SimplePriceDataDTO priceData = priceMapper.toSimplePriceDataDTO(product.getPrice());
		DataStockDTO stockData = stockMapper.toDataStockDTO(product.getStock());
		SimpleDataCategoryDTO categoryData = categoryMapper.toSimpleDataCategoryDTO(product.getCategory());
		SimpleDataManufacturerDTO manufacturerData = manufacturerMapper.toSimpleDataManufacturerDTO(
			product.getManufacturer());

		return productMapper.toDataProductDTO(product, priceData, stockData, categoryData, manufacturerData);
	}

	private UpdateProductPriceResponseDTO createUpdateProductPriceResponseDTO(Product product) {
		CompletePriceDataDTO priceData = priceMapper.toCompletePriceDataDTO(product.getPrice());
		return productMapper.toUpdateProductPriceResponseDTO(product, priceData);
	}
}
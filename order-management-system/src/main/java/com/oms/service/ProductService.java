package com.oms.service;

import com.oms.dto.ProductRequest;
import com.oms.dto.ProductResponse;
import com.oms.entity.Product;
import com.oms.exception.ResourceNotFoundException;
import com.oms.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity()
        );
        Product saved = productRepository.save(product);
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return toResponse(findProductOrThrow(id));
    }

    public Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}

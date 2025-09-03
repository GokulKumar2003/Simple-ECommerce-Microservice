package com.gokul.ecommerce.inventory_service.service;

import com.gokul.ecommerce.inventory_service.dto.ProductDto;
import com.gokul.ecommerce.inventory_service.entity.Product;
import com.gokul.ecommerce.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public List<ProductDto> getAllInventory(){
        //log.info("Fetching all inventory items");
        List<Product> inventories = productRepository.findAll();
        return inventories.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    public ProductDto getProductById(Long id){
        //log.info("Fetching product with id: {}", id);
        Optional<Product> inventory = productRepository.findById(id);

        return inventory.map(item -> modelMapper.map(item, ProductDto.class))
                .orElseThrow(()-> new RuntimeException("Product not found"));
    }
}

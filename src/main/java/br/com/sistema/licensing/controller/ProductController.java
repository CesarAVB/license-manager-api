package br.com.sistema.licensing.controller;

import br.com.sistema.licensing.dtos.ProductRequest;
import br.com.sistema.licensing.dtos.ProductResponse;
import br.com.sistema.licensing.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

 private final ProductService productService;

 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
     return productService.createProduct(request);
 }

 @GetMapping("/{id}")
 @ResponseStatus(HttpStatus.OK)
 public ProductResponse getProductById(@PathVariable Long id) {
     return productService.getProductById(id);
 }

 @GetMapping
 @ResponseStatus(HttpStatus.OK)
 public List<ProductResponse> getAllProducts() {
     return productService.getAllProducts();
 }

 @PutMapping("/{id}")
 @ResponseStatus(HttpStatus.OK)
 public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
     return productService.updateProduct(id, request);
 }

 @DeleteMapping("/{id}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public void deleteProduct(@PathVariable Long id) {
     productService.deleteProduct(id);
 }
}

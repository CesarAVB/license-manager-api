package br.com.sistema.licensing.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.licensing.dtos.ProductRequest;
import br.com.sistema.licensing.dtos.ProductResponse;
import br.com.sistema.licensing.exceptions.ResourceNotFoundException;
import br.com.sistema.licensing.model.Product;
import br.com.sistema.licensing.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Gera um construtor com argumentos para todos os campos final
public class ProductService {

 private final ProductRepository productRepository;

 @Transactional
 public ProductResponse createProduct(ProductRequest request) {
     // Verifica se já existe um produto com o mesmo nome
     if (productRepository.findByName(request.name()).isPresent()) {
         throw new IllegalArgumentException("Product with name '" + request.name() + "' already exists.");
     }

     Product product = new Product();
     product.setName(request.name()); // Acessando o componente 'name' do record
     product.setDescription(request.description()); // Acessando o componente 'description' do record
     Product savedProduct = productRepository.save(product);

     // Mapeia a entidade salva para um ProductResponse (Record)
     return new ProductResponse(savedProduct.getId(), savedProduct.getName(), savedProduct.getDescription());
 }

 @Transactional(readOnly = true) // Indica que esta operação é apenas de leitura
 public ProductResponse getProductById(Long id) {
     // Utiliza o método do repositório que já projeta para ProductResponse
     return productRepository.findProductResponseById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
 }

 @Transactional(readOnly = true)
 public List<ProductResponse> getAllProducts() {
     // Utiliza o método do repositório que já projeta para uma lista de ProductResponse
     return productRepository.findAllProductResponses();
 }

 @Transactional
 public ProductResponse updateProduct(Long id, ProductRequest request) {
     Product product = productRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

     // Opcional: Se o nome do produto for alterado, verificar unicidade
     if (!product.getName().equals(request.name())) {
         if (productRepository.findByName(request.name()).isPresent()) {
             throw new IllegalArgumentException("Product with name '" + request.name() + "' already exists.");
         }
     }

     product.setName(request.name());
     product.setDescription(request.description());
     Product updatedProduct = productRepository.save(product);

     // Mapeia a entidade atualizada para um ProductResponse (Record)
     return new ProductResponse(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getDescription());
 }

 @Transactional
 public void deleteProduct(Long id) {
     if (!productRepository.existsById(id)) {
         throw new ResourceNotFoundException("Product not found with id: " + id);
     }
     productRepository.deleteById(id);
 }

 // Método auxiliar para obter a entidade Product (ainda necessário para operações que modificam a entidade ou que precisam de seus relacionamentos)
 @Transactional(readOnly = true)
 public Product getProductEntityByName(String productName) {
     return productRepository.findByName(productName)
             .orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + productName));
 }
}

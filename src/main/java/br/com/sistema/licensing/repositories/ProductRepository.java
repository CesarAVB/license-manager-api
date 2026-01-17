package br.com.sistema.licensing.repositories;

import br.com.sistema.licensing.dtos.ProductResponse;
import br.com.sistema.licensing.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
 Optional<Product> findByName(String name); // Mantemos este para buscar a entidade completa quando necessário

 // Novo método para buscar um ProductResponse por ID
 @Query("SELECT new com.cesar.licensing.dto.ProductResponse(p.id, p.name, p.description) FROM Product p WHERE p.id = :id")
 Optional<ProductResponse> findProductResponseById(Long id);

 // Novo método para buscar todos os ProductResponse
 @Query("SELECT new com.cesar.licensing.dto.ProductResponse(p.id, p.name, p.description) FROM Product p")
 List<ProductResponse> findAllProductResponses();

 // Novo método para buscar um ProductResponse por nome
 @Query("SELECT new com.cesar.licensing.dto.ProductResponse(p.id, p.name, p.description) FROM Product p WHERE p.name = :name")
 Optional<ProductResponse> findProductResponseByName(String name);
}
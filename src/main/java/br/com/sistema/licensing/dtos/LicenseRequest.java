package br.com.sistema.licensing.dtos;

import br.com.sistema.licensing.model.LicenseStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record LicenseRequest(
        @NotBlank(message = "Product name is required")
        String productName,

        @NotBlank(message = "License key is required")
        @Size(min = 10, max = 50, message = "License key must be between 10 and 50 characters")
        String licenseKey,

        @NotBlank(message = "Licensed to is required")
        String licensedTo,

        @NotNull(message = "Issue date is required")
        LocalDateTime issueDate,

        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future") // Garante que a data de expiração seja no futuro
        LocalDateTime expirationDate,

        @NotNull(message = "License status is required")
        LicenseStatus status,

        Set<String> enabledFeatures, // Conjunto de funcionalidades habilitadas pela licença

        Integer maxUsers, // Número máximo de usuários permitidos, pode ser nulo

        String hardwareId // ID do hardware onde a licença será ativada, pode ser nulo
) {
    // Construtor compacto para adicionar validações customizadas, se necessário.
    // O construtor canônico (gerado automaticamente) já faz a atribuição dos campos.
    // Este construtor é executado APÓS as validações das anotações.
    public LicenseRequest {
        if (issueDate != null && expirationDate != null && issueDate.isAfter(expirationDate)) {
            throw new IllegalArgumentException("Issue date cannot be after expiration date.");
        }
        // Poderíamos adicionar mais validações aqui, como verificar se enabledFeatures não é nulo se o status for ACTIVE, etc.
    }
}

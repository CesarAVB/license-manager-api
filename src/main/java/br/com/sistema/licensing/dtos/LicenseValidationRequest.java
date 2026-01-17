package br.com.sistema.licensing.dtos;

import jakarta.validation.constraints.NotBlank;

public record LicenseValidationRequest(
        @NotBlank(message = "License key is required for validation")
        String licenseKey,

        @NotBlank(message = "Product name is required for validation")
        String productName,

        String hardwareId
) {}

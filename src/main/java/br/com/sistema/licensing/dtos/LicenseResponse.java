package br.com.sistema.licensing.dtos;

import br.com.sistema.licensing.model.LicenseStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record LicenseResponse(
     String licenseKey,
     String productName,
     String licensedTo,
     LocalDateTime issueDate,
     LocalDateTime expirationDate,
     LicenseStatus status,
     Set<String> enabledFeatures,
     Integer maxUsers,
     String hardwareId,
     LocalDateTime activationDate,
     String message
) {}

package br.com.sistema.licensing.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.licensing.dtos.LicenseRequest;
import br.com.sistema.licensing.dtos.LicenseResponse;
import br.com.sistema.licensing.dtos.LicenseValidationRequest;
import br.com.sistema.licensing.exceptions.LicenseException;
import br.com.sistema.licensing.exceptions.ResourceNotFoundException;
import br.com.sistema.licensing.model.License;
import br.com.sistema.licensing.model.LicenseStatus;
import br.com.sistema.licensing.model.Product;
import br.com.sistema.licensing.repositories.LicenseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LicenseService {

 private final LicenseRepository licenseRepository;
 private final ProductService productService; // Para buscar o produto associado

 // --- Métodos de Gerenciamento de Licenças (para o administrador/painel) ---

 @Transactional
 public LicenseResponse createLicense(LicenseRequest request) {
     Product product = productService.getProductEntityByName(request.productName()); // Acessando o componente 'productName' do record

     // Opcional: Gerar uma licenseKey se não for fornecida (ex: para chaves de ativação)
     String licenseKey = Optional.ofNullable(request.licenseKey()) // Acessando o componente 'licenseKey' do record
                                 .filter(key -> !key.isBlank())
                                 .orElseGet(() -> generateUniqueLicenseKey());

     // Verificar se a licenseKey já existe
     if (licenseRepository.findByLicenseKey(licenseKey).isPresent()) {
         throw new LicenseException("License key already exists: " + licenseKey);
     }

     License license = new License();
     license.setLicenseKey(licenseKey);
     license.setProduct(product);
     license.setLicensedTo(request.licensedTo());
     license.setIssueDate(request.issueDate());
     license.setExpirationDate(request.expirationDate());
     license.setStatus(request.status());
     license.setEnabledFeatures(request.enabledFeatures());
     license.setMaxUsers(request.maxUsers());
     license.setHardwareId(request.hardwareId()); // Pode ser nulo inicialmente
     license.setActivationDate(null); // Nulo até ser ativada

     License savedLicense = licenseRepository.save(license);
     return mapToLicenseResponse(savedLicense, "License created successfully.");
 }

 @Transactional(readOnly = true)
 public LicenseResponse getLicenseByKey(String licenseKey) {
     License license = licenseRepository.findByLicenseKey(licenseKey)
             .orElseThrow(() -> new ResourceNotFoundException("License not found with key: " + licenseKey));
     return mapToLicenseResponse(license, "License retrieved successfully.");
 }

 @Transactional(readOnly = true)
 public List<LicenseResponse> getAllLicenses() {
     return licenseRepository.findAll().stream()
             .map(license -> mapToLicenseResponse(license, null))
             .collect(Collectors.toList());
 }

 @Transactional
 public LicenseResponse updateLicense(String licenseKey, LicenseRequest request) {
     License license = licenseRepository.findByLicenseKey(licenseKey)
             .orElseThrow(() -> new ResourceNotFoundException("License not found with key: " + licenseKey));

     Product product = productService.getProductEntityByName(request.productName());

     license.setProduct(product);
     license.setLicensedTo(request.licensedTo());
     license.setIssueDate(request.issueDate());
     license.setExpirationDate(request.expirationDate());
     license.setStatus(request.status());
     license.setEnabledFeatures(request.enabledFeatures());
     license.setMaxUsers(request.maxUsers());
     license.setHardwareId(request.hardwareId());
     // Não atualizamos a activationDate aqui, ela é definida no processo de ativação

     License updatedLicense = licenseRepository.save(license);
     return mapToLicenseResponse(updatedLicense, "License updated successfully.");
 }

 @Transactional
 public void deleteLicense(String licenseKey) {
     License license = licenseRepository.findByLicenseKey(licenseKey)
             .orElseThrow(() -> new ResourceNotFoundException("License not found with key: " + licenseKey));
     licenseRepository.delete(license);
 }

 // --- Métodos de Ativação e Validação (para o cliente da aplicação) ---

 @Transactional
 public LicenseResponse activateLicense(LicenseValidationRequest request) {
     License license = licenseRepository.findByLicenseKeyAndProduct_Name(request.licenseKey(), request.productName()) // Acessando componentes do record
             .orElseThrow(() -> new ResourceNotFoundException("License not found for key and product."));

     if (license.getStatus() == LicenseStatus.ACTIVE) {
         // Se já está ativa, podemos verificar se o hardwareId mudou ou apenas retornar o status atual
         if (request.hardwareId() != null && !request.hardwareId().equals(license.getHardwareId())) {
             // Lógica para lidar com mudança de hardware:
             // 1. Permitir reativação (se for um limite de reativações)
             // 2. Negar e exigir nova licença
             // 3. Registrar a mudança e avisar o admin
             // Por enquanto, vamos permitir, mas registrar o novo hardwareId
             license.setHardwareId(request.hardwareId());
             license.setActivationDate(LocalDateTime.now()); // Reativação
             licenseRepository.save(license);
             return mapToLicenseResponse(license, "License re-activated on new hardware.");
         }
         return mapToLicenseResponse(license, "License is already active.");
     }

     if (license.getStatus() == LicenseStatus.REVOKED) {
         throw new LicenseException("License has been revoked.");
     }

     if (license.getExpirationDate().isBefore(LocalDateTime.now())) {
         license.setStatus(LicenseStatus.EXPIRED);
         licenseRepository.save(license);
         throw new LicenseException("License has expired.");
     }

     // Se a licença está PENDING_ACTIVATION ou SUSPENDED e pode ser reativada
     license.setStatus(LicenseStatus.ACTIVE);
     license.setActivationDate(LocalDateTime.now());
     license.setHardwareId(request.hardwareId()); // Vincula ao hardware na ativação
     License activatedLicense = licenseRepository.save(license);

     return mapToLicenseResponse(activatedLicense, "License activated successfully.");
 }

 @Transactional(readOnly = true)
 public LicenseResponse validateLicense(LicenseValidationRequest request) {
     License license = licenseRepository.findByLicenseKeyAndProduct_Name(request.licenseKey(), request.productName())
             .orElseThrow(() -> new ResourceNotFoundException("License not found for key and product."));

     if (license.getStatus() != LicenseStatus.ACTIVE) {
         String message = "License is not active. Current status: " + license.getStatus();
         if (license.getStatus() == LicenseStatus.EXPIRED) {
             message = "License has expired.";
         } else if (license.getStatus() == LicenseStatus.REVOKED) {
             message = "License has been revoked.";
         } else if (license.getStatus() == LicenseStatus.PENDING_ACTIVATION) {
             message = "License requires activation.";
         }
         return mapToLicenseResponse(license, message); // Retorna o status, mas com mensagem de erro
     }

     if (license.getExpirationDate().isBefore(LocalDateTime.now())) {
         license.setStatus(LicenseStatus.EXPIRED); // Atualiza o status no DB
         licenseRepository.save(license);
         return mapToLicenseResponse(license, "License has expired.");
     }

     // Validação de hardware (se a licença estiver vinculada a hardware)
     if (license.getHardwareId() != null && !license.getHardwareId().isBlank()) {
         if (request.hardwareId() == null || !request.hardwareId().equals(license.getHardwareId())) {
             return mapToLicenseResponse(license, "License is bound to a different hardware ID.");
         }
     }

     // Tudo certo, a licença é válida
     return mapToLicenseResponse(license, "License is valid and active.");
 }

 // --- Métodos Auxiliares ---

 // Este método agora usa o construtor do record LicenseResponse
 private LicenseResponse mapToLicenseResponse(License license, String message) {
     return new LicenseResponse(
             license.getLicenseKey(),
             license.getProduct().getName(),
             license.getLicensedTo(),
             license.getIssueDate(),
             license.getExpirationDate(),
             license.getStatus(),
             license.getEnabledFeatures(),
             license.getMaxUsers(),
             license.getHardwareId(),
             license.getActivationDate(),
             message
     );
 }

 private String generateUniqueLicenseKey() {
     // Gera uma chave UUID e remove os hífens para uma chave mais compacta
     return UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
 }
}

package br.com.sistema.licensing.controller;

import br.com.sistema.licensing.dtos.LicenseRequest;
import br.com.sistema.licensing.dtos.LicenseResponse;
import br.com.sistema.licensing.dtos.LicenseValidationRequest;
import br.com.sistema.licensing.services.LicenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class LicenseController {

 private final LicenseService licenseService;

 // --- Endpoints para Gerenciamento de Licenças (para o administrador/painel) ---

 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 public LicenseResponse createLicense(@Valid @RequestBody LicenseRequest request) {
     return licenseService.createLicense(request);
 }

 @GetMapping("/{licenseKey}")
 @ResponseStatus(HttpStatus.OK)
 public LicenseResponse getLicenseByKey(@PathVariable String licenseKey) {
     return licenseService.getLicenseByKey(licenseKey);
 }

 @GetMapping
 @ResponseStatus(HttpStatus.OK)
 public List<LicenseResponse> getAllLicenses() {
     return licenseService.getAllLicenses();
 }

 @PutMapping("/{licenseKey}")
 @ResponseStatus(HttpStatus.OK)
 public LicenseResponse updateLicense(@PathVariable String licenseKey, @Valid @RequestBody LicenseRequest request) {
     return licenseService.updateLicense(licenseKey, request);
 }

 @DeleteMapping("/{licenseKey}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public void deleteLicense(@PathVariable String licenseKey) {
     licenseService.deleteLicense(licenseKey);
 }

 // --- Endpoints para Ativação e Validação (para o cliente da aplicação) ---

 @PostMapping("/activate")
 @ResponseStatus(HttpStatus.OK)
 public LicenseResponse activateLicense(@Valid @RequestBody LicenseValidationRequest request) {
     return licenseService.activateLicense(request);
 }

 @PostMapping("/validate")
 @ResponseStatus(HttpStatus.OK)
 public LicenseResponse validateLicense(@Valid @RequestBody LicenseValidationRequest request) {
     return licenseService.validateLicense(request);
 }
}

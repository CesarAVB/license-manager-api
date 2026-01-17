package br.com.sistema.licensing.repositories;

import br.com.sistema.licensing.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
 Optional<License> findByLicenseKey(String licenseKey);
 Optional<License> findByLicenseKeyAndProduct_Name(String licenseKey, String productName);
}

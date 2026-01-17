package br.com.sistema.licensing.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "licenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licenseKey; // A chave única que o cliente usará para ativar/validar

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // O produto ao qual esta licença pertence

    @Column(nullable = false)
    private String licensedTo; // Nome do cliente/empresa que possui a licença

    @Column(nullable = false)
    private LocalDateTime issueDate; // Data de emissão da licença

    @Column(nullable = false)
    private LocalDateTime expirationDate; // Data de expiração da licença

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LicenseStatus status; // Status da licença (ACTIVE, EXPIRED, REVOKED, PENDING_ACTIVATION)

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "license_features", joinColumns = @JoinColumn(name = "license_id"))
    @Column(name = "feature")
    private Set<String> enabledFeatures; // Funcionalidades habilitadas por esta licença (ex: "RELATORIOS_AVANCADOS", "INTEGRACAO_API")

    private Integer maxUsers; // Número máximo de usuários permitidos (opcional)

    private String hardwareId; // ID do hardware onde a licença foi ativada (para licenças vinculadas a hardware, opcional)

    private LocalDateTime activationDate; // Data de ativação (se houver um processo de ativação)
}

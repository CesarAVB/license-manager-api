package br.com.sistema.licensing.model;

public enum LicenseStatus {
    ACTIVE,
    EXPIRED,
    REVOKED,
    PENDING_ACTIVATION,
    SUSPENDED // Para licenças de assinatura com pagamento em atraso
}

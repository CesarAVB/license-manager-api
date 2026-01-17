package br.com.sistema.licensing.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private Map<String, String> validationErrors; // Para erros de validação

    public ErrorDetails(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}

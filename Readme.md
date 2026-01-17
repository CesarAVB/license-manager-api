# Licensing - Microserviço de Gerenciamento de Licenças Baseadas em Servidor

Um microserviço REST robusto desenvolvido com **Spring Boot 3.5.9** e **Java 21** para gerenciar licenças de software, ativações, validações e controle de produtos com suporte a hardware vinculado e funcionalidades granulares.

## 📋 Características

- **Gerenciamento de Produtos**: Criar, atualizar, consultar e deletar produtos
- **Gerenciamento de Licenças**: Operações CRUD com suporte a chaves únicas
- **Ativação de Licenças**: Sistema de ativação com vinculação a hardware
- **Validação de Licenças**: Verificação de status, expiração e hardware vinculado
- **Funcionalidades Granulares**: Controle de features habilitadas por licença
- **Limite de Usuários**: Suporte a restrição de máximo de usuários por licença
- **Tratamento de Exceções**: Handler global para erros e validações
- **Documentação Automática**: Swagger UI/OpenAPI 3.0 integrado
- **Persistência**: Spring Data JPA com PostgreSQL
- **Segurança**: Spring Security configurado

## 🚀 Tecnologias

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 21 | Linguagem base |
| Spring Boot | 3.5.9 | Framework principal |
| Spring Data JPA | - | ORM/Persistência |
| Spring Security | - | Autenticação e autorização |
| Spring Validation | - | Validação de dados |
| PostgreSQL | - | Banco de dados |
| Lombok | - | Redução de boilerplate |
| Springdoc OpenAPI | 2.3.0 | Documentação API |

## 📦 Instalação e Configuração

### Pré-requisitos

- Java 21 instalado
- PostgreSQL instalado e rodando
- Maven 3.8.1+

### Passos

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/licensing.git
   cd licensing
   ```

2. **Configure o banco de dados**
   
   Crie um banco de dados PostgreSQL:
   ```sql
   CREATE DATABASE licensing_db;
   ```

3. **Configure as variáveis de ambiente**
   
   Crie um arquivo `application-local.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/licensing_db
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Construa e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Acesse a documentação**
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 📚 Estrutura do Projeto

```
src/main/java/br/com/sistema/licensing/
├── controller/
│   ├── LicenseController.java      # Endpoints de licenças
│   └── ProductController.java      # Endpoints de produtos
├── model/
│   ├── License.java                # Entidade de Licença
│   ├── Product.java                # Entidade de Produto
│   └── LicenseStatus.java          # Enum de status
├── services/
│   ├── LicenseService.java         # Lógica de licenças
│   └── ProductService.java         # Lógica de produtos
├── repositories/
│   ├── LicenseRepository.java      # Acesso a licenças
│   └── ProductRepository.java      # Acesso a produtos
├── dtos/
│   ├── LicenseRequest.java         # DTO de entrada
│   ├── LicenseResponse.java        # DTO de saída
│   ├── LicenseValidationRequest.java
│   ├── ProductRequest.java
│   └── ProductResponse.java
├── exceptions/
│   ├── GlobalExceptionHandler.java # Handler global
│   ├── LicenseException.java
│   └── ResourceNotFoundException.java
└── configurations/
    └── OpenApiConfig.java          # Configuração Swagger
```

## 🔌 Endpoints Principais

### Produtos
- `POST /api/products` - Criar produto
- `GET /api/products` - Listar todos
- `GET /api/products/{id}` - Obter por ID
- `PUT /api/products/{id}` - Atualizar
- `DELETE /api/products/{id}` - Deletar

### Licenças (Gerenciamento)
- `POST /api/licenses` - Criar licença
- `GET /api/licenses` - Listar todas
- `GET /api/licenses/{licenseKey}` - Obter por chave
- `PUT /api/licenses/{licenseKey}` - Atualizar
- `DELETE /api/licenses/{licenseKey}` - Deletar

### Licenças (Cliente)
- `POST /api/licenses/activate` - Ativar licença
- `POST /api/licenses/validate` - Validar licença

## 📊 Modelos de Dados

### Product
```java
{
  "id": 1,
  "name": "MeuSistema",
  "description": "Sistema de gestão empresarial"
}
```

### License
```java
{
  "licenseKey": "ABC123XYZ789",
  "productName": "MeuSistema",
  "licensedTo": "Empresa LTDA",
  "issueDate": "2024-01-01T00:00:00",
  "expirationDate": "2025-12-31T23:59:59",
  "status": "ACTIVE",
  "enabledFeatures": ["RELATORIOS", "INTEGRACAO_API"],
  "maxUsers": 10,
  "hardwareId": "HW123456",
  "activationDate": "2024-01-15T10:30:00"
}
```

### LicenseStatus
- `ACTIVE` - Licença ativa
- `PENDING_ACTIVATION` - Aguardando ativação
- `EXPIRED` - Expirada
- `REVOKED` - Revogada
- `SUSPENDED` - Suspensa

## 🔐 Validações

O projeto implementa validações em múltiplas camadas:

- **DTOs**: Anotações Jakarta Validation (@NotBlank, @Future, @Size, etc.)
- **Entidades**: Constraints de banco de dados
- **Serviços**: Lógica de negócio customizada
- **Controladores**: Binding automático com @Valid

### Exemplos de Validação

```java
// Licença deve ter produto cadastrado
// Chave deve ter entre 10-50 caracteres
// Data de expiração deve ser no futuro
// Data de emissão não pode ser depois da expiração
```

## 🛡️ Tratamento de Erros

A aplicação retorna respostas estruturadas para erros:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "License not found with key: INVALID_KEY",
  "details": "uri=/api/licenses/INVALID_KEY",
  "validationErrors": null
}
```

Códigos HTTP retornados:
- `200 OK` - Sucesso
- `201 Created` - Recurso criado
- `204 No Content` - Deletado com sucesso
- `400 Bad Request` - Erro de validação
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro interno

## 🔄 Fluxos Principais

### Fluxo de Ativação
1. Cliente solicita ativação com licenseKey, productName e hardwareId
2. Serviço valida se licença existe e pertence ao produto
3. Verifica se já está ativa (se sim, permite reativação em novo hardware)
4. Valida status e data de expiração
5. Define status como ACTIVE e registra data/hardware de ativação

### Fluxo de Validação
1. Cliente solicita validação com licenseKey e productName
2. Serviço valida se licença existe
3. Verifica status, expiração e hardware vinculado
4. Retorna status detalhado da licença

## 📝 Exemplo de Uso

### Criar Produto
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MeuSistema",
    "description": "Sistema de gestão"
  }'
```

### Criar Licença
```bash
curl -X POST http://localhost:8080/api/licenses \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "MeuSistema",
    "licenseKey": "ABC123XYZ789",
    "licensedTo": "Empresa LTDA",
    "issueDate": "2024-01-01T00:00:00",
    "expirationDate": "2025-12-31T23:59:59",
    "status": "PENDING_ACTIVATION",
    "enabledFeatures": ["RELATORIOS", "INTEGRACAO"],
    "maxUsers": 10
  }'
```

### Ativar Licença
```bash
curl -X POST http://localhost:8080/api/licenses/activate \
  -H "Content-Type: application/json" \
  -d '{
    "licenseKey": "ABC123XYZ789",
    "productName": "MeuSistema",
    "hardwareId": "HW123456"
  }'
```

## 🧪 Testes

Execute os testes unitários e de integração:

```bash
mvn test
```

## 📖 Documentação da API

Acesse a documentação interativa em:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🔧 Configuração de Ambiente

### Desenvolvimento (local)
```properties
spring.profiles.active=local
spring.jpa.show-sql=true
```

### Produção
```properties
spring.profiles.active=prod
logging.level.root=INFO
```

Configure variáveis de ambiente para produção:
- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

## 👨‍💻 Autor

**César Augusto**
- Email: cesar.augusto.rj1@gmail.com
- Portfolio: https://portfolio.cesaravb.com.br/

## 📞 Suporte

Para suporte, abra uma issue no repositório ou entre em contato pelo email.

---

**Última atualização**: Janeiro 2026
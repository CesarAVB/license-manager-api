package br.com.sistema.licensing.configurations;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(buildInfo()).servers(buildServers());
    }

    /**
     * Constrói as informações da API (título, versão, descrição, contato e licença)
     */
    private Info buildInfo() {
        return new Info()
                .title("Licensing - Microserviço de Gerenciamento de Licenças")
                .version("1.0.0")
                .description("Microserviço REST especializado em gerenciamento de licenças de software baseadas em servidor. " +
                        "Fornece funcionalidades de ativação de licenças, validação com vinculação a hardware, " +
                        "controle de produtos, funcionalidades granulares e limite de usuários. " +
                        "Desenvolvido com Spring Boot 3.5.9, Java 21 e boas práticas de arquitetura de microserviços.")
                .contact(buildContact())
                .license(buildLicense());
    }

    /**
     * Constrói as informações de contato
     */
    private Contact buildContact() {
        return new Contact()
                .name("César Augusto")
                .email("cesar.augusto.rj1@gmail.com")
                .url("https://portfolio.cesaraugusto.dev.br/");
    }

    /**
     * Constrói as informações de licença do projeto
     */
    private License buildLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Constrói a lista de servidores disponíveis para a API
     */
    private List<Server> buildServers() {
        Server developmentServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento - Ambiente Local");

        Server productionServer = new Server()
                .url("https://api-licensing.cesaravb.com.br/")
                .description("Servidor de Produção - Ambiente Produtivo");

        return List.of(developmentServer, productionServer);
    }
}
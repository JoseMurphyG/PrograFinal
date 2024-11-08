package com.example.chatbot.config;

import com.example.chatbot.service.ErrorLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class OpenAIConfig {

    @Autowired
    private ErrorLogService errorLogService; // Inyecta el servicio de log de errores

    @Bean
    public WebClient webClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            String errorMessage = "OpenAI API Key is missing or not set in environment variables.";
            errorLogService.logError(new RuntimeException(errorMessage), null, null); // Log error if the API key is missing
            throw new RuntimeException(errorMessage); // Optionally, throw an exception to prevent proceeding without API key
        }

        try {
            return WebClient.builder()
                    .baseUrl("https://api.openai.com/v1/")
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .build();
        } catch (Exception e) {
            errorLogService.logError(e, "Error while creating WebClient for OpenAI", null); // Log error if WebClient creation fails
            throw new RuntimeException("Failed to create WebClient for OpenAI API", e);
        }
    }
}

package com.example.chatbot;
import com.example.chatbot.service.ErrorLogService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.example.chatbot.service.TelegramBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;


@SpringBootApplication
public class ChatbotApplication {

    private final ErrorLogService errorLogService;

    @Autowired
    public ChatbotApplication(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    // Initialize and log a message when the application starts
    @PostConstruct
    public void init() {
        System.out.println("Spring Boot Application Initialized");
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotConfig botConfig) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botConfig);
            return botsApi;
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Log the error properly
            System.err.println("Error initializing TelegramBotsApi: " + e.getMessage());
            return null;
        }
    }
}

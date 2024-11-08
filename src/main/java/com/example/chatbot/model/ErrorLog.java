package com.example.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Data
@NoArgsConstructor
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String errorMessage;

    @Column(length = 4000)
    private String stackTrace;

    @Column(nullable = false)
    private String errorType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "chat_id", nullable = false) // Changed to String with @Column annotation
    private String chatId; // Changed from Long to String

    private String username; // Usuario de Telegram que generó el error

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

}

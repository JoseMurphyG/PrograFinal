package com.example.chatbot.repository;

import com.example.chatbot.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    // Change the type of chatId to String
    List<ErrorLog> findByChatId(String chatId);  // Accept String chatId

    List<ErrorLog> findByUsername(String username);
}

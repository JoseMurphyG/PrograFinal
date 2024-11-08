package com.example.chatbot.service;

import com.example.chatbot.model.ErrorLog;
import com.example.chatbot.repository.IErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final IErrorLogRepository errorLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogService.class);

    // Save error log with relevant details
    @Transactional
    public ErrorLog logError(Exception exception, String chatId, String username) {
        try {
            // Create and populate the error log
            ErrorLog errorLog = new ErrorLog();
            errorLog.setErrorMessage(exception.getMessage());
            errorLog.setErrorType(exception.getClass().getSimpleName());
            errorLog.setChatId(chatId);
            errorLog.setUsername(username);

            // Capture the stack trace as a string
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String stackTrace = sw.toString();

            // Optionally limit stack trace length for storage
            if (stackTrace.length() > 1000) {
                stackTrace = stackTrace.substring(0, 1000) + "...";
            }
            errorLog.setStackTrace(stackTrace);

            // Save the error log
            return errorLogRepository.save(errorLog);
        } catch (Exception e) {
            // Log failure to record the error log
            logger.error("Error saving error log: {}", e.getMessage(), e);
            throw new ErrorLoggingException("Failed to save error log", e);
        }
    }

    // Retrieve error logs by chatId
    public List<ErrorLog> findErrorsByChatId(String chatId) {
        try {
            return errorLogRepository.findByChatId(chatId);
        } catch (Exception e) {
            logger.error("Error retrieving errors by chatId {}: {}", chatId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Retrieve error logs by username
    public List<ErrorLog> findErrorsByUsername(String username) {
        try {
            return errorLogRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error retrieving errors by username {}: {}", username, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Custom exception for failure during error log saving
    public static class ErrorLoggingException extends RuntimeException {
        public ErrorLoggingException(String message) {
            super(message);
        }

        public ErrorLoggingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

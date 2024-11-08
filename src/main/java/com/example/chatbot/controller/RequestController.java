package com.example.chatbot.controller;

import com.example.chatbot.model.ErrorLog;
import com.example.chatbot.model.RequestModel;
import com.example.chatbot.service.RequestService;
import com.example.chatbot.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ErrorLogService errorLogService; // Inject error log service

    @GetMapping
    public ArrayList<RequestModel> getRequest() {
        try {
            return this.requestService.getRequest();
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Replace with chatId and username if available
            return new ArrayList<>(); // Return an empty list or error message
        }
    }

    @PostMapping
    public RequestModel saveRequest(@RequestBody RequestModel request) {
        try {
            return this.requestService.saveRequest(request);
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Replace with chatId and username if available
            return null; // Handle error case as per your application
        }
    }

    @GetMapping(path = "/{id}")
    public Optional<RequestModel> getClientById(@PathVariable String id) {
        try {
            return this.requestService.getById(id);  // Assuming method updated to accept String
        } catch (Exception e) {
            errorLogService.logError(e, null, null);
            return Optional.empty(); // Return empty Optional on error
        }
    }

    @DeleteMapping(path = "/{id}")
    public String deleteById(@PathVariable("id") String id) {
        try {
            boolean ok = this.requestService.deleteRequest(id);  // Assuming method updated to accept String
            if (ok) {
                return "The client with id: " + id + " has been deleted";
            } else {
                return "Error: unable to delete client";
            }
        } catch (Exception e) {
            errorLogService.logError(e, null, null);
            return "Error: An issue occurred while attempting to delete the client";
        }
    }

    @GetMapping("/errors/chat/{chatId}")
    public List<ErrorLog> getErrorsByChatId(@PathVariable String chatId) {  // Changed to String
        try {
            return errorLogService.findErrorsByChatId(chatId);  // Now passing String to the service method
        } catch (Exception e) {
            errorLogService.logError(e, chatId, null);  // Pass chatId as String (and username if available)
            return new ArrayList<>();
        }
    }

    @GetMapping("/errors/username/{username}")
    public List<ErrorLog> getErrorsByUsername(@PathVariable String username) {
        try {
            return errorLogService.findErrorsByUsername(username);
        } catch (Exception e) {
            errorLogService.logError(e, null, username);  // Pass username as String
            return new ArrayList<>();
        }
    }
}

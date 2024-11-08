package com.example.chatbot.service;

import com.example.chatbot.model.ErrorLog;
import com.example.chatbot.model.RequestModel;
import com.example.chatbot.repository.IRequestRepository;
import com.example.chatbot.service.ErrorLogService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    @Autowired
    IRequestRepository requestRepository;

    @Autowired
    ErrorLogService errorLogService; // Inyección del servicio de log de errores

    public ArrayList<RequestModel> getRequest(){
        try {
            return (ArrayList<RequestModel>) requestRepository.findAll();
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Registra el error
            return new ArrayList<>(); // Retorna una lista vacía en caso de error
        }
    }

    public RequestModel saveRequest(RequestModel request){
        try {
            return requestRepository.save(request);
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Registra el error
            return null; // Retorna null en caso de error
        }
    }

    // Modified method to accept String id and convert it to Long
    public Optional<RequestModel> getById(String id){
        try {
            Long idLong = Long.parseLong(id);  // Convert String to Long
            return requestRepository.findById(idLong);
        } catch (NumberFormatException e) {
            errorLogService.logError(e, null, null); // Log the error if conversion fails
            return Optional.empty(); // Return empty Optional in case of error
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Log other exceptions
            return Optional.empty();
        }
    }

    // Modified method to accept String id and convert it to Long
    public Boolean deleteRequest(String id){
        try {
            Long idLong = Long.parseLong(id);  // Convert String to Long
            requestRepository.deleteById(idLong); // Proceed with deletion
            return true;
        } catch (NumberFormatException e) {
            errorLogService.logError(e, null, null); // Log error if conversion fails
            return false; // Return false in case of error
        } catch (Exception e) {
            errorLogService.logError(e, null, null); // Log other exceptions
            return false; // Return false in case of error
        }
    }

    public List<ErrorLog> findErrorsByChatId(String chatId) {
        return errorLogService.findErrorsByChatId(chatId);
    }

    public List<ErrorLog> findErrorsByUsername(String username) {
        return errorLogService.findErrorsByUsername(username);
    }

}

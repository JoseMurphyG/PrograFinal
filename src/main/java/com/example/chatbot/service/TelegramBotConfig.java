package com.example.chatbot.service;

import com.example.chatbot.model.ErrorLog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TelegramBotConfig extends TelegramLongPollingBot {

    private final ErrorLogService errorLogService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private Map<String, String> userState = new HashMap<>();  // Change to String for chatId

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());  // Change to String
            String username = update.getMessage().getFrom().getUserName();

            try {
                String response = handleUserInteraction(chatId, messageText);
                sendMessage(chatId, response);  // Use String here
            } catch (Exception e) {
                // Registrar el error
                errorLogService.logError(e, chatId, username);  // Use String chatId
                // Enviar mensaje de error al usuario
                sendErrorMessage(chatId);
            }
        }
    }

    private void sendMessage(String chatId, String text) {  // Change to String
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);  // Use String chatId
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            String username = "unknown"; // En caso de que no podamos obtener el username en este punto
            errorLogService.logError(e, chatId, username);  // Use String chatId
        }
    }

    private void sendErrorMessage(String chatId) {  // Change to String
        try {
            SendMessage errorMessage = new SendMessage();
            errorMessage.setChatId(chatId);  // Use String chatId
            errorMessage.setText("Lo siento, ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo de nuevo más tarde.");
            execute(errorMessage);
        } catch (TelegramApiException e) {
            // Si falla incluso el envío del mensaje de error, solo lo registramos
            System.err.println("Error al enviar mensaje de error: " + e.getMessage());
        }
    }

    private String handleUserInteraction(String chatId, String messageText) {  // Change to String
        try {
            String response;
            if (!userState.containsKey(chatId)) {
                response = "¡Hola! ¿Cuál es tu nombre?";
                userState.put(chatId, "ASKED_NAME");
            } else if ("ASKED_NAME".equals(userState.get(chatId))) {
                response = "¡Mucho gusto, " + messageText + "! ¿Qué te gustaría saber?";
                userState.put(chatId, "ASKED_TOPIC");
            } else if ("ASKED_TOPIC".equals(userState.get(chatId))) {
                response = callChatGPT(messageText);
                userState.put(chatId, "DONE");
            } else {
                response = "¡Gracias por la conversación! Puedes preguntarme más cosas si lo deseas.";
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error en el manejo de la interacción del usuario", e);
        }
    }

    private String callChatGPT(String userMessage) {
        String responseMessage = "";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
            request.setHeader("Authorization", "Bearer " + openAiApiKey);
            request.setHeader("Content-Type", "application/json");

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            JSONObject message = new JSONObject().put("role", "user").put("content", userMessage);
            requestBody.put("messages", new JSONArray().put(message));

            StringEntity entity = new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);
                responseMessage = jsonObject.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } catch (IOException | ParseException e) {
                throw new RuntimeException("Error al procesar la respuesta de ChatGPT", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al conectar con ChatGPT", e);
        }
        return responseMessage;
    }
}

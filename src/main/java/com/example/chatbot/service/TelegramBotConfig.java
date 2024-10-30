package com.example.chatbot.service;

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
public class TelegramBotConfig extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private Map<Long, String> userState = new HashMap<>();

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
            long chatId = update.getMessage().getChatId();

            String response = handleUserInteraction(chatId, messageText);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String handleUserInteraction(long chatId, String messageText) {
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
                e.printStackTrace();
                responseMessage = "Lo siento, hubo un error al procesar tu solicitud.";
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al conectar con ChatGPT", e);
        }
        return responseMessage;
    }
}

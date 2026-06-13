package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.config.TelegramProperties;
import com.example.smm_cms.service.ITelegramService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements ITelegramService {
    private static final Logger log = LoggerFactory.getLogger(TelegramServiceImpl.class);
    private final RestClient restClient = RestClient.create("https://api.telegram.org");

    private final TelegramProperties telegramProperties;

    @Override
    @Async("telegramExecutor")
    public void sendTelegramMessage(String message) {
        try {
            String url = "/bot" + telegramProperties.getBotToken() + "/sendMessage";

            Map<String, Object> body = Map.of(
                    "chat_id", telegramProperties.getChatId(),
                    "text", message
            );

            restClient.post()
                    .uri(url)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send telegram message", e);
            throw new BaseException(400, e.getMessage());
        }
    }
}

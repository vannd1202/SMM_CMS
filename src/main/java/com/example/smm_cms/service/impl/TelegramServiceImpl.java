package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.config.TelegramProperties;
import com.example.smm_cms.entity.OrderEntity;
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



    private void sendTelegramMessage(TelegramProperties.BotConfig botConfig, String message) {
        try {
            String url = "/bot" + botConfig.getBotToken() + "/sendMessage";

            Map<String, Object> body = Map.of(
                    "chat_id", botConfig.getChatId(),
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

    @Override
    @Async("telegramExecutor")
    public void sendNewOrderMessage(OrderEntity order) {
        String message = """
                🆕 CÓ ĐƠN HÀNG MỚI

                Order ID: %s
                User ID: %s
                Service: %s
                Target: %s
                Quantity: %s
                Amount: %s
                Status: %s
                """.formatted(
                order.getId(),
                order.getUser().getId(),
                order.getPanelService().getName(),
                order.getTarget(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus().name()
        );

        sendTelegramMessage(telegramProperties.getNewOrder(), message);

    }

    @Override
    @Async("telegramExecutor")
    public void sendSuccessOrderMessage(OrderEntity order) {
        String message = """
                ✅ ĐƠN HÀNG GỬI PROVIDER THÀNH CÔNG

                Order ID: %s
                Provider Order ID: %s
                User ID: %s
                Service: %s
                Target: %s
                Quantity: %s
                Amount: %s
                Status: %s
                """.formatted(
                order.getId(),
                order.getProviderOrderId(),
                order.getUser().getId(),
                order.getPanelService().getName(),
                order.getTarget(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus().name()
        );

        sendTelegramMessage(telegramProperties.getSuccessOrder(), message);
    }
}

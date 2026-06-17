package com.example.smm_cms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Getter
@Setter
public class TelegramProperties {
    private BotConfig newOrder;
    private BotConfig successOrder;

    @Getter
    @Setter
    public static class BotConfig {
        private String botToken;
        private String chatId;
    }
}

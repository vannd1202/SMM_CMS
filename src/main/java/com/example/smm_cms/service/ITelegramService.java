package com.example.smm_cms.service;

import com.example.smm_cms.entity.OrderEntity;

public interface ITelegramService {
    void sendNewOrderMessage(OrderEntity order);

    void sendSuccessOrderMessage(OrderEntity order);
}

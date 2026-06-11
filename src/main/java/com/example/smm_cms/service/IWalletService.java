package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;

import java.math.BigDecimal;

public interface IWalletService {
    ResponseData<?> deposit(
            Long userId,
            BigDecimal amount,
            String note);

    ResponseData<?> deductOrder(
            Long userId,
            BigDecimal amount,
            Long orderId);

    ResponseData<?> refundOrder(
            Long userId,
            BigDecimal amount,
            Long orderId,
            String reason);
}

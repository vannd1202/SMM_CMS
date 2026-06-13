package com.example.smm_cms.controller;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.notify.MessageRequest;
import com.example.smm_cms.service.ITelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.PUBLIC + "/telegram")
public class TelegramController {
    private final ITelegramService telegramService;

    @PostMapping("/send")
    public ResponseEntity<ResponseData<?>> testTelegram(@RequestBody MessageRequest messageRequest) {
        telegramService.sendTelegramMessage(messageRequest.getMessage());

        ResponseData<?> responseData = new ResponseData<>();

        responseData.setCode(200);
        responseData.setMessage("Send telegram message success");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}

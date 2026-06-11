package com.example.smm_cms.controller.admin;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.wallet.TopupRequest;
import com.example.smm_cms.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PUBLIC+"/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final IWalletService walletService;

    @PostMapping("/topup")
    public ResponseData<?> topup(
            @RequestBody TopupRequest request) {

        return walletService.deposit(
                request.getUserId(),
                request.getAmount(),
                "Admin topup");


    }
}

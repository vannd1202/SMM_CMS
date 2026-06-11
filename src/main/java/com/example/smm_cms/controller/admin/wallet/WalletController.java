package com.example.smm_cms.controller.admin.wallet;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.wallet.SearchWalletTransactionRequest;
import com.example.smm_cms.dto.request.wallet.TopupRequest;
import com.example.smm_cms.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/transactions")
    public ResponseData<?> transactions(@ParameterObject SearchWalletTransactionRequest request) {
        return walletService.searchTransactions(request);
    }
}

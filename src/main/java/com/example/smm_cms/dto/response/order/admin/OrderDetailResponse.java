package com.example.smm_cms.dto.response.order.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.example.smm_cms.dto.response.wallet.WalletTransactionResponse;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailResponse {
    /**
     * Thông tin chính của Order
     */
    private OrderAdminResponse order;

    /**
     * Lịch sử thay đổi trạng thái
     */
    private List<OrderHistoryResponse> histories;

    /**
     * Lịch sử giao dịch ví liên quan
     * (ORDER, REFUND...)
     */
    private List<WalletTransactionResponse> walletTransactions;
}

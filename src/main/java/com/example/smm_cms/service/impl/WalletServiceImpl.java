package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.TransactionType;
import com.example.smm_cms.entity.User;
import com.example.smm_cms.entity.WalletTransactionEntity;
import com.example.smm_cms.repository.UserRepository;
import com.example.smm_cms.repository.WalletTransactionRepository;
import com.example.smm_cms.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends BaseService implements IWalletService {
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ResponseData<?> deposit(Long userId, BigDecimal amount, String note) {
        String logPrefix = "[WalletServiceImpl][deposit] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->new BaseException(400,"Không tìm thấy người dùng"));

            BigDecimal before =
                    user.getBalance();

            BigDecimal after =
                    before.add(amount);

            user.setBalance(after);

            userRepository.save(user);

            WalletTransactionEntity tx =
                    new WalletTransactionEntity();

            tx.setUserId(userId);

            tx.setAmount(amount);

            tx.setBalanceBefore(before);

            tx.setBalanceAfter(after);

            tx.setType(
                    TransactionType.DEPOSIT);

            tx.setNote(note);

            tx.setCreatedDate(
                    LocalDateTime.now());

            walletTransactionRepository.save(tx);
            responseData.setData("Nạp tiền thành công cho "+user.getUsername());

        }catch (BaseException be){
            LOGGER.error(logPrefix + be.getMessage());
            responseData.setData(be.getMessage());
        }
        catch (Exception e){
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setData("Nạp tiền thất bại");
        }
        return responseData;
    }
    @Transactional
    @Override
    public ResponseData<?> deductOrder(Long userId, BigDecimal amount, Long orderId) {
        String logPrefix = "[WalletServiceImpl][deductOrder] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() -> new BaseException(400, "Không tìm thấy người dùng"));

            if (user.getBalance()
                    .compareTo(amount) < 0) {

                throw new BaseException(
                        400,
                        "Số dư không đủ");
            }

            BigDecimal before =
                    user.getBalance();

            BigDecimal after =
                    before.subtract(amount);

            user.setBalance(after);

            userRepository.save(user);

            WalletTransactionEntity tx =
                    new WalletTransactionEntity();

            tx.setUserId(userId);

            tx.setAmount(amount.negate());

            tx.setBalanceBefore(before);

            tx.setBalanceAfter(after);

            tx.setType(
                    TransactionType.ORDER);

            tx.setReferenceId(orderId);

            tx.setCreatedDate(
                    LocalDateTime.now());

            walletTransactionRepository.save(tx);
            responseData.setData("Trừ tiền thành công cho "+user.getUsername());
        }catch (BaseException be){
            LOGGER.error(logPrefix + be.getMessage());
            responseData.setData(be.getMessage());
        }
        catch (Exception e){
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setData("Trừ tiền thất bại");
        }
        return responseData;
    }
    @Transactional
    @Override
    public ResponseData<?> refundOrder(Long userId, BigDecimal amount, Long orderId, String reason) {
        String logPrefix = "[WalletServiceImpl][refundOrder] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() -> new BaseException(400, "Không tìm thấy người dùng"));

            BigDecimal before =
                    user.getBalance();

            BigDecimal after =
                    before.add(amount);

            user.setBalance(after);

            userRepository.save(user);

            WalletTransactionEntity tx =
                    new WalletTransactionEntity();

            tx.setUserId(userId);

            tx.setAmount(amount);

            tx.setBalanceBefore(before);

            tx.setBalanceAfter(after);

            tx.setType(
                    TransactionType.REFUND);

            tx.setReferenceId(orderId);

            tx.setNote(reason);

            tx.setCreatedDate(
                    LocalDateTime.now());

            walletTransactionRepository.save(tx);
            responseData.setData("Hoàn tiền thành công cho "+user.getUsername());
        }catch (BaseException be){
            LOGGER.error(logPrefix + be.getMessage());
            responseData.setData(be.getMessage());
        }
        catch (Exception e){
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setData("Hoàn tiền thất bại");
        }
        return responseData;
    }


}

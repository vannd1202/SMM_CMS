package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.common.TransactionType;
import com.example.smm_cms.dto.request.wallet.SearchWalletTransactionRequest;
import com.example.smm_cms.dto.response.wallet.WalletTransactionResponse;
import com.example.smm_cms.entity.User;
import com.example.smm_cms.entity.WalletTransactionEntity;
import com.example.smm_cms.repository.UserRepository;
import com.example.smm_cms.repository.WalletTransactionRepository;
import com.example.smm_cms.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public ResponseData<?> searchTransactions(SearchWalletTransactionRequest request) {
        String logPrefix =
                "[WalletServiceImpl][searchTransactions] - ";

        ResponseData<ResponsePage<WalletTransactionResponse>>
                responseData = new ResponseData<>();
        try {

            Specification<WalletTransactionEntity> spec =
                    (root, query, cb) -> cb.conjunction();

            if (request.getUserId() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("user").get("id"),
                                        request.getUserId()));
            }

            if (request.getType() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("type"),
                                        request.getType()));
            }

            if (request.getFromDate() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.greaterThanOrEqualTo(
                                        root.get("createdDate"),
                                        request.getFromDate()
                                                .atStartOfDay()));
            }

            if (request.getToDate() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.lessThanOrEqualTo(
                                        root.get("createdDate"),
                                        request.getToDate()
                                                .atTime(
                                                        23,
                                                        59,
                                                        59)));
            }

            Page<WalletTransactionEntity> page =
                    walletTransactionRepository.findAll(
                            spec,
                            request.pageable());

            List<WalletTransactionResponse> content =
                    page.getContent()
                            .stream()
                            .map(this::toResponse)
                            .toList();
            responseData.setData(new ResponsePage<>(page, content));

        } catch (Exception e) {

            LOGGER.error(
                    logPrefix + e.getMessage(),
                    e);

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }
        return responseData;
    }

    private WalletTransactionResponse toResponse(
            WalletTransactionEntity entity) {

        return WalletTransactionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUser().getUsername())
                .type(entity.getType())
                .amount(entity.getAmount())
                .balanceBefore(
                        entity.getBalanceBefore())
                .balanceAfter(
                        entity.getBalanceAfter())
                .referenceId(
                        entity.getReferenceId())
                .note(entity.getNote())
                .createdDate(
                        entity.getCreatedDate())
                .build();
    }


}

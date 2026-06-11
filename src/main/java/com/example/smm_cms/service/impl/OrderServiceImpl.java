package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.dto.request.order.CreateOrderRequest;
import com.example.smm_cms.dto.request.order.SearchOrderRequest;
import com.example.smm_cms.dto.request.order.admin.SearchOrderAdminRequest;
import com.example.smm_cms.dto.response.order.CreateOrderResponse;
import com.example.smm_cms.dto.response.order.OrderResponse;
import com.example.smm_cms.dto.response.order.ProviderCreateOrderResponse;
import com.example.smm_cms.dto.response.order.ProviderStatusResponse;
import com.example.smm_cms.dto.response.order.admin.OrderAdminResponse;
import com.example.smm_cms.dto.response.order.admin.OrderDetailResponse;
import com.example.smm_cms.dto.response.order.admin.OrderHistoryResponse;
import com.example.smm_cms.dto.response.order.admin.OrderStatisticResponse;
import com.example.smm_cms.dto.response.wallet.WalletTransactionResponse;
import com.example.smm_cms.entity.*;
import com.example.smm_cms.repository.*;
import com.example.smm_cms.service.IOrderService;
import com.example.smm_cms.service.IWalletService;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl extends BaseService implements IOrderService {
    private final OrderRepository orderRepository;
    private final PanelRepository panelServiceRepository;
    private final ServiceMappingRepository serviceMappingRepository;
    private final ProviderClient providerClient;
    private final OrderHistoryRepository orderHistoryRepository;
    private final IWalletService walletService;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    @Transactional
    public ResponseData<?> create(
            CreateOrderRequest request) {

        String logPrefix =
                "[OrderServiceImpl][create] - ";

        ResponseData<CreateOrderResponse> responseData =
                new ResponseData<>();

        try {
            User user =
                    userRepository
                            .findById(
                                    request.getUserId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "User không tồn tại"));
            /*
             * 1. Validate Panel Service
             */
            PanelServiceEntity panelService =
                    panelServiceRepository
                            .findById(
                                    request.getPanelServiceId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Panel service không tồn tại"));

            if (!Boolean.TRUE.equals(
                    panelService.getActive())) {

                throw new BaseException(
                        400,
                        "Dịch vụ đang tạm ngưng");
            }

            /*
             * 2. Tìm provider ưu tiên cao nhất
             */
            ServiceMappingEntity mapping =
                    serviceMappingRepository
                            .findFirstByPanelServiceIdOrderByPriorityAsc(
                                    panelService.getId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Không tìm thấy provider"));

            ServiceEntity providerService =
                    mapping.getProviderService();

            ProviderEntity provider =
                    providerService.getProvider();

            /*
             * 3. Tính tiền
             */
            BigDecimal amount =
                    panelService.getSellPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            request.getQuantity()))
                            .divide(
                                    BigDecimal.valueOf(1000),
                                    2,
                                    RoundingMode.HALF_UP);

            /*
             * 4. Tạo Order trước
             */
            OrderEntity order =
                    new OrderEntity();
            order.setUser(user);
            order.setPanelService(
                    panelService);

            order.setMapping(
                    mapping);

            order.setTarget(
                    request.getTarget());

            order.setQuantity(
                    request.getQuantity());

            order.setCostPrice(
                    providerService.getRate());

            order.setSellPrice(
                    panelService.getSellPrice());

            order.setAmount(
                    amount);

            order.setStatus(
                    OrderStatus.CREATING);

            order.setRefunded(false);

            orderRepository.save(order);

            /*
             * 5. Trừ tiền Wallet
             * TODO Sprint Wallet
             */
        walletService.deductOrder(
                user.getId(),
                amount,
                order.getId());

            /*
             * 6. Gọi Provider
             */
            try {

                ProviderCreateOrderResponse providerResponse =
                        providerClient.createOrder(
                                provider.getApiUrl(),
                                provider.getApiKey(),
                                providerService.getProviderServiceId(),
                                request.getTarget(),
                                request.getQuantity());

                if (providerResponse == null) {

                    throw new BaseException(
                            500,
                            "Provider không phản hồi");
                }

                if (providerResponse.getError() != null) {

                    throw new BaseException(
                            400,
                            providerResponse.getError());
                }

                if (providerResponse.getOrder() == null) {

                    throw new BaseException(
                            400,
                            "Provider không trả về order id");
                }

                /*
                 * 7. Thành công
                 */
                order.setProviderOrderId(
                        providerResponse.getOrder());

                order.setStatus(
                        OrderStatus.PENDING);

                orderRepository.save(order);

            } catch (Exception ex) {

                /*
                 * 8. Refund nếu cần
                 */
                LOGGER.info("chạy hoàn tiền");
                refundOrderIfNeeded(
                        order,
                        ex.getMessage());

                throw ex;
            }

            CreateOrderResponse response =
                    CreateOrderResponse.builder()
                            .id(
                                    order.getId())
                            .providerOrderId(
                                    order.getProviderOrderId())
                            .status(
                                    order.getStatus().name())
                            .amount(
                                    order.getAmount())
                            .build();

            responseData.setCode(0);
            responseData.setMessage("Successful!");
            responseData.setData(response);

        } catch (BaseException ex) {

            LOGGER.warn(
                    logPrefix + ex.getMessage());

            responseData.setCode(
                    ex.getCode());

            responseData.setMessage(
                    ex.getMessage());

        } catch (Exception ex) {

            LOGGER.error(
                    logPrefix + ex.getMessage(),
                    ex);

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }

        return responseData;
    }
    @Override
    public ResponseData<?> getById(Long id) {
        String logPrefix = "[OrderServiceImpl][getById] - ";
        ResponseData<OrderEntity> responseData = new ResponseData<>();
        try {
            OrderEntity order =
                    orderRepository.findById(id)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Order không tồn tại"));
            responseData.setData(order);
        }catch (BaseException e) {
            LOGGER.warn(logPrefix + e.getMessage());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage(), e);
        }
        return responseData;
    }

    @Override
    public ResponseData<?> search(SearchOrderRequest request) {
        String logPrefix = "[OrderServiceImpl][search] - ";
        ResponseData<ResponsePage<OrderResponse>> responseData = new ResponseData<>();
        try {
            Page<OrderEntity> page =
                    orderRepository.findAll(
                            request.pageable());

            List<OrderResponse> content =
                    page.getContent()
                            .stream()
                            .map(this::toResponse)
                            .toList();

            responseData.setData(new ResponsePage<>(page, content));

        } catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage(), e);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData<?> adminSearch(SearchOrderAdminRequest request) {
        String logPrefix =
                "[OrderServiceImpl][searchAdmin] - ";

        ResponseData<ResponsePage<OrderAdminResponse>>
                responseData = new ResponseData<>();

        try {

            Specification<OrderEntity> spec =
                    (root, query, cb) -> cb.conjunction();

            if (request.getId() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("id"),
                                        request.getId()));
            }

            if (StringUtils.hasText(
                    request.getProviderOrderId())) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("providerOrderId"),
                                        request.getProviderOrderId()));
            }

            if (request.getUserId() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("user")
                                                .get("id"),
                                        request.getUserId()));
            }

            if (request.getPanelServiceId() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("panelService")
                                                .get("id"),
                                        request.getPanelServiceId()));
            }

            if (request.getStatus() != null) {

                spec = spec.and(
                        (root, query, cb) ->
                                cb.equal(
                                        root.get("status"),
                                        request.getStatus()));
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

            Page<OrderEntity> page =
                    orderRepository.findAll(
                            spec,
                            request.pageable());

            List<OrderAdminResponse> content =
                    page.getContent()
                            .stream()
                            .map(this::toAdminResponse)
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

    @Override
    public ResponseData<?> getDetail(Long orderId) {
        String logPrefix =
                "[OrderServiceImpl][getDetail] - ";

        ResponseData<OrderDetailResponse>
                responseData = new ResponseData<>();

        try {

            OrderEntity order =
                    orderRepository.findById(orderId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Order không tồn tại"));

            List<OrderHistoryResponse>
                    histories =
                    orderHistoryRepository
                            .findByOrderIdOrderByCreatedDateDesc(
                                    orderId)
                            .stream()
                            .map(this::toHistoryResponse)
                            .toList();

            List<WalletTransactionResponse>
                    walletTransactions =
                    walletTransactionRepository
                            .findByReferenceIdOrderByCreatedDateDesc(
                                    orderId)
                            .stream()
                            .map(this::toWalletResponse)
                            .toList();

            OrderDetailResponse response =
                    OrderDetailResponse.builder()
                            .order(
                                    toAdminResponse(
                                            order))
                            .histories(
                                    histories)
                            .walletTransactions(
                                    walletTransactions)
                            .build();

            responseData.setData(response);

        } catch (BaseException e) {

            LOGGER.warn(
                    logPrefix + e.getMessage());

            responseData.setCode(
                    e.getCode());

            responseData.setMessage(
                    e.getMessage());

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


    private OrderResponse toResponse(
            OrderEntity entity) {

        return OrderResponse.builder()
                .id(entity.getId())

                .panelServiceId(
                        entity.getPanelService() != null
                                ? entity.getPanelService().getId()
                                : null)

                .panelServiceName(
                        entity.getPanelService() != null
                                ? entity.getPanelService().getName()
                                : null)

                .providerOrderId(
                        entity.getProviderOrderId())

                .target(
                        entity.getTarget())

                .quantity(
                        entity.getQuantity())

                .costPrice(
                        entity.getCostPrice())

                .sellPrice(
                        entity.getSellPrice())

                .amount(
                        entity.getAmount())

                .startCount(
                        entity.getStartCount())

                .remains(
                        entity.getRemains())

                .providerStatus(
                        entity.getProviderStatus())

                .status(
                        entity.getStatus())

                .createdDate(
                        entity.getCreatedDate())

                .build();
    }

    @Transactional
    public ResponseData<?> syncOrder(Long orderId) {
        String logPrefix = "[OrderServiceImpl][syncOrder] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            OrderEntity order =
                    orderRepository.findById(orderId)
                            .orElseThrow();

            ServiceEntity providerService =
                    order.getMapping()
                            .getProviderService();

            ProviderEntity provider =
                    providerService.getProvider();

            ProviderStatusResponse response =
                    providerClient.getStatus(
                            provider.getApiUrl(),
                            provider.getApiKey(),
                            order.getProviderOrderId());

            OrderStatus oldStatus =
                    order.getStatus();

            OrderStatus newStatus =
                    mapStatus(
                            response.getStatus());

            if (oldStatus != newStatus) {

                saveHistory(
                        order,
                        oldStatus,
                        newStatus);

                order.setStatus(newStatus);
            }

            order.setProviderStatus(
                    response.getStatus());

            order.setStartCount(
                    response.getStartCount());

            order.setRemains(
                    response.getRemains());

            orderRepository.save(order);
            responseData.setMessage("Sync order thành công");

        } catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage(), e);
            responseData.setMessage("Sync order thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> getHistory(Long orderId) {

        String logPrefix =
                "[OrderServiceImpl][getHistory] - ";

        ResponseData<List<OrderHistoryResponse>>
                responseData = new ResponseData<>();

        try {

            OrderEntity order =
                    orderRepository.findById(orderId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Order không tồn tại"));
            List<OrderHistoryResponse>
                    histories =
                    orderHistoryRepository
                            .findByOrderIdOrderByCreatedDateDesc(
                                    order.getId())
                            .stream()
                            .map(this::toHistoryResponse)
                            .toList();

            responseData.setData(
                    histories);

        } catch (BaseException e) {

            LOGGER.warn(
                    logPrefix + e.getMessage());

            responseData.setCode(
                    e.getCode());

            responseData.setMessage(
                    e.getMessage());
        }
        catch (Exception e) {

            LOGGER.error(
                    logPrefix + e.getMessage(),
                    e);

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> refund(Long orderId, String reason) {

        ResponseData<String> responseData =
                new ResponseData<>();

        try {
            OrderEntity order =
                    orderRepository.findById(orderId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Order không tồn tại"));
            if (Boolean.TRUE.equals(
                    order.getRefunded())) {

                throw new BaseException(
                        400,
                        "Order đã được hoàn tiền");
            }

            walletService.refundOrder(
                    order.getUser().getId(),
                    order.getAmount(),
                    order.getId(),
                    reason);

            OrderStatus oldStatus =
                    order.getStatus();

            order.setRefunded(true);
            order.setRefundedDate(
                    LocalDateTime.now());
            order.setRefundReason(reason);
            order.setStatus(
                    OrderStatus.FAILED);

            orderRepository.save(order);

            saveHistory(
                    order,
                    oldStatus,
                    OrderStatus.FAILED,
                    "Manual refund: " + reason);

            responseData.setData(
                    "Refund thành công");

        } catch (BaseException e) {

            responseData.setCode(
                    e.getCode());

            responseData.setMessage(
                    e.getMessage());
        }catch (Exception e) {
            LOGGER.error(
                    "Refund order failed: "
                            + orderId
                            + " - "
                            + e.getMessage(),
                    e);

            responseData.setCode(500);
            responseData.setMessage(
                    "Refund thất bại");
        }


        return responseData;
    }

    @Override
    public ResponseData<?> statistic() {
        String logPrefix =
                "[OrderServiceImpl][statistic] - ";

        ResponseData<OrderStatisticResponse>
                responseData = new ResponseData<>();

        try {

            BigDecimal revenue =
                    Optional.ofNullable(
                                    orderRepository.sumRevenue())
                            .orElse(BigDecimal.ZERO);

            BigDecimal cost =
                    Optional.ofNullable(
                                    orderRepository.sumCost())
                            .orElse(BigDecimal.ZERO);

            BigDecimal profit =
                    revenue.subtract(cost);
            LOGGER.info(
                    "Revenue={}, Cost={}",
                    revenue,
                    cost);
            OrderStatisticResponse response =
                    OrderStatisticResponse.builder()
                            .creating(
                                    orderRepository.countByStatus(
                                            OrderStatus.CREATING))
                            .pending(
                                    orderRepository.countByStatus(
                                            OrderStatus.PENDING))
                            .processing(
                                    orderRepository.countByStatus(
                                            OrderStatus.PROCESSING))
                            .completed(
                                    orderRepository.countByStatus(
                                            OrderStatus.COMPLETED))
                            .partial(
                                    orderRepository.countByStatus(
                                            OrderStatus.PARTIAL))
                            .cancelled(
                                    orderRepository.countByStatus(
                                            OrderStatus.CANCELED))
                            .failed(
                                    orderRepository.countByStatus(
                                            OrderStatus.FAILED))
                            .refunded(
                                    orderRepository.countByRefundedTrue())
                            .totalRevenue(
                                    revenue.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .totalCost(
                                    cost.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .totalProfit(
                                    profit.setScale(
                                            2,
                                            RoundingMode.HALF_UP))
                            .build();

            responseData.setData(
                    response);

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


    private OrderStatus mapStatus(
            String providerStatus) {

        return switch (
                providerStatus.toUpperCase()) {

            case "PENDING" -> OrderStatus.PENDING;

            case "PROCESSING" -> OrderStatus.PROCESSING;

            case "IN PROGRESS" -> OrderStatus.IN_PROGRESS;

            case "COMPLETED" -> OrderStatus.COMPLETED;

            case "PARTIAL" -> OrderStatus.PARTIAL;

            case "CANCELED" -> OrderStatus.CANCELED;

            default -> OrderStatus.FAILED;
        };
    }


    private void saveHistory(
            OrderEntity order,
            OrderStatus oldStatus,
            OrderStatus newStatus) {

        OrderHistoryEntity history =
                new OrderHistoryEntity();

        history.setOrder(order);

        history.setOldStatus(
                oldStatus.name());

        history.setNewStatus(
                newStatus.name());

        history.setNote(
                "Sync từ provider");

        orderHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void syncPendingOrders() {
        LOGGER.info("Bắt đầu chạy đồng bộ order");
        List<OrderEntity> orders =
                orderRepository.findByStatusIn(
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PROCESSING,
                                OrderStatus.IN_PROGRESS));

        for (OrderEntity order : orders) {

            try {

                syncOrder(order.getId());

            } catch (Exception ex) {

                LOGGER.warn(
                        "Sync order failed: "
                                + order.getId()
                                + " - "
                                + ex.getMessage());
            }
        }
    }

    @Transactional
    public void refundOrderIfNeeded(
            OrderEntity order,
            String reason) {

        if (Boolean.TRUE.equals(
                order.getRefunded())) {

            return;
        }
        OrderStatus oldStatus =
                order.getStatus();

        walletService.refundOrder(
                order.getUser().getId(),
                order.getAmount(),
                order.getId(),
                reason);

        order.setRefunded(true);

        order.setRefundedDate(
                LocalDateTime.now());

        order.setRefundReason(
                reason);

        order.setStatus(
                OrderStatus.FAILED);

        order.setFailedDate(
                LocalDateTime.now());
        orderRepository.save(order);

        saveHistory(
                order,
                oldStatus,
                OrderStatus.FAILED,
                reason);
    }


    private void saveHistory(
            OrderEntity order,
            OrderStatus oldStatus,
            OrderStatus newStatus,
            String note) {

        OrderHistoryEntity history =
                new OrderHistoryEntity();

        history.setOrder(order);

        history.setOldStatus(
                oldStatus.name());

        history.setNewStatus(
                newStatus.name());

        history.setNote(note);

        orderHistoryRepository.save(history);
    }

    private OrderAdminResponse toAdminResponse(
            OrderEntity entity) {

        return OrderAdminResponse.builder()
                .id(entity.getId())
                .userId(
                        entity.getUser()
                                .getId())
                .username(
                        entity.getUser()
                                .getUsername())
                .panelServiceName(
                        entity.getPanelService()
                                .getName())
                .providerName(
                        entity.getMapping()
                                .getProviderService()
                                .getProvider()
                                .getName())
                .providerOrderId(
                        entity.getProviderOrderId())
                .target(
                        entity.getTarget())
                .quantity(
                        entity.getQuantity())
                .amount(
                        entity.getAmount())
                .status(
                        entity.getStatus())
                .refunded(
                        entity.getRefunded())
                .createdDate(
                        entity.getCreatedDate())
                .build();
    }

    private OrderHistoryResponse toHistoryResponse(
            OrderHistoryEntity entity) {

        return OrderHistoryResponse.builder()
                .id(entity.getId())
                .oldStatus(
                        entity.getOldStatus())
                .newStatus(
                        entity.getNewStatus())
                .note(
                        entity.getNote())
                .createdDate(
                        entity.getCreatedDate())
                .build();
    }

    private WalletTransactionResponse toWalletResponse(
            WalletTransactionEntity entity) {

        return WalletTransactionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(
                        entity.getUser() != null
                                ? entity.getUser().getUsername()
                                : null)
                .type(entity.getType())
                .amount(entity.getAmount())
                .balanceBefore(
                        entity.getBalanceBefore())
                .balanceAfter(
                        entity.getBalanceAfter())
                .referenceId(
                        entity.getReferenceId())
                .note(
                        entity.getNote())
                .createdDate(
                        entity.getCreatedDate())
                .build();
    }
}

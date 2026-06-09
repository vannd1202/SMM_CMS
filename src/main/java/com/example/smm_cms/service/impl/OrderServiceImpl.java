package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.dto.request.order.CreateOrderRequest;
import com.example.smm_cms.dto.request.order.SearchOrderRequest;
import com.example.smm_cms.dto.response.order.CreateOrderResponse;
import com.example.smm_cms.dto.response.order.OrderResponse;
import com.example.smm_cms.dto.response.order.ProviderCreateOrderResponse;
import com.example.smm_cms.dto.response.order.ProviderStatusResponse;
import com.example.smm_cms.entity.*;
import com.example.smm_cms.repository.OrderHistoryRepository;
import com.example.smm_cms.repository.OrderRepository;
import com.example.smm_cms.repository.PanelRepository;
import com.example.smm_cms.repository.ServiceMappingRepository;
import com.example.smm_cms.service.IOrderService;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl extends BaseService implements IOrderService {
    private final OrderRepository orderRepository;

    private final PanelRepository panelServiceRepository;

    private final ServiceMappingRepository serviceMappingRepository;

    private final ProviderClient providerClient;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public ResponseData<?> create(CreateOrderRequest request) {
        String logPrefix = "[OrderServiceImpl][create] - ";
        ResponseData<CreateOrderResponse> responseData = new ResponseData<>();
        try {
            PanelServiceEntity panelService =
                    panelServiceRepository
                            .findById(request.getPanelServiceId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Panel service không tồn tại"));
            if (!panelService.getActive()) {

                throw new BaseException(
                        400,
                        "Dịch vụ đang tạm ngưng");
            }
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
            BigDecimal amount =
                    panelService.getSellPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            request.getQuantity()))
                            .divide(
                                    BigDecimal.valueOf(1000));

            OrderEntity order =
                    new OrderEntity();

            order.setPanelService(panelService);
            order.setMapping(mapping);
//            order.setProviderOrderId();
            order.setTarget(request.getTarget());

            order.setQuantity(request.getQuantity());

            order.setCostPrice(
                    providerService.getRate());

            order.setSellPrice(
                    panelService.getSellPrice());

            order.setAmount(amount);

            order.setProviderOrderId(
                    providerResponse.getOrder());

            order.setStatus(
                    OrderStatus.PENDING);

            orderRepository.save(order);

            CreateOrderResponse response =
                    CreateOrderResponse.builder()
                            .id(order.getId())
                            .providerOrderId(
                                    order.getProviderOrderId())
                            .status(
                                    order.getStatus().name())
                            .amount(
                                    order.getAmount())
                            .build();
            responseData.setData(response);

        } catch (BaseException e) {
            LOGGER.warn(logPrefix + e.getMessage());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage(), e);
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


}

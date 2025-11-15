package com.sanskar.billingsoftware.service.impl;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sanskar.billingsoftware.entity.OrderEntity;
import com.sanskar.billingsoftware.entity.OrderItemEntity;
import com.sanskar.billingsoftware.io.*;
import com.sanskar.billingsoftware.respository.OrderRepository;
import com.sanskar.billingsoftware.service.OrderService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        OrderEntity newOrder = convertToOrderEntity(request);

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH ? PaymentDetails.PaymentStatus.COMPLETED : PaymentDetails.PaymentStatus.PENDING);

        newOrder.setPaymentDetails(paymentDetails);

        List<OrderItemEntity> orderItems = request.getCartItems().stream().map((item) -> convertToOrderItemEntity(item)).collect(Collectors.toList());

        newOrder.setItems(orderItems);

        newOrder = orderRepository.save(newOrder);

        return convertToReponse(newOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEntity existingOrder = orderRepository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(existingOrder);
    }

    @Override
    public List<OrderResponse> getLatestOrder() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map((order) -> convertToReponse(order)).collect(Collectors.toList());
    }

    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) throws RazorpayException {
        OrderEntity existingOrder = orderRepository.findByOrderId(request.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!verifyRazorpaySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            throw new RuntimeException("Payment Verification failed");
        }

        PaymentDetails paymentDetails = existingOrder.getPaymentDetails();

        paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());

        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

        existingOrder = orderRepository.save(existingOrder);

        return convertToReponse(existingOrder);
    }

    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderRepository.sumSalesByDate(date);
    }

    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderRepository.countByOrderDate(date);
    }

    @Override
    public List<OrderResponse> findRecentOrders() {
        return orderRepository.findRecentOrders(PageRequest.of(0, 5)).stream().map((order) -> convertToReponse(order)).collect(Collectors.toList());
    }

    private boolean verifyRazorpaySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {

        boolean isValid = Utils.verifyPaymentSignature(
                new JSONObject()
                        .put("razorpay_order_id", razorpayOrderId)
                        .put("razorpay_payment_id", razorpayPaymentId)
                        .put("razorpay_signature", razorpaySignature),
                razorpayKeySecret
        );

        if (isValid) {
            return true;
        }
        return false;
    }

    private OrderEntity convertToOrderEntity(OrderRequest request) {

        return OrderEntity.builder()
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
    }

    private OrderResponse convertToReponse(OrderEntity newOrder) {

        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .subtotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
                .paymentMethod(newOrder.getPaymentMethod())
                .items(newOrder.getItems().stream().map((item) -> convertToItemResponse(item)).collect(Collectors.toList()))
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(newOrder.getCreatedAt())
                .build();
    }

    private OrderItemResponse convertToItemResponse(OrderItemEntity item) {

        return OrderItemResponse.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    private OrderItemEntity convertToOrderItemEntity(OrderItemRequest item) {

        return OrderItemEntity.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}

package com.sanskar.billingsoftware.service;

import com.razorpay.RazorpayException;
import com.sanskar.billingsoftware.io.OrderRequest;
import com.sanskar.billingsoftware.io.OrderResponse;
import com.sanskar.billingsoftware.io.PaymentVerificationRequest;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    void deleteOrder(String orderId);

    List<OrderResponse> getLatestOrder();

    OrderResponse verifyPayment(PaymentVerificationRequest request) throws RazorpayException;

    Double sumSalesByDate(LocalDate date);

    Long countByOrderDate(LocalDate date);

    List<OrderResponse> findRecentOrders();
}

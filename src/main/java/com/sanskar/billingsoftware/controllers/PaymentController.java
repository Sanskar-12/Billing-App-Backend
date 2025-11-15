package com.sanskar.billingsoftware.controllers;

import com.razorpay.RazorpayException;
import com.sanskar.billingsoftware.io.OrderResponse;
import com.sanskar.billingsoftware.io.PaymentRequest;
import com.sanskar.billingsoftware.io.PaymentVerificationRequest;
import com.sanskar.billingsoftware.io.RazorpayOrderResponse;
import com.sanskar.billingsoftware.service.OrderService;
import com.sanskar.billingsoftware.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request) throws RazorpayException {
        return razorpayService.createOrder(request.getAmount(), request.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) throws RazorpayException {
        return orderService.verifyPayment(request);
    }
}

package com.sanskar.billingsoftware.service;

import com.razorpay.RazorpayException;
import com.sanskar.billingsoftware.io.RazorpayOrderResponse;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}

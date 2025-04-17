package com.travel_payment.cnpm.dto.request.tour;

import lombok.Data;

@Data
public class OrderConfirmRequest {
    private String status;
    private String orderCode;
}
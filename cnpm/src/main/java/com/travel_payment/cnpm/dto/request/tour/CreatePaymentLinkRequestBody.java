package com.travel_payment.cnpm.dto.request.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkRequestBody {
    private Integer tourId;
    private String productName;
    private String description;
    private int quantity;
    private int price;
    private String returnUrl;
    private String cancelUrl;
}

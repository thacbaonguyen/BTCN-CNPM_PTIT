package com.travel_payment.cnpm.services;

import com.travel_payment.cnpm.dto.request.tour.CreatePaymentLinkRequestBody;
import com.travel_payment.cnpm.dto.request.tour.OrderConfirmRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.Order;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    Order createOrder(CreatePaymentLinkRequestBody requestBody);

    void saveOrder(Order order);

    Order createFreeOrder(Tour tour);

    ResponseEntity<ApiResponse> updateStatus(OrderConfirmRequest request);
}

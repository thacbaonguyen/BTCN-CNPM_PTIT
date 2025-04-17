package com.travel_payment.cnpm.services;


import com.travel_payment.cnpm.dto.request.tour.CreatePaymentLinkRequestBody;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.Order;

public interface OrderDetailService {
    void createOrderDetail(Order order, CreatePaymentLinkRequestBody requestBody);

    void createOrderDetailFree(Order order, Tour tour);
}

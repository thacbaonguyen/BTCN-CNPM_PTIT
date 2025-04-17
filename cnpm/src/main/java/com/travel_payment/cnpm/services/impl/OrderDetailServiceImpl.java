package com.travel_payment.cnpm.services.impl;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.data.repository.CartRepository;
import com.travel_payment.cnpm.data.repository.OrderDetailsRepository;
import com.travel_payment.cnpm.data.repository.TourRepository;
import com.travel_payment.cnpm.dto.request.tour.CreatePaymentLinkRequestBody;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.Order;
import com.travel_payment.cnpm.entities.reference.OrderDetail;
import com.travel_payment.cnpm.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final TourRepository tourRepository;
    private final JwtFilter jwtFilter;
    @Override
    public void createOrderDetail(Order order, CreatePaymentLinkRequestBody requestBody) {
        Tour tour = tourRepository.findById(requestBody.getTourId()).orElse(null);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setPrice(requestBody.getPrice());
        orderDetail.setQuantity(requestBody.getQuantity());
        orderDetail.setTour(tour);
        orderDetailsRepository.save(orderDetail);
    }

    @Override
    public void createOrderDetailFree(Order order, Tour tour) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setTour(tour);
        orderDetail.setPrice(tour.getPrice());
        orderDetailsRepository.save(orderDetail);
    }


}
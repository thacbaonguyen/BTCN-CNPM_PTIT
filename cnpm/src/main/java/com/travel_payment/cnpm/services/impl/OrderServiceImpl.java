package com.travel_payment.cnpm.services.impl;

import com.travel_payment.cnpm.configurations.CustomUserDetailsService;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.data.repository.CartRepository;
import com.travel_payment.cnpm.data.repository.OrderRepository;
import com.travel_payment.cnpm.dto.request.tour.CreatePaymentLinkRequestBody;
import com.travel_payment.cnpm.dto.request.tour.OrderConfirmRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.core.User;
import com.travel_payment.cnpm.entities.reference.Order;
import com.travel_payment.cnpm.entities.reference.ShoppingCart;
import com.travel_payment.cnpm.enums.PaymentStatus;
import com.travel_payment.cnpm.exceptions.common.AppException;
import com.travel_payment.cnpm.services.OrderDetailService;
import com.travel_payment.cnpm.services.OrderService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final JwtFilter jwtFilter;
    private final OrderDetailService orderDetailService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CartRepository shoppingCartRepository;
    @Override
    public Order createOrder(CreatePaymentLinkRequestBody requestBody) {
        User user = customUserDetailsService.getUserDetails();

        Order order = new Order();
        order.setTotalAmount(requestBody.getPrice()*requestBody.getQuantity());
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.pending);
        order.setUser(user);
        return orderRepository.save(order);
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order createFreeOrder(Tour tour) {
        Order order = new Order();
        order.setTotalAmount(tour.getPrice());
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.paid);
        order.setUser(customUserDetailsService.getUserDetails());
        try {
            Order orderSave = orderRepository.save(order);
            orderDetailService.createOrderDetailFree(orderSave, tour);
            return orderSave;
        }
        catch (AppException e) {
            throw new AppException("Error create order free");
        }

    }

    @Override
    public ResponseEntity<ApiResponse> updateStatus(OrderConfirmRequest request) {
        Order order = orderRepository.findByOrderCode(request.getOrderCode());
        if (order == null) {
            return TravelResponse.generateResponse(null, "Cannot found this order", HttpStatus.NOT_FOUND);
        }
        if (request.getStatus().equalsIgnoreCase("pending")) {
            order.setPaymentStatus(PaymentStatus.pending);
            orderRepository.save(order);
        } else if (request.getStatus().equalsIgnoreCase("paid")) {
            order.setPaymentStatus(PaymentStatus.paid);
            orderRepository.save(order);
        } else if (request.getStatus().equalsIgnoreCase("cancelled")) {
            order.setPaymentStatus(PaymentStatus.cancelled);
            orderRepository.save(order);
        }
        shoppingCartRepository.deleteByUser(order.getUser().getUsername());
        return TravelResponse.generateResponse(null, "Success", HttpStatus.OK);
    }
}

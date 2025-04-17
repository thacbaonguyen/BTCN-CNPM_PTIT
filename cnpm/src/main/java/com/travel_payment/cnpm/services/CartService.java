package com.travel_payment.cnpm.services;

import com.travel_payment.cnpm.dto.request.tour.CartRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<ApiResponse> addNewProduct(CartRequest request);

    ResponseEntity<ApiResponse> getCart();

    ResponseEntity<ApiResponse> deleteProductFromCart(Integer courseId);
}

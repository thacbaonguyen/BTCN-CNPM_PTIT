package com.travel_payment.cnpm.controllers;

import com.travel_payment.cnpm.dto.request.tour.CartRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.services.CartService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody CartRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return TravelResponse.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return cartService.addNewProduct(request);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll() {
        return cartService.getCart();
    }

    @DeleteMapping("/delete/{tourId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("tourId") Integer tourId) {
        return cartService.deleteProductFromCart(tourId);
    }
}
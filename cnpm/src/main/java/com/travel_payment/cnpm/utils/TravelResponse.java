package com.travel_payment.cnpm.utils;

import com.travel_payment.cnpm.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class TravelResponse {
    public static <T> ResponseEntity<ApiResponse> generateResponse(T data, String message, HttpStatus status) {
        ApiResponse apiResponse = new ApiResponse(
                LocalDateTime.now(),
                status.value(),
                status == HttpStatus.OK || status == HttpStatus.CREATED ? "success" : "error",
                message,
                data
        );
        return new ResponseEntity<>(apiResponse, status);
    }
}

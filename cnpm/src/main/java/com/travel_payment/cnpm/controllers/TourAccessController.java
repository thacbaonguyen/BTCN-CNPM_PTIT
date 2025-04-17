package com.travel_payment.cnpm.controllers;

import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.services.impl.TourAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my-tour")
@RequiredArgsConstructor
public class TourAccessController {
    private final TourAccessService tourAccessService;
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> myTour() {
        return tourAccessService.myTour();
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<ApiResponse> getTourById(@PathVariable("tourId") Integer tourId) {
        return tourAccessService.getTourById(tourId);
    }
}

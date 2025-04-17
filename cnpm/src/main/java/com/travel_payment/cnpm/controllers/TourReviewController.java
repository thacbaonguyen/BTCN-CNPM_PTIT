package com.travel_payment.cnpm.controllers;

import com.travel_payment.cnpm.dto.request.tour.TourReview;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.TourReviewDTO;
import com.travel_payment.cnpm.services.TourReviewService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tour-review")
@RequiredArgsConstructor
public class TourReviewController {
    private final TourReviewService tourReviewService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertReview(@Valid @RequestBody TourReview request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return TravelResponse.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return tourReviewService.createTourReview(request);
    }

    @GetMapping("/all-review/{tourId}")
    public ResponseEntity<ApiResponse> getAllReview(@PathVariable("tourId") Integer tourId) {
        List<TourReviewDTO> tourReviewDTOS = tourReviewService.getTourReviews(tourId);
        return TravelResponse.generateResponse(tourReviewDTOS, "All review", HttpStatus.OK);
    }
}

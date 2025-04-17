package com.travel_payment.cnpm.services;

import com.travel_payment.cnpm.dto.request.tour.TourReview;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.TourReviewDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TourReviewService {
    ResponseEntity<ApiResponse> createTourReview(TourReview request);

    List<TourReviewDTO> getTourReviews(Integer tourId);
}

package com.travel_payment.cnpm.services;

import com.travel_payment.cnpm.dto.request.tour.TourRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourService {
    ResponseEntity<ApiResponse> createTour(TourRequest request);

    void uploadThumbnail(Integer tourId, MultipartFile file);

    ResponseEntity<ApiResponse> getAllTours(String search, Integer page, Integer pageSize, String order,
                                              String by, Float rating, List<String> durations, Boolean isFree);

    ResponseEntity<ApiResponse> getTourById(int id);

    ResponseEntity<ApiResponse> getAllToursByCategory(Integer categoryId, String search, Integer page,
                                                        Integer pageSize, String order, String by, Float rating, List<String> durations, Boolean isFree);

    ResponseEntity<ApiResponse> updateTour(Integer tourId, TourRequest request);

    ResponseEntity<ApiResponse> deleteTour(Integer tourId);
}

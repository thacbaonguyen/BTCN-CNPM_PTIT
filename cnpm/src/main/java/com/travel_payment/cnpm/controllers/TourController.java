package com.travel_payment.cnpm.controllers;

import com.travel_payment.cnpm.dto.request.tour.TourRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.services.TourService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tour")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody TourRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return TravelResponse.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return tourService.createTour(request);
    }
    @PostMapping("/upload/thumbnail-image/{tour-id}")
    public void uploadFeatureImage(@PathVariable("tour-id") Integer tourId,
                                   @RequestParam("thumbnail") MultipartFile file) {
        tourService.uploadThumbnail(tourId, file);
    }

    @GetMapping("/all-tours")
    public ResponseEntity<ApiResponse> findAllTours(@RequestParam(required = false) String search,
                                                      @RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "15") Integer pageSize,
                                                      @RequestParam(required = false) String order,
                                                      @RequestParam(required = false) String by,
                                                      @RequestParam(required = false) Float rating,
                                                      @RequestParam(value = "duration", required = false) List<String> durations,
                                                      @RequestParam(required = false) Boolean isFree) {

        return tourService.getAllTours(search, page, pageSize, order, by, rating, durations, isFree);
    }

    @GetMapping("/all-tour/category/{categoryId}")
    public ResponseEntity<ApiResponse> findAllTourByCategory(@PathVariable(value = "categoryId", required = false) Integer categoryId,
                                                               @RequestParam(required = false) String search,
                                                               @RequestParam(defaultValue = "1") Integer page,
                                                               @RequestParam(defaultValue = "15") Integer pageSize,
                                                               @RequestParam(required = false) String order,
                                                               @RequestParam(required = false) String by,
                                                               @RequestParam(required = false) Float rating,
                                                               @RequestParam(value = "duration", required = false) List<String> durations,
                                                               @RequestParam(required = false) Boolean isFree) {
        return tourService.getAllToursByCategory(categoryId, search, page, pageSize, order, by, rating, durations, isFree);
    }

    @GetMapping("/tour-details/{id}")
    public ResponseEntity<ApiResponse> findTourById(@PathVariable("id") Integer id) {
        return tourService.getTourById(id);
    }

    @PutMapping("/update/{tourId}")
    public ResponseEntity<ApiResponse> updateTour(@Valid @PathVariable("tourId") Integer tourId,
                                                    @RequestBody TourRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return TravelResponse.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return tourService.updateTour(tourId, request);
    }

    @DeleteMapping("/delete/{tourId}")
    public ResponseEntity<ApiResponse> deleteTour(@PathVariable("tourId") Integer tourId) {
        return tourService.deleteTour(tourId);
    }
}
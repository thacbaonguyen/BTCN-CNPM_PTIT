package com.travel_payment.cnpm.services.impl;
import com.travel_payment.cnpm.data.repository.TourCategoryRepo;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.entities.reference.TourCategory;
import com.travel_payment.cnpm.exceptions.common.AlreadyException;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TourCategoryRepo tourCategoryRepo;

    public ResponseEntity<ApiResponse> getAllCategories(){
        List<TourCategory> list = tourCategoryRepo.findAll();
        return TravelResponse.generateResponse(list, "Get all categories", HttpStatus.OK);
    }
}
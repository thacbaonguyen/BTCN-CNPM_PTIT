package com.travel_payment.cnpm.controllers;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.services.impl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCourseCategory() {
        return categoryService.getAllCategories();
    }
}
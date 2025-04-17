package com.travel_payment.cnpm.dto.request.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourCategoryRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
}

package com.travel_payment.cnpm.dto.request.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequest {
    @NotBlank(message = "Tour title cannot be blank")
    private String title;
    @NotBlank(message = "Excerpt cannot be blank")
    private String excerpt;

    @Size(min = 10, message = "Description must be lest than 10 character")
    private String description;
    private float price;

    private String isActive;
    private int duration;
    private int discount;

    private String departurePoint;
    private LocalDate startDate;
    private LocalDate endDate;

    private int categoryId;
}

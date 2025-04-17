package com.travel_payment.cnpm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourReviewDTO {
    private Integer id;
    private int rating;
    private String content;
    private String createdAt;
    private String author;
}

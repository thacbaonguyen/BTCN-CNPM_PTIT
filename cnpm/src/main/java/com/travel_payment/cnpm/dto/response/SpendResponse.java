package com.travel_payment.cnpm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpendResponse {
    private LocalDate createdAt;
    private float total;
}

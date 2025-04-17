package com.travel_payment.cnpm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpendByMothResponse {
    private int year;
    private int month;

    private Float total;
}

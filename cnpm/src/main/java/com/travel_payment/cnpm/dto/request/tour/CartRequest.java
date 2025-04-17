package com.travel_payment.cnpm.dto.request.tour;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CartRequest {

    @NotNull(message = "Tour cannot be empty")
    private Integer tourId;
}
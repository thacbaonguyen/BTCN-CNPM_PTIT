package com.travel_payment.cnpm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travel_payment.cnpm.entities.reference.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer id;
    @JsonProperty("tourBriefDTO")
    private TourBriefDTO tourBriefDTO;

    public CartDTO(ShoppingCart shoppingCart) {
        this.id = shoppingCart.getId();
        this.tourBriefDTO = new TourBriefDTO(shoppingCart.getTour());
    }
}
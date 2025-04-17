package com.travel_payment.cnpm.entities.reference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tourcategories")
@Data
public class TourCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

}

package com.travel_payment.cnpm.entities.reference;

import com.travel_payment.cnpm.entities.core.Tour;
import lombok.Data;


import javax.persistence.*;

@Entity
@Table(name = "orderdetails")
@Data
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tour_price")
    private float price;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
}

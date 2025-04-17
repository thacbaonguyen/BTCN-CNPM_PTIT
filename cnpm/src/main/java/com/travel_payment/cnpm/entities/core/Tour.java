package com.travel_payment.cnpm.entities.core;

import com.travel_payment.cnpm.entities.reference.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(name = "excerp", columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    private float price;

    @Column(name = "is_active", length = 50)
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "departurepoints")
    private String departurePoint;

    private int duration;
    private int discount;
    private float rate;

    @Column(name = "total_rate")
    private int totalRate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TourCategory category;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<ShoppingCart> shoppingCarts;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourReview> tourReviews;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetail;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourDestination> tourDestinations;
}
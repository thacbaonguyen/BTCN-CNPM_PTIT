package com.travel_payment.cnpm.dto.response;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.TourDestination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDTO {
    private Integer id;
    private String title;
    private String excerpt;
    private String description;
    private String thumbnail;
    private String createdAt;
    private int duration;
    private double rating;
    private int totalRate;
    private String category;
    private URL image;
    private float price;
    private boolean isActive;
    private Integer categoryId;
    private int discount;
    private String departure;
    private List<String> destination;
    private String startDate;
    private String endDate;

    private List<TourReviewDTO> tourReviews;

    public TourDTO(Tour tour, List<TourReviewDTO> tourReviews) {
        this.id = tour.getId();
        this.title = tour.getTitle();
        this.excerpt = tour.getExcerpt();
        this.description = tour.getDescription();
        this.thumbnail = tour.getThumbnail();
        this.createdAt = tour.getCreatedAt().toString();
        this.duration = tour.getDuration();
        this.category = tour.getCategory().getName();
        this.rating = tour.getRate();
        this.totalRate = tour.getTotalRate();
        this.tourReviews = tourReviews == null ? new ArrayList<>() : tourReviews;
        this.price = tour.getPrice();
        this.isActive = tour.isActive();
        this.categoryId = tour.getCategory().getId();
        this.discount = tour.getDiscount();
        this.destination = getAllDestination(tour.getTourDestinations());
        this.startDate = tour.getStartDate().toString();
        this.endDate = tour.getEndDate().toString();
        this.departure = tour.getDeparturePoint();

    }
    private List<String> getAllDestination(List<TourDestination> list){
        ArrayList<String> destinations = new ArrayList<>();
        for(TourDestination destination : list){
            destinations.add(destination.getDestination().getName());
        }
        return destinations;
    }
}

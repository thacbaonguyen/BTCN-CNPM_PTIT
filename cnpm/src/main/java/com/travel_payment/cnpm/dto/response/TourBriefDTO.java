package com.travel_payment.cnpm.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.TourDestination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourBriefDTO {
    private Integer id;
    private String title;
    private String excerpt;
    private String description;
    private String thumbnail;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String createdAt;
    private int duration;
    private double rating;
    private int totalRate;
    private float price;
    private String category;
    private String departure;
    private List<String> destination;
    private String startDate;
    private String endDate;
    private URL image;

    public TourBriefDTO(Tour tour) {
        this.id = tour.getId();
        this.title = tour.getTitle();
        this.excerpt = tour.getExcerpt();
        this.description = tour.getDescription();
        this.thumbnail = tour.getThumbnail();
        this.createdAt = tour.getCreatedAt().toString();
        this.duration = tour.getDuration();
        this.category = tour.getCategory().getName();
        this.price = tour.getPrice();
        this.totalRate = tour.getTotalRate();
        this.rating = tour.getRate();
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
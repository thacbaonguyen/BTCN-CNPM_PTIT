package com.travel_payment.cnpm.services.impl;

import com.travel_payment.cnpm.configurations.CustomUserDetailsService;
import com.travel_payment.cnpm.data.repository.TourRepository;
import com.travel_payment.cnpm.data.repository.TourReviewRepository;
import com.travel_payment.cnpm.dto.request.tour.TourReview;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.TourReviewDTO;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.core.User;
import com.travel_payment.cnpm.exceptions.common.AlreadyException;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.services.TourReviewService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourReviewServiceImpl implements TourReviewService {
    private final TourReviewRepository tourReviewRepository;
    private final ModelMapper modelMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final TourRepository tourRepository;

    @Override
    public ResponseEntity<ApiResponse> createTourReview(TourReview request) {
        User user = customUserDetailsService.getUserDetails();
        Tour tour = tourRepository.findById(request.getTourId()).orElseThrow(
                () -> new NotFoundException("tour not found")
        );
        boolean existing = tourReviewRepository.exists(request.getTourId(), user.getId());
        if (existing) {
            throw new AlreadyException("Your review already exists");
        }
        com.travel_payment.cnpm.entities.reference.TourReview tourReview = modelMapper.map(request, com.travel_payment.cnpm.entities.reference.TourReview.class);
        tourReview.setCreatedAt(LocalDateTime.now());
        tourReview.setUser(user);
        tourReview.setId(null);
        tourReviewRepository.save(tourReview);
        tour.setTotalRate(tour.getTotalRate() + 1);
        int rating = tourReview.getRating();
        float result = (rating + (tour.getRate() * tour.getTotalRate()))/(tour.getTotalRate() + 1);
        tour.setRate(result);
        tourRepository.save(tour);
        return TravelResponse.generateResponse(null, "Create rating success", HttpStatus.CREATED);
    }

    @Override
    public List<TourReviewDTO> getTourReviews(Integer tourId) {
        return tourReviewRepository.findByTourId(tourId)
                .stream().map(review -> {
                    TourReviewDTO tourReview = new TourReviewDTO();
                    tourReview.setId(review.getId());
                    tourReview.setContent(review.getContent());
                    tourReview.setRating(review.getRating());
                    tourReview.setCreatedAt(review.getCreatedAt().toString());
                    tourReview.setAuthor(review.getUser().getUsername());
                    return tourReview;
                }).toList();
    }
}

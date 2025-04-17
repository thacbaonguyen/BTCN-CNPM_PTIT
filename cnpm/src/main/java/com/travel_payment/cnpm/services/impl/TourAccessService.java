package com.travel_payment.cnpm.services.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.travel_payment.cnpm.configurations.CustomUserDetailsService;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.data.repository.OrderRepository;
import com.travel_payment.cnpm.data.repository.TourRepository;
import com.travel_payment.cnpm.data.specification.TourSpecification;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.TourBriefDTO;
import com.travel_payment.cnpm.dto.response.TourDTO;
import com.travel_payment.cnpm.dto.response.TourReviewDTO;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.services.TourReviewService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourAccessService {
    private final TourRepository tourRepository;
    private final OrderRepository orderRepository;
    private final TourReviewService tourReviewService;
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;

    public ResponseEntity<ApiResponse> myTour(){
        try {
            Pageable pageable = createPageable(1, 100, "asc", "title");
            Specification<Tour> spec = Specification.where(TourSpecification.hasPaid(jwtFilter.getCurrentUsername()));

            Page<TourBriefDTO> result = getTourBriefPage(spec, pageable);

            return TravelResponse.generateResponse(result, "View all tour successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return TravelResponse.generateResponse(null, "View all tour failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse> getTourById(Integer id) {
        try {
            TourDTO tourDTO = getTourDTO(id);
            if (tourDTO.getThumbnail() != null) {
                tourDTO.setImage(viewImageFromS3(tourDTO.getThumbnail()));
            }
            return TravelResponse.generateResponse(tourDTO, "View tour successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return TravelResponse.generateResponse(null, "View tour failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public boolean isAlreadyTour(Integer tourId){
        return orderRepository.checkExistsByTourId(tourId, userDetailsService.getUserDetails().getId());
    }

    private TourDTO getTourDTO(Integer id) {
        try {
            Tour tour = tourRepository.findByIdAndUserId(id, userDetailsService.getUserDetails().getId());
            List<TourReviewDTO> tourReviewDTOS = tourReviewService.getTourReviews(tour.getId());
            return new TourDTO(tour, tourReviewDTOS);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            throw new NotFoundException("Not found your tour");
        }

    }

    private Pageable createPageable(Integer page, Integer pageSize, String order, String by){
        Sort.Direction direction = order != null && order.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = by != null && !by.isEmpty() ? by : "createdAt";
        Pageable pageable = PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(direction, sortBy)
        );
        return pageable;
    }

    private Page<TourBriefDTO> getTourBriefPage(Specification<Tour> spec, Pageable pageable) {
        return tourRepository.findAll(spec, pageable)
                .map(tour -> {
                    TourBriefDTO tourBriefDTO = new TourBriefDTO(tour);
                    if (tour.getThumbnail() != null){
                        tourBriefDTO.setImage(viewImageFromS3(tour.getThumbnail()));
                    }

                    return tourBriefDTO;
                });
    }

    private URL viewImageFromS3(String fileName){
        try {
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            GeneratePresignedUrlRequest preSignedUrlRequest = new GeneratePresignedUrlRequest
                    (bucketFeature, fileName, HttpMethod.GET)
                    .withExpiration(expiration);
            return amazonS3.generatePresignedUrl(preSignedUrlRequest);
        }
        catch (Exception ex){
            throw new NotFoundException("Cannot find image with name " + fileName);
        }
    }
}
package com.travel_payment.cnpm.services.impl;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.contants.TravelConstants;
import com.travel_payment.cnpm.data.repository.TourCategoryRepo;
import com.travel_payment.cnpm.data.repository.TourRepository;
import com.travel_payment.cnpm.data.repository.TourReviewRepository;
import com.travel_payment.cnpm.data.repository.UserRepository;
import com.travel_payment.cnpm.data.specification.TourSpecification;
import com.travel_payment.cnpm.dto.request.tour.TourRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.TourBriefDTO;
import com.travel_payment.cnpm.dto.response.TourDTO;
import com.travel_payment.cnpm.dto.response.TourReviewDTO;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.reference.TourCategory;
import com.travel_payment.cnpm.exceptions.common.AppException;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.exceptions.user.PermissionException;
import com.travel_payment.cnpm.services.TourReviewService;
import com.travel_payment.cnpm.services.TourService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourServiceImpl implements TourService {

    private final ModelMapper modelMapper;

    private final TourRepository tourRepository;
    private final TourReviewRepository tourReviewRepository;
    private final TourReviewService tourReviewService;
    private final AmazonS3 amazonS3;

    private final JwtFilter jwtFilter;
    private final TourCategoryRepo tourCategoryRepo;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;

    @Override
    public ResponseEntity<ApiResponse> createTour(TourRequest request) {
        if(jwtFilter.isAdmin() || jwtFilter.isManager()){
            Tour tour = modelMapper.map(request, Tour.class);
            tour.setId(null);
            tour.setCreatedAt(LocalDate.now());
            tour.setRate(0);
            tour.setTotalRate(0);
            tour.setActive(Boolean.parseBoolean(request.getIsActive()) );
            tourRepository.save(tour);
            Map<String, Object> response = new HashMap<>();
            response.put("id", tour.getId());
            return TravelResponse.generateResponse(response, "Insert tour success", HttpStatus.CREATED);
        }
        throw new PermissionException(TravelConstants.PERMISSION_DENIED);
    }

    @Override
    public void uploadThumbnail(Integer tourId, MultipartFile file) {
        Tour tour = tourRepository.findById(tourId).orElseThrow(
                () -> new NotFoundException(String.format("tour with id '%d' not found", tourId))
        );
        try {
            if (validateFile(file)) {
                String oldFilename = tour.getThumbnail();
                String fileName = uploadToS3(file);
                tour.setThumbnail(fileName);
                tourRepository.save(tour);
                if (oldFilename != null) {
                    deleteFromS3(oldFilename);
                    log.info("update action and clear cache tour:{}", tour.getThumbnail());
                }
            }
        }
        catch (AppException | IOException e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllTours(String search, Integer page, Integer pageSize,
                                                     String order, String by,
                                                     Float rating, List<String> durations, Boolean isFree) {
        try {
            Pageable pageable = createPageable(page, pageSize, order, by);
            log.info("current page {}", page);
            log.info("current pageS {}", pageSize);
            // Create specification
            Specification<Tour> spec = Specification.where(TourSpecification.hasSearchText(search)).and(TourSpecification.hasRating(rating))
                    .and(TourSpecification.hasDuration(durations))
                    .and(TourSpecification.hasPrice(isFree));

            Page<TourBriefDTO> result = getTourBriefPage(spec, pageable);

            return TravelResponse.generateResponse(result, "View all tour successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return TravelResponse.generateResponse(null, "View all tour failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    // xu ly sau
    @Override
    public ResponseEntity<ApiResponse> getTourById(int id) {
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

    @Override
    public ResponseEntity<ApiResponse> getAllToursByCategory(Integer categoryId, String search, Integer page,
                                                               Integer pageSize, String order, String by,
                                                               Float rating, List<String> durations, Boolean isFree) {
        try {
            Pageable pageable = createPageable(page, pageSize, order, by);
            log.info("current page {}", page);
            log.info("current pageS {}", pageSize);
            // Create specificationString
            Specification<Tour> spec = Specification.where(TourSpecification.hasSearchText(search))
                    .and(TourSpecification.hasCategory(categoryId))
                    .and(TourSpecification.hasRating(rating))
                    .and(TourSpecification.hasDuration(durations))
                    .and(TourSpecification.hasPrice(isFree));

            Page<TourBriefDTO> result =  getTourBriefPage(spec, pageable);

            return TravelResponse.generateResponse(result, "View all tour successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return TravelResponse.generateResponse(null, "View all tour failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateTour(Integer tourId, TourRequest request) {
        Tour tour = tourRepository.findById(tourId).orElseThrow(
                () -> new NotFoundException(String.format("Tour with id '%d' not found", tourId))
        );
        TourCategory category = tourCategoryRepo.findById(request.getCategoryId()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id '%d' not found", request.getCategoryId()))
        );
        tour.setTitle(request.getTitle());
        tour.setExcerpt(request.getExcerpt());
        tour.setDescription(request.getDescription());
        tour.setPrice(request.getPrice());
        tour.setDiscount(request.getDiscount());
        tour.setDuration(request.getDuration());
        tour.setActive(Boolean.parseBoolean(request.getIsActive()));
        tour.setCategory(category);
        tourRepository.save(tour);
        return TravelResponse.generateResponse(null, "Update tour successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteTour(Integer tourId) {
        Tour tour = tourRepository.findById(tourId).orElseThrow(
                () -> new NotFoundException(String.format("Tour with id '%d' not found", tourId))
        );
        tourRepository.delete(tour);
        return TravelResponse.generateResponse(null, "Delete tour successfully", HttpStatus.OK);
    }

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        amazonS3.putObject(bucketFeature, fileName, file.getInputStream(), objectMetadata);
        return fileName;
    }

    /**
     * delete file trên cloud
     * @param oldFileName
     */
    private void deleteFromS3(String oldFileName) {
        amazonS3.deleteObject(bucketFeature, oldFileName);
    }

    /**
     * Lấy thông tin hình ảnh dạng presignedUrl
     * @param fileName
     * @return
     */
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

    /**
     * Validate file trước khi up lên cloud
     * @param file
     * @return
     */
    private boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty");
        }
        if (file.getSize() == 0){
            throw new AppException("File is empty");
        }
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new AppException("File is not an image");
        }
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new AppException("File is too large");
        }
        return true;
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

    private TourDTO getTourDTO(int id) {
        Tour tour = tourRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Tour with id '%d' not found", id))
        );
        List<TourReviewDTO> tourReviewDTOS = tourReviewService.getTourReviews(tour.getId());
        return new TourDTO(tour, tourReviewDTOS);
    }
}
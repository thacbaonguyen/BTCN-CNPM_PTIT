package com.travel_payment.cnpm.services.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel_payment.cnpm.configurations.CustomUserDetailsService;
import com.travel_payment.cnpm.configurations.JwtFilter;
import com.travel_payment.cnpm.data.repository.CartRepository;
import com.travel_payment.cnpm.data.repository.TourRepository;
import com.travel_payment.cnpm.dto.request.tour.CartRequest;
import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.dto.response.CartDTO;
import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.core.User;
import com.travel_payment.cnpm.entities.reference.ShoppingCart;
import com.travel_payment.cnpm.exceptions.common.AlreadyException;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.services.CartService;
import com.travel_payment.cnpm.services.OrderService;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final JwtFilter jwtFilter;
    private final AmazonS3 amazonS3;
    private final CustomUserDetailsService userDetailsService;
    private final TourAccessService tourAccessService;
    private final TourRepository tourRepository;
    private final OrderService orderService;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;
    @Override
    public ResponseEntity<ApiResponse> addNewProduct(CartRequest request) {
        boolean isAlready = tourAccessService.isAlreadyTour(request.getTourId());
        if (isAlready) {
            throw new AlreadyException("You already own this tour. Please check it in your tours.");
        }
        User user = userDetailsService.getUserDetails();
        BigInteger tourCount = cartRepository.countTour(jwtFilter.getCurrentUsername(), request.getTourId());
        if (tourCount.compareTo(BigInteger.ZERO) > 0) {
            throw new AlreadyException("This tour already exists in your cart");
        }

        boolean freeTour = checkIsFreeTour(request.getTourId());
        if (freeTour) {
            return TravelResponse.generateResponse(null, "The tour is ready. Start learning now!", HttpStatus.CREATED);
        }

        ShoppingCart cart = modelMapper.map(request, ShoppingCart.class);
        cart.setId(null);
        cart.setUser(user);
        cartRepository.save(cart);
        return TravelResponse.generateResponse(null, "Insert tour on cart success", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> getCart() {
        try {
            List<CartDTO> cartDTOS = cartRepository.findByUser(jwtFilter.getCurrentUsername())
                    .stream().map(item -> {
                        CartDTO cartDTO = new CartDTO(item);
                        String fileName = cartDTO.getTourBriefDTO().getThumbnail();
                        cartDTO.getTourBriefDTO().setImage(viewImageFromS3(fileName));
                        cartDTO.getTourBriefDTO().setRating(item.getTour().getRate());
                        return cartDTO;
                    }).toList();
            return TravelResponse.generateResponse(cartDTOS, "Get cart success", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return TravelResponse.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<ApiResponse> deleteProductFromCart(Integer tourId) {
        try {
            cartRepository.deleteProductFromCart(jwtFilter.getCurrentUsername(), tourId);
            return TravelResponse.generateResponse(null, "Delete tour on cart success", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error delete tour on cart {}",e.getMessage(), e.getCause());
            return TravelResponse.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private boolean checkIsFreeTour(Integer tourId) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty()) {
            throw new NotFoundException("tour not found");
        }
        if (tour.get().getPrice() == 0){
            orderService.createFreeOrder(tour.get());
            return true;
        }
        return false;
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
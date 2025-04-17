package com.travel_payment.cnpm.services.impl;

import com.travel_payment.cnpm.data.repository.DestinationRepository;
import com.travel_payment.cnpm.dto.request.tour.DestinationRequest;
import com.travel_payment.cnpm.entities.reference.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {
    private final DestinationRepository destinationRepository;

    public Destination insertDestination(DestinationRequest request) {
        Destination destination = new Destination();
        destination.setName(request.getName());
        destinationRepository.save(destination);
        return destination;
    }

    public List<Destination> getAllDestinationByTourId(Integer tourId) {
        List<Destination> destinations = destinationRepository.getAllByTourId(tourId);
        return destinations;
    }

}

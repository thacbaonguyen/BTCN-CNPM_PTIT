package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.reference.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Integer> {

    @Query("""
            select d from Destination d join TourDestination td join td.tour t where t.id = :tourId
""")
    List<Destination> getAllByTourId(@Param("tourId") Integer tourId);
}

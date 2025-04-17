package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.reference.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TourReviewRepository extends JpaRepository<TourReview, Integer> {

    @Query(value = "SELECT COALESCE(AVG(rating), 0) FROM tourreviews WHERE toud_id = :tourId", nativeQuery = true)
    double averageRating(@Param("tourId") Integer tourId);
    @Query(value = """
                    SELECT cv from TourReview cv WHERE cv.tour.id = :tourId order by cv.createdAt
                    """)
    List<TourReview> findByTourId(@Param("tourId") Integer tourId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM tourreviews WHERE tour_id = :tourId AND user_id = :userId)", nativeQuery = true)
    BigInteger existsByUserId(@Param("tourId") Integer tourId, @Param("userId") Integer userId);

    default boolean exists(Integer tourId, Integer userId) {
        BigInteger result = existsByUserId(tourId, userId);
        return result != null && result.intValue() == 1;
    }
}

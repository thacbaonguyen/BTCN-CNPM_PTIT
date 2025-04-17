package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.core.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer>, JpaSpecificationExecutor<Tour> {
    Tour findByTitle(String title);

    @Query("""
            SELECT c FROM Tour c JOIN c.orderDetail od JOIN od.order o 
            WHERE c.id = :tourId AND o.user.id = :userId AND o.paymentStatus = 'paid'
            """)
    Tour findByIdAndUserId(@Param("tourId")Integer id, @Param("userId") Integer userId);
}

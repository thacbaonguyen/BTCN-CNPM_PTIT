package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.reference.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderCode(String orderCode);

    @Query(value = """
                    SELECT EXISTS (
                        SELECT 1 FROM orderdetails as od
                        JOIN orders as o ON od.order_id = o.id
                        WHERE od.tour_id = :tourId and o.payment_status = 'paid' and o.user_id = :userId
                    )
                    """, nativeQuery = true)
    Integer existsByTourId(@Param("tourId") Integer tourId, @Param("userId") Integer userId);

    default boolean checkExistsByTourId(Integer tourId, Integer userId) {
        return existsByTourId(tourId, userId) == 1;
    }
}

package com.travel_payment.cnpm.data.repository;


import com.travel_payment.cnpm.entities.reference.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Integer> {
}

package com.travel_payment.cnpm.data.repository;

import com.travel_payment.cnpm.entities.reference.TourCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourCategoryRepo extends JpaRepository<TourCategory, Integer> {
    TourCategory findByName(String name);
}

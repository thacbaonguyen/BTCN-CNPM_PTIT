package com.travel_payment.cnpm.data.repository;
import com.travel_payment.cnpm.entities.reference.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, Integer> {
    @Query(value = """
                    SELECT * FROM shoppingcarts as s JOIN users as u on u.id = s.user_id WHERE u.user_name = :username
                   """, nativeQuery = true)
    List<ShoppingCart> findByUser(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = """
                DELETE FROM shoppingcarts 
                WHERE user_id = (SELECT id FROM users WHERE user_name = :username) 
                AND tour_id = :tourId
               """, nativeQuery = true)
    void deleteProductFromCart(@Param("username") String username, @Param("tourId") Integer tourId);

    @Modifying
    @Transactional
    @Query(value = """
                DELETE FROM shoppingcarts 
                WHERE user_id = (SELECT id FROM users WHERE user_name = :username) 
               """, nativeQuery = true)
    void deleteByUser(@Param("username") String username);

    @Query(value = """
                    SELECT COUNT(*) FROM shoppingcarts as s JOIN users as u on u.id = s.user_id WHERE u.user_name = :username and tour_id = :tourId
                   """, nativeQuery = true)
    BigInteger countTour(@Param("username") String username, @Param("tourId") Integer tourId);
}
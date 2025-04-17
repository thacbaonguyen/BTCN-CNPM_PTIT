package com.travel_payment.cnpm.data.specification;

import com.travel_payment.cnpm.entities.core.Tour;
import com.travel_payment.cnpm.entities.core.User;
import com.travel_payment.cnpm.entities.reference.Order;
import com.travel_payment.cnpm.entities.reference.OrderDetail;
import com.travel_payment.cnpm.entities.reference.TourCategory;
import com.travel_payment.cnpm.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecification {
    public static Specification<Tour> hasSearchText(String searchText) {
        if (searchText == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + searchText + "%");
        };
    }

    public static Specification<Tour> hasRating(Float rating) {
        if (rating == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("rate"), rating);
        };
    }

    public static Specification<Tour> hasDuration(List<String> durations) {
        if (durations == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String duration : durations) {
                switch (duration.toLowerCase()) {
                    case "extrashort":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 0, 1));
                        break;
                    case "short":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 1, 2));
                        break;
                    case "medium":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 2, 3));
                        break;
                    case "long":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 3, 4));
                        break;
                    case "extralong":
                        predicates.add(criteriaBuilder.between(root.get("duration"), 4, Integer.MAX_VALUE));
                        break;
                    default:
                        break;
                }
            }
            if (predicates.isEmpty()) {
                return null;
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Tour> hasPrice(Boolean isFree) {
        if (isFree == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            if (!isFree) {
                return criteriaBuilder.equal(root.get("discount"), 0);
            } else {
                return criteriaBuilder.greaterThan(root.get("discount"), 0);
            }
        };
    }

    public static Specification<Tour> hasCategory(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Join<Tour, TourCategory> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }
    public static Specification<Tour> hasPaid(String username) {
        return (root, query, criteriaBuilder) -> {
            Join<Tour, OrderDetail> orderDetailJoin = root.join("orderDetail", JoinType.INNER);
            Join<OrderDetail, Order> orderJoin = orderDetailJoin.join("order", JoinType.INNER);
            Join<Order, User> userJoin = orderJoin.join("user", JoinType.INNER);
            Predicate paymentStatusPredicate = criteriaBuilder.equal(
                    orderJoin.get("paymentStatus"), PaymentStatus.paid);
            Predicate userIdPredicate = criteriaBuilder.equal(
                    userJoin.get("username"), username);
            return criteriaBuilder.and(paymentStatusPredicate, userIdPredicate);
        };
    }
}

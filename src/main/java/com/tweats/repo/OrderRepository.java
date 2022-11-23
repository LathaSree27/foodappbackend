package com.tweats.repo;

import com.tweats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM ORDERS o WHERE o.category_id = ?1 and o.date = ?2 and o.is_delivered = ?3", nativeQuery = true)
    List<Order> getAllOrdersByCategoryDateAndStatus(long categoryId, Date date, boolean isDelivered);

    @Query(value = "SELECT SUM(i.price*o.quantity) FROM ordered_item o JOIN item i ON o.item_id = i.id WHERE o.order_id IN (SELECT o.id FROM ORDERS o WHERE o.category_id = ?1 and o.date = ?2 and o.is_delivered = true)", nativeQuery = true)
    BigDecimal getRevenueOfCompletedOrdersByCategoryIdAndDate(long categoryId, Date date);

}

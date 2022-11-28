package com.tweats.repo;

import com.tweats.model.Order;
import com.tweats.model.constants.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM ORDERS o WHERE o.category_id = ?1 and o.date = ?2 and o.status = ?3", nativeQuery = true)
    List<Order> getAllCompletedOrdersByCategoryAndDate(long categoryId, Date date, String status);

    @Query(value = "SELECT SUM(i.price*o.quantity) FROM ordered_item o JOIN item i ON o.item_id = i.id WHERE o.order_id IN (SELECT o.id FROM ORDERS o WHERE o.category_id = ?1 and o.date = ?2 and o.status = ?3)", nativeQuery = true)
    BigDecimal getRevenueOfCompletedOrdersByCategoryIdAndDate(long categoryId, Date date, String status);

    @Query(value = "SELECT * FROM ORDERS o WHERE o.category_id = ?1 and o.status= ?2 order by o.date desc", nativeQuery = true)
    List<Order> getAllActiveOrdersByCategoryId(long categoryId, String status);
}

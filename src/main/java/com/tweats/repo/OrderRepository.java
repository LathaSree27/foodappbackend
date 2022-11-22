package com.tweats.repo;

import com.tweats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM ORDERS o WHERE o.category_id = ?1 and o.date = ?2 and o.is_delivered = true", nativeQuery = true)
    List<Order> getAllCompletedOrdersByCategory(long categoryId, Date date);

}

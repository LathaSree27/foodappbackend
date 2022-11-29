package com.tweats.repo;

import com.tweats.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndCategoryId(long userId, long categoryId);

    @Query(value = "SELECT SUM(c.QUANTITY*i.PRICE) FROM CART_ITEM c JOIN ITEM i ON c.ITEM_ID = i.ID WHERE CART_ID = ?1", nativeQuery = true)
    Optional<BigDecimal> getBillAmount(Long cartId);
}

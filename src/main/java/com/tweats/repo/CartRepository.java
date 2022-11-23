package com.tweats.repo;

import com.tweats.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query(value = "SELECT * FROM CART c WHERE c.USER_ID = ?1 AND c.CATEGORY_ID = ?2", nativeQuery = true)
    public Cart findCartByUserIdAndCategoryId(long userId, long categoryId);
}

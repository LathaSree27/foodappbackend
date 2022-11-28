package com.tweats.repo;

import com.tweats.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByUserId(long id);

    Optional<Category> findByUser_id(long user_Id);
}

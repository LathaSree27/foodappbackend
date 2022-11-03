package com.tweats.repo;

import com.tweats.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByUser_id(long user_Id);
}

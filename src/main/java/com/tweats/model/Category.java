package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "category name cannot be empty!")
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false, name = "is_open")
    private boolean isOpen = false;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category(String name, Image image, User user) {
        this.name = name;
        this.image = image;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

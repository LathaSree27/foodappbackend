package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private boolean is_open;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category(String name, Image image, User user) {
        this.name = name;
        this.image = image;
        this.is_open = false;
        this.user = user;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setIs_open(boolean is_open) {
        this.is_open = is_open;
    }


    public void setUser(User user) {
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

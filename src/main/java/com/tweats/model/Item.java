package com.tweats.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false)
    private boolean is_available;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Item() {
    }

    public Item(String name, Image image, BigDecimal price, Category category) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.is_available = false;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

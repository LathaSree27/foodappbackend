package com.tweats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty
    @NotBlank(message = "Item name should be provided")
    @Column(nullable = false)
    @ApiModelProperty(name = "item name", value = "name of the item", required = true, example = "item_name", position = 1)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @JsonProperty
    @Column(nullable = false)
    @ApiModelProperty(name = "item price", value = "price of item", required = true, example = "100", position = 3)
    private BigDecimal price;


    @JsonProperty
    @Column(nullable = false)
    @ApiModelProperty(name = "is_available", value = "is available", required = true, example = "true", position = 4)
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

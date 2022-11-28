package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "item")
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Item name can't be empty!")
    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Min(value = 0, message = "Price can't be negative!")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, name = "is_available")
    private boolean isAvailable = true;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "item")
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<OrderedItem> orderedItems;

    public Item(String name, Image image, BigDecimal price, Category category) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && isAvailable == item.isAvailable && name.equals(item.name) && image.equals(item.image) && price.equals(item.price) && category.equals(item.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, price, isAvailable, category);
    }
}

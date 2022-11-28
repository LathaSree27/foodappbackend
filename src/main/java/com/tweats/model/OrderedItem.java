package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Objects;

@Entity
@Table(name = "ordered_item")
@NoArgsConstructor
@Getter
@Setter
public class OrderedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "item_id")
    private Item item;

    @Min(value = 1,message = "Quantity can't be less than 1")
    private long quantity;

    public OrderedItem(Order order, Item item, long quantity) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedItem that = (OrderedItem) o;
        return id == that.id && quantity == that.quantity && item.equals(that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, quantity);
    }
}

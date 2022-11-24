package com.tweats.model;

import com.tweats.model.constants.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    @JoinColumn(name = "category_id")
    @OneToOne
    private Category category;

    @OneToMany(mappedBy = "order")
    private Set<OrderedItem> orderedItems;

    public Order(Date date, User user, Category category) {
        this.date = date;
        this.user = user;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && date.equals(order.date) && status.equals(order.status) && user.equals(order.user) && category.equals(order.category) && orderedItems.equals(order.orderedItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status, user, category, orderedItems);
    }
}

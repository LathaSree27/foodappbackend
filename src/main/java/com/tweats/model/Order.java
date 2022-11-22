package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
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

    private boolean isDelivered=false;

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
}

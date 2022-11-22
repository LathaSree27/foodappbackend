package com.tweats.model;

import javax.persistence.*;

@Entity
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

    public Category() {
    }

    public Category(String name, Image image, boolean is_open, User user) {
        this.name = name;
        this.image = image;
        this.is_open = is_open;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isIs_open() {
        return is_open;
    }


    public User getUser() {
        return user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_open(boolean is_open) {
        this.is_open = is_open;
    }


    public void setUser(User user) {
        this.user = user;
    }
}

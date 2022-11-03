package com.tweats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty
    @Column(nullable = false)
    private String name;
    @JsonProperty
    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @JsonProperty
    @Column(nullable = false)
    private boolean is_open;
    @JsonProperty
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category() {
    }

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
}

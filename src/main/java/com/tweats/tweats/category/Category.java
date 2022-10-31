package com.tweats.tweats.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tweats.tweats.image.Image;
import com.tweats.tweats.items.Item;
import com.tweats.tweats.users.User;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty
    @NotBlank(message = "category name should be provided")
    @Column(nullable = false)
    @ApiModelProperty(name = "category_name", value = "name of category", example = "category_name", required = true, position = 1)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;


    @Column(nullable = false)
    @ApiModelProperty(name = "is_open", value = "is_open", example = "is_open", required = true, position = 3)
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

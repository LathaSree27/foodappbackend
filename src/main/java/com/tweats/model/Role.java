package com.tweats.model;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public Role() {
    }

    private String name;

    public Role(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.tweats.tweats.users;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty
    @NotBlank(message = "user name must be provided")
    @Column(nullable = false)
    @ApiModelProperty(name = "username", value = "name of the user", required = true, example = "username", position = 1)
    private String name;

    @JsonProperty
    @NotBlank(message = "user email must be provided")
    @Column(nullable = false, unique = true)
    @ApiModelProperty(name = "email", value = "email of the user", required = true, example = "email", position = 2)
    private String email;

    @JsonProperty
    @NotBlank(message = "user password must be provided")
    @Column(nullable = false)
    @ApiModelProperty(name = "password", value = "password of the user", required = true, example = "password", position = 3)
    private String password;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public User() {
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

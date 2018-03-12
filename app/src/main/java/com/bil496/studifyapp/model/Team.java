package com.bil496.studifyapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by burak on 3/12/2018.
 */

public class Team implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("utilitScore")
    private Float utilitScore;
    @SerializedName("users")
    private List<User> users;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getUtilitScore() {
        return utilitScore;
    }

    public void setUtilitScore(Float utilitScore) {
        this.utilitScore = utilitScore;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

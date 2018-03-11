package com.bil496.studifyapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by burak on 3/11/2018.
 */

public class User implements Serializable{
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("username")
    private String username;
    @SerializedName("profile_pic")
    private String profilePic;

    public User(Integer id, String name, String username, String profilePic) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.profilePic = profilePic;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}

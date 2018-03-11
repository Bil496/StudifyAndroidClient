package com.bil496.studifyapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by burak on 3/11/2018.
 */

public class Subtopic implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;

    public Subtopic(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

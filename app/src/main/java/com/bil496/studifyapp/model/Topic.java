package com.bil496.studifyapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by burak on 3/11/2018.
 */

public class Topic implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("userEnrolled")
    private Boolean userEnrolled;
    @SerializedName("userCount")
    private Integer size;
    @SerializedName("subtopics")
    private List<Subtopic> subtopics;

    public Topic() {
        super();
    }

    public Topic(Integer id, String title, Integer size, List<Subtopic> subtopics) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.subtopics = subtopics;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getUserEnrolled() {
        return userEnrolled;
    }

    public void setUserEnrolled(Boolean userEnrolled) {
        this.userEnrolled = userEnrolled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Subtopic> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(List<Subtopic> subtopics) {
        this.subtopics = subtopics;
    }
}

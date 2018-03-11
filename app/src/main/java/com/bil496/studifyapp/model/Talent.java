package com.bil496.studifyapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by burak on 3/11/2018.
 */

public class Talent implements Serializable {
    @SerializedName("subtopicId")
    private Integer subtopicId;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("score")
    private Float score;

    public Talent(Integer subtopicId, Integer userId, Float score) {
        this.subtopicId = subtopicId;
        this.userId = userId;
        this.score = score;
    }

    public Integer getSubtopicId() {
        return subtopicId;
    }

    public void setSubtopicId(Integer subtopicId) {
        this.subtopicId = subtopicId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}

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
    @SerializedName("jointUtility")
    private Integer utilityScore;
    @SerializedName("locked")
    private Boolean isLocked;
    @SerializedName("topic")
    private Topic topic;
    @SerializedName("members")
    private List<User> users;
    @SerializedName("requests")
    private List<JoinRequest> requests;

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

    public Integer getUtilityScore() {
        return utilityScore;
    }

    public void setUtilityScore(Integer utilityScore) {
        this.utilityScore = utilityScore;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public List<JoinRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<JoinRequest> requests) {
        this.requests = requests;
    }
}

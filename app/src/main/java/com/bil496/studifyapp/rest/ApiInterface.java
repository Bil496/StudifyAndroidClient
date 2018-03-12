package com.bil496.studifyapp.rest;

import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.model.Talent;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by burak on 3/11/2018.
 */

public interface ApiInterface {
    @GET("places")
    Call<Place[]> getPlaces();

    @GET("places/{place_id}/topics")
    Call<Topic[]> getTopics(@Header("userId") int userId, @Path("place_id") long placeId);

    @GET("topics/{topic_id}/teams")
    Call<Team[]> getTeams(@Header("userId") int userId, @Path("topic_id") long topicId);

    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @POST("/topics/{id}/talents")
    Call<Integer> postTalents(@Header("userId") int userId, @Path("id") int topicId, @Body Talent[] talents);

    @POST("places/{place_id}/topics")
    Call<Topic> postTopic(@Path("place_id") long placeId, @Body Topic topic);

    @POST("topics/{topic_id}/teams")
    Call<Team[]> createTeam(@Header("userId") int userId, @Path("topic_id") int topicId);
}

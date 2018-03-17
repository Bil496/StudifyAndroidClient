package com.bil496.studifyapp.rest;

import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.model.Talent;
import com.bil496.studifyapp.model.Team;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.model.User;

import okhttp3.ResponseBody;
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
    @GET("locations")
    Call<Place[]> getPlaces();

    @GET("locations/{location_id}/topics")
    Call<Topic[]> getTopics(@Header("userId") int userId, @Path("location_id") long placeId);

    @GET("topics/{topic_id}/teams")
    Call<Team[]> getTeams(@Header("userId") int userId, @Path("topic_id") long topicId);

    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @POST("/topics/{id}/talentLevels")
    Call<ResponseBody> postTalents(@Header("userId") int userId, @Path("id") int topicId, @Body Talent[] talents);

    @POST("locations/{location_id}/topics")
    Call<Integer> postTopic(@Path("location_id") long placeId, @Body Topic topic);

    @POST("topics/{topic_id}/teams")
    Call<Integer> createTeam(@Header("userId") int userId, @Path("topic_id") int topicId);

    @POST("firebase_token")
    Call<ResponseBody> saveToken(@Header("userId") int userId, @Body String firebaseToken);

    @GET("team")
    Call<Team> getTeam(@Header("userId") int userId);

    @POST("/teams/{teamId}/kick/{kickedUserId}")
    Call<ResponseBody> kickUser(@Header("userId") int userId, @Path("teamId") int teamId, @Path("kickedUserId") int kickedUserId);

    @POST("/teams/{teamId}/lock")
    Call<ResponseBody> lockTeam(@Header("userId") int userId, @Path("teamId") int teamId);

    @POST("/teams/{teamId}/unlock")
    Call<ResponseBody> unlockTeam(@Header("userId") int userId, @Path("teamId") int teamId);
}

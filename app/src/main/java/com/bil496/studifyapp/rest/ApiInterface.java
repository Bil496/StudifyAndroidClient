package com.bil496.studifyapp.rest;

import com.bil496.studifyapp.model.Place;
import com.bil496.studifyapp.model.PlaceResponse;
import com.bil496.studifyapp.model.Topic;
import com.bil496.studifyapp.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by burak on 3/11/2018.
 */

public interface ApiInterface {
    @GET("places?_sort=title")
    Call<Place[]> getPlaces();

    @GET("places/{place_id}/topics?_sort=user_count&_order=desc&_embed=subtopics")
    Call<String> getTopics(@Path("place_id") long placeId);

    @GET("topics/{topic_id}/teams?_embed=users&sort=utility_score&order=desc")
    Call<String> getTeams(@Path("topic_id") long topicId);

    @GET("users")
    Call<User[]> getUser(@Query("username") String username);

    @GET("places/{place_id}/topics?_sort=user_count&_order=desc&_embed=subtopics")
    Call<Topic[]> getTopics(@Path("place_id") Integer placeId);
}

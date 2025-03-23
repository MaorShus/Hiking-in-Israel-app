package com.example.endproject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

//handle the requests for the server
public interface ApiService {
    @POST("/api/auth/register")
    Call<ApiResponse> register(@Body User user);

    @POST("/api/auth/login")
    Call<ApiResponse> login(@Body User user);

    @GET("/api/auth/northTrails")
    Call<List<Trail>> getNorthTrails();

    @GET("/api/auth/centerTrails")
    Call<List<Trail>> getCenterTrails();

    @GET("/api/auth/southTrails")
    Call<List<Trail>> getSouthTrails();

    @GET("api/auth/getTrail/{name}")
    Call<Trail> getTrailByName(@Path("name") String trailName);

    @PUT("api/auth/trails/{trail_num}")
    Call<Void> updateTrail(
            @Path("trail_num") int trailNum,
            @Body Trail trail
    );

    @GET("/api/auth/getAllTrails")
    Call<List<String>> getAllTrails();

    @POST("/api/auth/addFavorites") // Replace with your API endpoint
    Call<Void> addFavoriteTrail(@Body FavoriteTrail favoriteTrail);

    @GET("/api/auth/deleteFavorite/{trail_num}/{user_name}")
    Call<Void> deleteFavoriteTrail(
            @Path("trail_num") String trailNum,
            @Path("user_name") String username
    );

    @GET("/api/auth/countFavorites/{trail_num}/{user_name}")
    Call<CountResponse> countFavorites(
            @Path("trail_num") String trailNum,
            @Path("user_name") String username
    );

    @GET("/api/auth/getFavoriteTrails/{user_name}")
    Call<List<String>> getFavoriteTrails(@Path("user_name") String username);

    @POST("/api/auth/addBugReport") // Update the endpoint as per your server
    Call<Void> addBugReport(@Body BugReport bugReport);

    @GET("api/auth/getUnfixedBugs")
    Call<List<Bug>> getUnfixedBugs();

    @POST("/api/auth/markBugAsFixed")
    Call<Void> markBugAsFixed(@Body Bug bug);

    // New method to fetch most favorited trails
    @GET("/api/auth/getMostFavoritedTrails")
    Call<List<TrailFavorite>> getMostFavoritedTrails();
}
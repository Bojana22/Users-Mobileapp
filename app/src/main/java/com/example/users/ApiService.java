package com.example.users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/au/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
package com.company.connector

import com.bonitasoft.test.model.PersonResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface StarWarsService {

    @Headers("Accept: application/json")
    @GET("api/people")
    Call<PersonResponse> person(@Query("search") String name);
}
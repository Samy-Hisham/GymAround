package com.android.gymaround

import retrofit2.http.GET
import retrofit2.http.Query

interface GymsApiService {

    @GET("gyms.json")
    suspend fun getGym(): List<Gym>

    @GET("gyms.json?orderBy=\"id\"")
    suspend fun getGymDetail(
        @Query("equalTo") id: Int
    ): Map<String, Gym>

}
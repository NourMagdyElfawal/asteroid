package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


    private const val url = BASE_URL
    private const val apiKey = API_KEY

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(url)
    .build()



interface AsteroidApiService{

        @GET("planetary/apod")
       suspend fun getPhoto(
            @Query("api_key") api_key:String=apiKey
        ):PictureOfDay

        @GET("neo/rest/v1/feed")
        suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") api_key:String=apiKey
    ): ResponseBody



}



        object AsteroidApi {
            val retrofitService : AsteroidApiService by lazy {
                retrofit.create(AsteroidApiService::class.java) }
        }


//interface AsteroidApiService {
//
//    @GET("neo/rest/v1/feed")
//    fun getAsteroidsAsync(
//        @Query("start_date") startDate: String,
//        @Query("end_date") endDate: String,
//        @Query("api_key") apiKey: String = Constants.API_KEY
//    ): Deferred<ResponseBody>
//
//    @GET("planetary/apod")
//    fun getPictureOfDayAsync(
//        @Query("api_key") apiKey: String = Constants.API_KEY
//    ): Deferred<PictureOfDay>
//}
//
//private val moshi = Moshi.Builder()
//    .add(KotlinJsonAdapterFactory())
//    .build()
//
//object Network {
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .build()
//
//    val service: AsteroidApiService = retrofit.create(AsteroidApiService::class.java)
//}

package org.xapps.apps.weatherx.services.remote

import org.xapps.apps.weatherx.services.models.Weather
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface WeatherApi {

    companion object {
        const val UNIT_METRIC = "metric"
        const val UNIT_IMPERIAL = "imperial"
    }

    @GET("onecall?exclude=minutely,hourly,daily,alerts")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    suspend fun current(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather


    @GET("onecall?exclude=current,hourly,daily,alerts")
    @Headers(
        "Accept: application/json",
        "Context-Type: application/json"
    )
    suspend fun minutely(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather


    @GET("onecall?exclude=current,minutely,daily,alerts")
    @Headers(
        "Accept: application/json",
        "Content-type: application/json"
    )
    suspend fun hourly(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather


    @GET("onecall?exclude=current,minutely,hourly,alerts")
    @Headers(
        "Accept: application/json",
        "Content-type: application/json"
    )
    suspend fun daily(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather


    @GET("onecall?exclude=minutely,alerts")
    @Headers(
        "Accept: application/json",
        "Content-type: application/json"
    )
    suspend fun currentHourlyDaily(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather


    @GET("onecall")
    @Headers(
        "Accept: application/json",
        "Content-type: application/json"
    )
    suspend fun weather(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String
    ): Weather

}
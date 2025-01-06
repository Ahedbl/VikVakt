package com.example.vikvakt

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class JsonParser {

    data class ForecastData(
        val hourly: ReceivedHourlyData
    )

    data class ReceivedHourlyData(
        val time: List<String>,
        @SerializedName("temperature_2m") val temperature2m: List<Double>,
        val rain: List<Double>,
        @SerializedName("wind_speed_10m") val windSpeed : List<Double>,
        @SerializedName("wind_direction_10m") val windDirection : List<Double>,
        @SerializedName("wind_gusts_10m") val windGusts : List<Double>,
        @SerializedName("relative_humidity_2m") val humidity : List<Double>
    )

    fun parseWeatherData(jsonString: String): ForecastData {
        val gson = Gson()
        return gson.fromJson(jsonString, ForecastData::class.java)
    }
}

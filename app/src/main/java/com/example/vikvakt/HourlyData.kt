package com.example.vikvakt

import com.google.gson.annotations.SerializedName

class HourlyData(val time: String,
                 val temp: Double,
                 val rain: Double,
                 val windSpeed: Double,
                 val windDirection: Double,
                 val windGusts: Double,
                 val relativeHumidity: Double) {




    override fun toString(): String {
        return "Time: $time, Temperature: $temp, Rain: $rain, Wind speed: $windSpeed, Wind direction: $windDirection"
    }


}
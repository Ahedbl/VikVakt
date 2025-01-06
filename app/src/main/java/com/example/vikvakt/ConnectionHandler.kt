package com.example.vikvakt

import android.content.Context
import android.util.JsonReader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class ConnectionHandler {

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun readApiResponse(apiConnection: HttpURLConnection): String?{
        try{
            var resultJson = StringBuilder()
            var scanner = Scanner(apiConnection.inputStream)
            while (scanner.hasNext()){
                resultJson.append(scanner.nextLine())
            }
            scanner.close()
            return resultJson.toString()
        }catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    fun fetchHeightApiResponse(urlString: String): HttpURLConnection?{
        try{
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            return conn
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    fun fetchApiResponse(urlString: String): HttpURLConnection?{
            try{
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                return conn
            } catch (e: IOException) {
                e.printStackTrace()
            }
        return null
    }
    fun weatherFetch(hourlyList : MutableList<HourlyData>?, latitude: Double, longitude : Double, context : Context)  {
        CoroutineScope(Dispatchers.IO).launch {
            val fileHandler = FileHandler(context)
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,relative_humidity_2m,rain,wind_speed_10m,wind_direction_10m,wind_gusts_10m"
            val apiConnection = fetchApiResponse(url)
            if (apiConnection != null && apiConnection.responseCode == 200) {
                val jsonResponse = readApiResponse(apiConnection)
                if (jsonResponse != null) {
                    val parser = JsonParser()
                    val forecastData = parser.parseWeatherData(jsonResponse)
                    for((i, item) in forecastData.hourly.time.withIndex()){
                        val temp = HourlyData(forecastData.hourly.time[i],
                                                            forecastData.hourly.temperature2m[i],
                                                            forecastData.hourly.rain[i],
                                                            forecastData.hourly.windSpeed[i],
                                                            forecastData.hourly.windDirection[i],
                                                            forecastData.hourly.windGusts[i],
                                                            forecastData.hourly.humidity[i],)
                        hourlyList?.add(temp)
                        //Log.d("added", temp.toString())
                    }
                    if (hourlyList != null) {
                        fileHandler.saveToFile("Latitude: $latitude | Longitude: $longitude", hourlyList)
                    }
                } else {
                    Log.d("ConnectionHandler", "Failed to read API response")
                }
            } else {
                Log.d("ConnectionHandler", "Error connecting to API")
            }
        }
    }


}
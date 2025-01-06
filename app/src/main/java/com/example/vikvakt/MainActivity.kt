package com.example.vikvakt


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo

class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private lateinit var timeLineBar: SeekBar
    private lateinit var dateView: TextView
    private lateinit var infoView: TextView
    private lateinit var bitmap: Bitmap
    private var correctedArray = Array(256) { IntArray(256) }
    private lateinit var correctedBitmap: Bitmap
    private var elevationArray: IntArray = IntArray(0)
    private val connectionHandler = ConnectionHandler()
    private val windColorValues = Array(256) { DoubleArray(256) }

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fillMatrixWithExampleData()  //TODO ---------------------------------------- ändra


        val hourlyList: MutableList<HourlyData> = mutableListOf()
        val windLayout: ImageView = findViewById(R.id.windLayout)  // Få tag på GridLayout



        timeLineBar = findViewById(R.id.timeLineBar)
        dateView = findViewById(R.id.dateView)
        infoView = findViewById(R.id.infoView)
        val arrowImage: ImageView = findViewById(R.id.overlayArrow)

        timeLineBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d("SliderDebug", "Slider moved: Progress = $progress")
                if (hourlyList.isNotEmpty()) {
                    timeLineBar.max = hourlyList.size - 1
                    val selectedData = hourlyList[progress]

                    val date = "Date: " + selectedData.time
                    val info = "Info: " + progress * 2
                    dateView.text = date
                    infoView.text = info


                    arrowImage.rotation = selectedData.windDirection.toFloat()
                    var i = 0;
                    var j = 0;
                    val array = Array(256) { IntArray(256) }
                    val pixelArray = IntArray(bitmap.width * bitmap.height)
                    bitmap.getPixels(pixelArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                    val windSpeed = selectedData.windSpeed.toFloat()
                    val windDirection = selectedData.windDirection.toFloat()

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        mapView = findViewById(R.id.mapView)
        val imageView: ImageView = findViewById(R.id.overlayImageView)

        // Visa koordinater
        val locationButton: Button = findViewById(R.id.locationButton)
        locationButton.setOnClickListener {
            val zoom = 13
            val cameraState = mapView?.getMapboxMap()?.cameraState
            if (cameraState != null) {
                val latitude = cameraState.center.latitude()
                val longitude = cameraState.center.longitude()

                Log.d(TAG, "Lat: $latitude, Lng: $longitude")
                val tile = latLonToTile(latitude, longitude, zoom)
                Log.d(TAG, "Converted to tile: $tile")
            } else {
                Log.e(TAG, "Camera location error")
            }
            setZoomLevelAnimated(zoom.toDouble())
        }

        val simulationButton: Button = findViewById(R.id.simulateButton)
        simulationButton.setOnClickListener {
            val cameraState = mapView?.getMapboxMap()?.cameraState
            val zoomLevel = cameraState?.zoom
            Log.d(TAG, "Current zoom level: $zoomLevel")
        }

        val downloadButton: Button = findViewById(R.id.downloadButton)
        downloadButton.setOnClickListener {
            download(imageView, windLayout, hourlyList)
        }
    }

    private fun download(imageView: ImageView,windLayout: ImageView, hourlyList: MutableList<HourlyData>) {
        Thread {
            try {
                val zoom = 13
                val cameraState = mapView?.getMapboxMap()?.cameraState
                    ?: throw Exception("Camera state is null")
                val latitude = cameraState.center.latitude()
                val longitude = cameraState.center.longitude()
                weatherData(longitude, latitude, hourlyList)
                Log.d(TAG, "Lat: $latitude, Lng: $longitude")
                val tile = latLonToTile(latitude, longitude, zoom)
                Log.d(TAG, "Tile: $tile")

                val imageHandler = ImageHandler()
                val imageUrl =
                    "https://api.mapbox.com/v4/mapbox.terrain-rgb/$zoom/${tile.first}/${tile.second}.pngraw?access_token=pk.eyJ1IjoiYWhlZGJsIiwiYSI6ImNtNWZ2dGp3ejAyMm8yaHNkams2bXhoMHEifQ.-rX_RY7LlI1MkX2oWIrpJw"

                bitmap = imageHandler.downloadImage(imageUrl) ?: throw Exception("Failed to download image")
                imageView.setImageBitmap(bitmap)

                val pixelArray = imageHandler.extractPixels(bitmap)
                elevationArray = IntArray(pixelArray.size) { decodeElevation(pixelArray[it]).toInt() }
                //correctedArray = arrayOf(IntArray(pixelArray.size) { decodeElevation(pixelArray[it]).toInt() })
                var direction = hourlyList.get(0).windDirection
                var speed = hourlyList.get(0).windSpeed
                var listOfPixels = WindDirection.windStartPositions(direction) //vart startar vinden från
                var windPixelsList = ArrayList<WindPixel>() // vilka linjer bildas av vinden
                for (pixel in listOfPixels){
                    var position = Calc.oneDimensionToTwo(pixel, 256)
                    val heights: MutableList<Int> = mutableListOf()
                    var linePositions = Calc.getLinePositions(position.first, position.second, direction, 256, 256)
                    for(spot in linePositions){
                        //Log.d("Windmap", "Stop 3")
                        var heightPos = Calc.oneDimensionToTwo(spot, 256)
                        /*Log.d("Windmap", "Spot: $spot, Position: ${Calc.oneDimensionToTwo(spot, 256)}")
                        Log.d("Windmap", "Accessing correctedArray at: (${heightPos.first}, ${heightPos.second})")
                        if (heightPos.first >= correctedArray.size || heightPos.second >= correctedArray[0].size) {
                            Log.e("Windmap", "Index out of bounds: (${heightPos.first}, ${heightPos.second})")
                        } */

                        heights.add(elevationArray[spot])
                    }

                    var element = WindPixel(linePositions, heights, speed)
                    windPixelsList.add(element)
                }
                //Log.d("Windmap", "Stop 6")
                for(line in windPixelsList){
                    //Log.d("Windmap", "Stop 7")
                    var i = 0;
                    for(index in line.positions){
                        val positions = Calc.oneDimensionToTwo(line.positions[i], 256)
                        //Log.d("Windmap", "Calculated position in the array: ${positions.first} and ${positions.second}")
                        windColorValues[positions.first][positions.second] = line.windAtPosition[i]
                        i += 1;
                        if(i >= line.positions.size-1) break
                    }
                    //Log.d("Windmap", "Stop 9")
                }
                //Log.d("Windmap", windColorValues.toString())
                val windMap = arrayToBitmap(windColorValues)
                windLayout.setImageBitmap(windMap)
            } catch (e: Exception) {
                Log.e(TAG, "Error during download: ${e.message}")
            }
        }.start()
    }

    private fun weatherData(latitude: Double, longitude: Double, hourlyList: MutableList<HourlyData>) {
        connectionHandler.weatherFetch(hourlyList, latitude, longitude, this)

        if (latitude != null && longitude != null) {
            Log.d(TAG, "Weather data fetch started for Lat: $latitude, Lng: $longitude")
        } else {
            Log.d(TAG, "Invalid input: Latitude or Longitude is null")
        }
    }

    private fun latLonToTile(lat: Double, lon: Double, zoom: Int): Pair<Int, Int> {
        val n = Math.pow(2.0, zoom.toDouble())
        val x = ((lon + 180.0) / 360.0 * n).toInt()
        val y = ((1.0 - Math.log(Math.tan(Math.toRadians(lat)) + 1.0 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2.0 * n).toInt()
        return Pair(x, y)
    }

    private fun setZoomLevelAnimated(zoomLevel: Double) {
        val cameraOptions = CameraOptions.Builder()
            .zoom(zoomLevel)
            .build()
        val animationOptions = MapAnimationOptions.Builder()
            .duration(1000)
            .build()

        mapView?.getMapboxMap()?.easeTo(cameraOptions, animationOptions)
    }

    private fun decodeElevation(pixel: Int): Double {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return ((r * 256 * 256 + g * 256 + b) / 10.0) - 10000
    }
    private fun setPixels(){

    }

    private fun fillMatrixWithExampleData() {
        for (i in 0..255) {
            for (j in 0..255) {
                windColorValues[i][j] = Math.random() * 50 // Exempelvärden mellan 0 och 50
            }
        }
    }
    fun arrayToBitmap(array: Array<DoubleArray>): Bitmap {
        val width = array.size
        val height = array[0].size
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Gå igenom varje element i arrayen och sätt pixelvärdet
        for (i in 0 until width) {
            for (j in 0 until height) {
                val value = array[i][j]
                val color = getColorForValue(value)

                // Sätt pixeln på rätt plats (j är x, i är y)
                bitmap.setPixel(j, i, color)
            }
        }
        return bitmap
    }
    private fun getColorForValue(value: Double): Int {
        // Mappa värde mellan 0 och 50 till en färgskala
        val scaledValue = value.coerceIn(0.0, 50.0)  // Säkerställ att värdet är mellan 0 och 50

        // Här gör vi en linjär övergång från blått (0) till rött (50)
        val red = (scaledValue / 50 * 255).toInt()  // Värdet 50 ger röd färg
        val green = 0  // Håller grönt på 0 för enkelhetens skull
        val blue = (255 - (scaledValue / 50 * 255)).toInt()  // Värdet 0 ger blå färg

        return Color.rgb(red, green, blue)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}

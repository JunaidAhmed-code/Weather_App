package com.example.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.SearchView

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Islamabad")
        SearchCity()
    }
    private fun SearchCity(){
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true}
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/") // Corrected base URL
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "47598bdf73bca1eac10ca3cc74211b85", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.Humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                       binding.date.text = date().toString()
                       binding.cityName.text = "$cityName"

                    changeImageaccordingtoweathercondition(condition)

                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

                   TODO("Not yet implemented")
            // Handle API call failure here (e.g., log the error, display an error message)
            }
        })
    }
    private fun changeImageaccordingtoweathercondition(conditions : String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                    binding.root.setBackgroundResource(R.drawable.colud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    private fun date(): String{
        val sdf = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}
package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.api.WeatherApiService
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel : ViewModel() {

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather

    private val _forecast = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecast: StateFlow<List<ForecastItem>> = _forecast

    private val _iconUrl = MutableStateFlow<String?>(null)
    val iconUrl: StateFlow<String?> get() = _iconUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = WeatherApiService.fetchWeather(city, apiKey)
                if (weatherResponse != null) {
                    _currentWeather.value = weatherResponse
                    fetchWeatherIcon(weatherResponse.weather.firstOrNull()?.icon.orEmpty())
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to fetch weather. Please check your API key or city name."
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }

    // Function to fetch weather forecast data for a given city
    fun fetchForecastData(city: String, apiKey: String) {
        // Launch a coroutine in the ViewModel scope
        viewModelScope.launch {
            try {
                // Fetch forecast data from the API
                val forecastResponse = WeatherApiService.fetchForecast(city, apiKey)

                // Check if the response is not null and update the UI state
                if (forecastResponse != null) {
                    // Update the forecast data if the response is valid
                    _forecast.value = forecastResponse.list
                    // Clear any previous error message
                    _errorMessage.value = null
                } else {
                    // Set an error message if the response is null
                    _errorMessage.value = "Failed to fetch forecast data. Please check your API key or city name."
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the API call and set an error message
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }


    private fun fetchWeatherIcon(iconId: String) {
        if (iconId.isNotEmpty()) {
            _iconUrl.value = "https://openweathermap.org/img/wn/$iconId@2x.png"
        }
    }
}

package com.phoenix.carhub.data.repository

import com.google.gson.annotations.SerializedName
import com.phoenix.carhub.data.model.ForecastItem
import com.phoenix.carhub.data.model.WeatherData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Open-Meteo API — free, no key required. https://open-meteo.com

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude")       lat: Double,
        @Query("longitude")      lon: Double,
        @Query("current")        current: String = "temperature_2m,weather_code",
        @Query("hourly")         hourly: String  = "temperature_2m,weather_code",
        @Query("forecast_hours") hours: Int      = 4,
        @Query("timezone")       timezone: String = "auto",
        @Query("timeformat")     timeformat: String = "unixtime"
    ): OpenMeteoResponse
}

data class OpenMeteoResponse(
    val current: OpenMeteoCurrent?,
    val hourly:  OpenMeteoHourly?
)

data class OpenMeteoCurrent(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code")   val weatherCode: Int
)

data class OpenMeteoHourly(
    val time: List<Long>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("weather_code")   val weatherCode: List<Int>
)

fun wmoCodeToDescription(code: Int): String = when (code) {
    0         -> "Clear"
    1, 2      -> "Partly Cloudy"
    3         -> "Overcast"
    in 45..48 -> "Foggy"
    in 51..67 -> "Rainy"
    in 71..77 -> "Snow"
    in 80..82 -> "Showers"
    in 95..99 -> "Thunderstorm"
    else      -> "Clear"
}

fun wmoCodeToEmoji(code: Int): String = when (code) {
    0         -> "☀️"
    1, 2      -> "⛅"
    3         -> "☁️"
    in 45..48 -> "🌫️"
    in 51..67 -> "🌧️"
    in 71..77 -> "❄️"
    in 80..82 -> "🌧️"
    in 95..99 -> "⛈️"
    else      -> "🌡️"
}

@Singleton
class WeatherRepository @Inject constructor() {

    private val api: OpenMeteoApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherData> =
        runCatching {
            val r = api.getWeather(lat, lon)
            val c = r.current ?: throw Exception("No data")
            WeatherData(
                temperature = c.temperature,
                description = wmoCodeToDescription(c.weatherCode),
                iconCode    = c.weatherCode.toString()
            )
        }

    suspend fun getForecast(lat: Double, lon: Double): Result<List<ForecastItem>> =
        runCatching {
            val r = api.getWeather(lat, lon)
            val h = r.hourly ?: return@runCatching emptyList()
            val fmt = SimpleDateFormat("h:mm a", Locale.getDefault())
            h.time.take(4).mapIndexed { i, ts ->
                ForecastItem(
                    time        = fmt.format(Date(ts * 1000L)),
                    temperature = h.temperature.getOrElse(i) { 0.0 },
                    iconCode    = h.weatherCode.getOrElse(i) { 0 }.toString()
                )
            }
        }
}

package com.houserose

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import java.time.Instant
import java.time.Instant.ofEpochSecond
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(Logging)
    }

    val last = Instant.now()

    val timeformat = DateTimeFormatter.ofPattern("HH:mm")

    runBlocking {
        while (true) {
            delay(30000)
            val check = Instant.now()
            val localTime = LocalTime.now(ZoneId.systemDefault()).format(timeformat)
            val destinations: List<String> = listOf<String>(
                "https://hooks.slack.com/services/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",
                "https://hooks.slack.com/services/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"
            )

            if (localTime.toString() == "5:15") {
                if (check.epochSecond - last.epochSecond < 100) {

                    val json = defaultSerializer()

                    val here = (client.get<W>() {
                        url("https://api.openweathermap.org/data/2.5/weather")
                        parameter("zip", "01108,us")
                        parameter("appid", "a6be9aa97ad5b06e2ccd3b40e935151a")
                        parameter("units", "imperial")
                    })

                    val format = DateTimeFormatter.ofPattern("EEEE, d MMMM y @ HH:mm")

                    val message = "description: " + here.weather[0].description +
                            "\ntemp: " + here.main.temp +
                            "\nfeels: " + here.main.feels_like +
                            "\nhumidity: " + here.main.humidity + "%" +
                            "\nsunrise: " + LocalDateTime.ofInstant(
                        ofEpochSecond(here.sys.sunrise.toLong()),
                        ZoneId.systemDefault()
                    ).format(format) +
                            "\nsunset: " + LocalDateTime.ofInstant(
                        ofEpochSecond(here.sys.sunset.toLong()),
                        ZoneId.systemDefault()
                    ).format(format)

                    destinations.forEach { destination ->
                        client.post<String>() {
                            url(destination)
                            headers {
                                append("Content-type", "application/json")
                            }
                            body = TextContent(message, ContentType.Text.Plain)
                        }
                    }

                    println("description: " + here.weather[0].description)
                    println("temp: " + here.main.temp)
                    println("feels: " + here.main.feels_like)
                    println("humidity: " + here.main.humidity + "%")
                    println(
                        "sunrise: " + LocalDateTime.ofInstant(
                            ofEpochSecond(here.sys.sunrise.toLong()),
                            ZoneId.systemDefault()
                        ).format(format)
                    )
                    println(
                        "sunset: " + LocalDateTime.ofInstant(
                            ofEpochSecond(here.sys.sunset.toLong()),
                            ZoneId.systemDefault()
                        ).format(format)
                    )
                    println(here)
                } else {
                    println("$localTime: multi-hit")
                }
            } else {
                println("$localTime: not yet")
            }
        }
    }
}

data class Every(val n: Long, val unit: TimeUnit)

data class W(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind,
    val rain: Rain
)

data class Clouds(
    val all: Int
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class Main(
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)


data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)

data class Rain(
    @JsonProperty("1h") val hour: Double
)
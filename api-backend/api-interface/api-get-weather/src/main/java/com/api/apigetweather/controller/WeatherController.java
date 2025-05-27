package com.api.apigetweather.controller;

import com.api.apigetweather.pojo.WeatherCityName;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wxy.api.sdk.utils.GatewayHeaderUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 天气API
 *
 * @author wxy
 */
@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController
{
    @PostMapping("/city")
    public String getTodayWeather(@RequestBody WeatherCityName city, HttpServletRequest myRequest, HttpServletRequest gatewayRequest)
    {
        GatewayHeaderUtils.validateGatewayHeaders(gatewayRequest);
        String apiKey = "449934a30645b02e3939b200fa7cab46"; // API 密钥
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city.getCity() + "&appid=" + apiKey + "&units=metric";
        Map<String, Object> map = new HashMap<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseBody = "";

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                responseBody = response.body().string();
                log.info("Weather Data: {}", responseBody);
            }
            else {
                log.info("Failed to fetch weather data: {}", response.code());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        String description = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
        double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

        map.put("Weather", description);
        map.put("Temperature", temperature + "°C");
        map.put("Humidity", humidity + "%");

        return map.toString();
    }
}

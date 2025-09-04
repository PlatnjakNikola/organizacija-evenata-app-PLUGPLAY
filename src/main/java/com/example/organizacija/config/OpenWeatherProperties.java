// package prilagodi svom projektu (npr. com.example.organizacija.config)
package com.example.organizacija.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "openweather")
public class OpenWeatherProperties {
    // getters/setters
    private String apiKey;
    private String baseUrl;
    private String units = "metric";
    private String lang = "hr";

}

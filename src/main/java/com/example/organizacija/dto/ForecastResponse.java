package com.example.organizacija.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {
    private String cod;
    private int cnt;
    private List<ForecastItem> list;
    private City city;

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
        private int timezone; // seconds offset from UTC
    }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        private long dt; // epoch seconds UTC
        private Main main;
        private String dt_txt;
    }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        @JsonProperty("temp_min") private Double tempMin;
        @JsonProperty("temp_max") private Double tempMax;
    }
}

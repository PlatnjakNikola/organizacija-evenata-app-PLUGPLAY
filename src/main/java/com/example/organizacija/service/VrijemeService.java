package com.example.organizacija.service;


import com.example.organizacija.config.OpenWeatherProperties;
import com.example.organizacija.dto.ForecastResponse;
import com.example.organizacija.model.DailySummary;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VrijemeService {

    private final RestTemplate restTemplate;
    private final OpenWeatherProperties props;

    public VrijemeService(RestTemplate restTemplate, OpenWeatherProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    // Returns up to 5 days of min/max by city name using /data/2.5/forecast
    public List<DailySummary> get5DayDailyMinMax(String city) {
        // Build URL
        String url = String.format(
                "%s/data/2.5/forecast?q=%s&units=%s&lang=%s&appid=%s",
                props.getBaseUrl(), encode(city), props.getUnits(), props.getLang(), props.getApiKey()
        );

        ForecastResponse res;
        try {
            res = restTemplate.getForObject(url, ForecastResponse.class);
        } catch (RestClientException ex) {
            throw new RuntimeException("Could not fetch forecast: " + ex.getMessage(), ex);
        }
        if (res == null || res.getCod() == null || !res.getCod().equals("200")) {
            String code = (res != null ? res.getCod() : "null");
            throw new RuntimeException("OpenWeather response not OK (cod=" + code + ")");
        }

        int tzSeconds = res.getCity() != null ? res.getCity().getTimezone() : 0;
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzSeconds);

        // Group 3-hour slots by local date and compute min/max for each date
        Map<LocalDate, List<ForecastResponse.ForecastItem>> byDate = res.getList().stream()
                .collect(Collectors.groupingBy(item ->
                                Instant.ofEpochSecond(item.getDt()).atOffset(offset).toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<DailySummary> out = new ArrayList<>();
        for (Map.Entry<LocalDate, List<ForecastResponse.ForecastItem>> e : byDate.entrySet()) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (ForecastResponse.ForecastItem it : e.getValue()) {
                double tMin = it.getMain().getTempMin() != null ? it.getMain().getTempMin() : it.getMain().getTemp();
                double tMax = it.getMain().getTempMax() != null ? it.getMain().getTempMax() : it.getMain().getTemp();
                if (tMin < min) min = tMin;
                if (tMax > max) max = tMax;
            }
            out.add(new DailySummary(e.getKey(), min, max));
        }

        // Keep first 5 days
        return out.stream().limit(5).collect(Collectors.toList());
    }

    private String encode(String s) {
        // Very simple encoding; for real code use URLEncoder.encode(s, UTF-8)
        return s.replace(" ", "%20");
    }
}

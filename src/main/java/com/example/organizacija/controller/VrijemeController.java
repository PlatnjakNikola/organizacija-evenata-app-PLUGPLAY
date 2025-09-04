package com.example.organizacija.controller;


import com.example.organizacija.model.DailySummary;
import com.example.organizacija.service.VrijemeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequestMapping("/vrijeme") // <-- sve rute ispod /vrijeme
@RequiredArgsConstructor
public class VrijemeController {

    private final VrijemeService vrijemeService;

    // GET /result?city=Zagreb
    @GetMapping("/result")
    public String result(@RequestParam(defaultValue = "Zagreb") String city, Model model) {
        try {
            List<DailySummary> days = vrijemeService.get5DayDailyMinMax(city);
            model.addAttribute("city", city);
            model.addAttribute("days", days);
            model.addAttribute("updatedAt", ZonedDateTime.now());
        } catch (Exception ex) {
            model.addAttribute("city", city);
            model.addAttribute("error", ex.getMessage());
        }
        return "vrijeme/result"; // <-- Gleda templates/vrijeme/result.html
    }

    // Opcionalno: /vrijeme bez /result preusmjeri na isti view
    @GetMapping
    public String index(@RequestParam(defaultValue = "Zagreb") String city, Model model) {
        return result(city, model);
    }
}

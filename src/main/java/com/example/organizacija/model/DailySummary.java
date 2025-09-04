package com.example.organizacija.model;


import java.time.LocalDate;

public record DailySummary(LocalDate date, double minC, double maxC) {
}


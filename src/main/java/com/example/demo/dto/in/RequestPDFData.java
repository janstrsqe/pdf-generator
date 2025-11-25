package com.example.demo.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class RequestPDFData {

    private String unitName;
    private Instant date;
    private Location location;
    private String category;
    private String title;
    private String personnelCategories;
    private String incidentDescription;
    private String chronology;
    private String incidentAnalysis;
    private Summary summary;

    @Getter
    @AllArgsConstructor
    public static class Location {
        private String locationName;
        private String coordinate;
        private String elevation;
        private String benchLevel;
        private EnvironmentCondition environmentCondition;
        private String locationPicturePath;
    }

    @Getter
    @AllArgsConstructor
    public static class EnvironmentCondition {
        private int temperature;
        private int windVelocity;
        private String weather;
        private String visibility;
        private String surfaceConditions;
    }

    @Getter
    @AllArgsConstructor
    public static class Summary {
        private int injuriesPerson;
        private int brokenEquipment;
        private int hoursDowntime;
    }

    public ZonedDateTime dateToWIB(){
        return date.atZone(ZoneId.of("Asia/Jakarta"));
    }

}

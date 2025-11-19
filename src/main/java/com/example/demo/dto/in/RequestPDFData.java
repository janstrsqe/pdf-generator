package com.example.demo.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class RequestPDFData {

    private String unitName;
    private Instant date;
    private String location;
    private String category;
    private String title;
    private String personnelCategories;
    private String incidentDescription;
    private int injuriesPerson;
    private int brokenEquipment;
    private int hoursDowntime;

    public ZonedDateTime dateToWIB(){
        return date.atZone(ZoneId.of("Asia/Jakarta"));
    }

}

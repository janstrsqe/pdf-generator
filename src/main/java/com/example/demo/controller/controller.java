package com.example.demo.controller;


import com.example.demo.service.IncidentReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class controller {

    private final IncidentReportService incidentReportService;

    @GetMapping("/pdf")
    public ResponseEntity<Boolean> getCardActivityLog(
            HttpServletRequest request
    ) {
        incidentReportService.generateIncidentPDFReport();
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}

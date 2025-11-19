package com.example.demo.service.impl.report;

import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.service.IncidentReportService;
import com.example.demo.service.impl.report.pdf.IncidentPDFReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IncidentReportServiceImpl implements IncidentReportService {

    private final IncidentPDFReport incidentPDFReport;

    public void generateIncidentPDFReport() {
        RequestPDFData dto = new RequestPDFData(
                "Unit Test 123",
                Instant.parse("2025-11-10T08:30:00Z"),
                "PT Berau Coal",
                "Kategori Insiden",
                "KEGAGALAN SISTEM HIDROLIK",
                "1 Operator, 2 Mekanik",
                "Pada tanggal 15 Oktober 2024 pukul 14:35 WIB, terjadi kegagalan sistem hidrolik pada unit Excavator HD 785-7 dengan nomor unit EX-127 yang sedang beroperasi di area Pit 3 Selatan, Lati Mine. Insiden terjadi saat operator sedang melakukan aktivitas penggalian material overburden.\n" +
                        "\n" +
                        "Kegagalan sistem hidrolik menyebabkan boom excavator turun secara tiba-tiba dan tidak terkontrol, mengakibatkan bucket menghantam area kerja dengan keras. Tidak ada korban jiwa atau luka-luka dalam insiden ini, namun unit mengalami kerusakan signifikan pada sistem hidrolik dan struktur boom.",
                0, 1, 72);

        incidentPDFReport.generateIncidentReportPDF(dto);
    }

}

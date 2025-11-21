package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;

import java.util.List;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class Slide2IncidentPDFReport extends IncidentPDFPage {

    public Slide2IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class Slide2Data {
        private String date;
        private String location;
        private String category;
        private String personnelCategories;
        private String incidentDescription;
        private int injuriesPerson;
        private int brokenEquipment;
        private int hoursDowntime;
    }

    private Slide2Data toSlide2Data(RequestPDFData request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, 'Pukul' hh:mm 'WIB'");
        String formattedDate = request.dateToWIB().format(formatter);
        return new Slide2Data(formattedDate,
                request.getLocation(), request.getCategory(),
                request.getPersonnelCategories(),
                request.getIncidentDescription(), request.getInjuriesPerson(),
                request.getBrokenEquipment(), request.getHoursDowntime());
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide2Data slide1Data = toSlide2Data(request);
        PDPage page = initPage(defaultPDFComponent);

        PDRectangle rect = page.getMediaBox();
        float pageWidth = rect.getWidth();
        float pageHeight = rect.getHeight();

        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        initSlidePage(defaultPDFComponent, cs, 1, "Ringkasan Insiden");

        float tableX = 40;
        float tableY = page.getMediaBox().getHeight() - 80;

        // -------------------------
        // 1️⃣ DEFINE TABLE COLUMNS
        // -------------------------
        List<PDFTableBuilder.TableColumn> columns = List.of(
                new PDFTableBuilder.TableColumn("Layer", 300, TextAlignment.CENTER, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("Yang Diharapkan", 450, TextAlignment.CENTER, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("Yang Terjadi", 450, TextAlignment.CENTER, TextAlignment.CENTER),
                new PDFTableBuilder.TableColumn("Rekomendasi Perbaikan", 450, TextAlignment.CENTER, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("Status", 150, TextAlignment.CENTER, TextAlignment.CENTER)
        );

        // -------------------------
        // 2️⃣ DEFINE ROW DATA
        // -------------------------
        List<List<String>> rows = List.of(
                List.of(
                        "Layer 1\nEngineering Design",
                        "Sistem hidrolik dirancang dengan pressure relief valve dan sensor tekanan",
                        "Tidak ada sensor tekanan real-time untuk monitoring kondisi sistem",
                        "Instalasi sensor tekanan hidrolik dengan alert system",
                        "X"
                ),
                List.of(
                        "Layer 2\nAdministrative Controls",
                        "Jadwal PM ketat dengan auto-stop unit yang melewati due date Test Panjangin Text\n Lebih Panjang lagi",
                        "PM schedule tidak enforced, unit tetap operasi meski overdue 6 bulan",
                        "Implementasi sistem auto-lock untuk unit overdue PM",
                        "X"
                ),
                List.of(
                        "Layer 3\nSupervision & Training",
                        "Operator & supervisor mengenali early warning hydraulic failure",
                        "Operator lanjutkan operasi meski dengar suara abnormal",
                        "Program training intensif hydraulic system early warning",
                        "X"
                )
        );

        // -------------------------
        // 3️⃣ STYLE TABLE (OPTIONAL)
        // -------------------------
        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(22)
                        .headerBgColor(new Color(24, 94, 57))
                        .headerTextColor(Color.WHITE)
                        .font(defaultPDFComponent.getFontBold())
                        .build())
                .cornerRadius(10)
                .lineColor(Color.WHITE)
                .build();

        // -------------------------
        // 4️⃣ DRAW ROUNDED TABLE
        // -------------------------
        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                    .cs(cs)
                    .tablePosition(new PDFTableBuilder.TablePosition(50, pageHeight-200))
                    .columns(columns)
                    .rows(rows)
                    .tableStyle(style)
                    .build()
        );

        cs.close();
    }

}

package com.example.demo.service.impl.report.pdf;

import com.example.demo.dto.in.RequestPDFData;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IncidentPDFReport {

    private final CoverPageIncidentPDFReport coverPageIncidentPDFReport;
    private final Slide1IncidentPDFReport slide1IncidentPDFReport;
    private final Slide2IncidentPDFReport slide2IncidentPDFReport;

    public void generateIncidentReportPDF(RequestPDFData requestPDFData) {
        try (PDDocument document = new PDDocument()) {

            // === Load Fonts ===
            PDFont fontRegular = PDType0Font.load(
                    document,
                    getClass().getResourceAsStream("/assets/Montserrat-Regular.ttf")
            );

            PDFont fontBold = PDType0Font.load(
                    document,
                    getClass().getResourceAsStream("/assets/Montserrat-Bold.ttf")
            );

            PDImageXObject slideBackground = PDImageXObject.createFromFile(
                    new File("src/main/resources/assets/image-2.jpg").getAbsolutePath(),
                    document
            );

            PDImageXObject coverBackground = PDImageXObject.createFromFile(
                    new File("src/main/resources/assets/image.jpg").getAbsolutePath(),
                    document
            );

            DefaultPDFComponent defaultPDFComponent = new DefaultPDFComponent(
                    document, new PDRectangle(2304, 1302),
                    fontRegular, fontBold, coverBackground, slideBackground);

            coverPageIncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide1IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide2IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);

            document.save("incident-report.pdf");

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}

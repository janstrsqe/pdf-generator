package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class Slide5IncidentPDFReport extends IncidentPDFPage {

    public Slide5IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
        this.slideNumber = 5;
        this.slideName = "Analisa Penyebab Kejadian";
    }

    @AllArgsConstructor
    private static class Slide5Data {
        private String locationName;
        private String date;
        private String chronology;
    }

    private Slide5Data toSlide5Data(RequestPDFData request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = request.dateToWIB().format(formatter);
        return new Slide5Data(request.getLocation().getLocationName(),formattedDate, request.getIncidentAnalysis());
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide5Data slide5Data = toSlide5Data(request);
        float pageHeight = defaultPDFComponent.getCustomSize().getHeight();
        PDPageContentStream cs = newSlide(null, defaultPDFComponent);

        String text = String.format("Tanggal: %s | Lokasi: %s", slide5Data.date, slide5Data.locationName);
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                100,
                                pageHeight - 280,
                                1200,
                                50))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(text)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(30)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                110,
                                pageHeight - 1200,
                                2100,
                                890))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(229, 231, 235))
                                .fillColor(new Color(249, 250, 251))
                                .rounded(10)
                                .strokeLine(6)
                                .padding(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                130,
                                pageHeight - 390,
                                2080,
                                80))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Penyebab Kejadian")
                                .fontSize(30)
                                .fontColor(Color.BLACK)
                                .align(TextAlignment.LEFT)
                                .font(defaultPDFComponent.getFontBold())
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                130,
                                220,
                                2080,
                                720))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide5Data.chronology)
                                .fontSize(25)
                                .fontColor(Color.BLACK)
                                .align(TextAlignment.LEFT)
                                .font(defaultPDFComponent.getFontRegular())
                                .build())
                        .build()
        );

        cs.close();
    }

}

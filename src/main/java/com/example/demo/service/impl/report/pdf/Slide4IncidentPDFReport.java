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
public class Slide4IncidentPDFReport extends IncidentPDFPage {

    public Slide4IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
        this.slideNumber =  4;
        this.slideName = "Kronologi Kejadian";
    }

    @AllArgsConstructor
    private static class Slide4Data {
        private String locationName;
        private String date;
        private String chronology;
    }

    private Slide4Data toSlide4Data(RequestPDFData request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = request.dateToWIB().format(formatter);
        return new Slide4Data(request.getLocation().getLocationName(),formattedDate, request.getChronology());
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide4Data slide4Data = toSlide4Data(request);
        float pageHeight = defaultPDFComponent.getCustomSize().getHeight();
        PDPageContentStream cs = newSlide(null, defaultPDFComponent);

        String text = String.format("Tanggal: %s | Lokasi: %s", slide4Data.date, slide4Data.locationName);
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
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide4Data.chronology)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(229, 231, 235))
                                .fillColor(new Color(249, 250, 251))
                                .rounded(10)
                                .strokeLine(6)
                                .padding(50)
                                .build())
                        .build()
        );

        cs.close();
    }

}

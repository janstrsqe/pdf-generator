package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class Slide1IncidentPDFReport extends IncidentPDFPage {

    public Slide1IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class Slide1Data {
        private String date;
        private String location;
        private String category;
        private String personnelCategories;
        private String incidentDescription;
        private int injuriesPerson;
        private int brokenEquipment;
        private int hoursDowntime;
    }

    private Slide1Data toSlide1Data(RequestPDFData request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, 'Pukul' hh:mm 'WIB'");
        String formattedDate = request.dateToWIB().format(formatter);
        return new Slide1Data(formattedDate,
                request.getLocation().getLocationName(), request.getCategory(),
                request.getPersonnelCategories(),
                request.getIncidentDescription(), request.getSummary().getInjuriesPerson(),
                request.getSummary().getBrokenEquipment(), request.getSummary().getHoursDowntime());
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide1Data slide1Data = toSlide1Data(request);
        PDPage page = initPage(defaultPDFComponent);

        PDRectangle rect = page.getMediaBox();
        float pageWidth = rect.getWidth();
        float pageHeight = rect.getHeight();

        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        initSlidePage(defaultPDFComponent, cs, 1, "Ringkasan Insiden");

        float boxX = 110;
        float boxY = pageHeight - 380;
        float boxWidth = 1028;
        float boxHeight = 145;
        float spaceOfBox = 30;

        drawInfoSection(
                cs,
                boxX, boxY,
                boxWidth, boxHeight,
                "Tanggal & Waktu Kejadian",
                slide1Data.date,
                80, 20,
                30, -20,
                defaultPDFComponent,
                defaultPDFComponent.icons.get("calendar")
        );

        drawInfoSection(
                cs,
                boxX + boxWidth + spaceOfBox,
                boxY,
                boxWidth, boxHeight,
                "Lokasi Insiden",
                slide1Data.location,
                70, 20,
                20, -20,
                defaultPDFComponent,
                defaultPDFComponent.icons.get("location")
        );

        drawInfoSection(
                cs,
                boxX,
                boxY - boxHeight - spaceOfBox,
                boxWidth, boxHeight,
                "Kategori Insiden",
                slide1Data.category,
                80, 20,
                30, -20,
                defaultPDFComponent,
                defaultPDFComponent.icons.get("warning")
        );

        drawInfoSection(
                cs,
                boxX + boxWidth + spaceOfBox,
                boxY - boxHeight - spaceOfBox,
                boxWidth, boxHeight,
                "Personel Terlibat",
                slide1Data.personnelCategories,
                80, 20,
                30, -20,
                defaultPDFComponent,
                defaultPDFComponent.icons.get("group")
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX - 10,
                                (pageHeight / 2) - 20,
                                2100,
                                80))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Deskripsi Kejadian")
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.WHITE)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(33, 87, 50))
                                .strokeLine(2)
                                .fillColor(new Color(33, 87, 50))
                                .rounded(5)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX - 25,
                                (pageHeight / 2) - 330,
                                2100,
                                300))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide1Data.incidentDescription)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        boxX = boxX-10;
        boxY = (pageHeight / 2) - 500;
        boxWidth = 1025;
        boxHeight = 150;

        drawStatBox(
                cs,
                boxX, boxY, boxWidth, boxHeight,
                String.valueOf(slide1Data.injuriesPerson),
                "Cedera",
                "Personel",
                new Color(250, 177, 177),
                new Color(255, 229, 229),
                defaultPDFComponent
        );

        drawStatBox(
                cs,
                boxX-10 + boxWidth + 50, boxY, boxWidth, boxHeight,
                String.valueOf(slide1Data.brokenEquipment),
                "Kerusakan Peralatan",
                "Unit Heavy Equipment",
                new Color(252, 241, 148),
                new Color(254, 252, 234),
                defaultPDFComponent
        );

        cs.close();
    }

    private void drawStatBox (
            PDPageContentStream cs,
            float x,
            float y,
            float boxWidth,
            float boxHeight,
            String valueText,
            String titleText,
            String subtitleText,
            Color strokeColor,
            Color fillColor,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x, y, boxWidth, boxHeight))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeLine(5)
                                .strokeColor(strokeColor)
                                .fillColor(fillColor)
                                .rounded(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x+ 10, y-30, boxWidth, boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(valueText)
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.BLACK)
                                .fontSize(50)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()

        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x + 10, y + 30,
                                boxWidth, boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(titleText)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x + 60, y - 38,
                                boxWidth - 10, boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(subtitleText)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );
    }

    private void drawInfoSection(
            PDPageContentStream cs,
            float x,
            float y,
            float boxWidth,
            float boxHeight,
            String label,
            String value,
            float labelOffsetX,
            float labelOffsetY,
            float valueOffsetX,
            float valueOffsetY,
            DefaultPDFComponent defaultPDFComponent,
            PDImageXObject icon
    ) throws IOException{

        // Background box
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(x, y, boxWidth, boxHeight))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(213, 219, 229))
                                .strokeLine(2)
                                .fillColor(new Color(249, 250, 252))
                                .rounded(7)
                                .build())
                        .build()
        );

        cs.drawImage(icon, x + labelOffsetX - 30,  y + 78, 30, 30);

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x + labelOffsetX - 10,
                                y + labelOffsetY,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(label)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x + valueOffsetX,
                                y + valueOffsetY,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(value)
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.BLACK)
                                .fontSize(30)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );
    }


}

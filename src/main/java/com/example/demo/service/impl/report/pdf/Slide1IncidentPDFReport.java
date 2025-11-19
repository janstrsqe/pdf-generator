package com.example.demo.service.impl.report.pdf;

import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class Slide1IncidentPDFReport extends IncidentPDFPage {

    public Slide1IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder) {
        super(pdfBoxBuilder);
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
                request.getLocation(), request.getCategory(),
                request.getPersonnelCategories(),
                request.getIncidentDescription(), request.getInjuriesPerson(),
                request.getBrokenEquipment(), request.getHoursDowntime());
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
        float boxHeight = 120;
        float spaceOfBox = 30;
        Color sliceColor = new Color(33, 87, 50);

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX,
                                boxY,
                                boxWidth,
                                boxHeight
                        ))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(249, 250, 251))
                                .strokeLine(2)
                                .fillColor(new Color(249, 250, 251))
                                .rounded(0)
                                .build())
                        .build(),
                 sliceColor
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + 80,
                                pageHeight - 360,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Tanggal & Waktu Kejadian")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(null)
                                .strokeLine(0)
                                .fillColor(null)
                                .rounded(0)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + 30,
                                pageHeight - 400,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide1Data.date)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(30)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox,
                                boxY,
                                boxWidth,
                                boxHeight
                        ))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(249, 250, 251))
                                .strokeLine(2)
                                .fillColor(new Color(249, 250, 251))
                                .rounded(0)
                                .build())
                        .build(),
                sliceColor
        );


        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox + 70,
                                pageHeight - 360,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Lokasi Insiden")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox + 20,
                                pageHeight - 400,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide1Data.location)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(30)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX,
                                boxY - boxHeight - spaceOfBox,
                                boxWidth,
                                boxHeight
                        ))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(249, 250, 251))
                                .strokeLine(2)
                                .fillColor(new Color(249, 250, 251))
                                .rounded(0)
                                .build())
                        .build(),
                sliceColor
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + 80,
                                pageHeight - 510,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Kategori Insiden")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + 30,
                                pageHeight - 550,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide1Data.category)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(30)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(null)
                                .strokeLine(0)
                                .fillColor(null)
                                .rounded(0)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox,
                                boxY - boxHeight - spaceOfBox,
                                boxWidth,
                                boxHeight
                        ))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(249, 250, 251))
                                .strokeLine(2)
                                .fillColor(new Color(249, 250, 251))
                                .rounded(0)
                                .build())
                        .build(),
                sliceColor
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox + 80,
                                pageHeight - 510,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Personel Terlibat")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX + boxWidth + spaceOfBox + 30,
                                pageHeight - 550,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slide1Data.personnelCategories)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(30)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX - 10,
                                (pageHeight / 2) - 10,
                                2100,
                                80))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Deskripsi Kejadian")
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.WHITE)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(new Color(33, 87, 50))
                                .strokeLine(2)
                                .fillColor(new Color(33, 87, 50))
                                .rounded(0)
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
                                .fontSize(30)
                                .align(PDFBoxBuilder.Align.LEFT)
                                .build())
                        .build()
        );

        boxX = boxX-10;
        boxY = (pageHeight / 2) - 570;
        boxWidth = 650;
        boxHeight = 200;

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX,
                                boxY,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(String.format("%s", slide1Data.injuriesPerson))
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(177, 30, 27))
                                .fontSize(50)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeLine(10)
                                .strokeColor(new Color(247, 204, 203))
                                .fillColor(new Color(252, 243, 242))
                                .rounded(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX,
                                boxY + (boxHeight / 2) - 50,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Cedera")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(212, 36, 34))
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX,
                                boxY + (boxHeight / 2) - 150,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Personel")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        float boxX2 = (pageWidth / 2 - boxWidth / 2) - 15;
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX2,
                                boxY,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(String.format("%s", slide1Data.brokenEquipment))
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(128, 78, 19))
                                .fontSize(50)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeLine(10)
                                .strokeColor(new Color(252, 241, 148))
                                .fillColor(new Color(254, 252, 234))
                                .rounded(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX2,
                                boxY + (boxHeight / 2) - 50,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Kerusakan Peralatan")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(161, 106, 25))
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX2,
                                boxY + (boxHeight / 2) - 150,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Unit Heavy Equipment")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        float boxX3 = (pageWidth - boxX + 10 - boxWidth) - 30;
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX3,
                                boxY,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(String.format("%s", slide1Data.hoursDowntime))
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(35, 70, 221))
                                .fontSize(50)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeLine(10)
                                .strokeColor(new Color(196, 218, 252))
                                .fillColor(new Color(240, 246, 254))
                                .rounded(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX3,
                                boxY + (boxHeight / 2) - 50,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Downtime")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(114, 147, 246))
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                boxX3,
                                boxY + (boxHeight / 2) - 150,
                                boxWidth,
                                boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Jam Operasi")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.GRAY)
                                .fontSize(25)
                                .align(PDFBoxBuilder.Align.CENTER)
                                .build())
                        .build()
        );

        cs.close();
    }

}

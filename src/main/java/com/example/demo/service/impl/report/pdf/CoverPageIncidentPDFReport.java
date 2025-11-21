package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class CoverPageIncidentPDFReport extends IncidentPDFPage {

    public CoverPageIncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class CoverData {
        private String unitName;
        private String date;
        private String location;
        private String category;
        private String title;
    }

    private CoverData toCoverData(RequestPDFData request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = request.dateToWIB().format(formatter);
        return new CoverData(
                request.getUnitName(), formattedDate,
                request.getLocation(), request.getCategory(),
                request.getTitle());
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        CoverData coverData = toCoverData(request);
        PDPage page = initPage(defaultPDFComponent);

        PDRectangle rect = page.getMediaBox();
        float pageWidth = rect.getWidth();
        float pageHeight = rect.getHeight();

        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        cs.drawImage(defaultPDFComponent.getCoverBackground(), 0, 0, pageWidth, pageHeight);

        // ===========================
        // TITLE BOX: "LAPORAN INVESTIGASI INSIDEN"
        // ===========================

        float boxWidth = 700f;
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                .cs(cs)
                .boxPosition(new PDFBoxBuilder.BoxPosition((pageWidth - boxWidth) / 2, pageHeight / 2 + 150, boxWidth, 80f))
                .boxText(PDFBoxBuilder.BoxText.builder()
                        .text("LAPORAN INVESTIGASI INSIDEN")
                        .font(defaultPDFComponent.getFontRegular())
                        .fontColor(Color.WHITE)
                        .fontSize(30)
                        .align(TextAlignment.CENTER)
                        .build())
                .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                        .strokeLine(2)
                        .strokeColor(Color.WHITE)
                        .build())
                .build());

        // ===========================
        // MAIN TITLE: KEGAGALAN SISTEM HIDROLIK
        // ===========================

        boxWidth = 2000f;
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                (pageWidth - boxWidth) / 2,
                                pageHeight / 2,
                                boxWidth,
                                130f))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(coverData.title)
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.WHITE)
                                .fontSize(95)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        // Sub-title
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                (pageWidth - boxWidth) / 2,
                                pageHeight / 2 - 120,
                                boxWidth,
                                120f))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(coverData.unitName)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(45)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        // ===========================
        // 3 COLUMN INFO
        // ===========================
        float colY = pageHeight / 2 - 200;
        float colSize = 450f;
        float colHeight = 50f;
        float rangeBetweenCol = 60;
        Color greenAdjusted = new Color(88, 181, 115);

        float col1X = 400;

        // Column 1: Tanggal Insiden
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col1X,
                                colY,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Tanggal Insiden")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(greenAdjusted)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col1X,
                                colY - rangeBetweenCol,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(coverData.date)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        // Column 2: Lokasi Insiden
        float col2X = col1X + 500;
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col2X - 20,
                                colY - 60,
                                1,
                                colHeight * 2 + 10))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(greenAdjusted)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col2X,
                                colY,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Lokasi")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(greenAdjusted)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col2X,
                                colY - rangeBetweenCol,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(coverData.location)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());


        // Column 3: Kategori
        float col3X = col2X + 500;

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col3X - 20,
                                colY - 60,
                                1,
                                colHeight * 2 + 10))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(greenAdjusted)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col3X,
                                colY,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Kategori")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(greenAdjusted)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                col3X,
                                colY - rangeBetweenCol,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(coverData.category)
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(30)
                                .align(TextAlignment.CENTER)
                                .build())
                        .build());

        // ===========================
        // FOOTER
        // ===========================
        rangeBetweenCol = 40;
        float colXFooter = 100;
        float colYFooter = 100;

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                colXFooter,
                                colYFooter,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Tim Investigasi")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(20)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                colXFooter,
                                colYFooter - rangeBetweenCol,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Safety Department - PT Berau Coal")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(20)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build());

        float badgeX = 120;
        float badgeY = pageHeight - 290;
        float badgeWidth = 420;
        float badgeHeight = 150;

        pdfBoxBuilder.drawRoundedBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                    .cs(cs)
                    .boxPosition(new PDFBoxBuilder.BoxPosition(badgeX, badgeY, badgeWidth, badgeHeight))
                    .boxStyle(PDFBoxBuilder.BoxStyle.builder().fillColor(Color.WHITE).rounded(20).build())
                    .build());
        cs.fill();

        cs.beginText();
        cs.setFont(defaultPDFComponent.getFontRegular(), 35);
        cs.setNonStrokingColor(new Color(33, 87, 50));  // green
        cs.newLineAtOffset(badgeX + 70, badgeY + badgeHeight - 70);
        cs.showText("PT BERAU COAL");
        cs.endText();

        cs.beginText();
        cs.setFont(defaultPDFComponent.getFontRegular(), 28);
        cs.setNonStrokingColor(new Color(108,108,108)); // gray
        cs.newLineAtOffset(badgeX + 70, badgeY + badgeHeight - 105);
        cs.showText("Energy for Better Life");
        cs.endText();

        colXFooter = pageWidth - colXFooter - colSize;

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                colXFooter,
                                colYFooter,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Dokumen Rahasia")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(20)
                                .align(TextAlignment.RIGHT)
                                .build())
                        .build());


        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                colXFooter,
                                colYFooter - rangeBetweenCol,
                                colSize,
                                colHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Hanya Untuk Keperluan Internal")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(20)
                                .align(TextAlignment.RIGHT)
                                .build())
                        .build());

        cs.close();
    }

}

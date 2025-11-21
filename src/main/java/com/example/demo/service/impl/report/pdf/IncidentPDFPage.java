package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IncidentPDFPage {

    protected final PDFBoxBuilder pdfBoxBuilder;
    protected final PDFTableBuilder pdfTableBuilder;

    public PDPage initPage(DefaultPDFComponent defaultPDFComponent){
        PDPage page = new PDPage(defaultPDFComponent.getCustomSize());
        defaultPDFComponent.getDocument().addPage(page);

        return page;
    }

    public void initSlidePage(
            DefaultPDFComponent defaultPDFComponent, PDPageContentStream cs,
            int slideNumber, String slideName
    ) throws IOException {
        PDRectangle pageSize = defaultPDFComponent.getCustomSize();
        float pageHeight = pageSize.getHeight();
        float pageWidth = pageSize.getWidth();

        cs.drawImage(defaultPDFComponent.getSlideBackground(), 0, 0, pageWidth, pageHeight);

        // -----------------------------------------------------------
        // 2. Page Title
        // -----------------------------------------------------------
        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                80,
                                pageHeight - 90,
                                1100,
                                40))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(String.format("Slide %s", slideNumber))
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.WHITE)
                                .fontSize(25)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                80,
                                pageHeight - 160,
                                1100,
                                80))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(slideName)
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.WHITE)
                                .fontSize(55)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build());

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                pageWidth - 100 - 400,
                                pageHeight - 120,
                                400,
                                60))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("PT BERAU COAL")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(new Color(33, 87, 50))
                                .fontSize(25)
                                .align(TextAlignment.CENTER)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeColor(Color.WHITE)
                                .strokeLine(0)
                                .fillColor(Color.WHITE)
                                .rounded(10)
                                .build())
                        .build());
    }

}

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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class Slide3IncidentPDFReport extends IncidentPDFPage {

    public Slide3IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class Slide3Data {
        private String locationName;
        private String coordinate;
        private String elevation;
        private String benchLevel;
        private int temperature;
        private int windVelocity;
        private String weather;
        private String visibility;
        private String surfaceConditions;
        private String locationPicturePath;
    }

    private Slide3Data toSlide3Data(RequestPDFData request) {
        return new Slide3Data(
                request.getLocation().getLocationName(), request.getLocation().getCoordinate(),
                request.getLocation().getElevation(), request.getLocation().getBenchLevel(),
                request.getLocation().getEnvironmentCondition().getTemperature(), request.getLocation().getEnvironmentCondition().getWindVelocity(),
                request.getLocation().getEnvironmentCondition().getWeather(), request.getLocation().getEnvironmentCondition().getVisibility(),
                request.getLocation().getEnvironmentCondition().getSurfaceConditions(),
                request.getLocation().getLocationPicturePath()
        );
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide3Data slide3Data = toSlide3Data(request);
        PDPage page = initPage(defaultPDFComponent);

        PDRectangle rect = page.getMediaBox();
        float pageHeight = rect.getHeight();

        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        initSlidePage(defaultPDFComponent, cs, 3, "Data Lokasi Insiden");

        List<PDFTableBuilder.TableColumn> columns = List.of(new PDFTableBuilder.TableColumn("Detail Lokasi", 900, TextAlignment.LEFT, TextAlignment.LEFT));
        List<List<String>> rows = List.of(List.of(""));

        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.YELLOW)
                        .oddRowBgColor(Color.YELLOW)
                        .rowHeight(400)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(30)
                        .headerBgColor(new Color(24, 94, 57))
                        .headerTextColor(Color.WHITE)
                        .headerHeight(65)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .cornerRadius(20)
                .lineColor(Color.BLACK)
                .borderWidth(10)
                .padding(15)
                .build();

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                .cs(cs)
                .tablePosition(new PDFTableBuilder.TablePosition(110, pageHeight-250))
                .columns(columns)
                .rows(rows)
                .tableStyle(style)
                .build());

        float padding = 140;
        writeNotes(cs, padding, pageHeight-410,
                defaultPDFComponent.getFontRegular(), "Area Operasi", slide3Data.locationName,
                defaultPDFComponent.icons.get("location"));

        writeNotes(cs, padding, pageHeight-490,
                defaultPDFComponent.getFontRegular(), "Koordinat GPS", slide3Data.coordinate,
                defaultPDFComponent.icons.get("cursor"));

        writeNotes(cs, padding, pageHeight-570,
                defaultPDFComponent.getFontRegular(), "Elevasi", slide3Data.elevation,
                defaultPDFComponent.icons.get("elevation"));

        writeNotes(cs, padding, pageHeight-660,
                defaultPDFComponent.getFontRegular(), "Bench Level", slide3Data.benchLevel,
                defaultPDFComponent.icons.get("pin"));

        style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .rowHeight(400)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(30)
                        .headerBgColor(new Color(45, 92, 242))
                        .headerTextColor(Color.WHITE)
                        .headerHeight(65)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .cornerRadius(20)
                .lineColor(new Color(45, 92, 242))
                .borderWidth(5)
                .padding(25)
                .build();

        columns = List.of(new PDFTableBuilder.TableColumn("Kondisi Lingkungan", 900, TextAlignment.LEFT, TextAlignment.LEFT));

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(110, pageHeight-750))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());

        writeNotes(cs, padding, pageHeight-910,
                defaultPDFComponent.getFontRegular(), "Suhu Udara", String.format("%s°C", slide3Data.temperature),
                defaultPDFComponent.icons.get("thermometer"));

        writeNotes(cs, 650, pageHeight-910,
                defaultPDFComponent.getFontRegular(), "Kecepatan Angin", String.format("%s km/jam", slide3Data.windVelocity),
                defaultPDFComponent.icons.get("air"));

        writeNotes(cs, padding-10, pageHeight-1000,
                defaultPDFComponent.getFontRegular(), "Cuaca", slide3Data.weather,
                null);

        writeNotes(cs, padding-10, pageHeight-1090,
                defaultPDFComponent.getFontRegular(), "Visibilitas", slide3Data.visibility,
                null);

        writeNotes(cs, padding-10, pageHeight-1180,
                defaultPDFComponent.getFontRegular(), "Kondisi Permukaan", slide3Data.surfaceConditions,
                null);

        cs.close();
    }


    public void writeNotes(
        PDPageContentStream cs,
        float x, float y,
        PDFont font,
        String text1, String text2,
        PDImageXObject icon
    ) throws IOException {

        if (icon != null){
            cs.drawImage(icon, x,  y + 15, 30, 30);
            x += 30;
        }

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x,
                                y,
                                700,
                                35))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(text2)
                                .font(font)
                                .fontColor(Color.BLACK)
                                .fontSize(20)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x,
                                y + 30,
                                700,
                                30))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(text1)
                                .font(font)
                                .fontColor(Color.GRAY)
                                .fontSize(20)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );
    }
}

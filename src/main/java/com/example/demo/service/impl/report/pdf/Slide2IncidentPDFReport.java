package com.example.demo.service.impl.report.pdf;

import com.example.demo.constants.TextAlignment;
import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.util.PDFBoxBuilder;
import com.example.demo.util.PDFTableBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.io.IOException;

@Component
public class Slide2IncidentPDFReport extends IncidentPDFPage {

    public Slide2IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class Slide2Data {

    }

    @AllArgsConstructor
    private static class Person {
        private String name;
        private String sidNumber;
        private int age;
        private String position;
        private String company;
        private int workExperience;
        private String mcuValidUntil;
        private String profiling;
        private List<String> competence;
    }

    @AllArgsConstructor
    public static class SectionResult {
        public PDPageContentStream cs;
        public int personsUsed;
        public boolean nextPage;
    }

    private Slide2Data toSlide2Data(RequestPDFData request) {
        return new Slide2Data();
    }

    private PDPageContentStream newSlide (
            PDPageContentStream lastCs,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException{
        if (lastCs != null) {
            lastCs.close();
        }

        PDPage page = initPage(defaultPDFComponent);
        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        initSlidePage(defaultPDFComponent, cs, 2, "Data Korban dan Pelaku");

        return cs;
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide2Data slide2Data = toSlide2Data(request);

        float pageHeight = defaultPDFComponent.getCustomSize().getHeight();

        PDPageContentStream cs = newSlide(null, defaultPDFComponent);
        drawInfoBox(cs, pageHeight, defaultPDFComponent);

        Person person = new Person("Test Name", "1233", 23, "Tesstt", "Yuhuuu", 5, "32", "Merah", List.of("Test (12 Desember 2021)", "Test 2 ", "Test (12 Desember 2021)"));

        SectionResult result = drawPictureSection(cs, 110, pageHeight - 310, "Data Saksi", defaultPDFComponent, List.of(person, person, person, person, person, person), 0);
        cs = result.cs;

        float y = result.nextPage ? pageHeight - 280 : 630;
        result = drawPictureSection(cs, 110, y, "Data Pengawas", defaultPDFComponent, List.of(person, person), result.personsUsed);
        cs = result.cs;

        y = result.nextPage ? pageHeight - 250 : 630;
        if (result.nextPage) {
            cs = newSlide(cs, defaultPDFComponent);
        }
        drawFieldFacts(y, cs, defaultPDFComponent, List.of("Test Line 1", "Test Line 2", "Test Line 3", "Test Line 4"));
        cs.close();
    }

    private void drawInfoBox (
            PDPageContentStream cs,
            float pageHeight,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {

        float x = 110;
        float y = (pageHeight - 290);
        float boxWidth = 2100;
        float boxHeight = 70;

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x, y, boxWidth, boxHeight))
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .strokeLine(5)
                                .strokeColor(new Color(0, 108, 235))
                                .fillColor(new Color(235, 243, 253))
                                .rounded(10)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x+50, y+15, boxWidth, boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Catatan")
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.BLACK)
                                .fontSize(18)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x+50, y-15,
                                boxWidth, boxHeight))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text("Tidak ada korban jiwa atau luka dalam insiden ini. Data di bawah menunjukan personel yang terlibat langsung dalam kejadian")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(18)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );
    }

    private SectionResult drawPictureSection (
            PDPageContentStream cs,
            float x, float y,
            String sectionName,
            DefaultPDFComponent defaultPDFComponent,
            List<Person> persons,
            int usedOnPage
    ) throws IOException {

        final int MAX_PER_PAGE = 4;
        boolean firstPage = true;

        int index = 0;

        float defaultYDeduction = 80;
        float Ydeduction = defaultYDeduction;
        float deductionForResetPage = 10;

        while (index < persons.size()) {
            int remainingPageCapacity = MAX_PER_PAGE - usedOnPage;
            int left = persons.size() - index;
            int countThisPage = Math.min(left, remainingPageCapacity);

            if (countThisPage == 0) {
                cs = newSlide(cs, defaultPDFComponent);
                y = 1070;
                usedOnPage = 0;
                continue;
            }

            List<List<String>> emptyRows = emptyRowsFor(countThisPage);

            List<PDFTableBuilder.TableColumn> columns =
                    List.of(new PDFTableBuilder.TableColumn(
                            sectionName, 2100, TextAlignment.LEFT, TextAlignment.LEFT));

            PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                    .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                            .bodyFontSize(20)
                            .bodyTextColor(Color.black)
                            .evenRowBgColor(Color.WHITE)
                            .oddRowBgColor(Color.WHITE)
                            .rowHeight(410)
                            .font(defaultPDFComponent.getFontRegular())
                            .fontBold(defaultPDFComponent.getFontBold())
                            .build())
                    .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                            .headerFontSize(30)
                            .headerBgColor(new Color(24, 94, 57))
                            .headerTextColor(Color.WHITE)
                            .headerHeight(65)
                            .font(defaultPDFComponent.getFontRegular())
                            .build())
                    .cornerRadius(20)
                    .lineColor(Color.GRAY)
                    .borderWidth(3)
                    .padding(15)
                    .showHeader(firstPage)
                    .build().gridLine(3, true, true, false, false);

            firstPage = false;

            pdfTableBuilder.drawTable(
                    PDFTableBuilder.PDFTableParam.builder()
                            .cs(cs)
                            .tablePosition(new PDFTableBuilder.TablePosition(x, y))
                            .columns(columns)
                            .rows(emptyRows)
                            .tableStyle(style)
                            .build());

            y -= Ydeduction;
            if (Ydeduction == deductionForResetPage) {
                Ydeduction = defaultYDeduction;
            }

            for (int k = 0; k < countThisPage; k++) {

                Person p = persons.get(index + k);
                float offsetX = ((k % 2) == 0) ? 70 : 1070;
                float xPoint = x + offsetX;

                drawPersonBox(cs, xPoint, y, p, defaultPDFComponent);

                if (k % 2 == 1) {
                    y -= 413;
                }
            }

            index += countThisPage;
            usedOnPage += countThisPage;

            if (index < persons.size()) {
                cs = newSlide(cs, defaultPDFComponent);
                y = 1070;
                firstPage = false;
                usedOnPage = 0;
                Ydeduction = deductionForResetPage;
            }
        }

        if (usedOnPage % 2 == 1){
            usedOnPage ++;
        }

        return new SectionResult(cs, usedOnPage, usedOnPage == MAX_PER_PAGE);
    }

    private List<List<String>> emptyRowsFor(int count) {
        int rows = (int) Math.ceil(count / 2.0);
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            result.add(List.of(""));
        }
        return result;
    }

    private void drawPersonBox (
        PDPageContentStream cs,
        float x, float y, Person person,
        DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        List<PDFTableBuilder.TableColumn> columns = List.of(new PDFTableBuilder.TableColumn("Data Saksi", 960, TextAlignment.LEFT, TextAlignment.LEFT));
        List<List<String>> rows = List.of(List.of(""));

        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .rowHeight(380)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(30)
                        .headerBgColor(new Color(24, 94, 57))
                        .headerTextColor(Color.WHITE)
                        .headerHeight(65)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .cornerRadius(0)
                .showHeader(false)
                .padding(15)
                .build();

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(x, y))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());

        columns = List.of(new PDFTableBuilder.TableColumn("", 200, TextAlignment.LEFT, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("", 440, TextAlignment.LEFT, TextAlignment.LEFT));

        rows = List.of(
                List.of("Nama", String.format("**%s**", person.name)),
                List.of("SID", String.format("**%s**", person.sidNumber)),
                List.of("Umur", String.format("**%s**", person.age)),
                List.of("Jabatan", String.format("**%s**", person.position)),
                List.of("Perusahaan", String.format("**%s**", person.company)),
                List.of("Pengalaman", String.format("**%s Tahun**", person.workExperience)),
                List.of("MCU Berlaku", String.format("**%s**", person.mcuValidUntil)),
                List.of("Profiling", String.format("**%s**", person.profiling)),
                List.of("Kompetensi", ""));

        style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder().font(defaultPDFComponent.getFontRegular()).build())
                .showHeader(false)
                .padding(3)
                .build();

        cs.drawImage(defaultPDFComponent.pictureProfile, x + 30, y - 180, 150, 150);

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(x + 220, y - 10))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());

        columns = List.of(new PDFTableBuilder.TableColumn("", 10, TextAlignment.LEFT, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("", 430, TextAlignment.LEFT, TextAlignment.LEFT));

        rows = person.competence.stream().map(
                competence -> List.of("**•**", String.format("**%s**", competence))).toList();

        style.setPadding(3);
        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(x + 420, y - 267))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());
    }

    private void drawFieldFacts (
            float y,
            PDPageContentStream cs,
            DefaultPDFComponent defaultPDFComponent,
            List<String> fieldFacts
    ) throws IOException {
        List<PDFTableBuilder.TableColumn> columns = List.of(new PDFTableBuilder.TableColumn("Fakta Lapangan", 2100, TextAlignment.LEFT, TextAlignment.LEFT));
        List<List<String>> rows = List.of(List.of(""));

        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .rowHeight(410)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(30)
                        .headerBgColor(new Color(24, 94, 57))
                        .headerTextColor(Color.WHITE)
                        .headerHeight(65)
                        .font(defaultPDFComponent.getFontRegular())
                        .build())
                .cornerRadius(20)
                .lineColor(Color.GRAY)
                .borderWidth(2)
                .padding(15)
                .build();

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(110, y))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());

        style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder().font(defaultPDFComponent.getFontRegular()).build())
                .showHeader(false)
                .build();

        if (!fieldFacts.isEmpty()) {
            columns = List.of(new PDFTableBuilder.TableColumn("", 20, TextAlignment.LEFT, TextAlignment.LEFT),
                    new PDFTableBuilder.TableColumn("", 2000, TextAlignment.LEFT, TextAlignment.LEFT));

            rows = fieldFacts.stream().map(
                    fact -> List.of("**•**", String.format("**%s**", fact))).toList();

            pdfTableBuilder.drawTable(
                    PDFTableBuilder.PDFTableParam.builder()
                            .cs(cs)
                            .tablePosition(new PDFTableBuilder.TablePosition(150, y - 90))
                            .columns(columns)
                            .rows(rows)
                            .tableStyle(style)
                            .build());
        }
    }

}

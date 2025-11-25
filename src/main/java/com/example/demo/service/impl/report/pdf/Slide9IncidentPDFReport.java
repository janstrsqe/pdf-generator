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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class Slide9IncidentPDFReport extends IncidentPDFPage {

    public Slide9IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
        this.slideNumber = 9;
        this.slideName = "Tindakan Perbaikan";
    }

    @AllArgsConstructor
    private static class Slide9Data {
    }

    private Slide9Data toSlide9Data(RequestPDFData request) {
        return new Slide9Data();
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide9Data slide9Data = toSlide9Data(request);
        float pageHeight = defaultPDFComponent.getCustomSize().getHeight();
        PDPageContentStream cs = newSlide(null, defaultPDFComponent);
        List<List<String>> rows = List.of(
                List.of("1", "1", "1", "1", "Tidakan Segera"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tidakan Segera"),
                List.of("1", "1", "1", "1", "Tindakan Jangka Panjang (3-6 Bulan)"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tindakan Jangka Pendek (0-3 Bulan)"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tidakan Segera"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tindakan Jangka Panjang (3-6 Bulan)"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tindakan Jangka Pendek (0-3 Bulan)"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tindakan Jangka Panjang (3-6 Bulan)"),
                List.of("1", "1", "1", "1", ""),
                List.of("1", "1", "1", "1", "Tindakan Jangka Pendek (0-3 Bulan)")
        );

        drawPagedTable(cs, pageHeight, defaultPDFComponent, rows);

        cs.close();
    }

    public void drawPagedTable (
            PDPageContentStream cs,
            float pageHeight,
            DefaultPDFComponent defaultPDFComponent,
            List<List<String>> rows
    ) throws IOException {

        final int MAX_ROWS_PER_PAGE = 10;
        List<Page> pages = chunkRows(rows, MAX_ROWS_PER_PAGE);

        for (int i = 0; i < pages.size(); i++) {
            Page pageRows = pages.get(i);

            drawTable(cs, pageHeight, defaultPDFComponent, pageRows.rows, pageRows.lastColumns);

            if (i < pages.size() - 1) {
                cs = newSlide(cs, defaultPDFComponent);
            }
        }


        cs.close();
    }

    public void drawTable(
            PDPageContentStream cs, float pageHeight,
            DefaultPDFComponent defaultPDFComponent,
            List<List<String>> rows,
            List<String> statuses
    ) throws IOException {

        List<PDFTableBuilder.TableColumn> columns = List.of(
            new PDFTableBuilder.TableColumn("No", 100, TextAlignment.LEFT, TextAlignment.LEFT),
            new PDFTableBuilder.TableColumn("Keterangan", 1040, TextAlignment.LEFT, TextAlignment.LEFT),
            new PDFTableBuilder.TableColumn("FA", 320, TextAlignment.LEFT, TextAlignment.LEFT),
            new PDFTableBuilder.TableColumn("Tanggal", 320, TextAlignment.LEFT, TextAlignment.LEFT),
            new PDFTableBuilder.TableColumn("Status", 320, TextAlignment.LEFT, TextAlignment.LEFT)
        );

        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(20)
                        .bodyTextColor(Color.black)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .rowHeight(80)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(25)
                        .headerTextColor(Color.BLACK)
                        .headerBgColor(Color.WHITE)
                        .headerHeight(65)
                        .font(defaultPDFComponent.getFontBold())
                        .build())
                .cornerRadius(10)
                .padding(25)
                .borderWidth((float) 0.5)
                .lineColor(Color.GRAY)
                .build().gridLine(1, true, true, false, false);

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(110, pageHeight - 250))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());

        float x = 1915;
        float y = pageHeight - 370;
        for (String status : statuses) {
            if (!status.isBlank()){
                Color text = new Color (217, 43, 43);
                Color fill = new Color (255, 229, 229);
                float boxSize = 120;

                if (status.equals("Tindakan Jangka Pendek (0-3 Bulan)")){
                    text =  new Color (228, 90, 24);
                    fill = new Color (255, 254, 222);
                    boxSize = 250;
                } else if (status.equals("Tindakan Jangka Panjang (3-6 Bulan)")){
                    text =  new Color (21, 147, 103);
                    fill = new Color (212, 255, 240);
                    boxSize = 250;
                }

                pdfBoxBuilder.drawBox(
                        PDFBoxBuilder.PDFBoxDrawingParam.builder()
                                .cs(cs)
                                .boxPosition(new PDFBoxBuilder.BoxPosition(x, y, boxSize, 30))
                                .boxText(PDFBoxBuilder.BoxText.builder()
                                        .text(status)
                                        .font(defaultPDFComponent.getFontBold())
                                        .fontColor(text)
                                        .fontSize(12)
                                        .align(TextAlignment.CENTER)
                                        .build())
                                .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                        .strokeLine(2)
                                        .strokeColor(text)
                                        .fillColor(fill)
                                        .padding(5)
                                        .rounded(5)
                                        .build())
                                .build());
                }
            y -= 80;
        }
    }

    private List<Page> chunkRows(List<List<String>> rows, int maxPerPage) {

        List<Page> pages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i += maxPerPage) {
            int end = Math.min(i + maxPerPage, rows.size());

            List<List<String>> chunk = new ArrayList<>();
            List<String> lastColumns = new ArrayList<>();

            // Process each row in this chunk
            for (List<String> row : rows.subList(i, end)) {

                List<String> mutableRow = new ArrayList<>(row);

                if (!mutableRow.isEmpty()) {
                    String last = mutableRow.get(mutableRow.size() - 1);
                    lastColumns.add(last);
                    mutableRow.set(mutableRow.size() - 1, "");
                }

                chunk.add(mutableRow);
            }

            Page page = new Page(chunk, lastColumns);
            pages.add(page);
        }

        return pages;
    }

    @AllArgsConstructor
    private static class Page {
        public List<List<String>> rows;
        public List<String> lastColumns;
    }

}

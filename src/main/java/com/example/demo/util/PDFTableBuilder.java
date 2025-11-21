package com.example.demo.util;

import com.example.demo.constants.TextAlignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFTableBuilder {

    @Data
    @Builder
    public static class PDFTableParam {
        protected PDPageContentStream cs;
        private TablePosition tablePosition;
        private List<TableColumn> columns;
        private List<List<String>> rows;
        private TableStyle tableStyle;
    }

    @AllArgsConstructor
    public static class TablePosition {
        private float x;
        private float y;
    }

    @Data
    @AllArgsConstructor
    public static class TableColumn {
        private String header;
        private float width;
        private TextAlignment alignHeader;
        private TextAlignment alignBody;
    }

    @Data
    @Builder
    public static class HeaderStyle {
        private float headerFontSize;
        private Color headerBgColor;
        private Color headerTextColor;
        private float headerHeight;
        private PDFont font;
    }

    @Data
    @Builder
    public static class BodyStyle {
        private float bodyFontSize;
        private Color bodyTextColor;
        private float rowHeight;
        private Color evenRowBgColor;
        private Color oddRowBgColor;
        private PDFont font;
    }

    @Data
    @Builder
    public static class TableStyle {
        private HeaderStyle headerStyle;
        private BodyStyle bodyStyle;
        private float cornerRadius;
        private Color lineColor;
        private float borderWidth;
        private float gridLineWidth;
        private float padding;

        @Builder.Default
        private boolean showHeader = true;
    }

    public void drawTable(
            PDFTableParam param
    ) throws IOException {

        float padding = 5;
        float leading = param.tableStyle.headerStyle.headerFontSize * 1.3f;

        float tableWidth = 0;
        for (TableColumn col : param.columns)
            tableWidth += col.width;

        List<Float> rowHeights = new ArrayList<>();
        float countedRowHeight;
        for (List<String> row : param.rows) {
            if (param.tableStyle.bodyStyle.getRowHeight() > 0) {
                countedRowHeight = param.tableStyle.bodyStyle.getRowHeight();
            } else {
                countedRowHeight = getRowHeight(param.columns, row, param.tableStyle.bodyStyle.font, param.tableStyle.bodyStyle.bodyFontSize, leading, padding);
            }
            rowHeights.add(countedRowHeight);
        }

        List<String> headerRow = getHeaderRow(param.columns);
        float headerHeight;
        if (param.tableStyle.getHeaderStyle().getHeaderHeight() > 0){
            headerHeight = param.tableStyle.getHeaderStyle().getHeaderHeight();
        } else {
            headerHeight = getRowHeight(param.columns, headerRow, param.tableStyle.headerStyle.font, param.tableStyle.headerStyle.headerFontSize, leading, padding);
        }

        // ---- 1. Compute total height with/without header ----
        float tableHeight = param.tableStyle.showHeader ? headerHeight : 0;
        for (float h : rowHeights) tableHeight += h;

        // ---- 2. Draw rounded border ----
        drawFullRoundedBorder(
                param.cs, param.tablePosition.x, param.tablePosition.y - tableHeight, tableWidth, tableHeight,
                param.tableStyle.cornerRadius, param.tableStyle.lineColor, param.tableStyle.borderWidth
        );

        float cursorY = param.tablePosition.y;

        // ---- 3. Draw header (if enabled) ----
        if (param.tableStyle.showHeader) {

            drawRoundedHeaderFill(
                    param.cs, param.tablePosition.x, param.tablePosition.y - headerHeight, tableWidth, headerHeight,
                    param.tableStyle.cornerRadius, param.tableStyle.headerStyle.headerBgColor
            );

            float headerCenterY = param.tablePosition.y - (headerHeight / 2f) - (param.tableStyle.headerStyle.headerFontSize * 0.35f);

            float cursorX = param.tablePosition.x;

            for (int i = 0; i < param.columns.size(); i++) {

                String text = headerRow.get(i);
                TableColumn col = param.columns.get(i);

                float colWidth = col.width;
                float textWidth = param.tableStyle.headerStyle.font.getStringWidth(text) / 1000 * param.tableStyle.headerStyle.headerFontSize;

                float textX = cursorX + padding;

                textX = switch (col.alignHeader) {
                    case CENTER -> cursorX + (colWidth - textWidth) / 2f;
                    case RIGHT -> cursorX + colWidth - textWidth - padding;
                    default -> textX;
                };

                param.cs.beginText();
                param.cs.setFont(param.tableStyle.headerStyle.font, param.tableStyle.headerStyle.headerFontSize);
                param.cs.setNonStrokingColor(param.tableStyle.headerStyle.headerTextColor);
                param.cs.newLineAtOffset(textX, headerCenterY);
                param.cs.showText(text);
                param.cs.endText();

                cursorX += colWidth;
            }

            cursorY = param.tablePosition.y - headerHeight; // move below header
        }

        // ---- 4. Draw rows + optional grid ----
        for (int r = 0; r < param.rows.size(); r++) {
            float rh = rowHeights.get(r);
            float rowTop = cursorY;
            float rowBottom = cursorY - rh;

            // Background
            param.cs.setNonStrokingColor((r % 2 == 0) ? param.tableStyle.bodyStyle.evenRowBgColor : param.tableStyle.bodyStyle.oddRowBgColor);
            param.cs.addRect(param.tablePosition.x, rowBottom, tableWidth, rh);
            param.cs.fill();

            // Text
            drawRowContent(param.cs, param.tablePosition.x, rowTop, param.columns, param.rows.get(r), rh, param.tableStyle.bodyStyle.font, param.tableStyle.bodyStyle.bodyFontSize, param.tableStyle.bodyStyle.bodyTextColor, leading, padding);

            // ---- Draw internal grid if enabled ----
            if (param.tableStyle.gridLineWidth > 0) {
                param.cs.setStrokingColor(param.tableStyle.lineColor);
                param.cs.setLineWidth(param.tableStyle.gridLineWidth);

                // Horizontal line
                param.cs.moveTo(param.tablePosition.x, rowBottom);
                param.cs.lineTo(param.tablePosition.x + tableWidth, rowBottom);
                param.cs.stroke();

                // Vertical lines
                float cx = param.tablePosition.x;
                for (TableColumn col : param.columns) {
                    cx += col.width;
                    param.cs.moveTo(cx, rowBottom);
                    param.cs.lineTo(cx, rowTop);
                    param.cs.stroke();
                }
            }

            cursorY -= rh;
        }
    }

    private void drawFullRoundedBorder(
            PDPageContentStream cs,
            float x, float y,
            float width, float height,
            float radius,
            Color borderColor,
            float borderWidth
    ) throws IOException {

        float right = x + width;
        float top = y + height;

        cs.setLineWidth(borderWidth);
        cs.setStrokingColor(borderColor);

        cs.moveTo(x + radius, y);
        cs.lineTo(right - radius, y);
        cs.curveTo(right, y, right, y, right, y + radius);

        cs.lineTo(right, top - radius);
        cs.curveTo(right, top, right, top, right - radius, top);

        cs.lineTo(x + radius, top);
        cs.curveTo(x, top, x, top, x, top - radius);

        cs.lineTo(x, y + radius);
        cs.curveTo(x, y, x, y, x + radius, y);

        cs.closePath();
        cs.stroke();
    }
    private void drawRoundedHeaderFill(
            PDPageContentStream cs,
            float x, float y,
            float width, float height,
            float radius,
            Color bgColor
    ) throws IOException {

        float right = x + width;
        float top = y + height;

        cs.setNonStrokingColor(bgColor);

        cs.moveTo(x, y);
        cs.lineTo(x, top - radius);
        cs.curveTo(x, top, x, top, x + radius, top);

        cs.lineTo(right - radius, top);
        cs.curveTo(right, top, right, top, right, top - radius);

        cs.lineTo(right, y);
        cs.closePath();
        cs.fill();
    }
    private List<String> getHeaderRow(List<TableColumn> columns) {
        List<String> header = new ArrayList<>();
        for (TableColumn col : columns) header.add(col.header);
        return header;
    }

    private float getRowHeight(
            List<TableColumn> columns,
            List<String> row,
            PDFont font,
            float fontSize,
            float leading,
            float padding
    ) throws IOException {

        float max = 0;

        for (int i = 0; i < columns.size(); i++) {
            List<String> wrapped = wrapText(row.get(i), font, fontSize, columns.get(i).width - padding * 2);
            float h = wrapped.size() * leading + padding * 2;
            if (h > max) max = h;
        }
        return max;
    }

    private List<String> wrapText(
            String text,
            PDFont font,
            float fontSize,
            float maxWidth
    ) throws IOException {

        if (text == null) return List.of("");

        String[] paragraphs = text
                .replace("\r", "")
                .split("\n");

        List<String> lines = new ArrayList<>();

        for (String para : paragraphs) {

            String p = para.trim();

            if (p.isEmpty()) {
                lines.add("");  // add blank line
                continue;
            }

            String[] words = p.split(" ");
            StringBuilder current = new StringBuilder();

            for (String w : words) {
                if (w.isEmpty()) continue;

                String test = current + w + " ";
                float width = font.getStringWidth(test) / 1000 * fontSize;

                if (width > maxWidth && current.length() > 0) {
                    lines.add(current.toString().trim());
                    current = new StringBuilder(w + " ");
                } else {
                    current.append(w).append(" ");
                }
            }

            if (!current.isEmpty()) {
                lines.add(current.toString().trim());
            }
        }

        return lines;
    }

    private void drawRowContent(
            PDPageContentStream cs,
            float x,
            float topY,
            List<TableColumn> columns,
            List<String> row,
            float rowHeight,
            PDFont font,
            float fontSize,
            Color textColor,
            float leading,
            float padding
    ) throws IOException {

        float cursorX = x;

        for (int i = 0; i < columns.size(); i++) {

            float colWidth = columns.get(i).width;
            float textAreaWidth = colWidth - padding * 2;

            List<String> lines = wrapText(
                    row.get(i),
                    font,
                    fontSize,
                    textAreaWidth
            );

            float textY = topY - padding - fontSize;

            for (String line : lines) {

                float textWidth = font.getStringWidth(line) / 1000 * fontSize;

                float textX = switch (columns.get(i).alignBody) {
                    case CENTER -> cursorX + (colWidth - textWidth) / 2f;
                    case RIGHT -> cursorX + colWidth - textWidth - padding;
                    default -> cursorX + padding;
                };

                cs.beginText();
                cs.setFont(font, fontSize);
                cs.setNonStrokingColor(textColor);
                cs.newLineAtOffset(textX, textY);
                cs.showText(line);
                cs.endText();

                textY -= leading;
            }

            cursorX += colWidth;
        }
    }

}

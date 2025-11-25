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
        private PDFont fontBold;
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

        @Builder.Default
        private float padding = 5;

        @Builder.Default
        private boolean showHeader = true;

        private List<Color> rowColors;

        @Builder.Default
        private boolean gridTop = true;

        @Builder.Default
        private boolean gridBottom = true;

        @Builder.Default
        private boolean gridLeft = true;

        @Builder.Default
        private boolean gridRight = true;

        public TableStyle gridLine(float gridLineWidth, boolean gridTop, boolean gridBottom, boolean gridLeft, boolean gridRight){
            this.gridLineWidth = gridLineWidth;
            this.gridTop = gridTop;
            this.gridBottom = gridBottom;
            this.gridLeft = gridLeft;
            this.gridRight = gridRight;
            return this;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TextSegment {
        private String text;
        private PDFont font;
        private float fontSize;
    }

    public void drawTable(PDFTableParam param) throws IOException {

        float padding = param.tableStyle.padding;
        float leading = param.tableStyle.bodyStyle.bodyFontSize * 1.3f;

        if (param.tableStyle.headerStyle.headerFontSize > 0) {
            leading = param.tableStyle.headerStyle.headerFontSize * 1.3f;
        }

        float tableWidth = 0;
        for (TableColumn col : param.columns)
            tableWidth += col.width;

        List<Float> rowHeights = new ArrayList<>();
        for (List<String> row : param.rows) {
            float rh = param.tableStyle.bodyStyle.getRowHeight() > 0
                    ? param.tableStyle.bodyStyle.getRowHeight()
                    : getRowHeight(param.columns, row,
                    param.tableStyle.bodyStyle.font,
                    param.tableStyle.bodyStyle.fontBold,
                    param.tableStyle.bodyStyle.bodyFontSize,
                    leading, padding);

            rowHeights.add(rh);
        }

        List<String> headerRow = getHeaderRow(param.columns);
        float headerHeight = param.tableStyle.headerStyle.getHeaderHeight() > 0
                ? param.tableStyle.headerStyle.getHeaderHeight()
                : getRowHeight(param.columns, headerRow,
                param.tableStyle.headerStyle.font,
                param.tableStyle.headerStyle.font,
                param.tableStyle.headerStyle.headerFontSize,
                leading, padding);

        float tableHeight = param.tableStyle.showHeader ? headerHeight : 0;
        for (float h : rowHeights) tableHeight += h;

        param.cs.saveGraphicsState();
        addRoundedClip(param.cs,
                param.tablePosition.x,
                param.tablePosition.y - tableHeight,
                tableWidth,
                tableHeight,
                param.tableStyle.cornerRadius);

        float cursorY = param.tablePosition.y;

        if (param.tableStyle.showHeader) {

            drawRoundedHeaderFill(
                    param.cs,
                    param.tablePosition.x,
                    param.tablePosition.y - headerHeight,
                    tableWidth,
                    headerHeight,
                    param.tableStyle.cornerRadius,
                    param.tableStyle.headerStyle.headerBgColor
            );

            float headerCenterY = param.tablePosition.y
                    - (headerHeight / 2f)
                    - (param.tableStyle.headerStyle.headerFontSize * 0.35f);

            float cursorX = param.tablePosition.x;
            for (int i = 0; i < param.columns.size(); i++) {
                String text = headerRow.get(i);
                TableColumn col = param.columns.get(i);

                float colWidth = col.width;
                float textWidth = param.tableStyle.headerStyle.font.getStringWidth(text)
                        / 1000 * param.tableStyle.headerStyle.headerFontSize;

                float textX = switch (col.alignHeader) {
                    case CENTER -> cursorX + (colWidth - textWidth) / 2f;
                    case RIGHT -> cursorX + colWidth - textWidth - padding;
                    default -> cursorX + padding;
                };

                param.cs.beginText();
                param.cs.setFont(param.tableStyle.headerStyle.font,
                        param.tableStyle.headerStyle.headerFontSize);
                param.cs.setNonStrokingColor(param.tableStyle.headerStyle.headerTextColor);
                param.cs.newLineAtOffset(textX, headerCenterY);
                param.cs.showText(text);
                param.cs.endText();

                cursorX += colWidth;
            }

            if (param.tableStyle.showHeader) {
                cursorY = param.tablePosition.y - headerHeight;
            } else {
                cursorY = param.tablePosition.y;
            }
        }

        for (int r = 0; r < param.rows.size(); r++) {
            float rh = rowHeights.get(r);
            float rowTop = cursorY;
            float rowBottom = cursorY - rh;

            Color bgColor;
            List<Color> customColors = param.tableStyle.rowColors;
            if (customColors != null && r < customColors.size() && customColors.get(r) != null) {
                bgColor = customColors.get(r);
            } else {
                bgColor = (r % 2 == 0)
                        ? param.tableStyle.bodyStyle.evenRowBgColor
                        : param.tableStyle.bodyStyle.oddRowBgColor;
            }

            param.cs.setNonStrokingColor(bgColor);
            param.cs.addRect(param.tablePosition.x, rowBottom, tableWidth, rh);
            param.cs.fill();

            drawRowContent(param.cs,
                    param.tablePosition.x,
                    rowTop,
                    param.columns,
                    param.rows.get(r),
                    rh,
                    param.tableStyle.bodyStyle.font,
                    param.tableStyle.bodyStyle.fontBold,
                    param.tableStyle.bodyStyle.bodyFontSize,
                    param.tableStyle.bodyStyle.bodyTextColor,
                    leading,
                    padding);

            cursorY -= rh;
        }

        param.cs.restoreGraphicsState();

        if (param.tableStyle.gridLineWidth > 0) {
            TableStyle ts = param.tableStyle;

            if (ts.lineColor != null) {
                param.cs.setStrokingColor(ts.lineColor);
                param.cs.setLineWidth(ts.gridLineWidth);
            }

            float x = param.tablePosition.x;
            float yTop = param.tablePosition.y;
            float yBottom = param.tablePosition.y - tableHeight;
            float right = x + tableWidth;

            if (ts.gridTop && ts.showHeader) {
                float headerBottom = param.tablePosition.y - headerHeight;
                param.cs.moveTo(x, headerBottom);
                param.cs.lineTo(right, headerBottom);
                param.cs.stroke();
            }

            float cy = param.tableStyle.showHeader
                    ? yTop - headerHeight
                    : yTop;
            for (float rh : rowHeights) {
                float rowBottom = cy - rh;

                if (ts.gridBottom && rowBottom > yBottom) {
                    param.cs.moveTo(x, rowBottom);
                    param.cs.lineTo(right, rowBottom);
                    param.cs.stroke();
                }

                cy -= rh;
            }

            if (ts.gridLeft && ts.borderWidth <= 0) {
                param.cs.moveTo(x, yTop);
                param.cs.lineTo(x, yBottom);
                param.cs.stroke();
            }

            if (ts.gridRight && ts.borderWidth <= 0) {
                param.cs.moveTo(right, yTop);
                param.cs.lineTo(right, yBottom);
                param.cs.stroke();
            }
        }

        drawFullRoundedBorder(
                param.cs,
                param.tablePosition.x,
                param.tablePosition.y - tableHeight,
                tableWidth,
                tableHeight,
                param.tableStyle.cornerRadius,
                param.tableStyle.lineColor,
                param.tableStyle.borderWidth
        );
    }


    private void drawFullRoundedBorder(
            PDPageContentStream cs,
            float x, float y,
            float width, float height,
            float radius,
            Color borderColor,
            float borderWidth
    ) throws IOException {

        if (borderWidth <= 0 || borderColor == null) {
            return;
        }

        x += borderWidth / 2f;
        y += borderWidth / 2f;
        width -= borderWidth;
        height -= borderWidth;

        float right = x + width;
        float top = y + height;

        float c = 0.552284749831f * radius;

        cs.setStrokingColor(borderColor);
        cs.setLineWidth(borderWidth);
        cs.setLineCapStyle(1);

        cs.moveTo(x + radius, y);
        cs.lineTo(right - radius, y);

        cs.curveTo(
                right - radius + c, y,
                right, y + radius - c,
                right, y + radius
        );

        cs.lineTo(right, top - radius);

        cs.curveTo(
                right, top - radius + c,
                right - radius + c, top,
                right - radius, top
        );

        cs.lineTo(x + radius, top);

        cs.curveTo(
                x + radius - c, top,
                x, top - radius + c,
                x, top - radius
        );

        cs.lineTo(x, y + radius);

        cs.curveTo(
                x, y + radius - c,
                x + radius - c, y,
                x + radius, y
        );

        cs.stroke();
    }

    private void addRoundedClip(PDPageContentStream cs,
                                float x, float y,
                                float w, float h, float r) throws IOException {

        float c = 0.552284749831f * r;

        cs.moveTo(x + r, y);
        cs.lineTo(x + w - r, y);

        cs.curveTo(
                x + w - r + c, y,
                x + w, y + r - c,
                x + w, y + r
        );

        cs.lineTo(x + w, y + h - r);

        cs.curveTo(
                x + w, y + h - r + c,
                x + w - r + c, y + h,
                x + w - r, y + h
        );

        cs.lineTo(x + r, y + h);

        cs.curveTo(
                x + r - c, y + h,
                x, y + h - r + c,
                x, y + h - r
        );

        cs.lineTo(x, y + r);

        cs.curveTo(
                x, y + r - c,
                x + r - c, y,
                x + r, y
        );

        cs.closePath();
        cs.clip();
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

        if (bgColor != null){
            cs.setNonStrokingColor(bgColor);
        }

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
            PDFont normalFont,
            PDFont boldFont,
            float fontSize,
            float leading,
            float padding
    ) throws IOException {

        float max = 0;

        for (int i = 0; i < columns.size(); i++) {
            float textAreaWidth = columns.get(i).width - padding * 2;

            List<List<TextSegment>> wrapped = wrapTextSegmented(
                    row.get(i),
                    normalFont,
                    boldFont,
                    fontSize,
                    textAreaWidth
            );

            float h = wrapped.size() * leading + padding * 2;

            if (h > max) max = h;
        }

        return max;
    }

    private void drawRowContent(
            PDPageContentStream cs,
            float x,
            float topY,
            List<TableColumn> columns,
            List<String> row,
            float rowHeight,
            PDFont font,
            PDFont boldFont,
            float fontSize,
            Color textColor,
            float leading,
            float padding
    ) throws IOException {

        float cursorX = x;

        for (int i = 0; i < columns.size(); i++) {

            float colWidth = columns.get(i).width;
            float textAreaWidth = colWidth - padding * 2;

            List<List<TextSegment>> wrappedLines = wrapTextSegmented(
                    row.get(i),
                    font,
                    boldFont,
                    fontSize,
                    textAreaWidth
            );

            float textY = topY - padding - fontSize;

            for (List<TextSegment> line : wrappedLines) {

                float lineWidth = 0;
                for (TextSegment seg : line) {
                    lineWidth += seg.getFont().getStringWidth(seg.getText())
                            / 1000 * seg.getFontSize();
                }

                float textX = switch (columns.get(i).alignBody) {
                    case CENTER -> cursorX + (colWidth - lineWidth) / 2f;
                    case RIGHT -> cursorX + colWidth - lineWidth - padding;
                    default -> cursorX + padding;
                };

                cs.beginText();
                cs.newLineAtOffset(textX, textY);
                cs.setNonStrokingColor(textColor);

                for (TextSegment seg : line) {
                    cs.setFont(seg.getFont(), seg.getFontSize());
                    cs.showText(seg.getText());
                }

                cs.endText();

                textY -= leading;
            }

            cursorX += colWidth;
        }
    }

    private List<TextSegment> parseMarkdownSegments(
            String text,
            PDFont normalFont,
            PDFont boldFont,
            float fontSize
    ) {
        List<TextSegment> segments = new ArrayList<>();

        StringBuilder buffer = new StringBuilder();
        boolean bold = false;

        for (int i = 0; i < text.length(); i++) {

            if (text.startsWith("**", i)) {

                if (!buffer.isEmpty()) {
                    segments.add(new TextSegment(
                            buffer.toString(),
                            bold ? boldFont : normalFont,
                            fontSize
                    ));
                    buffer.setLength(0);
                }

                bold = !bold;
                i++;
                continue;
            }

            buffer.append(text.charAt(i));
        }

        if (!buffer.isEmpty()) {
            segments.add(new TextSegment(
                    buffer.toString(),
                    bold ? boldFont : normalFont,
                    fontSize
            ));
        }

        // Safety: never return empty list
        if (segments.isEmpty()) {
            segments.add(new TextSegment(text, normalFont, fontSize));
        }

        return segments;
    }

    private List<List<TextSegment>> wrapTextSegmented(
            String text,
            PDFont normalFont,
            PDFont boldFont,
            float fontSize,
            float maxWidth
    ) throws IOException {

        List<List<TextSegment>> resultLines = new ArrayList<>();

        String[] paragraphs = text.replace("\r", "").split("\n");

        for (String para : paragraphs) {

            List<TextSegment> segments = parseMarkdownSegments(
                    para,
                    normalFont,
                    boldFont,
                    fontSize
            );

            List<TextSegment> currentLine = new ArrayList<>();
            float currentWidth = 0;

            for (TextSegment seg : segments) {

                List<String> tokens = tokenizePreserveSpaces(seg.getText());

                for (String token : tokens) {

                    float tokenWidth = seg.getFont().getStringWidth(token)
                            / 1000f * seg.getFontSize();

                    if (currentWidth + tokenWidth > maxWidth && !currentLine.isEmpty()) {
                        resultLines.add(new ArrayList<>(currentLine));
                        currentLine.clear();
                        currentWidth = 0;
                    }

                    currentLine.add(new TextSegment(
                            token,
                            seg.getFont(),
                            seg.getFontSize()
                    ));

                    currentWidth += tokenWidth;
                }
            }

            if (!currentLine.isEmpty()) {
                resultLines.add(currentLine);
            }
        }

        return resultLines;
    }

    private List<String> tokenizePreserveSpaces(String text) {
        List<String> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (char c : text.toCharArray()) {

            if (c == ' ') {
                if (!buffer.isEmpty()) {
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                }
                tokens.add(" ");
            } else {
                buffer.append(c);
            }
        }

        if (!buffer.isEmpty()) {
            tokens.add(buffer.toString());
        }

        return tokens;
    }

}

package com.example.demo.util;

import com.example.demo.constants.TextAlignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PDFBoxBuilder {

    @Builder
    @AllArgsConstructor
    public static class PDFBoxDrawingParam {
        protected PDPageContentStream cs;
        protected BoxPosition boxPosition;
        protected BoxStyle boxStyle;
        protected BoxText boxText;
    }

    @AllArgsConstructor
    public static class BoxPosition {
        protected float boxX;
        protected float boxY;
        protected float boxWidth;
        protected float boxHeight;
    }

    @Builder
    @AllArgsConstructor
    public static class BoxStyle {
        protected Color strokeColor;
        protected Color fillColor;
        protected float rounded;
        protected float strokeLine;
        protected float padding;
    }

    @Builder
    @AllArgsConstructor
    public static class BoxText {
        protected String text;
        protected float fontSize;
        protected PDFont font;
        protected Color fontColor;
        protected TextAlignment align;
    }

    public void drawBox(PDFBoxDrawingParam param, Color sliceColor) throws IOException {
        drawBox(param);

        param.boxPosition.boxX = param.boxPosition.boxX - 10;
        param.boxPosition.boxWidth = 10;
        param.boxStyle.strokeLine = 2;
        param.boxStyle.fillColor = sliceColor;

        drawBox(param);
    }

    public void drawBox(PDFBoxDrawingParam param) throws IOException {

        // Default style if none
        param.boxStyle = param.boxStyle != null
                ? param.boxStyle
                : new BoxStyle(null, null, 0, 0, 20);

        float padding = (param.boxText != null && param.boxStyle.padding > 0)
                ? param.boxStyle.padding
                : 20;

        // -------------------------
        // 1. Draw box (stroke + fill)
        // -------------------------
        if (param.boxStyle.strokeColor != null) {
            param.cs.setStrokingColor(param.boxStyle.strokeColor);
            param.cs.setLineWidth(param.boxStyle.strokeLine);

            if (param.boxStyle.rounded > 0) {
                drawRoundedBox(param);
            } else {
                param.cs.addRect(
                        param.boxPosition.boxX,
                        param.boxPosition.boxY,
                        param.boxPosition.boxWidth,
                        param.boxPosition.boxHeight
                );
            }

            param.cs.stroke();

            if (param.boxStyle.fillColor != null) {
                param.cs.setNonStrokingColor(param.boxStyle.fillColor);

                if (param.boxStyle.rounded > 0) {
                    drawRoundedBox(param);
                } else {
                    param.cs.addRect(
                            param.boxPosition.boxX,
                            param.boxPosition.boxY,
                            param.boxPosition.boxWidth,
                            param.boxPosition.boxHeight
                    );
                }

                param.cs.fill();
            }
        }

        // -------------------------
        // 2. Draw Text
        // -------------------------
        if (param.boxText != null) {

            List<String> lines = new ArrayList<>();
            float maxWidth = param.boxPosition.boxWidth - (padding * 2);
            float lineHeight = param.boxText.fontSize * 1.3f;

            // Split paragraphs on newline
            String[] rawLines = param.boxText.text
                    .replace("\r\n", "\n")
                    .replace('\r', '\n')
                    .split("\n");

            for (String raw : rawLines) {

                if (raw.isEmpty()) {
                    lines.add("");
                    continue;
                }

                String[] words = raw.split(" ");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {
                    String temp = currentLine + word + " ";
                    float width = param.boxText.font.getStringWidth(temp) / 1000 * param.boxText.fontSize;

                    if (width > maxWidth && !currentLine.isEmpty()) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word + " ");
                    } else {
                        currentLine.append(word).append(" ");
                    }
                }

                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
            }

            if (lines.isEmpty()) return;

            // Limit lines by box height
            int maxLines = (int) (param.boxPosition.boxHeight / lineHeight);
            if (lines.size() > maxLines) {
                lines = lines.subList(0, maxLines);
            }

            // Start writing
            param.cs.beginText();
            param.cs.setFont(param.boxText.font, param.boxText.fontSize);
            param.cs.setNonStrokingColor(param.boxText.fontColor);

            float cursorY;

            if (lines.size() == 1) {
                cursorY =
                        param.boxPosition.boxY
                                + (param.boxPosition.boxHeight / 2f)
                                - (param.boxText.fontSize / 3f);
            } else {
                cursorY =
                        param.boxPosition.boxY
                                + param.boxPosition.boxHeight
                                - param.boxText.fontSize * 1.2f
                                - padding;
            }

            for (String line : lines) {
                float lineWidth = param.boxText.font.getStringWidth(line) / 1000 * param.boxText.fontSize;

                float textX = switch (param.boxText.align) {
                    case LEFT -> param.boxPosition.boxX + padding;
                    case RIGHT -> param.boxPosition.boxX + param.boxPosition.boxWidth - lineWidth - padding;
                    default -> param.boxPosition.boxX + (param.boxPosition.boxWidth - lineWidth) / 2f;
                };

                param.cs.newLineAtOffset(textX, cursorY);
                param.cs.showText(line);
                param.cs.newLineAtOffset(-textX, -cursorY);

                cursorY -= lineHeight;
            }

            param.cs.endText();
        }
    }

    public void drawRoundedBox(
            PDFBoxDrawingParam param
    ) throws IOException {
        PDPageContentStream cs = param.cs;
        float x = param.boxPosition.boxX;
        float y = param.boxPosition.boxY;
        float w = param.boxPosition.boxWidth;
        float h = param.boxPosition.boxHeight;
        float r = param.boxStyle.rounded;

        if (param.boxStyle.strokeColor != null) {
            cs.setStrokingColor(param.boxStyle.strokeColor);
        }

        if (param.boxStyle.fillColor != null) {
            cs.setNonStrokingColor(param.boxStyle.fillColor);
        }

        float c = 0.552284749831f * r;

        cs.moveTo(x + r, y);
        cs.lineTo(x + w - r, y);

        cs.curveTo(x + w - r + c, y, x + w, y + r - c, x + w, y + r);
        cs.lineTo(x + w, y + h - r);

        cs.curveTo(x + w, y + h - r + c, x + w - r + c, y + h, x + w - r, y + h);
        cs.lineTo(x + r, y + h);

        cs.curveTo(x + r - c, y + h, x, y + h - r + c, x, y + h - r);
        cs.lineTo(x, y + r);

        cs.curveTo(x, y + r - c, x + r - c, y, x + r, y);

        cs.closePath();
    }

}

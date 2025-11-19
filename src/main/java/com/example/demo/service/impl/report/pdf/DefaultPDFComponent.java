package com.example.demo.service.impl.report.pdf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@AllArgsConstructor
@Getter
public class DefaultPDFComponent {
    private PDDocument document;
    private PDRectangle customSize;
    private PDFont fontRegular;
    private PDFont fontBold;

    PDImageXObject coverBackground;
    PDImageXObject slideBackground;
}

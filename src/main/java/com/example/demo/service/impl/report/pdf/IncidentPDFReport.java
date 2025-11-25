package com.example.demo.service.impl.report.pdf;

import com.example.demo.dto.in.RequestPDFData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IncidentPDFReport {

    private final CoverPageIncidentPDFReport coverPageIncidentPDFReport;
    private final Slide1IncidentPDFReport slide1IncidentPDFReport;
    private final Slide2IncidentPDFReport slide2IncidentPDFReport;
    private final Slide3IncidentPDFReport slide3IncidentPDFReport;
    private final Slide4IncidentPDFReport slide4IncidentPDFReport;
    private final Slide5IncidentPDFReport slide5IncidentPDFReport;
    private final Slide7IncidentPDFReport slide7IncidentPDFReport;
    private final Slide9IncidentPDFReport slide9IncidentPDFReport;

    public void generateIncidentReportPDF(RequestPDFData requestPDFData) {
        try (PDDocument document = new PDDocument()) {

            // === Load Fonts ===
            PDFont fontRegular = PDType0Font.load(
                    document,
                    getClass().getResourceAsStream("/assets/Montserrat-Regular.ttf")
            );

            PDFont fontBold = PDType0Font.load(
                    document,
                    getClass().getResourceAsStream("/assets/Montserrat-Bold.ttf")
            );

            PDImageXObject slideBackground = PDImageXObject.createFromFile(
                    new File("src/main/resources/assets/image-2.jpg").getAbsolutePath(),
                    document
            );

            PDImageXObject coverBackground = PDImageXObject.createFromFile(
                    new File("src/main/resources/assets/image.jpg").getAbsolutePath(),
                    document
            );

            PDImageXObject profilePicture = PDImageXObject.createFromFile(
                    new File("src/main/resources/assets/profile.png").getAbsolutePath(),
                    document
            );

            Map<String, PDImageXObject> icons = getIcon("src/main/resources/assets/icon",document);

            DefaultPDFComponent defaultPDFComponent = new DefaultPDFComponent(
                    document, new PDRectangle(2304, 1302),
                    fontRegular, fontBold, coverBackground, slideBackground, profilePicture, icons);

            coverPageIncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide1IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide2IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
//            slide3IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide4IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide5IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide7IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);
            slide9IncidentPDFReport.generatePage(requestPDFData, defaultPDFComponent);

            document.save("incident-report.pdf");

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private Map<String, PDImageXObject> getIcon(String directory, PDDocument document) throws IOException {
        Path dir = Paths.get(directory);
        Map<String, PDImageXObject> icons = new HashMap<>();
        try (var stream = Files.list(dir)) {
            stream
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        PDImageXObject temp = PDImageXObject.createFromFile(
                                new File(String.format("%s/%s", directory, path.getFileName())).getAbsolutePath(),
                                document
                        );
                        icons.put(FilenameUtils.getBaseName(path.getFileName().toString()), temp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }

        return icons;
    }

}

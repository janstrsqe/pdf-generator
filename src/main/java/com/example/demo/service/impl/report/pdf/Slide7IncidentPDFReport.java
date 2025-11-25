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

import java.util.LinkedList;
import java.util.List;
import java.awt.*;
import java.io.IOException;

@Component
public class Slide7IncidentPDFReport extends IncidentPDFPage {

    private List<String> layer1 = List.of(
            "HIRA", "SOP (Policy, Procedure, IK, Std & form)", "Do and Don’ts Policy",
            "26 High Risk Activity", "Golden Rules", "Personal Permit (ID/SIMPER/KIMPER)", "Management Review",
            "Regulation Compliance", "Resource", "Organizational Structure & Leadership",
            "Pengelolaan Kontraktor (CHSEMS)", "Izin Operasi", "Rencana K3L", "Training Kompetensi",
            "Management of Change", "Audit (internal dan eksternal)", "Compliance Assessment",
            "Recruitment (incl. psikososial)", "Commissioning", "MCU", "Hubungan Industrial", "Social & Community Development");

    private List<String> layer2 = List.of(
            "JSA", "Rencana kerja (weekly-up plan) / Plan maintenance system",
            "Emergency Preparedness", "Safety Accountability Program (SAP)",
            "Design/General Arrangement", "Standarisasi Tools", "Tools Inspection",
            "Lingkungan Kerja", "Maintenance", "HSE Campaign",
            "Pembelian dan Penanganan Material", "Incident Investigation & Reporting", "Safety Dashboard & Evaluation",
            "", "", "", "", "", "", "", "", "");

    private List<String> layer3 = List.of(
            "P5M / Safety Briefing", "DOP",
            "P2H (incl emergency equipment)", "Rencana kerja harian / Daily Maintenance",
            "Pengecekan before after loading", "Kondisi area kerja", "Last minute check",
            "Pengawasan pekerjaan oleh pengawas", "Safety patrol", "Pelaksanaan pekerjaan sesuai SOP",
            "Fit to work (mental & physical)", "Fatigue Test", "Izin kerja khusus",
            "Pemenuhan rambu/safety sign/IMO sign", "Drugs / alcohol influence", "Security check & patrol",
            "", "", "", "", "", "");

    private List<String> layer4 = List.of(
            "Fatigue alarm", "LOTO",
            "Geotech RADAR / RADAR marine", "In-cabin Camera",
            "Speed Awareness Monitoring", "GPS (posisi dan kecepatan)", "CCTV",
            "Echosounder", "Sensor/alarm", "Wind Indicator",
            "", "", "", "", "", "", "", "", "", "", "", "");

    private List<String> layer5 = List.of(
            "ADP", "Guarding/cover benda berputar dan titik jepit",
            "Tanggul", "ROPS", "Emergency Response", "Safety Devices", "Control System",
            "Fender", "Jangkar", "", "", "", "", "", "", "", "", "", "", "", "", "");

    public Slide7IncidentPDFReport(PDFBoxBuilder pdfBoxBuilder, PDFTableBuilder pdfTableBuilder) {
        super(pdfBoxBuilder, pdfTableBuilder);
    }

    @AllArgsConstructor
    private static class Slide7Data {
    }

    private Slide7Data toSlide7Data(RequestPDFData request) {
        return new Slide7Data();
    }

    public void generatePage(
            RequestPDFData request,
            DefaultPDFComponent defaultPDFComponent
    ) throws IOException {
        Slide7Data slide7Data = toSlide7Data(request);
        PDPage page = initPage(defaultPDFComponent);

        PDRectangle rect = page.getMediaBox();
        float pageHeight = rect.getHeight();

        PDPageContentStream cs = new PDPageContentStream(defaultPDFComponent.getDocument(), page);
        initSlidePage(defaultPDFComponent, cs, 7, "Analisa Layer of Protection (SBS)");

        drawInfoBox(cs, pageHeight, defaultPDFComponent);

        float x = 110;
        float y = pageHeight - 380;
        float addition = 424;

        drawLayer(cs, x, y, pageHeight, defaultPDFComponent, layer1, "Layer 1", "Organization Roles & Responsibility");
        x += addition;
        drawLayer(cs, x, y, pageHeight, defaultPDFComponent, layer2, "Layer 2", "Plan Readiness");
        x += addition;
        drawLayer(cs, x, y, pageHeight, defaultPDFComponent, layer3, "Layer 3", "Work Readiness and Monitoring");
        x += addition;
        drawLayer(cs, x, y, pageHeight, defaultPDFComponent, layer4, "Layer 4", "Preventive Defense");
        x += addition;
        drawLayer(cs, x, y, pageHeight, defaultPDFComponent, layer5, "Layer 5", "Contact Defense");

        cs.close();
    }

    private void drawLayer (
            PDPageContentStream cs,
            float x, float y, float pageHeight,
            DefaultPDFComponent defaultPDFComponent,
            List<String> layers,
            String text1, String text2
    ) throws IOException {

        pdfBoxBuilder.drawBox(
                PDFBoxBuilder.PDFBoxDrawingParam.builder()
                        .cs(cs)
                        .boxPosition(new PDFBoxBuilder.BoxPosition(
                                x, pageHeight - 380, 400, 60))
                        .boxText(PDFBoxBuilder.BoxText.builder()
                                .text(String.format("%s\n%s", text1, text2))
                                .font(defaultPDFComponent.getFontBold())
                                .fontColor(Color.WHITE)
                                .fontSize(18)
                                .align(TextAlignment.CENTER)
                                .build())
                        .boxStyle(PDFBoxBuilder.BoxStyle.builder()
                                .fillColor(new Color(24, 94, 57))
                                .strokeColor(new Color(24, 94, 57))
                                .strokeLine(1)
                                .rounded(10)
                                .padding(5)
                                .build())
                        .build()
        );

        List<Color> colors = new LinkedList<>();
        final int[] i = {0};
        List<List<String>> rows = layers.stream().filter(
                layer -> !layer.isEmpty()).map(layer -> {
            if (i[0] == 3){
                colors.add(Color.RED);
            } else if (i[0] > 1 && i[0] < 7) {
                colors.add(Color.YELLOW);
            } else if (i[0] == 9){
                colors.add(Color.GREEN);
            } else {
                colors.add(Color.WHITE);
            }
            i[0]++;
            return List.of(String.format("%s", i[0]),layer);
        }).toList();

        List<PDFTableBuilder.TableColumn> columns = List.of(
                new PDFTableBuilder.TableColumn("No", 45, TextAlignment.LEFT, TextAlignment.LEFT),
                new PDFTableBuilder.TableColumn("Activity", 355, TextAlignment.LEFT, TextAlignment.LEFT)
        );

        PDFTableBuilder.TableStyle style = PDFTableBuilder.TableStyle.builder()
                .bodyStyle(PDFTableBuilder.BodyStyle.builder()
                        .bodyFontSize(18)
                        .bodyTextColor(Color.BLACK)
                        .evenRowBgColor(Color.WHITE)
                        .oddRowBgColor(Color.WHITE)
                        .font(defaultPDFComponent.getFontRegular())
                        .fontBold(defaultPDFComponent.getFontBold())
                        .build())
                .headerStyle(PDFTableBuilder.HeaderStyle.builder()
                        .headerFontSize(18)
                        .headerTextColor(Color.BLACK)
                        .headerBgColor(Color.WHITE)
                        .headerHeight(50)
                        .font(defaultPDFComponent.getFontBold())
                        .build())
                .cornerRadius(10)
                .lineColor(Color.GRAY)
                .borderWidth((float) 0.5)
                .padding(4)
                .rowColors(colors)
                .build().gridLine(1, true, true, false, false);

        pdfTableBuilder.drawTable(
                PDFTableBuilder.PDFTableParam.builder()
                        .cs(cs)
                        .tablePosition(new PDFTableBuilder.TablePosition(x, y - 30))
                        .columns(columns)
                        .rows(rows)
                        .tableStyle(style)
                        .build());
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
                                .strokeColor(new Color(252, 241, 148))
                                .fillColor(new Color(254, 252, 234))
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
                                .text("Swiss Cheese Model Analysis")
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
                                .text("Analisa menunjukkan bahwa keempat layer of protection gagal berfungsi secara bersamaan, memungkinkan hazard berkembang menjadi insiden aktual.")
                                .font(defaultPDFComponent.getFontRegular())
                                .fontColor(Color.BLACK)
                                .fontSize(18)
                                .align(TextAlignment.LEFT)
                                .build())
                        .build()
        );
    }

}

package org.example.db_zlagoda.create_reports;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFCreator {
    public void createPDF(List<List<String>> data, String filePath) throws IOException {
        PDPageContentStream contentStream = null;

        try (PDDocument document = new PDDocument()) {
//            PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage page = new PDPage();
            document.addPage(page);

            contentStream = new PDPageContentStream(document, page);
            PDType0Font font = PDType0Font.load(document, new File("src/main/resources/fonts/Academy.ttf"));
            contentStream.setFont(font, 8);
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float cellMargin = 5f;

            List<String> headers = data.get(0);
            int cols = headers.size();
            float rowHeight = 20f;
            float tableRowHeight = 30f;
            float tableHeaderHeight = 40f;
            float tableWidthAdjustment = tableWidth / cols;
            contentStream.drawLine(margin, yPosition, margin + tableWidth, yPosition);
            yPosition -= tableHeaderHeight;

            for (List<String> row : data.subList(1, data.size())) {
                float maxRowHeight = 0;
                for (int col = 0; col < cols; col++) {
                    String text = row.get(col);
                    float textWidth = font.getStringWidth(text) / 1000 * 8;
                    float cellHeight = calculateCellHeight(text, font, tableWidthAdjustment - 2 * cellMargin);
                    maxRowHeight = Math.max(maxRowHeight, cellHeight);
                }

                if (yPosition - maxRowHeight < margin) {
                    contentStream.close();

                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    contentStream = new PDPageContentStream(document, newPage);
                    contentStream.moveTo(50, newPage.getMediaBox().getHeight() - 50);
                    contentStream.setFont(font, 8);
                    yPosition = newPage.getMediaBox().getHeight() - margin;

                    // Draw table header for the new page
                    contentStream.drawLine(margin, yPosition, margin + tableWidth, yPosition);
                    yPosition -= tableHeaderHeight;
                }

                contentStream.drawLine(margin, yPosition, margin + tableWidth, yPosition);
                float nexty = yPosition - tableRowHeight;
                for (int col = 0; col < cols; col++) {
                    String text = row.get(col);
                    float textWidth = font.getStringWidth(text) / 1000 * 8;
                    if (textWidth > tableWidthAdjustment - 2 * cellMargin) {
                        List<String> lines = breakTextIntoLines(text, font, tableWidthAdjustment - 2 * cellMargin);
                        float lineHeight = 8;
                        float linePosition = yPosition - 15;
                        for (String line : lines) {
                            contentStream.beginText();
                            contentStream.moveTextPositionByAmount(margin + cellMargin + col * tableWidthAdjustment, linePosition);
                            contentStream.showText(line);
                            contentStream.endText();
                            linePosition -= lineHeight;
                        }
                    } else {
                        contentStream.beginText();
                        contentStream.moveTextPositionByAmount(margin + cellMargin + col * tableWidthAdjustment, yPosition - 15);
                        contentStream.showText(text);
                        contentStream.endText();
                    }
                }
                yPosition = nexty;
                yPosition -= maxRowHeight / 2;
            }

            contentStream.drawLine(margin, yPosition, margin + tableWidth, yPosition);
        } finally {
            if (contentStream != null) {
                contentStream.close();
            }
        }
    }


    private float calculateCellHeight(String text, PDFont font, float maxWidth) throws IOException {
        List<String> lines = breakTextIntoLines(text, font, maxWidth);
        return lines.size() * 8;
    }


    private List<String> breakTextIntoLines(String text, PDFont font, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        float width = 0;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            float charWidth = font.getStringWidth(String.valueOf(character)) / 1000 * 8;
            if (width + charWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                width = 0;
            }
            currentLine.append(character);
            width += charWidth;
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }
}

package br.ufca.sgco.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

import java.io.File;
import java.io.IOException;

public class PdfService {
    public static void salvarPDF(String conteudo, String filename) {
        String baseDir = "Documentação SGCO/Emitidos";
        File dir = new File(baseDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        String filepath = baseDir + "/" + filename;
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);

                String[] lines = conteudo.split("\n");
                for (String line : lines) {
                    // limpeza básica para evitar travamentos com caracteres não suportados.
                    String safeLine = line.replace("é", "e").replace("ã", "a").replace("í", "i")
                                          .replace("ó", "o").replace("ç", "c").replace("á", "a")
                                          .replace("ú", "u").replace("õ", "o").replace("â", "a")
                                          .replace("ê", "e").replace("ô", "o");
                    safeLine = safeLine.replace("É", "E").replace("Ã", "A").replace("Í", "I")
                                          .replace("Ó", "O").replace("Ç", "C").replace("Á", "A")
                                          .replace("Ú", "U").replace("Õ", "O");

                    contentStream.showText(safeLine);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(filepath);
            System.out.println("Arquivo criado: " + filepath);
        } catch (IOException e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}

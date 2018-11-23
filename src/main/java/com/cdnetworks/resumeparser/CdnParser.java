package com.cdnetworks.resumeparser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;
import org.springframework.util.StringUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.awt.geom.Rectangle2D;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.ITextString;
import org.pdfclown.documents.contents.TextChar;
import org.pdfclown.documents.interaction.annotations.TextMarkup;
import org.pdfclown.documents.interaction.annotations.TextMarkup.MarkupTypeEnum;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.tools.TextExtractor;
import org.pdfclown.util.math.Interval;
import org.pdfclown.util.math.geom.Quad;

public class CdnParser {
    
    public ArrayList<Integer> parse(File file, ArrayList<String> searchTokens) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        String content = "";
        ArrayList<Integer> keyWordsCount = new ArrayList<Integer>();

        try (InputStream stream = new FileInputStream(file)) {
                parser.parse(stream, handler, metadata, new ParseContext());
                content = handler.toString();
                //System.out.println(content);
        }
        System.out.println("search tokens: " + searchTokens.toString());
        
        for(String token : searchTokens){
            int wordCount = StringUtils.countOccurrencesOf(content.toLowerCase(), token.toLowerCase());
            keyWordsCount.add(wordCount);
        }
        
        if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals("docx")) {
            Path path = Paths.get(file.getPath());
            byte[] byteData = Files.readAllBytes(path);
            try {
                XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(byteData));
                for (XWPFParagraph p : doc.getParagraphs()) {
                    for (String token : searchTokens) {
                    int runNumber = 0;
                        while (runNumber < p.getRuns().size()) {
                            XWPFRun r = p.getRuns().get(runNumber);
                            String runText = r.getText(0);
                            if (runText != null) {
                                if (runText.toLowerCase().contains(token)) {
                                    highlightToken(token, p, runNumber);
                                }
                            }
                            runNumber++;
                        }
                    }
                }
                
                File directory = new File(System.getProperty("user.dir") + "\\temp");
                
                doc.write(new FileOutputStream(directory + "\\" + file.getName()));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        
        if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals("pdf")) {
            HighlightPDF PDFhighlighter = new HighlightPDF();
            
            PDFhighlighter.highlight(file, searchTokens);
        }
        
        return keyWordsCount;
    }
    
    private int highlightToken(String token, XWPFParagraph p, int i) {
        XWPFRun currentRun = p.getRuns().get(i);
        String currentRunText = currentRun.text();
        int tokenLength = token.length();
        int tokenIndex = currentRunText.toLowerCase().indexOf(token);
        int addedRuns = 0;
        p.removeRun(i);
        
        if (tokenIndex > 0) {
            XWPFRun before = p.insertNewRun(i);
            before.setText(currentRunText.substring(0, tokenIndex));
            
            addedRuns++;
        }
        
        XWPFRun sentenceRun = p.insertNewRun(i + addedRuns);
        sentenceRun.setText(currentRunText.substring(tokenIndex, tokenIndex + tokenLength));
        CTShd cTShd = sentenceRun.getCTR().addNewRPr().addNewShd();
        cTShd.setFill("FFFA00");
        
        if(tokenIndex + tokenLength != currentRunText.length()) {
            XWPFRun after = p.insertNewRun( i + addedRuns + 1);
            after.setText(currentRunText.substring(tokenIndex + tokenLength));
            addedRuns ++;
        }
        return addedRuns;
    }
}

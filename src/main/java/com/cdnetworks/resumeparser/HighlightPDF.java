package com.cdnetworks.resumeparser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;

public class HighlightPDF extends PDFTextStripper {
    static List<double[]> coordinates;
    static ArrayList tokenStream;
    static ArrayList<String> searchTokens;

    public HighlightPDF() throws IOException {
        //data structed containing coordinates information for each token
        coordinates = new ArrayList<>();

        //List of words extracted from text (considering a whitespace-based tokenization)
        tokenStream = new ArrayList();

        searchTokens = new ArrayList<>();
    }

    public void highlight(File file, ArrayList<String> query) throws IOException {

        try {   
            PDDocument document = PDDocument.load(file);
            //extended PDFTextStripper class
            PDFTextStripper stripper = new HighlightPDF();
            searchTokens = query;
            //Get number of pages
            int number_of_pages = document.getDocumentCatalog().getPages().getCount();

            //The method writeText will invoke an override version of writeString
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);

            //Print collected information
            //System.out.println(tokenStream);
            //System.out.println(tokenStream.size());
            //System.out.println(coordinates.size());

            double page_height;
            double page_width;
            double width, height, minx, maxx, miny, maxy;
            int rotation;

            //scan each page and highlitht all the words inside them
            for (int page_index = 0; page_index < number_of_pages; page_index++) {   
                //get current page
                PDPage page = document.getPage(page_index);

                //Get annotations for the selected page
                List<PDAnnotation> annotations = page.getAnnotations();

                //Define a color to use for highlighting text
                PDColor yellow = new PDColor(new float[] { 200, 250, 0 }, PDDeviceRGB.INSTANCE);

                //Page height and width
                page_height = page.getMediaBox().getHeight();
                page_width  = page.getMediaBox().getWidth();

                //Scan collected coordinates
                for (int i=0; i<coordinates.size(); i++) {
                    //if the current coordinates are not related to the current
                    //page, ignore them
                    if ((int) coordinates.get(i)[4] != (page_index+1))
                        continue;
                    else
                    {
                        //get rotation of the page...portrait..landscape..
                        rotation = (int) coordinates.get(i)[7];

                         //page rotated of 90degrees
                        if (rotation == 90) {
                            height = coordinates.get(i)[5];
                            width = coordinates.get(i)[6];
                            width = (page_height * width)/page_width;

                            //define coordinates of a rectangle
                            maxx = coordinates.get(i)[1];
                            minx = coordinates.get(i)[1] - height;
                            miny = coordinates.get(i)[0];
                            maxy = coordinates.get(i)[0] + width;
                        } else //i should add here the cases -90/-180 degrees
                        {
                           height = coordinates.get(i)[5];
                           minx = coordinates.get(i)[0];
                           maxx = coordinates.get(i)[2];
                           miny = page_height - coordinates.get(i)[1];
                           maxy = page_height - coordinates.get(i)[3] + height;
                        }

                        //Add an annotation for each scanned word
                        PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
                        txtMark.setColor(yellow);
                        txtMark.setConstantOpacity((float)0.3); // 30% transparent
                        PDRectangle position = new PDRectangle();
                        position.setLowerLeftX((float) minx);
                        position.setLowerLeftY((float) miny);
                        position.setUpperRightX((float) maxx);
                        position.setUpperRightY((float) ((float) maxy+height));
                        txtMark.setRectangle(position);

                        float[] quads = new float[8];
                        quads[0] = position.getLowerLeftX();  // x1
                        quads[1] = position.getUpperRightY()-2; // y1
                        quads[2] = position.getUpperRightX(); // x2
                        quads[3] = quads[1]; // y2
                        quads[4] = quads[0];  // x3
                        quads[5] = position.getLowerLeftY()-2; // y3
                        quads[6] = quads[2]; // x4
                        quads[7] = quads[5]; // y5
                        txtMark.setQuadPoints(quads);
                        txtMark.setContents(tokenStream.get(i).toString());
                        annotations.add(txtMark);
                    }    
                }
            }

            //Saving the document in a new file
            File directory = new File(System.getProperty("user.dir") + "\\temp");
            File highlighted_doc = new File(directory + "\\" + file.getName());
            document.save(highlighted_doc);

            document.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException { 
        String tmpStr = "";
        int counter = 1;
        double minx = 0,maxx = 0,miny = 0,maxy =0; 
        double height = 0;
        double width = 0;
        int rotation = 0;

        for(String token : searchTokens) {
            if(string.toLowerCase().contains(token.toLowerCase())) {
                int tokenIndex = string.toLowerCase().indexOf(token.toLowerCase());
                for(int idx = tokenIndex; idx < (tokenIndex + token.length()); idx++) {
                    TextPosition text = textPositions.get(idx);
                    rotation = text.getRotation();

                    if (text.getHeight() > height)
                        height = text.getHeight(); 

                    if (text.getWidth() > width)
                        width = text.getWidth();

                    //if it is the first char of the current word
                    if (counter == 1) {
                        minx = text.getX();
                        miny = text.getY();
                    }

                    //if it is the last char of the current word
                    if (counter == token.length()) {
                        maxx = text.getEndX();
                        maxy = text.getY();
                    }

                    tmpStr += text;
                    counter += 1;
                }
            }
        }
        if(!tmpStr.isEmpty()) {
            tokenStream.add(tmpStr);
            double word_coordinates [] = {minx,miny,maxx,maxy,this.getCurrentPageNo(), height, width, rotation};
            coordinates.add(word_coordinates);
        }
    }
}
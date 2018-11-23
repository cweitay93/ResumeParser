package com.cdnetworks.resumeparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserController implements Runnable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ParserController.class);
    
    //Parser
    private static CdnParser cdnParser = null;
    
    private int percent;
    private String output;
    
    private String keywords;
    private String resumeFolder;
    private String outputFolder;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
    
    public ParserController(String keywords, String resumeFolder, String outputFolder) {
        this.keywords = keywords;
        this.resumeFolder = resumeFolder;
        this.outputFolder = outputFolder;
    }
    
    public void run() {
        try {
            Queue<String> postFixQuery = new LinkedList<>();
            ArrayList<String> searchTokens = new ArrayList<String>();
            ArrayList<List<String>> resumeList = new ArrayList<List<String>>();
            ArrayList<Resume> resumeObjects = new ArrayList<Resume>();
            resumeList = findFiles(resumeFolder);
            
            File directory = new File(System.getProperty("user.dir") + "/temp");
            if (! directory.exists()) {
                directory.mkdir();
            } else {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
            }
            
            cdnParser = new CdnParser();
            int count = 0;
            
            
            postFixQuery = processQuery(keywords);
            
            for(String token : postFixQuery){
                if(!token.contains("AND") && !token.contains("OR") && !token.contains("NOT")){
                    searchTokens.add(token);
                }
            }
            
            for (List<String> resume : resumeList){
                File tmpFile = new File(resume.get(1));
                Resume resumeObj = new Resume(resume.get(0), cdnParser.parse(tmpFile, searchTokens), tmpFile.getAbsolutePath());
                System.out.println(resumeObj.getLoc());
                resumeObjects.add(resumeObj);
                
                ++count;
                percent = (int) ((count * 100.0f) / resumeList.size());
                setOutput("Processed " + count + " out of " + resumeList.size() + " resumes.");
                
            }
            
            for(Resume resume : resumeObjects){
                Stack<Integer> resultStack = new Stack<>();
                
                for(String token : postFixQuery){
                    // score evaluated at each stage
                    int result = 0;
                    if(!token.equals("AND") && !token.equals("OR") && !token.equals("NOT")){
                        int index = searchTokens.indexOf(token);
                        result = (int) resume.getKeyWordsCount().get(index);
                    }
                    
                    // else if AND operator
                    else if(token.equals("AND")){
                        int rightOperand = resultStack.pop();
                        int leftOperand = resultStack.pop();
                        result = booleanAnd(leftOperand, rightOperand);
                    }
                    
                    // else if OR operator
                    else if(token.equals("OR")){
                        int rightOperand = resultStack.pop();
                        int leftOperand = resultStack.pop();
                        result = booleanOr(leftOperand, rightOperand);
                    }
                    
                    // else if NOT operator
                    else if(token.equals("NOT")){
                        int rightOperand = resultStack.pop();
                        result = booleanNot(rightOperand);
                    }
                    
                    // push evaluated result back to stack
                    resultStack.push(result);
                }
                
                if(resultStack.size() != 1){
                    System.out.println("ERROR: Result Stack. Please check valid query. Capacity: " + resultStack.size());
                }
                resume.setTotalScore(resultStack.pop());
                System.out.println("Resume Name: " + resume.getResumeName());
                System.out.println("Resume Score: " + resume.getTotalScore());
                
            }
            
            Collections.sort(resumeObjects);
            File checkFile = new File(writeToFile(resumeObjects, keywords, searchTokens, outputFolder));
            if(checkFile.exists()) {
                LOGGER.info("Parsing Statistics:");
                LOGGER.info("\tProcessed Resumes: {}", resumeObjects.size());
            }
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
    }
    
    private ArrayList<List<String>> findFiles(String folderURL) throws Exception {
        ArrayList<List<String>> resumeList = new ArrayList<List<String>>();
        
        File folder = new File(folderURL);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if(!file.getName().startsWith("~$")){
                    ArrayList<String> resume = new ArrayList<>();
                    resume.add(file.getName());
                    resume.add(file.getAbsolutePath());
                    resumeList.add(resume);
                }
            } else if (file.isDirectory()) {
                //System.out.println("Directory " + file.getName());
                ArrayList<List<String>> tmpList = new ArrayList<List<String>>();
                tmpList = findFiles(file.getAbsolutePath());
                for (List<String> fileName : tmpList) {
                    resumeList.add(fileName);
                }
            }
        }
        return resumeList;
    }
    
    private Queue<String> processQuery(String query) {
        // declare data structs
        Boolean isPhrase = false;
        String phrase = "";
        String[] tmpQuery;
        ArrayList<String> cleanQuery = new ArrayList<String>();
        
        // prepare query
        query = query.replaceAll("[(]", "( ");
        query = query.replaceAll("[)]", " )");
        if(query.endsWith("\""))
            query = query.concat(" ");
        if(query.startsWith("\""))
            query = query.replaceFirst("\"", "\" ");
        query = query.replace("\" ", " \" ");
        query = query.replace(" \"", " \" ");
        
        tmpQuery = query.split(" ");
        
        // join phrases
        for (String token : tmpQuery) {
            if(!token.equals(" ") && !token.equals("")) {
                if(token.equals("\"") && !isPhrase){
                    isPhrase = true;
                } else if(isPhrase){
                    if(!token.equals("\"")){
                        phrase = phrase.concat(token + " ");
                    } else {
                        if(!phrase.equals(""))
                            cleanQuery.add(phrase.substring(0, phrase.length()-1));
                        isPhrase = false;
                        phrase = "";
                    }
                } else {
                    cleanQuery.add(token);
                }
            }
        }
        
        return shuntingYard(cleanQuery);
    }
    
    private Queue<String> shuntingYard(ArrayList<String> queryTokens){
        // declare data struct
        Stack<String> operatorStack = new Stack<>();
        Queue<String> outputList = new LinkedList<>();
        Map<String,Integer> precedence = new HashMap<String,Integer>();
        
        // define precedence
        precedence.put("NOT", 3);
        precedence.put("AND", 2);
        precedence.put("OR", 1);
        precedence.put("(", 0);
        precedence.put(")", 0);
        
        // traverse through all tokens and sort
        for(String token : queryTokens){
            // if left bracket, push onto operator stack
            if(token.equals("(")){
                operatorStack.push(token);
            }
            
            // if right bracket, pop all operators from operator stack onto output until we hit left bracket
            else if(token.equals(")")){
                String operator = operatorStack.pop();
                while(!operator.equals("(")){
                    outputList.add(operator);
                    operator = operatorStack.pop();
                }
            }
            
            // if operator, pop operators from operator stack to queue if they are of higher precedence
            else if(precedence.containsKey(token)){
                if(!operatorStack.isEmpty()){
                    String currentOperator = operatorStack.lastElement();
                    while(!operatorStack.isEmpty() && (precedence.get(currentOperator) > precedence.get(token))){
                        outputList.add(operatorStack.pop());
                        if(!operatorStack.isEmpty()){
                            currentOperator = operatorStack.lastElement();
                        }
                    }
                }
                operatorStack.push(token);
            }
            
            // else if operands, add to output list
            else {
                outputList.add(token.toLowerCase());
            }
        }
        
        // while there are still operators on the stack, pop them into the queue
        while(!operatorStack.isEmpty()){
            outputList.add(operatorStack.pop());
        }
        return outputList;
    }
    
    private int booleanAnd(int leftOperand, int rightOperand){
        return leftOperand * rightOperand;
    }
    
    private int booleanOr(int leftOperand, int rightOperand){
        return leftOperand + rightOperand;
    }
    
    private int booleanNot(int rightOperand){
        if(rightOperand > 0)
            return 0;
        return 1;
    }
    
    private String writeToFile(ArrayList<Resume> resumeObjects, String query, ArrayList<String> keywords, String outputLocation) throws FileNotFoundException, IOException{
        
        ArrayList<String> columns = new ArrayList<String>();
        columns.addAll(Arrays.asList("Ranking", "Resume", "Relevance Score"));
        keywords.forEach((token) -> {
            columns.add(token);
        });
        // Create a workbook
        XSSFWorkbook workBook = new XSSFWorkbook();
        
        CreationHelper createHelper = workBook.getCreationHelper();
        
        // Create a sheet
        Sheet sheet = workBook.createSheet("Resumes");
        
        // Create a font for header cells
        Font headerFont = workBook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 16);
        headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());
        
        // Create a fonr for h2 cells
        Font h2Font = workBook.createFont();
        h2Font.setBold(true);
        h2Font.setFontHeightInPoints((short) 12);
        
        // Create a CellStyle with header font
        CellStyle headerCellStyle = workBook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        
        // Create a CellStyle with h2 font
        CellStyle h2CellStyle = workBook.createCellStyle();
        h2CellStyle.setFont(h2Font);
        h2CellStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        h2CellStyle.setBorderBottom(BorderStyle.MEDIUM);
        h2CellStyle.setBorderTop(BorderStyle.MEDIUM);
        h2CellStyle.setBorderLeft(BorderStyle.MEDIUM);
        h2CellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        // Create a CellStyle for resume font
        CellStyle resumeCellStyle = workBook.createCellStyle();
        resumeCellStyle.setAlignment(HorizontalAlignment.LEFT);

        // Create a Header Row
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        
        // Create Header Cell
        headerCell.setCellValue("CDN Resume Search Result");
        headerCell.setCellStyle(headerCellStyle);
        
        // Create Query Row
        Row queryRow = sheet.createRow(1);
        Cell queryLabel = queryRow.createCell(0);
        queryLabel.setCellValue("Query:");
        
        // Create Query Cells
        Cell queryCell = queryRow.createCell(1);
        queryCell.setCellValue(query);
        
        // Create h2 Row
        Row h2Row = sheet.createRow(2);

        // Create h2 cells
        for(int i = 0; i < columns.size(); i++) {
            Cell h2Cell = h2Row.createCell(i);
            h2Cell.setCellValue(columns.get(i));
            h2Cell.setCellStyle(h2CellStyle);
        }
        
        // Create resume objects cells
        for(int i = 0; i < resumeObjects.size(); i++) {
            Resume resume = resumeObjects.get(i);
            File tmpResume = new File(resume.getLoc());
            int ranking = i+1;
            ArrayList<Integer> keyWordsCount = resume.getKeyWordsCount();
            
            // Create Resume object row
            Row resumeRow = sheet.createRow(3+i);
            
            // Create cells for respective headings
            Cell rankingCell = resumeRow.createCell(0);
            rankingCell.setCellValue(ranking);
            Cell nameCell = resumeRow.createCell(1);
            nameCell.setCellValue(resume.getResumeName());
            Hyperlink link = createHelper.createHyperlink(HyperlinkType.FILE);
            if((FilenameUtils.getExtension(tmpResume.getName())).toLowerCase().equals("docx") || 
                    (FilenameUtils.getExtension(tmpResume.getName())).toLowerCase().equals("pdf")) {
                File f = new File(System.getProperty("user.dir") + "\\temp\\" + tmpResume.getName());
                link.setAddress(f.toURI().toString());
            } else {
                link.setAddress(tmpResume.toURI().toString());
            }
            nameCell.setHyperlink(link);

            Cell scoreCell = resumeRow.createCell(2);
            scoreCell.setCellValue(resume.getTotalScore());
            scoreCell.setCellStyle(resumeCellStyle);
            
            for(int j = 0; j < keyWordsCount.size(); j++){
                Cell keyCountCell = resumeRow.createCell(j+3);
                keyCountCell.setCellValue(keyWordsCount.get(j));
                keyCountCell.setCellStyle(resumeCellStyle);
            }
        }
        
        for(int i = 1; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Output to file
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename;
        if(outputLocation.equals("")||outputLocation.equals(" ")){
            filename = outputLocation + "Search_Results_" + timestamp + ".xlsx";
        } else {
            filename = outputLocation + "\\Search_Results_" + timestamp + ".xlsx";
        }
        
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            workBook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        // Closing the workbook
        workBook.close();
        
        return filename;
    }
}

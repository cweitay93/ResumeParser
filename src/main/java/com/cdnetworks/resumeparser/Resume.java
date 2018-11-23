package com.cdnetworks.resumeparser;

import java.util.ArrayList;

public class Resume implements Comparable<Resume> {
    String resumeName;
    ArrayList<Integer> keyWordsCount;
    int totalScore;
    String loc;

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
    
    public Resume(String resumeName, ArrayList<Integer> keyWordsCount, String location) {
        this.resumeName = resumeName;
        this.keyWordsCount = keyWordsCount;
        this.totalScore = 0;
        this.loc = location;
    }

    public String getResumeName() {
        return resumeName;
    }

    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public ArrayList<Integer> getKeyWordsCount() {
        return keyWordsCount;
    }

    public void addKeyWordCount(int keyWordCount) {
        this.keyWordsCount.add(keyWordCount);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    
    @Override
    public int compareTo(Resume compareResume) {
        int compareScore = ((Resume) compareResume).getTotalScore();
        //ascending
        //return this.fileSize - compareBytes;
        
        //descending
        return compareScore - this.totalScore;
    }
    
}

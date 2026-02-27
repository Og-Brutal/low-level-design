package main.java.com.apm.dto;

public class IndexRow {

    private String chapterName;
    private int sentenceNumber;

    public IndexRow() {
    }

    public IndexRow(String chapterName, int sentenceNumber) {
        this.chapterName = chapterName;
        this.sentenceNumber = sentenceNumber;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public void setSentenceNumber(int sentenceNumber) {
        this.sentenceNumber = sentenceNumber;
    }

    @Override
    public String toString() {
        return "IndexRow{" +
                "chapterName='" + chapterName + '\'' +
                ", sentenceNumber=" + sentenceNumber +
                '}';
    }
}

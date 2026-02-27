package com.arabicprose.dto;

public class ChapterDTO {
    private int chapterId;
    private int bookId;
    private String chapterName;
    private int chapterOrder;
    private String description;

    public ChapterDTO() {
        this.chapterId = 0;
        this.bookId = 0;
        this.chapterName = "";
        this.chapterOrder = -1; // Change from 0 to -1
        this.description = "";
    }

    public ChapterDTO(int chapterId, int bookId, String chapterName, int chapterOrder, String description) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.chapterName = chapterName;
        this.chapterOrder = chapterOrder;
        this.description = description;
    }

    // Getters and Setters
    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getChapterName() { return chapterName; }
    public void setChapterName(String chapterName) { this.chapterName = chapterName; }

    public int getChapterOrder() { return chapterOrder; }
    public void setChapterOrder(int chapterOrder) { this.chapterOrder = chapterOrder; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        if (chapterName == null || chapterName.isEmpty()) {
            return "No Chapters Available";
        }
        return chapterName + (description != null && !description.isEmpty() ? " - " + description : "");
    }
}
package main.java.com.apm.dto;

public class ChapterDTO {
    private int chapterId;
    private int bookId;
    private String chapterName;

    // ✅ Default Constructor
    public ChapterDTO() {}

    // ✅ Parameterized Constructor
    public ChapterDTO(int chapterId, int bookId, String chapterName) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.chapterName = chapterName;
    }

    // ✅ Getters and Setters
    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    // ✅ Optional: toString() for easy debugging
    @Override
    public String toString() {
        return "ChapterDTO {" +
               "chapterId=" + chapterId +
               ", bookId=" + bookId +
               ", chapterName='" + chapterName + '\'' +
               '}';
    }
}

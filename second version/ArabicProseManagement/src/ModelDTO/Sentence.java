package ModelDTO;

public class Sentence {
	private int sentenceId;
    private int bookId;
    private String bookTitle;
    private int sentenceNumber;
    private String text;
    private String textDiacritized;
    private String translation;
    private String notes;
    

    // Constructors
    public Sentence() {}

    public Sentence(int sentenceId, int bookId, int sentenceNumber, String text) {
        this.sentenceId = sentenceId;
        this.bookId = bookId;
        this.sentenceNumber = sentenceNumber;
        this.text = text;
    }

    public Sentence(int bookId, int sentenceNumber, String text) {
        this.bookId = bookId;
        this.sentenceNumber = sentenceNumber;
        this.text = text;
    }

    // Getters and Setters
    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public void setSentenceNumber(int sentenceNumber) {
        this.sentenceNumber = sentenceNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextDiacritized() {
        return textDiacritized;
    }

    public void setTextDiacritized(String textDiacritized) {
        this.textDiacritized = textDiacritized;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    @Override
    public String toString() {
        return sentenceNumber + ": " + text;
    }
}

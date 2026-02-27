package main.java.com.apm.dto;

public class TokenDTO {

    private int tokenId;       // token_id (PK)
    private int sentenceId;    // sentence_id (FK)
    private int lemmaId;       // lemma_id (FK)
    private String token;      // token text

    // --- Constructors ---
    public TokenDTO() {}

    public TokenDTO(int tokenId, int sentenceId, int lemmaId, String token) {
        this.tokenId = tokenId;
        this.sentenceId = sentenceId;
        this.lemmaId = lemmaId;
        this.token = token;
    }

    // --- Getters and Setters ---
    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "TokenDTO{" +
                "tokenId=" + tokenId +
                ", sentenceId=" + sentenceId +
                ", lemmaId=" + lemmaId +
                ", token='" + token + '\'' +
                '}';
    }
}

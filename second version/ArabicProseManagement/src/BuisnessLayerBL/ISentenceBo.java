package BuisnessLayerBL;

public interface ISentenceBo {
  public boolean addSentence(Sentence sentence);
  public boolean updateSentences(Sentence sentence);
  public List<Sentence> getAllSentences();
  public Sentence getSentenceById(int sentenceId);
  public boolean deleteSentence(int SentenceId);
  public List<Sentence> searchSentences(String keyword);
}

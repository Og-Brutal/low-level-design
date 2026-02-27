package com.apm.dal;
import java.util.ArrayList;
import java.util.Set;

public interface INgramDAO {
    boolean saveNgrams(int sentenceId, Set<String> ngrams);
    ArrayList<Integer> getCandidateSentenceIds(Set<String> ngrams);
}
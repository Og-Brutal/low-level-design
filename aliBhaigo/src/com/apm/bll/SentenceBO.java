package com.apm.bll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import com.apm.bll.utils.AppLogger;
import com.apm.bll.utils.SimilarityAlgorithm;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;
import com.apm.dto.SimilarityResultDTO;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;

public class SentenceBO implements ISentenceBO, IObserveable {
    private IDataAccessLayerFasade daf;
    private ITokenBO tokenBO;
    private ArrayList<IObserver> observers;
    private static final Logger logger = AppLogger.getLogger(SentenceBO.class);

    public SentenceBO(IDataAccessLayerFasade daf, ITokenBO tokenBO, ArrayList<IObserver> observers) {
        this.daf = daf;
        this.tokenBO = tokenBO;
        this.observers = observers;
        logger.info("SentenceBO initialized.");
    }

    @Override
    public boolean createSentence(String chapterName, String text, String textDiacritized, String translation, String notes) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) {
            logger.warning("Chapter not found: " + chapterName);
            return false;
        }

        boolean check = daf.createSentence(chapterID, text, textDiacritized, translation, notes);

        if (check) {
            tokenBO.addToken(text);

            // Update N-gram Index
            int sentenceId = daf.searchSentence(text);
            if (sentenceId != -1) {
                Set<String> ngrams = SimilarityAlgorithm.getCharNGrams(text, 3);
                daf.saveNgrams(sentenceId, ngrams);
            }

            update();
            logger.info("Sentence created successfully in chapter: " + chapterName);
        } else {
            logger.warning("Failed to create sentence in chapter: " + chapterName);
        }

        return check;
    }

    @Override
    public ArrayList<SentenceDTO> retrieveSentence(String chapterName) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) {
            logger.warning("Chapter not found while retrieving sentences: " + chapterName);
            return null;
        }
        return daf.retrieveSentence(chapterID);
    }

    @Override
    public boolean updateSenetence(String chapterName, int sentenceNumber, String text, String textDiacritized, String translation, String notes) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) {
            logger.warning("Chapter not found while updating sentence: " + chapterName);
            return false;
        }

        SentenceDTO sentence = daf.sentenceByNumbers(chapterID, sentenceNumber);
        tokenBO.deleteTokensBySentence(sentence.getText());

        boolean check = daf.updateSenetence(chapterID, sentenceNumber, text, textDiacritized, translation, notes);
        if (check) {
            tokenBO.addToken(text);
            update();
            logger.info("Sentence updated successfully: " + text);
        } else {
            logger.warning("Failed to update sentence: " + text);
        }
        return check;
    }

    @Override
    public boolean deleteSentence(String chapterName, int sentenceNumber) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) {
            logger.warning("Chapter not found while deleting sentence: " + chapterName);
            return false;
        }

        boolean check = daf.deleteSentence(chapterID, sentenceNumber);
        if (check) {
            update();
            logger.info("Sentence deleted successfully from chapter: " + chapterName);
        } else {
            logger.warning("Failed to delete sentence number " + sentenceNumber + " from chapter: " + chapterName);
        }
        return check;
    }

    @Override
    public int searchSentence(String text) {
        return daf.searchSentence(text);
    }

    @Override
    public SentenceDTO sentenceByNumbers(String chapterName, int sentenceNumber) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID != -1) {
            return daf.sentenceByNumbers(chapterID, sentenceNumber);
        }
        logger.warning("Chapter not found while fetching sentence by number: " + chapterName);
        return null;
    }

    @Override
    public ArrayList<String> getSentencesByToken(String token) {
        return daf.getSentencesByToken(token);
    }

    @Override
    public ArrayList<String> getSentencesByTokenPattern(String tokenPattern) {
        return daf.getSentencesByTokenPattern(tokenPattern);
    }

    @Override
    public boolean addObserver(IObserver observer) {
        this.observers.add(observer);
        return true;
    }

    @Override
    public boolean removeObserver() {
        return true;
    }

    @Override
    public void update() {
        for (IObserver observer : observers) {
            observer.autoRefresh("Sentence");
        }
    }

    @Override
    public ArrayList<SentenceDTO> getSentencesByRoot(String rootText) {
        return daf.getSentencesByRoot(rootText);
    }

    @Override
    public double checkSimilarity(String s1, String s2) {
        return SimilarityAlgorithm.jaccardCharNGram(s1, s2, 3);
    }

    @Override
    public ArrayList<SimilarityResultDTO> findSimilarSentences(String inputSentence, double threshold) {
        ArrayList<SimilarityResultDTO> results = new ArrayList<>();
        Set<String> inputNgrams = SimilarityAlgorithm.getCharNGrams(inputSentence, 3);
        if (inputNgrams.isEmpty()) return results;

        ArrayList<Integer> candidateIds = daf.getCandidateSentenceIds(inputNgrams);
        for (int id : candidateIds) {
            SentenceDTO candidate = daf.getSentenceById(id);
            if (candidate != null) {
                double score = SimilarityAlgorithm.jaccardCharNGram(inputSentence, candidate.getText(), 3);
                logger.info("Sentence ID: " + id + " similarity score: " + score);

                if (score >= threshold) {
                    String sourceUrl = daf.getSourcePath(id);
                    results.add(new SimilarityResultDTO(candidate.getText(), score, sourceUrl));
                    logger.info("Sentence passed threshold: " + candidate.getText());
                }
            }
        }

        Collections.sort(results, (a, b) -> Double.compare(b.getScore(), a.getScore()));
        logger.info("findSimilarSentences completed for input: " + inputSentence);
        return results;
    }
}

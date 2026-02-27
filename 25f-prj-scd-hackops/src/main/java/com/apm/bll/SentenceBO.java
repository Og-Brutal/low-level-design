package main.java.com.apm.bll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


import com.apm.bll.utils.SimilarityAlgorithm;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;
import com.apm.dto.SimilarityResultDTO;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;

public class SentenceBO implements ISentenceBO,IObserveable{
	private IDataAccessLayerFasade daf;
	private ITokenBO tokenBO;
	private ArrayList<IObserver> observers;
	
	public SentenceBO(IDataAccessLayerFasade daf,ITokenBO tokenBO,ArrayList<IObserver> observers)
	{
		this.daf = daf;
		this.tokenBO=tokenBO;
		this.observers=observers;
	}

	@Override
    public boolean createSentence(String chapterName, String text, String textDiacritized, String translation, String notes) {
        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) return false;

        boolean check = daf.createSentence(chapterID, text, textDiacritized, translation, notes);
        
        if (check) {
            tokenBO.addToken(text);
            
            // 🟢 NEW: Update N-gram Index
            int sentenceId = daf.searchSentence(text); // Assuming this returns the newly created ID
            if(sentenceId != -1) {
                Set<String> ngrams = SimilarityAlgorithm.getCharNGrams(text,3);
                daf.saveNgrams(sentenceId, ngrams);
            }

            update();
        }
        return check;
    }

	@Override
	public ArrayList<SentenceDTO> retrieveSentence(String chapterName) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return null;
		}
		return daf.retrieveSentence(chapterID);
	}

	@Override
	public boolean updateSenetence(String chapterName, int sentenceNumber, String text, String textDiacritized,String translation, String notes) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		SentenceDTO sentence=daf.sentenceByNumbers(chapterID, sentenceNumber);
		boolean istokenDeleted=tokenBO.deleteTokensBySentence(sentence.getText());
		boolean check=daf.updateSenetence(chapterID, sentenceNumber, text, textDiacritized, translation, notes);
		if(check) {
			tokenBO.addToken(text);
			update();
		}
		return check;
	}

	@Override
	public boolean deleteSentence(String chapterName, int sentenceNumber) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID==-1) {
			return false;
		}
		boolean check=daf.deleteSentence(chapterID, sentenceNumber);
		if(check) {
			update();
		}
		return check;
	}

	@Override
	public int searchSentence(String text) {
		return daf.searchSentence(text);
	}

	@Override
	public SentenceDTO sentenceByNumbers(String chapterName, int sentenceNumber) {
		int chapterID=daf.searchChapter(chapterName);
		if(chapterID!=-1) {
			return daf.sentenceByNumbers(chapterID, sentenceNumber);
		}
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
		for(IObserver observer :observers) {
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
	
	
	// 🟢 NEW FUNCTION: The Optimized Search Logic
    public ArrayList<SimilarityResultDTO> findSimilarSentences(String inputSentence, double threshold) {
        ArrayList<SimilarityResultDTO> results = new ArrayList<>();
        
        // 1. Generate N-grams for Input
        Set<String> inputNgrams = SimilarityAlgorithm.getCharNGrams(inputSentence,3);
        if(inputNgrams.isEmpty()) return results;

        // 2. Get Candidates via Inverted Index (Fast Filtering)
        ArrayList<Integer> candidateIds = daf.getCandidateSentenceIds(inputNgrams);
        
        // 3. Detailed Comparison
        for(int id : candidateIds) {
            // Retrieve full sentence data (You might need to add a method in DAL to get DTO by ID)
            SentenceDTO candidate = daf.getSentenceById(id); // You need to ensure this exists in DAL
            
            if(candidate != null) {
                
                // 4. Calculate Jaccard Similarity
                double score = SimilarityAlgorithm.jaccardCharNGram(inputSentence, candidate.getText(),3);
                System.out.println("Score "+score);
                // 5. Threshold Filter
                if(score >= threshold) {
                	System.out.println("Sentence"+candidate.getSentenceNumber()+" Score "+score+" ThreshHold "+threshold);
                    // Build Result DTO
                    // Retrieve Book/Chapter names for the "URL"
                    String sourceUrl = daf.getSourcePath(id); // Implement this lookup
                    results.add(new SimilarityResultDTO(candidate.getText(), score, sourceUrl));
                }
            }
        }
        
        // 6. Sort by Score (Descending)
        Collections.sort(results, (a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return results;
    }
}
	


package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.TokenDTO;

public class TokenBO implements ITokenBO {
	private IDataAccessLayerFasade daf;
	private ILemmaBO lemmaBO;
	private ISegmentedTokenBO segmentedTokenBO; 
	
	public TokenBO(IDataAccessLayerFasade daf,ILemmaBO lemmaBO,ISegmentedTokenBO segmentedTokenBO)
	{
		this.daf = daf;
		this.lemmaBO=lemmaBO;
		this.segmentedTokenBO=segmentedTokenBO;
	}


	@Override
	public boolean addToken(String text) {
		boolean inserted =false;
		int sentenceID=daf.searchSentence(text);
		ArrayList<String> tokensList=TextProcessingUtil.analyzeAndSegmentSentence(text);
		if(lemmaBO.addLemmas(tokensList)) {
			inserted=true;
			for(String token:tokensList) {
				String lemma=TextProcessingUtil.getLemma(token);
				int lemmaID=daf.searchLemma(lemma);
				if(lemmaID!=-1) {
					int tokenID=daf.addToken(sentenceID,lemmaID , token);
					segmentedTokenBO.addSegments(tokenID, token);
				}
			}
		}
		return inserted;
	}


	@Override
	public ArrayList<TokenDTO> getTokensByLemma(String lemma) {
		ArrayList<TokenDTO> tokenList=null;
		int lemmaID=daf.searchLemma(lemma);
		if(lemmaID!=-1) {
			tokenList=daf.getTokensByLemma(lemmaID);
		}
		return tokenList;
	}


	@Override
	public ArrayList<TokenDTO> getAllTokens() {
		return daf.getAllTokens();
	}


	@Override
	public boolean deleteTokensBySentence(String sentence) {
		int sentenceID=daf.searchSentence(sentence);
		if(sentenceID!=-1) {
			ArrayList<Integer> tokenIDs=daf.deleteTokensBySentence(sentenceID);
			segmentedTokenBO.deleteSegments(tokenIDs);
		}
		return false;
	}


	@Override
	public String getTokenText(int tokenID) {
		
		return daf.getTokenText(tokenID);
	}
    
    /**
     * Finds the segmented token details (Lemma, Root, Stem, Prefix) for a given token text.
     * This relies on the TextProcessingUtil to perform the analysis.
     */
    public SegmentedTokenDTO getSegmentedTokenDetails(String token) {
        // NOTE: Since the TokenDTO doesn't contain Lemma/Root directly and there is no 
        // DA method to look up a SegmentedTokenDTO by token text, we must re-run the 
        // morphological analysis on the token text itself.
        
        String prefix = TextProcessingUtil.getPrefix(token);
        String stem = TextProcessingUtil.getStem(token);
        String lemma = TextProcessingUtil.getLemma(token);
        String root = TextProcessingUtil.getThreeLetterRoot(lemma);
        
        // Find the token ID to fill the DTO fields (optional but good practice)
        // Since we don't have the sentence context, we just search for the lemma ID.
        int lemmaID = daf.searchLemma(lemma);
        
        // This simulates a full DTO structure returned from the database, 
        // using the calculated values.
        SegmentedTokenDTO dto = new SegmentedTokenDTO(
            0, // segmentedTokenId (ignored)
            0, // tokenId (requires sentence context for proper lookup)
            prefix,
            stem,
            lemma,
            root
        );
        return dto;
    }
}
package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.apm.bll.utils.AppLogger;
import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.bll.utils.SegmentationUtil;
import com.apm.bll.utils.TokanizationUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.TokenDTO;

public class TokenBO implements ITokenBO {
    private IDataAccessLayerFasade daf;
    private ILemmaBO lemmaBO;
    private ISegmentedTokenBO segmentedTokenBO; 
    private static final Logger logger = AppLogger.getLogger(TokenBO.class);

    public TokenBO(IDataAccessLayerFasade daf, ILemmaBO lemmaBO, ISegmentedTokenBO segmentedTokenBO) {
        this.daf = daf;
        this.lemmaBO = lemmaBO;
        this.segmentedTokenBO = segmentedTokenBO;
        logger.info("TokenBO initialized.");
    }

    @Override
    public boolean addToken(String text) {
        boolean inserted = false;
        int sentenceID = daf.searchSentence(text);
        ArrayList<String> tokensList = TokanizationUtil.analyzeAndSegmentSentence(text);

        if (lemmaBO.addLemmas(tokensList)) {
            inserted = true;
            for (String token : tokensList) {
                String lemma = LemmaUtil.getLemma(token);
                int lemmaID = daf.searchLemma(lemma);
                if (lemmaID != -1) {
                    int tokenID = daf.addToken(sentenceID, lemmaID, token);
                    segmentedTokenBO.addSegments(tokenID, token);
                    logger.info("Token added: " + token + ", Lemma: " + lemma + ", TokenID: " + tokenID);
                } else {
                    logger.warning("Lemma not found for token: " + token);
                }
            }
        } else {
            logger.warning("No lemmas added for sentence: " + text);
        }

        return inserted;
    }

    @Override
    public ArrayList<TokenDTO> getTokensByLemma(String lemma) {
        ArrayList<TokenDTO> tokenList = null;
        int lemmaID = daf.searchLemma(lemma);
        if (lemmaID != -1) {
            tokenList = daf.getTokensByLemma(lemmaID);
            logger.info("Retrieved " + (tokenList != null ? tokenList.size() : 0) + " tokens for lemma: " + lemma);
        } else {
            logger.warning("Lemma not found: " + lemma);
        }
        return tokenList;
    }

    @Override
    public ArrayList<TokenDTO> getAllTokens() {
        ArrayList<TokenDTO> allTokens = daf.getAllTokens();
        logger.info("Retrieved all tokens. Count: " + (allTokens != null ? allTokens.size() : 0));
        return allTokens;
    }

    @Override
    public boolean deleteTokensBySentence(String sentence) {
        int sentenceID = daf.searchSentence(sentence);
        if (sentenceID != -1) {
            ArrayList<Integer> tokenIDs = daf.deleteTokensBySentence(sentenceID);
            boolean deletedSegments = segmentedTokenBO.deleteSegments(tokenIDs);
            logger.info("Deleted " + (tokenIDs != null ? tokenIDs.size() : 0) + " tokens and their segments for sentence: " + sentence);
            return deletedSegments;
        } else {
            logger.warning("Sentence not found while deleting tokens: " + sentence);
            return false;
        }
    }

    @Override
    public String getTokenText(int tokenID) {
        String tokenText = daf.getTokenText(tokenID);
        logger.info("Fetched token text for tokenID " + tokenID + ": " + tokenText);
        return tokenText;
    }

    @Override
    public SegmentedTokenDTO getSegmentedTokenDetails(String token) {
        String prefix = SegmentationUtil.getPrefix(token);
        String stem = SegmentationUtil.getStem(token);
        String lemma = LemmaUtil.getLemma(token);
        String root = RootUtil.getThreeLetterRoot(lemma);
        int lemmaID = daf.searchLemma(lemma);

        SegmentedTokenDTO dto = new SegmentedTokenDTO(
            0, // segmentedTokenId
            0, // tokenId
            prefix,
            stem,
            lemma,
            root
        );

        logger.info("Segmented token details calculated for token: " + token + " | Prefix: " + prefix + ", Stem: " + stem + ", Lemma: " + lemma + ", Root: " + root);
        return dto;
    }

    @Override
    public ArrayList<String> getAllTokensByBook(String bookName) {
        int bookId = daf.searchBook(bookName);
        if (bookId == -1) {
            logger.warning("Book not found while fetching tokens: " + bookName);
            return null;
        }
        ArrayList<String> tokens = daf.getAllTokensByBook(bookId);
        logger.info("Fetched " + (tokens != null ? tokens.size() : 0) + " tokens for book: " + bookName);
        return tokens;
    }
}

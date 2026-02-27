package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.bll.utils.SegmentationUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.bll.utils.AppLogger;

public class SegmentedTokenBO implements ISegmentedTokenBO {

    private IDataAccessLayerFasade daf;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(SegmentedTokenBO.class);

    public SegmentedTokenBO(IDataAccessLayerFasade daf) {
        this.daf = daf;
        logger.info("SegmentedTokenBO initialized.");
    }

    @Override
    public boolean addSegments(int tokenID, String token) {
        logger.info("Adding segments for tokenID: " + tokenID + ", token: " + token);

        String prefix = SegmentationUtil.getPrefix(token);
        String stem = SegmentationUtil.getStem(token);
        String lemma = LemmaUtil.getLemma(token);
        String root = RootUtil.getThreeLetterRoot(lemma);

        logger.info("Segment details - Prefix: " + prefix + ", Stem: " + stem + ", Lemma: " + lemma + ", Root: " + root);

        boolean added = daf.addSegments(tokenID, prefix, stem, lemma, root);
        if (added) {
            logger.info("Segments added successfully for tokenID: " + tokenID);
        } else {
            logger.warning("Failed to add segments for tokenID: " + tokenID);
        }
        return added;
    }

    @Override
    public ArrayList<SegmentedTokenDTO> getAllSegments() {
        logger.info("Fetching all segmented tokens.");
        ArrayList<SegmentedTokenDTO> segments = daf.getAllSegments();
        logger.info("Total segments fetched: " + (segments != null ? segments.size() : 0));
        return segments;
    }

    @Override
    public boolean deleteSegments(ArrayList<Integer> tokenIDs) {
        if (tokenIDs == null || tokenIDs.isEmpty()) {
            logger.warning("No token IDs provided for deletion.");
            return false;
        }

        boolean allDeleted = true;
        for (Integer tokenID : tokenIDs) {
            if (tokenID != null) {
                boolean deleted = daf.deleteSegments(tokenID);
                if (deleted) {
                    logger.info("Deleted segments for tokenID: " + tokenID);
                } else {
                    logger.warning("Failed to delete segments for tokenID: " + tokenID);
                    allDeleted = false;
                }
            }
        }

        logger.info("Segment deletion completed. Success: " + allDeleted);
        return allDeleted;
    }
}

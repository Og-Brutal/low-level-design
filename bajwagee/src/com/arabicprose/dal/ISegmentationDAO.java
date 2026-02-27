package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.SegmentationDTO;

public interface ISegmentationDAO {
    void addSegmentation(SegmentationDTO segmentation) throws SQLException;
    SegmentationDTO getSegmentationByTokenId(int tokenId) throws SQLException;
    void deleteSegmentationByTokenId(int tokenId) throws SQLException;
    
    // New method for browsing
    List<SegmentationDTO> getAllSegmentations() throws SQLException;
}
package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.AnalysisResultDTO;

public interface IAnalysisResultDAO {
    void addAnalysisResult(AnalysisResultDTO analysis) throws SQLException;
    AnalysisResultDTO getAnalysisResultById(int analysisId) throws SQLException;
    List<AnalysisResultDTO> getAnalysisResultsBySentenceId(int sentenceId) throws SQLException;
    void deleteAnalysisResult(int analysisId) throws SQLException;
    boolean analysisExistsForSentence(int sentenceId) throws SQLException;
}
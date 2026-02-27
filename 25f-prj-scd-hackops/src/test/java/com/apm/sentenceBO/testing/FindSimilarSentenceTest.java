package test.java.com.apm.sentenceBO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.SentenceBO;
import com.apm.bll.utils.SimilarityAlgorithm;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;
import com.apm.dto.SimilarityResultDTO;

class FindSimilarSentenceTest {
    @Mock
    IDataAccessLayerFasade daf;

    @InjectMocks
    @Spy
    SentenceBO sentenceBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sentenceBO = spy(new SentenceBO(daf, null, null)); // tokenBO not needed here
    }

    @Test
    void tp1_emptyInputNgrams_returnsEmpty() {
        try (MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class)) {
            mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("input", 3))
                      .thenReturn(Collections.emptySet());

            ArrayList<SimilarityResultDTO> result = sentenceBO.findSimilarSentences("input", 0.5);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void tp2_noCandidates_returnsEmpty() {
        try (MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class)) {
            mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("input", 3))
                      .thenReturn(Set.of("ngr1","ngr2"));

            when(daf.getCandidateSentenceIds(Set.of("ngr1","ngr2")))
                .thenReturn(new ArrayList<>());

            ArrayList<SimilarityResultDTO> result = sentenceBO.findSimilarSentences("input", 0.5);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void tp3_candidateIsNull_returnsEmpty() {
        try (MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class)) {
            mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("input", 3))
                      .thenReturn(Set.of("ngr"));

            when(daf.getCandidateSentenceIds(Set.of("ngr")))
                .thenReturn(new ArrayList<>(Collections.singletonList(1)));
            when(daf.getSentenceById(1)).thenReturn(null);

            ArrayList<SimilarityResultDTO> result = sentenceBO.findSimilarSentences("input", 0.5);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void tp4_similarityBelowThreshold_noResultAdded() {
        try (MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class)) {
            mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("input", 3))
                      .thenReturn(Set.of("ngr"));

            when(daf.getCandidateSentenceIds(Set.of("ngr")))
                .thenReturn(new ArrayList<>(Collections.singletonList(1)));

            SentenceDTO candidate = mock(SentenceDTO.class);
            when(daf.getSentenceById(1)).thenReturn(candidate);
            when(candidate.getText()).thenReturn("candidate text");

            mockStatic.when(() -> SimilarityAlgorithm.jaccardCharNGram("input", "candidate text", 3))
                      .thenReturn(0.3); // below threshold 0.5

            ArrayList<SimilarityResultDTO> result = sentenceBO.findSimilarSentences("input", 0.5);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void tp5_similarityAboveThreshold_resultAddedAndSorted() {
        try (MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class)) {
            mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("input", 3))
                      .thenReturn(Set.of("ngr"));

            when(daf.getCandidateSentenceIds(Set.of("ngr")))
                .thenReturn(new ArrayList<>(List.of(1,2)));

            SentenceDTO candidate1 = mock(SentenceDTO.class);
            when(candidate1.getText()).thenReturn("candidate1");
            when(candidate1.getSentenceNumber()).thenReturn(1);

            SentenceDTO candidate2 = mock(SentenceDTO.class);
            when(candidate2.getText()).thenReturn("candidate2");
            when(candidate2.getSentenceNumber()).thenReturn(2);

            when(daf.getSentenceById(1)).thenReturn(candidate1);
            when(daf.getSentenceById(2)).thenReturn(candidate2);

            mockStatic.when(() -> SimilarityAlgorithm.jaccardCharNGram("input", "candidate1", 3))
                      .thenReturn(0.6); // passes threshold 0.5
            mockStatic.when(() -> SimilarityAlgorithm.jaccardCharNGram("input", "candidate2", 3))
                      .thenReturn(0.8); // passes threshold 0.5

            when(daf.getSourcePath(1)).thenReturn("url1");
            when(daf.getSourcePath(2)).thenReturn("url2");

            ArrayList<SimilarityResultDTO> result = sentenceBO.findSimilarSentences("input", 0.5);

            assertEquals(2, result.size());
            assertEquals("candidate2", result.get(0).getText()); // sorted descending by score
            assertEquals("candidate1", result.get(1).getText());
        }
    }
}

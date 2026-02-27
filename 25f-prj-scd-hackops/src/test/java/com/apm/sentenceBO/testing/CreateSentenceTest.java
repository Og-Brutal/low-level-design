package test.java.com.apm.sentenceBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.ITokenBO;
import com.apm.bll.SentenceBO;
import com.apm.bll.utils.SimilarityAlgorithm;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.observers.IObserver;

class CreateSentenceTest {

    @Mock
    IDataAccessLayerFasade daf;

    @Mock
    ITokenBO tokenBO;

    ArrayList<IObserver> observers = new ArrayList<>();

    @InjectMocks
    @Spy // To spy on update() method
    SentenceBO sentenceBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sentenceBO = new SentenceBO(daf, tokenBO, observers);
    }

    @Test
    void tp1_chapterNotFound_returnsFalse() {
        when(daf.searchChapter("Unknown")).thenReturn(-1);

        boolean result = sentenceBO.createSentence("Unknown", "text", "", "", "");

        assertFalse(result);
        verifyNoInteractions(tokenBO);
        verify(daf, never()).createSentence(anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void tp2_createSentenceFails_returnsFalse() {
        when(daf.searchChapter("Chapter1")).thenReturn(100);
        when(daf.createSentence(100, "text", "", "", "")).thenReturn(false);

        boolean result = sentenceBO.createSentence("Chapter1", "text", "", "", "");

        assertFalse(result);
        verifyNoInteractions(tokenBO);
        verify(daf, never()).searchSentence(anyString());
    }

    @Test
    void tp3_createSentenceSucceeds_noNgrams() {
        when(daf.searchChapter("Chapter2")).thenReturn(200);
        when(daf.createSentence(200, "text", "", "", "")).thenReturn(true);
        when(daf.searchSentence("text")).thenReturn(-1);

        SentenceBO spySentenceBO = spy(sentenceBO);

        boolean result = spySentenceBO.createSentence("Chapter2", "text", "", "", "");

        assertTrue(result);
        verify(tokenBO).addToken("text");
        verify(daf).searchSentence("text");
        verify(daf, never()).saveNgrams(anyInt(), anySet());
        verify(spySentenceBO).update();
    }

    @Test
    void tp4_createSentenceSucceeds_withNgrams() {
        when(daf.searchChapter("Chapter3")).thenReturn(300);
        when(daf.createSentence(300, "text", "", "", "")).thenReturn(true);
        when(daf.searchSentence("text")).thenReturn(400);

        // Mock static method
        MockedStatic<SimilarityAlgorithm> mockStatic = mockStatic(SimilarityAlgorithm.class);
        mockStatic.when(() -> SimilarityAlgorithm.getCharNGrams("text", 3))
                  .thenReturn(Set.of("tex", "ext"));

        SentenceBO spySentenceBO = spy(sentenceBO);

        boolean result = spySentenceBO.createSentence("Chapter3", "text", "", "", "");

        assertTrue(result);
        verify(tokenBO).addToken("text");
        verify(daf).saveNgrams(400, Set.of("tex", "ext"));
        verify(spySentenceBO).update();

        mockStatic.close();
    }

}

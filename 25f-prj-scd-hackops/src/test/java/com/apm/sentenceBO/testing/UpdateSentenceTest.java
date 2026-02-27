package test.java.com.apm.sentenceBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.ITokenBO;
import com.apm.bll.SentenceBO;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SentenceDTO;
import com.apm.observers.IObserver;

class UpdateSentenceTest {

    @Mock
    IDataAccessLayerFasade daf;

    @Mock
    ITokenBO tokenBO;

    ArrayList<IObserver> observers = new ArrayList<>();

    @InjectMocks
    @Spy
    SentenceBO sentenceBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sentenceBO = new SentenceBO(daf, tokenBO, observers);
    }

    @Test
    void tp1_chapterNotFound_returnsFalse() {
        when(daf.searchChapter("Unknown")).thenReturn(-1);

        boolean result = sentenceBO.updateSenetence("Unknown", 1, "text", "", "", "");

        assertFalse(result);
        verifyNoInteractions(tokenBO);
        verify(daf, never()).sentenceByNumbers(anyInt(), anyInt());
        verify(daf, never()).updateSenetence(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void tp2_updateFails_returnsFalse() {
        when(daf.searchChapter("Chapter1")).thenReturn(100);

        SentenceDTO sentence = mock(SentenceDTO.class);
        when(daf.sentenceByNumbers(100, 1)).thenReturn(sentence);
        when(sentence.getText()).thenReturn("old text");

        when(tokenBO.deleteTokensBySentence("old text")).thenReturn(true);
        when(daf.updateSenetence(100, 1, "new text", "", "", "")).thenReturn(false);

        boolean result = sentenceBO.updateSenetence("Chapter1", 1, "new text", "", "", "");

        assertFalse(result);
        verify(tokenBO).deleteTokensBySentence("old text");
        verify(tokenBO, never()).addToken(anyString());
        
    }

    @Test
    void tp3_updateSucceeds_returnsTrue() {
        when(daf.searchChapter("Chapter2")).thenReturn(200);

        SentenceDTO sentence = mock(SentenceDTO.class);
        when(daf.sentenceByNumbers(200, 2)).thenReturn(sentence);
        when(sentence.getText()).thenReturn("old text");

        when(tokenBO.deleteTokensBySentence("old text")).thenReturn(true);
        when(daf.updateSenetence(200, 2, "new text", "", "", "")).thenReturn(true);

        SentenceBO spySentenceBO = spy(sentenceBO);

        boolean result = spySentenceBO.updateSenetence("Chapter2", 2, "new text", "", "", "");

        assertTrue(result);
        verify(tokenBO).deleteTokensBySentence("old text");
        verify(tokenBO).addToken("new text");
        verify(spySentenceBO).update();
    }
}

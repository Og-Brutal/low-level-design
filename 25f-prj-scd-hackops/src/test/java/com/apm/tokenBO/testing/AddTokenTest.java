package test.java.com.apm.tokenBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.ILemmaBO;
import com.apm.bll.ISegmentedTokenBO;
import com.apm.bll.TokenBO;
import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.TokanizationUtil;
import com.apm.dal.IDataAccessLayerFasade;

class AddTokenTest {

    @Mock
    IDataAccessLayerFasade daf;

    @Mock
    ILemmaBO lemmaBO;

    @Mock
    ISegmentedTokenBO segmentedTokenBO;

    @InjectMocks
    @Spy
    TokenBO tokenBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenBO = spy(new TokenBO(daf, lemmaBO, segmentedTokenBO));
    }

    @Test
    void tp1_addLemmasFalse_returnsFalse() {
        when(daf.searchSentence("text")).thenReturn(10);

        try (MockedStatic<TokanizationUtil> mockTok = mockStatic(TokanizationUtil.class)) {
            ArrayList<String> tokensList = new ArrayList<>();
            tokensList.add("token1");
            tokensList.add("token2");

            mockTok.when(() -> TokanizationUtil.analyzeAndSegmentSentence("text"))
                   .thenReturn(tokensList);

            when(lemmaBO.addLemmas(tokensList)).thenReturn(false);

            boolean result = tokenBO.addToken("text");

            assertFalse(result);
            verify(daf, never()).searchLemma(anyString());
            verify(daf, never()).addToken(anyInt(), anyInt(), anyString());
            verify(segmentedTokenBO, never()).addSegments(anyInt(), anyString());
        }
    }

    @Test
    void tp2_allLemmasFound_insertionSucceeds() {
        when(daf.searchSentence("text")).thenReturn(20);

        try (MockedStatic<TokanizationUtil> mockTok = mockStatic(TokanizationUtil.class);
             MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class)) {

            ArrayList<String> tokensList = new ArrayList<>();
            tokensList.add("token1");
            tokensList.add("token2");

            mockTok.when(() -> TokanizationUtil.analyzeAndSegmentSentence("text"))
                   .thenReturn(tokensList);

            when(lemmaBO.addLemmas(tokensList)).thenReturn(true);

            mockLemma.when(() -> LemmaUtil.getLemma("token1")).thenReturn("lemma1");
            mockLemma.when(() -> LemmaUtil.getLemma("token2")).thenReturn("lemma2");

            when(daf.searchLemma("lemma1")).thenReturn(101);
            when(daf.searchLemma("lemma2")).thenReturn(102);

            when(daf.addToken(20, 101, "token1")).thenReturn(201);
            when(daf.addToken(20, 102, "token2")).thenReturn(202);

            boolean result = tokenBO.addToken("text");

            assertTrue(result);
            verify(segmentedTokenBO).addSegments(201, "token1");
            verify(segmentedTokenBO).addSegments(202, "token2");
        }
    }

    @Test
    void tp3_someLemmaNotFound_skipsInsertionForThatToken() {
        when(daf.searchSentence("text")).thenReturn(30);

        try (MockedStatic<TokanizationUtil> mockTok = mockStatic(TokanizationUtil.class);
             MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class)) {

            ArrayList<String> tokensList = new ArrayList<>();
            tokensList.add("token1");
            tokensList.add("token2");

            mockTok.when(() -> TokanizationUtil.analyzeAndSegmentSentence("text"))
                   .thenReturn(tokensList);

            when(lemmaBO.addLemmas(tokensList)).thenReturn(true);

            mockLemma.when(() -> LemmaUtil.getLemma("token1")).thenReturn("lemma1");
            mockLemma.when(() -> LemmaUtil.getLemma("token2")).thenReturn("lemma2");

            when(daf.searchLemma("lemma1")).thenReturn(101);
            when(daf.searchLemma("lemma2")).thenReturn(-1);

            when(daf.addToken(30, 101, "token1")).thenReturn(201);

            boolean result = tokenBO.addToken("text");

            assertTrue(result);
            verify(segmentedTokenBO).addSegments(201, "token1");
            verify(segmentedTokenBO, never()).addSegments(anyInt(), eq("token2"));
        }
    }


}

package test.java.com.apm.lemmaBO.testing;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.LemmaBO;
import com.apm.bll.RootBO;
import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;

class addLemmaTest {

    @Mock
    IDataAccessLayerFasade daf;

    @Mock
    RootBO rootBO;

    @InjectMocks
    @Spy
    LemmaBO lemmaBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lemmaBO = spy(new LemmaBO(daf, rootBO));
    }

    @Test
    void tp1_addRootsFalse_returnsFalse() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("token1");
        tokens.add("token2");

        try (MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class)) {
            ArrayList<String> lemmas = new ArrayList<>();
            lemmas.add("lemma1");
            lemmas.add("lemma2");

            mockLemma.when(() -> LemmaUtil.getLemmaList(tokens)).thenReturn(lemmas);

            when(rootBO.addRoots(lemmas)).thenReturn(false);

            boolean result = lemmaBO.addLemmas(tokens);

            assertFalse(result);
            verify(daf, never()).searchRoot(anyString());
            verify(daf, never()).addLemmas(anyInt(), anyString());
        }
    }

    @Test
    void tp2_allRootsFound_insertionSucceeds() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("token1");
        tokens.add("token2");

        try (MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class);
             MockedStatic<RootUtil> mockRoot = mockStatic(RootUtil.class)) {

            ArrayList<String> lemmas = new ArrayList<>();
            lemmas.add("lemma1");
            lemmas.add("lemma2");

            mockLemma.when(() -> LemmaUtil.getLemmaList(tokens)).thenReturn(lemmas);

            when(rootBO.addRoots(lemmas)).thenReturn(true);

            mockRoot.when(() -> RootUtil.getThreeLetterRoot("lemma1")).thenReturn("r1");
            mockRoot.when(() -> RootUtil.getThreeLetterRoot("lemma2")).thenReturn("r2");

            when(daf.searchRoot("r1")).thenReturn(101);
            when(daf.searchRoot("r2")).thenReturn(102);

            boolean result = lemmaBO.addLemmas(tokens);

            assertTrue(result);
            verify(daf).addLemmas(101, "lemma1");
            verify(daf).addLemmas(102, "lemma2");
        }
    }

    @Test
    void tp3_someRootsNotFound_skipsInsertionForThatLemma() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("token1");
        tokens.add("token2");

        try (MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class);
             MockedStatic<RootUtil> mockRoot = mockStatic(RootUtil.class)) {

            ArrayList<String> lemmas = new ArrayList<>();
            lemmas.add("lemma1");
            lemmas.add("lemma2");

            mockLemma.when(() -> LemmaUtil.getLemmaList(tokens)).thenReturn(lemmas);

            when(rootBO.addRoots(lemmas)).thenReturn(true);

            mockRoot.when(() -> RootUtil.getThreeLetterRoot("lemma1")).thenReturn("r1");
            mockRoot.when(() -> RootUtil.getThreeLetterRoot("lemma2")).thenReturn("r2");

            when(daf.searchRoot("r1")).thenReturn(101);
            when(daf.searchRoot("r2")).thenReturn(-1); // not found

            boolean result = lemmaBO.addLemmas(tokens);

            assertTrue(result);
            verify(daf).addLemmas(101, "lemma1");
            verify(daf, never()).addLemmas(anyInt(), eq("lemma2"));
        }
    }
}

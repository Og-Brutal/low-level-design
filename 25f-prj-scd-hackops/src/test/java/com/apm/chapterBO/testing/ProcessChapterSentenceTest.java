package test.java.com.apm.chapterBO.testing;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.apm.bll.ChapterBO;
import com.apm.bll.ISentenceBO;
import com.apm.bll.utils.TextProcessingUtil;

class ProcessChapterSentenceTest {

    @Mock
    ISentenceBO sentenceBO;

    @InjectMocks
    ChapterBO chapterBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize @Mock and @InjectMocks
    }

    @Test
    void tp1_sentencesNull_returnsImmediately() {
        try (MockedStatic<TextProcessingUtil> util = mockStatic(TextProcessingUtil.class)) {
            util.when(() -> TextProcessingUtil.ChapterInToSentence("content"))
                .thenReturn(null);

            chapterBO.processChapterSentences("Chapter1", "content");

            verifyNoInteractions(sentenceBO);
        }
    }

    @Test
    void tp2_sentencesEmpty_returnsImmediately() {
        try (MockedStatic<TextProcessingUtil> util = mockStatic(TextProcessingUtil.class)) {
            util.when(() -> TextProcessingUtil.ChapterInToSentence("content"))
                .thenReturn(new ArrayList<>());

            chapterBO.processChapterSentences("Chapter2", "content");

            verifyNoInteractions(sentenceBO);
        }
    }

    @Test
    void tp3_allSentencesInsertedSuccessfully() {
        try (MockedStatic<TextProcessingUtil> util = mockStatic(TextProcessingUtil.class)) {
            List<String> sentences = List.of("Sentence 1", "Sentence 2");
            util.when(() -> TextProcessingUtil.ChapterInToSentence("content"))
                .thenReturn(new ArrayList<>(sentences));

            when(sentenceBO.createSentence(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

            chapterBO.processChapterSentences("Chapter3", "content");

            for (String s : sentences) {
                verify(sentenceBO).createSentence("Chapter3", s, "", "", "");
            }
        }
    }

    @Test
    void tp4_someSentencesFailToInsert() {
        try (MockedStatic<TextProcessingUtil> util = mockStatic(TextProcessingUtil.class)) {
            List<String> sentences = List.of("Good sentence", "Bad sentence");
            util.when(() -> TextProcessingUtil.ChapterInToSentence("content"))
                .thenReturn(new ArrayList<>(sentences));

            when(sentenceBO.createSentence("Chapter4", "Good sentence", "", "", ""))
                .thenReturn(true);
            when(sentenceBO.createSentence("Chapter4", "Bad sentence", "", "", ""))
                .thenReturn(false);

            chapterBO.processChapterSentences("Chapter4", "content");

            verify(sentenceBO).createSentence("Chapter4", "Good sentence", "", "", "");
            verify(sentenceBO).createSentence("Chapter4", "Bad sentence", "", "", "");
        }
    }
}

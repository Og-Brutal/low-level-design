package test.java.com.apm.chapterBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.apm.bll.ChapterBO;
import com.apm.bll.ISentenceBO;
import com.apm.bll.utils.TextProcessingUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.observers.IObserver;
@ExtendWith(MockitoExtension.class)
class SentenceExtractorTest {

	@Mock private IDataAccessLayerFasade daf;
    @Mock private ISentenceBO sentenceBO;
    @Mock private ArrayList<IObserver> observers;

    @InjectMocks
    private ChapterBO chapterBO;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TP1: Book not found → return false")
    void tp1_bookNotFound_returnsFalse() {
        when(daf.searchBook("Unknown Book")).thenReturn(-1);

        boolean result = chapterBO.sentenceExtracter("Unknown Book", "any.txt");

        assertFalse(result);
        verify(daf, never()).getData(anyString());
    }

    
    @Test
    @DisplayName("TP2: File empty or null → return false")
    void tp2_emptyFile_returnsFalse() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.write(file, new byte[0]); // empty file

        when(daf.searchBook("Book")).thenReturn(100);
        when(daf.getData(file.toString())).thenReturn("");

        boolean result = chapterBO.sentenceExtracter("Book", file.toString());

        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: First line empty → no chapter name → return false")
    void tp3_noChapterName_returnsFalse() throws Exception {
        Path file = tempDir.resolve("nochapter.txt");
        Files.write(file, "\nLine one\nLine two".getBytes());

        when(daf.searchBook("Book")).thenReturn(100);
        when(daf.getData(file.toString())).thenReturn("\nLine one\nLine two");

        boolean result = chapterBO.sentenceExtracter("Book", file.toString());

        assertFalse(result);
    }



}

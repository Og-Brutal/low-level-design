package test.java.java.com.apm.bookBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apm.bll.BookBO;
import com.apm.bll.IChapterBO;
import com.apm.dal.IDataAccessLayerFasade;

class ChapterSeparaterTest {

	@Mock private IDataAccessLayerFasade daf;
    @Mock private IChapterBO chapterBO;

    @InjectMocks
    private BookBO bookBO;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TP1: File not found or empty content → return false")
    void tp1_fileNotFoundOrEmpty_returnsFalse() {
        when(daf.getData(anyString())).thenReturn(null);

        boolean result = bookBO.chapterSeparater("nonexistent.txt");

        assertFalse(result);
        verify(daf, never()).createBook(anyString(), anyInt(), isNull());
    }

    @Test
    @DisplayName("TP2: No ##CHAPTER## separator → return false")
    void tp2_noChapterSeparator_returnsFalse() throws Exception {
        Path file = tempDir.resolve("nochapters.txt");
        Files.write(file, "This book has no chapters".getBytes(StandardCharsets.UTF_8));

        when(daf.getData(file.toString())).thenReturn(null);

        boolean result = bookBO.chapterSeparater(file.toString());

        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: One valid chapter → full success path → return true")
    void tp3_oneValidChapter_success_returnsTrue() throws Exception {
        Path file = tempDir.resolve("onechapter.txt");
        String content = "My Great Book\n" +
                         "##CHAPTER##\n" +
                         "Introduction\n" +
                         "This is the first chapter content.";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        when(daf.getData(file.toString())).thenReturn(content);
        when(daf.createBook("onechapter", -1, null)).thenReturn(true);
        when(daf.searchBook("onechapter")).thenReturn(777);
        when(daf.searchChapter("Introduction")).thenReturn(888);
        when(chapterBO.createChapter("onechapter", "Introduction")).thenReturn(true);

        boolean result = bookBO.chapterSeparater(file.toString());

        assertTrue(result);
        verify(chapterBO).processChapterSentences("Introduction", "This is the first chapter content.");
    }

    @Test
    @DisplayName("TP4: Multiple chapters + one empty → skip empty → return true")
    void tp4_multipleChaptersIncludingEmpty_success() throws Exception {
        Path file = tempDir.resolve("multi.txt");
        String content = "Multi Book\n" +
                         "##CHAPTER##\n" +
                         "Chapter One\n" +
                         "Text one\n" +
                         "##CHAPTER##\n" +
                         "\n" +
                         "##CHAPTER##\n" +
                         "Chapter Two\n" +
                         "Text two";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        when(daf.getData(file.toString())).thenReturn(content);
        when(daf.createBook("multi", -1, null)).thenReturn(true);
        when(daf.searchBook("multi")).thenReturn(999);
        when(daf.searchChapter("Chapter One")).thenReturn(1001);
        when(daf.searchChapter("Chapter Two")).thenReturn(1002);
        when(chapterBO.createChapter(eq("multi"), anyString())).thenReturn(true);

        boolean result = bookBO.chapterSeparater(file.toString());

        assertTrue(result);
        verify(chapterBO).processChapterSentences("Chapter One", "Text one");
        verify(chapterBO).processChapterSentences("Chapter Two", "Text two");
    }

    @Test
    @DisplayName("TP5: createBook fails but book already exists → continue → return true")
    void tp5_bookCreationFails_butExists_stillSuccess() throws Exception {
        Path file = tempDir.resolve("exists.txt");
        String content = "##CHAPTER##\n" +
                         "Only Chapter\n" +
                         "Some content";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        when(daf.getData(file.toString())).thenReturn(content);
        when(daf.createBook("exists", -1, null)).thenReturn(false);
        when(daf.searchBook("exists")).thenReturn(555);
        when(daf.searchChapter("Only Chapter")).thenReturn(666);
        when(chapterBO.createChapter("exists", "Only Chapter")).thenReturn(true);

        boolean result = bookBO.chapterSeparater(file.toString());

        assertTrue(result);
        verify(chapterBO).processChapterSentences("Only Chapter", "Some content");
    }

    @Test
    @DisplayName("TP6: Exception anywhere → catch → return false")
    void tp6_exceptionInProcessing_returnsFalse() throws Exception {
        Path file = tempDir.resolve("crash.txt");
        String content = "##CHAPTER##\n" +
                         "Crash Chapter\n" +
                         "Boom!";
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        when(daf.getData(file.toString())).thenReturn(content);
        when(daf.createBook("crash", -1, null)).thenReturn(true);
        when(daf.searchBook("crash")).thenReturn(111);
        when(daf.searchChapter("Crash Chapter")).thenReturn(222);
        when(chapterBO.createChapter("crash", "Crash Chapter")).thenReturn(true);

        doThrow(new RuntimeException("Simulated crash")).when(chapterBO)
                .processChapterSentences("Crash Chapter", "Boom!");

        boolean result = bookBO.chapterSeparater(file.toString());

        assertFalse(result);
    }
}

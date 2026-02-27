package test.java.com.apm.bookBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.apm.bll.BookBO;
import com.apm.bll.IChapterBO;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.observers.IObserver;
@ExtendWith(MockitoExtension.class)
class CreateBookTest {
	@Mock private IDataAccessLayerFasade daf;
    @Mock private IChapterBO chapterBO;
    @Mock private ArrayList<IObserver> observers;

    @InjectMocks
    private BookBO bookBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TP1: Author not found → return false")
    void tp1_authorNotFound() {
        when(daf.searchAuthor("Ghost")).thenReturn(-1);

        assertFalse(bookBO.createBook("New Book", "Ghost", "Modern"));
        verify(daf, never()).searchBook(anyString());
        verify(daf, never()).createBook(anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("TP2: Book already exists → return false")
    void tp2_bookAlreadyExists() {
        when(daf.searchAuthor("Ibn Taymiyyah")).thenReturn(77);
        when(daf.searchBook("Majmoo al-Fatawa")).thenReturn(123);

        assertFalse(bookBO.createBook("Majmoo al-Fatawa", "Ibn Taymiyyah", "Classical"));
        verify(daf).searchBook("Majmoo al-Fatawa");
        verify(daf, never()).createBook(anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("TP3: All good → createBook returns true → return true")
    void tp3_success_createReturnsTrue() {
        when(daf.searchAuthor("Imam Nawawi")).thenReturn(88);
        when(daf.searchBook("Sharh Sahih Muslim")).thenReturn(-1);

        // CORRECT: match exact signature: String, int, String
        when(daf.createBook("Sharh Sahih Muslim", 88, "Classical")).thenReturn(true);

        boolean result = bookBO.createBook("Sharh Sahih Muslim", "Imam Nawawi", "Classical");

        assertTrue(result);
        verify(daf).createBook("Sharh Sahih Muslim", 88, "Classical");
    }

    @Test
    @DisplayName("TP4: All good → createBook returns false → return false")
    void tp4_createReturnsFalse() {
        when(daf.searchAuthor("Al-Suyuti")).thenReturn(99);
        when(daf.searchBook("Tafsir al-Jalalayn")).thenReturn(-1);

        when(daf.createBook("Tafsir al-Jalalayn", 99, "Classical")).thenReturn(false);

        assertFalse(bookBO.createBook("Tafsir al-Jalalayn", "Al-Suyuti", "Classical"));
    }

  
}

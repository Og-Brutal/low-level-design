package test.java.com.apm.segmentedTokenBO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.SegmentedTokenBO;
import com.apm.dal.IDataAccessLayerFasade;

class DeleteSegmentsToken {


    @Mock
    IDataAccessLayerFasade daf;

    @InjectMocks
    @Spy
    SegmentedTokenBO segmentedTokenBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        segmentedTokenBO = spy(new SegmentedTokenBO(daf));
    }

    @Test
    void tp1_nullList_returnsFalse() {
        boolean result = segmentedTokenBO.deleteSegments(null);
        assertFalse(result);
        verify(daf, never()).deleteSegments(anyInt());
    }

    @Test
    void tp2_emptyList_returnsFalse() {
        boolean result = segmentedTokenBO.deleteSegments(new ArrayList<>());
        assertFalse(result);
        verify(daf, never()).deleteSegments(anyInt());
    }

    @Test
    void tp3_allDeleted_returnsTrue() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(101);
        ids.add(102);

        when(daf.deleteSegments(101)).thenReturn(true);
        when(daf.deleteSegments(102)).thenReturn(true);

        boolean result = segmentedTokenBO.deleteSegments(ids);
        assertTrue(result);

        verify(daf).deleteSegments(101);
        verify(daf).deleteSegments(102);
    }

    @Test
    void tp4_someDeleted_returnsFalse() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(101);
        ids.add(102);

        when(daf.deleteSegments(101)).thenReturn(true);
        when(daf.deleteSegments(102)).thenReturn(false);

        boolean result = segmentedTokenBO.deleteSegments(ids);
        assertFalse(result);

        verify(daf).deleteSegments(101);
        verify(daf).deleteSegments(102);
    }

    @Test
    void tp5_listWithNulls_skipsNulls() {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(101);
        ids.add(null);
        ids.add(102);

        when(daf.deleteSegments(101)).thenReturn(true);
        when(daf.deleteSegments(102)).thenReturn(true);

        boolean result = segmentedTokenBO.deleteSegments(ids);
        assertTrue(result);

        verify(daf).deleteSegments(101);
        verify(daf).deleteSegments(102);
        // verify that null was skipped (no call for null)
        verify(daf, times(2)).deleteSegments(anyInt());
    }
}

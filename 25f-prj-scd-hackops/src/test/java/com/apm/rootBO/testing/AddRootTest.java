package test.java.com.apm.rootBO.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.RootBO;
import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;

class AddRootTest {


    @Mock
    IDataAccessLayerFasade daf;

    @InjectMocks
    @Spy
    RootBO rootBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rootBO = spy(new RootBO(daf));
    }

    @Test
    void tp1_emptyRootList_loopSkipped_returnsTrue() {
        ArrayList<String> lemmas = new ArrayList<>();
        lemmas.add("lemma1");

        try (MockedStatic<RootUtil> mockRootUtil = mockStatic(RootUtil.class)) {
            mockRootUtil.when(() -> RootUtil.getRootList(lemmas))
                        .thenReturn(new ArrayList<>()); // empty list

            boolean result = rootBO.addRoots(lemmas);

            assertTrue(result);
            verify(daf, never()).addRoots(anyString());
        }
    }

    @Test
    void tp2_multipleRoots_insertionSucceeds() {
        ArrayList<String> lemmas = new ArrayList<>();
        lemmas.add("lemma1");
        lemmas.add("lemma2");

        try (MockedStatic<RootUtil> mockRootUtil = mockStatic(RootUtil.class)) {
            ArrayList<String> roots = new ArrayList<>();
            roots.add("root1");
            roots.add("root2");

            mockRootUtil.when(() -> RootUtil.getRootList(lemmas))
                        .thenReturn(roots);

            boolean result = rootBO.addRoots(lemmas);

            assertTrue(result);
            verify(daf).addRoots("root1");
            verify(daf).addRoots("root2");
        }
    }

}

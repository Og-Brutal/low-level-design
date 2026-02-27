package test.java.com.apm.tokenBO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.apm.bll.TokenBO;
import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.bll.utils.SegmentationUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.SegmentedTokenDTO;

class GetSegmentedTokenDetailsTest {


    @Mock
    IDataAccessLayerFasade daf;

    @InjectMocks
    @Spy
    TokenBO tokenBO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenBO = spy(new TokenBO(daf, null, null)); // lemmaBO and segmentedTokenBO not needed here
    }

    @Test
    void testGetSegmentedTokenDetails_returnsCorrectDTO() {
        String token = "running";

        try (MockedStatic<SegmentationUtil> mockSeg = mockStatic(SegmentationUtil.class);
             MockedStatic<LemmaUtil> mockLemma = mockStatic(LemmaUtil.class);
             MockedStatic<RootUtil> mockRoot = mockStatic(RootUtil.class)) {

            // Mock static utilities
            mockSeg.when(() -> SegmentationUtil.getPrefix(token)).thenReturn("run");
            mockSeg.when(() -> SegmentationUtil.getStem(token)).thenReturn("ning");

            mockLemma.when(() -> LemmaUtil.getLemma(token)).thenReturn("run");
            mockRoot.when(() -> RootUtil.getThreeLetterRoot("run")).thenReturn("r-n");

            // Mock daf.searchLemma
            when(daf.searchLemma("run")).thenReturn(101);

            SegmentedTokenDTO dto = tokenBO.getSegmentedTokenDetails(token);

            // Assertions
            assertNotNull(dto);
            assertEquals("run", dto.getPrefix());
            assertEquals("ning", dto.getStem());
            assertEquals("run", dto.getLemma());
            assertEquals("r-n", dto.getRoot());

            // Optional: verify DAO call
            verify(daf).searchLemma("run");
        }
    }
}

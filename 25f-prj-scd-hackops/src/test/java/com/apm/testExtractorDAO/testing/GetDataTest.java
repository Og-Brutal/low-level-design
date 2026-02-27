package test.java.com.apm.testExtractorDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import com.apm.dal.TextExtracter;

class GetDataTest {

	private TextExtracter fileUtil = new TextExtracter();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("TP1: Empty file → return empty string")
    void getData_EmptyFile_ReturnsEmptyString() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        String result = fileUtil.getData(emptyFile.toString());

        assertEquals("", result);
    }

 
    @Test
    @DisplayName("TP2: File does not exist → IOException → return null")
    void getData_FileNotFound_ReturnsNull() {
        String result = fileUtil.getData("/invalid/path/does-not-exist.txt");

        assertNull(result);
    }




    // Better and one more solid test for exception
    @Test
    @DisplayName("TP3/TP4/TP5: Any IOException → return null")
    void getData_AnyIOException_ReturnsNull() {
        String result = fileUtil.getData("/root/secret.txt"); // likely no permission

        assertNull(result);
    }

}

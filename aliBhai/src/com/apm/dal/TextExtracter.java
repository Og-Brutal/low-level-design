package com.apm.dal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TextExtracter implements ITextExtracter {

    @Override
    public String getData(String path) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

        } catch (IOException e) {
            System.err.println("Error reading file at: " + path);
            e.printStackTrace();
            return null;
        }

        return content.toString().trim();
    }
}

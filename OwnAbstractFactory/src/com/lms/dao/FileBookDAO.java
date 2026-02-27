package com.lms.dao;

import com.lms.dto.BookDTO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBookDAO implements IBookDAO {

    // File placed beside the src folder
    private static final String FILE_PATH = "Book";

    @Override
    public BookDTO getBook() {
        // If you only need the first book, you can return from list.get(0)
        List<BookDTO> books = readAllBooks();
        return books.isEmpty() ? null : books.get(0);
    }

    // Optional: read all books from the file
    public List<BookDTO> readAllBooks() {
        List<BookDTO> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String name = null;
            String era = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                if (line.startsWith("name:")) {
                    name = line.substring("name:".length()).trim();
                } else if (line.startsWith("era:")) {
                    era = line.substring("era:".length()).trim();
                }

                // When both fields are found, make a book object
                if (name != null && era != null) {
                    books.add(new BookDTO(name, era));
                    name = null;
                    era = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
}

package com.lms.dal;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.lms.dtl.BookDTO;

public class FileBookDAO implements IBookDAO {
    private String filePath;

    public FileBookDAO(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void addBook(BookDTO book) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public BookDTO getBookById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int bookId = Integer.parseInt(parts[0]);
                if (bookId == id) {
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	
}

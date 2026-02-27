package com.abft.dal;

import java.io.*;

public class FileBookDAO implements IBookDAO {
    private String filePath;

    public FileBookDAO(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void addBook(Book book) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(book.getId() + "," + book.getTitle() + "," + book.getAuthor());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book getBookById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int bookId = Integer.parseInt(parts[0]);
                if (bookId == id) {
                    return new Book(bookId, parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

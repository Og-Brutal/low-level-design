package PresentationLayerPL;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import BuisnessLayerBL.AuthorBo;
import BuisnessLayerBL.BookBo;
import BuisnessLayerBL.BuisnessFacade;
import DataAcessLayerDAL.AuthorDOA;
import DataAcessLayerDAL.BookDOA;
import DataAcessLayerDAL.DataAccessFacacde;
import ModelDTO.Author;
import ModelDTO.Book;

/**
 * Swing-based UI for the Arabic Prose Management System.
 */
public class ArabicProseUserInterface extends JFrame {

    private BuisnessFacade facade;

    // Author tab components
    private JTextField authorNameField;
    private JTextArea authorBioArea;
    private JTextField authorSearchField;

    // Book tab components
    private JTextField bookIdField;
    private JTextField bookTitleField;
    private JTextField bookAuthorIdField;
    private JTextField bookEraField;
    private JTextField bookAuthorNameField;
    private JTextField bookSearchField;

    public ArabicProseUserInterface() {
        // Data layer
        AuthorDOA authorDao = new AuthorDOA();
        BookDOA bookDao = new BookDOA();
        DataAccessFacacde dataFacade = new DataAccessFacacde(authorDao, bookDao);

        // Business layer
        AuthorBo authorBo = new AuthorBo(dataFacade);
        BookBo bookBo = new BookBo(dataFacade);

        // Facade
        facade = new BuisnessFacade(authorBo, bookBo);

        initUI();
    }

    private void initUI() {
        setTitle("Arabic Prose Management - UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Authors", createAuthorsPanel());
        tabs.addTab("Books", createBooksPanel());

        add(tabs);
    }

    private JPanel createAuthorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Name:"), c);
        c.gridx = 1;
        authorNameField = new JTextField(30);
        form.add(authorNameField, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Biography:"), c);
        c.gridx = 1;
        authorBioArea = new JTextArea(5, 30);
        form.add(new JScrollPane(authorBioArea), c);

        c.gridx = 0; c.gridy = 2;
        JButton addAuthorBtn = new JButton("Add Author");
        form.add(addAuthorBtn, c);

        c.gridx = 1;
        JButton updateAuthorBtn = new JButton("Update Author");
        form.add(updateAuthorBtn, c);

        c.gridx = 2;
        JButton deleteAuthorBtn = new JButton("Delete Author");
        form.add(deleteAuthorBtn, c);

        panel.add(form, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by name:"));
        authorSearchField = new JTextField(20);
        searchPanel.add(authorSearchField);
        JButton searchAuthorBtn = new JButton("Search");
        searchPanel.add(searchAuthorBtn);

        JTextArea authorResultArea = new JTextArea(8, 60);
        authorResultArea.setEditable(false);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(authorResultArea), BorderLayout.SOUTH);

        // Button Actions
        addAuthorBtn.addActionListener(e -> {
            String name = authorNameField.getText().trim();
            String bio = authorBioArea.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Author name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = facade.addAuthor(name, bio);
            JOptionPane.showMessageDialog(this, ok ? "Author added." : "Failed to add author.");
        });

        updateAuthorBtn.addActionListener(e -> {
            String oldName = JOptionPane.showInputDialog(this, "Enter existing author name to update:");
            if (oldName == null || oldName.trim().isEmpty()) return;
            String newName = authorNameField.getText().trim();
            String bio = authorBioArea.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "New name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = facade.updateAuthor(oldName.trim(), newName, bio);
            JOptionPane.showMessageDialog(this, ok ? "Author updated." : "Failed to update author.");
        });

        deleteAuthorBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter author name to delete:");
            if (name == null || name.trim().isEmpty()) return;
            boolean ok = facade.deleteAuthor(name.trim());
            JOptionPane.showMessageDialog(this, ok ? "Author deleted." : "Failed to delete author.");
        });

        searchAuthorBtn.addActionListener(e -> {
            String keyword = authorSearchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Type a name to search.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Author a = facade.retrieveAuthor(keyword);
            if (a == null) {
                authorResultArea.setText("Author not found for: " + keyword);
            } else {
                authorResultArea.setText(
                    "Author ID: " + a.getAuthorId() + "\n" +
                    "Name: " + a.getName() + "\n" +
                    "Biography: " + a.getBiography()
                );
            }
        });

        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Book ID:"), c);
        c.gridx = 1;
        bookIdField = new JTextField(10);
        form.add(bookIdField, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Title:"), c);
        c.gridx = 1;
        bookTitleField = new JTextField(30);
        form.add(bookTitleField, c);

        c.gridx = 0; c.gridy = 2;
        form.add(new JLabel("Author ID:"), c);
        c.gridx = 1;
        bookAuthorIdField = new JTextField(10);
        form.add(bookAuthorIdField, c);

        c.gridx = 0; c.gridy = 3;
        form.add(new JLabel("Era:"), c);
        c.gridx = 1;
        bookEraField = new JTextField(20);
        form.add(bookEraField, c);

        c.gridx = 0; c.gridy = 4;
        form.add(new JLabel("Author Name:"), c);
        c.gridx = 1;
        bookAuthorNameField = new JTextField(30);
        form.add(bookAuthorNameField, c);

        c.gridx = 0; c.gridy = 5;
        JButton addBookBtn = new JButton("Add Book");
        form.add(addBookBtn, c);
        c.gridx = 1;
        JButton updateBookBtn = new JButton("Update Book");
        form.add(updateBookBtn, c);
        c.gridx = 2;
        JButton deleteBookBtn = new JButton("Delete Book");
        form.add(deleteBookBtn, c);

        panel.add(form, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by title:"));
        bookSearchField = new JTextField(20);
        searchPanel.add(bookSearchField);
        JButton searchBookBtn = new JButton("Search");
        searchPanel.add(searchBookBtn);

        JTextArea bookResultArea = new JTextArea(8, 60);
        bookResultArea.setEditable(false);
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(bookResultArea), BorderLayout.SOUTH);

        addBookBtn.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText().trim());
                String title = bookTitleField.getText().trim();
                int authorId = Integer.parseInt(bookAuthorIdField.getText().trim());
                String era = bookEraField.getText().trim();
                String authorName = bookAuthorNameField.getText().trim();

                if (title.isEmpty() || authorName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Author Name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean ok = facade.addBook(bookId, title, authorId, era, authorName);
                JOptionPane.showMessageDialog(this, ok ? "Book added." : "Failed to add book.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Book ID and Author ID must be numbers.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateBookBtn.addActionListener(e -> {
            String oldTitle = JOptionPane.showInputDialog(this, "Enter existing book title to update:");
            if (oldTitle == null || oldTitle.trim().isEmpty()) return;
            try {
                int bookId = Integer.parseInt(bookIdField.getText().trim());
                String newTitle = bookTitleField.getText().trim();
                String era = bookEraField.getText().trim();
                if (newTitle.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "New title is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = facade.updateBook(oldTitle.trim(), newTitle, bookId, era);
                JOptionPane.showMessageDialog(this, ok ? "Book updated." : "Failed to update book.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Book ID must be a number.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Enter book title to delete:");
            if (title == null || title.trim().isEmpty()) return;
            boolean ok = facade.deleteBook(title.trim());
            JOptionPane.showMessageDialog(this, ok ? "Book deleted." : "Failed to delete book.");
        });

        searchBookBtn.addActionListener(e -> {
            String keyword = bookSearchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Type a title to search.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Book b = facade.retrieveBook(keyword);
            if (b == null) {
                bookResultArea.setText("No book found for: " + keyword);
            } else {
                bookResultArea.setText(
                    "Book ID: " + b.getBookid() + "\n" +
                    "Title: " + b.getTitle() + "\n" +
                    "Author ID: " + b.getAuthorid() + "\n" +
                    "Era: " + b.getEra() + "\n" +
                    "Author Name: " + b.getAuthorname()
                );
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArabicProseUserInterface ui = new ArabicProseUserInterface();
            ui.setVisible(true);
        });
    }
}

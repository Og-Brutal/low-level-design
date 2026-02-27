package BuisnessLayerBL;

import DataAcessLayerDAL.DataAccessFacacde;
import ModelDTO.Book;

public class BookBo implements IBookBo {
	DataAccessFacacde df;
	public BookBo(DataAccessFacacde df)
	{
		this.df=df;
	}
	 
	public boolean addBook(int bookId,String title,int authorId,String era,String authorname)
	{
		
		    if (title == null || title.trim().isEmpty() || authorname == null || authorname.trim().isEmpty()) {
		        System.out.println("Book title and author name cannot be empty!");
		        return false;
		    }

		    if (bookId <= 0 || authorId <= 0) {
		        System.out.println("Invalid IDs! Book ID and Author ID must be positive.");
		        return false;
		    }

		    // Check if the book already exists
		    int existingBookId = df.searchBook(title.trim());
		    if (existingBookId != -1) {
		        System.out.println("Book already exists with title: " + title);
		        return false;
		    }

		    return df.addBook(bookId, title.trim(), authorId, era, authorname.trim());
		

	}
	
	public boolean updateBook(String bookName,String newBookName,int bookId,String era)
	  { 
		   if (bookName == null || newBookName == null || bookName.trim().isEmpty() || newBookName.trim().isEmpty()) {
		        System.out.println("Book names cannot be empty!");
		        return false;
		    }

		    int foundBookId = df.searchBook(bookName.trim());
		    if (foundBookId == -1) {
		        System.out.println("Book not found: " + bookName);
		        return false;
		    }

		    if (bookName.trim().equalsIgnoreCase(newBookName.trim())) {
		        System.out.println("Old and new book names are the same. Nothing to update.");
		        return false;
		    }

		    return df.updateBook(bookName.trim(), newBookName.trim(), bookId, era);
		
	  }
	
	   public boolean deleteBook(String bookTitle)

	   {
		   if (bookTitle == null || bookTitle.trim().isEmpty()) {
		        System.out.println("Book title cannot be empty!");
		        return false;
		    }

		    int bookId = df.searchBook(bookTitle.trim());
		    if (bookId == -1) {
		        System.out.println("Book not found: " + bookTitle);
		        return false;
		    }

		    return df.deleteBook(bookTitle.trim());
	   }
	  
	   public Book retrieveBook (String keyword)
	   {
		   if (keyword == null || keyword.trim().isEmpty()) {
		        System.out.println("Keyword cannot be null or empty!");
		        return null;
		    }

		    Book book = df.retrieveBook(keyword.trim());
		    if (book == null) {
		        System.out.println("No book found for keyword: " + keyword);
		        return null;
		    }

		    return book;
	   }
	  
	   public int searchBook(String title)
	   {
		   if (title == null || title.trim().isEmpty()) {
		        System.out.println("Book title cannot be null or empty!");
		        return -1;
		    }
		    return df.searchBook(title.trim());
	   }
	  

}

package DataAcessLayerDAL;

import ModelDTO.Author;
import ModelDTO.Book;

public class DataAccessFacacde implements IDataFacade {
        IAuthorDOA author;
        IBookDOA book;
        public DataAccessFacacde(IAuthorDOA author,IBookDOA book)
        {
        	this.author=author;
        	this.book=book;
        }
        
       public boolean addAuthor(String name,String biography )
       {
    	   return author.addAuthor(name, biography);
       }
 	   public boolean updateAuthor(String oldname,String newname,String biography)
 	   {
 		   return author.updateAuthor(oldname, newname, biography);
 	   }
 	   public boolean deleteAuthor(String authorname)
 	   {
 		   return author.deleteAuthor(authorname);
 	   }
 	   public Author retrieveAuthor (String keyword)
 	   {
 		   return author.retrieveAuthor(keyword);
 	   }
 	   public int searchAuthor(String authorname)
 	   {
 		   return author.searchAuthor(authorname);
 	   }
 	   
 	  public boolean addBook(int bookId,String title,int authorId,String era,String authorname)
 	  {
 		  return book.addBook(bookId, title, authorId, era, authorname);
 	  }
	   public boolean updateBook(String bookName,String newBookName,int bookId,String era)
	   {
		   return book.updateBook(bookName, newBookName, bookId, era);
	   }
	   public boolean deleteBook(String bookTitle)
	   {
		   return book.deleteBook(bookTitle);
	   }
	   public Book retrieveBook(String keyword) {
		   return book.retrieveBook(keyword);
	   }
	   public int searchBook(String title) {
		   return book.searchBook(title);
	   }
	   
	   
}

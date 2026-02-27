package DataAcessLayerDAL;
import ModelDTO.Book;
public interface IBookDOA {
	public boolean addBook(int bookId,String title,int authorId,String era,String authorname);
	   public boolean updateBook(String bookName,String newBookName,int bookId,String era);
	   public boolean deleteBook(String bookTitle);
	   public Book retrieveBook(String keyword);
	   public int searchBook(String title);
	   
	   
}

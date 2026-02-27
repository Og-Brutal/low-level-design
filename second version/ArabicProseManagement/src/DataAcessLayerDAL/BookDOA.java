package DataAcessLayerDAL;

import ModelDTO.Book;

public class BookDOA  implements IBookDOA{
//database logic
	public boolean addBook(int bookId,String title,int authorId,String era,String authorname)
	{
		//db query
	}
	   public boolean updateBook(String bookName,String newBookName,int bookId,String era)
	   {
		   //db query
	   }
	   public boolean deleteBook(String bookTitle);
	   {
		   //db query
	   }
	   public Book retrieveBook(String keyword)
	   {
		   //db query
	   }
	   public int searchBook(String title)
	   {
		   //db query
	   }
	   
}

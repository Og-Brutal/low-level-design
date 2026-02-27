package com.abft.pl;

import com.abft.dal.*;
import com.abft.bll.*;

public class Main {
    public static void main(String[] args) {
    	IDAOFactory factory = AbstractDAOFactory.getInstance();
    	
    	IBookDAO bookDAO = factory.createBookDAO();
    	IMemberDAO memberDAO = factory.createMemberDAO();

    	IDALFacade facade = new DALFacade(bookDAO, memberDAO);

    	BookBO bookBO = new BookBO(facade);
    	MemberBO memberBO = new MemberBO(facade);

 
    	bookBO.addBook(new Book(1, "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides"));
    	memberBO.addMember(new Member(1, "Alice Smith"));

    	Book book = bookBO.getBook(1);
    	Member member = memberBO.getMember(1);

    	System.out.println("Book: " + (book != null ? book.getTitle() : "Not Present"));
    	System.out.println("Member: " + (member != null ? member.getName() : "Not Present"));

    }
}

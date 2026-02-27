package main;

import com.lms.bll.BookBO;
import com.lms.bll.FasadeBO;
import com.lms.bll.MemberBO;
import com.lms.dal.BookDAO;
import com.lms.dal.FasadeDAO;
import com.lms.dal.MemberDAO;
import com.lms.pl.Presentation;

public class Main {
	public static void main(String[] args)
	{
		BookDAO bookDao= new BookDAO();
		MemberDAO memberDao = new MemberDAO();
		FasadeDAO  fdao = new FasadeDAO(bookDao,memberDao);
		BookBO bookBo = new BookBO(fdao);
		MemberBO memberBo = new MemberBO(fdao);
		FasadeBO fbo = new FasadeBO(bookBo,memberBo);
		Presentation present = new Presentation(fbo);
	}
}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.testing.junit.Utilties;

class UtiltiesTest {
	
	Utilties utils;
	@BeforeAll
	// Run at the start
	
	
	@BeforeEach
	void reset() {
		 utils=new Utilties();
	}

	@Test
	void test1() {
		
		Assertions.assertTrue(utils.isLowerCase("hello"));
		
	}
	
	@Test
	void test3() {
		
		Assertions.assertFalse(utils.isLowerCase("Hello"));
		
	}
	@Test
	void test4() {
		
		Assertions.assertFalse(utils.isLowerCase("HELLO"));	
	}
	
	@Test
	void test5() {
		
		Assertions.assertTrue(utils.isLowerCase("@hello"));	
	}
	
	@Test
	void test6() {
		
		Assertions.assertTrue(utils.isLowerCase(""));
	}
	
	@Test
	void test7() {
		
		Assertions.assertTrue(utils.isLowerCase("aaaaaa"));	
	}
	
	@Test
	void test8() {
		
		Assertions.assertTrue(utils.isLowerCase("zzzzz"));
	}
	
	@Test
	void test9() {
		
		Assertions.assertTrue(utils.isLowerCase(null));
	}
	

}

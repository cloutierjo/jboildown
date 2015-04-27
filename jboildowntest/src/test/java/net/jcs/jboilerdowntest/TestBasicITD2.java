package net.jcs.jboilerdowntest;

import org.junit.Before;
import org.junit.Test;

public class TestBasicITD2 {

	private Test2 test2;
	@Before
	public void setUp() throws Exception {
		test2 = new Test2();
	}

	@Test
	public void callITD(){
		test2.getLol();
	}

	@Test
	public void callGetter(){
		test2.getData();
	}

}

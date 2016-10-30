package net.jcs.jboilerdowntest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestBasicClassGeneration {

	private static final String TEST_DATA = "test data";
	private static final boolean TEST_BOLEAN_DATA = true;
	private BasicTestClass basicTestClass;

	@Before
	public void setUp() throws Exception {
		basicTestClass = new BasicTestClass();
	}

	@Test
	public void callSetterOnPrivateField() {
		basicTestClass.setPrivateField(TEST_DATA);
		assertEquals(TEST_DATA, basicTestClass.getPrivateField());
	}

	@Test
	public void callSetterOnProtectedField() {
		basicTestClass.setProtectedField(TEST_BOLEAN_DATA);
		assertEquals(TEST_BOLEAN_DATA, basicTestClass.isProtectedField());
		assertEquals(TEST_BOLEAN_DATA, basicTestClass.protectedField);
	}

	@Test
	public void callSetterOnPublicField() {
		basicTestClass.setPublicField(TEST_BOLEAN_DATA);
		assertEquals(TEST_BOLEAN_DATA, basicTestClass.isPublicField());
		assertEquals(TEST_BOLEAN_DATA, basicTestClass.publicField);
	}

	@Test
	public void callSetterOnGenericField() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(TEST_DATA);
		basicTestClass.setListField(list);
		assertEquals(1, basicTestClass.getListField().size());
		assertEquals(TEST_DATA, basicTestClass.getListField().get(0));
	}

	@Test(expected = Exception.class)
	@Ignore("error to fix later")
	public void callSetterOnGenericFieldWithWrongType() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(TEST_DATA);
		basicTestClass.setListField(list);

		List<Integer> list2 = basicTestClass.getListField();
		list2.add(2);
		assertEquals(2, basicTestClass.getListField().size());
		assertEquals(TEST_DATA, basicTestClass.getListField().get(0));
		assertEquals(2, basicTestClass.getListField().get(1));
	}

}

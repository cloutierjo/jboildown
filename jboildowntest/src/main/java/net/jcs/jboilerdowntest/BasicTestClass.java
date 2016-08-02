package net.jcs.jboilerdowntest;

import java.util.List;

import net.jcs.jboildown.annotation.Getter;
import net.jcs.jboildown.annotation.Setter;

@Getter
@Setter
public class BasicTestClass {

	/**
	 * the data value
	 */
	private String privateField;
	protected boolean protectedField;
	public Boolean publicField;
	private List<String> listField;
}

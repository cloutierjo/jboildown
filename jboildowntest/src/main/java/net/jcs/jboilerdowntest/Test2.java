package net.jcs.jboilerdowntest;

import java.util.List;

import net.jcs.jboildown.annotation.Getter;
import net.jcs.jboildown.annotation.Setter;

@Getter
public class Test2 {

	/**
	 * the data value
	 */
	private String data;
	@Setter  
	private boolean ips; 
	private Boolean blas; 
	private List<String> lol;
}

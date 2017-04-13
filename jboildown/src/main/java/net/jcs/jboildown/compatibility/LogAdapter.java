package net.jcs.jboildown.compatibility;

public interface LogAdapter {

	void info(String string);

	void error(String string, Exception e);

}

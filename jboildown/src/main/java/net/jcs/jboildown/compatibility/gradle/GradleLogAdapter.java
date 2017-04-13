package net.jcs.jboildown.compatibility.gradle;

import net.jcs.jboildown.compatibility.LogAdapter;

public class GradleLogAdapter implements LogAdapter {

	public GradleLogAdapter() {
	}

	@Override
	public void info(String content) {
		System.out.println(content);
	}

	@Override
	public void error(String content, Exception error) {
		System.err.println(content);
		error.printStackTrace(System.err);
	}

}

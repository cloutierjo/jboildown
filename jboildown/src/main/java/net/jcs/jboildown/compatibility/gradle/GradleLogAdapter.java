package net.jcs.jboildown.compatibility.gradle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcs.jboildown.compatibility.LogAdapter;

public class GradleLogAdapter implements LogAdapter {
	Logger LOG = LoggerFactory.getLogger("jboildown");

	@Override
	public void info(String content) {
		LOG.info(content);
	}

	@Override
	public void error(String content, Exception error) {
		LOG.error(content, error);
	}

}

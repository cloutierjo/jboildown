package net.jcs.jboildown.compatibility.maven;

import org.apache.maven.plugin.AbstractMojo;

import net.jcs.jboildown.compatibility.LogAdapter;

public class MavenLogAdapter implements LogAdapter {

	AbstractMojo mojo;

	public MavenLogAdapter(AbstractMojo mojo) {
		this.mojo = mojo;
	}

	@Override
	public void info(String content) {
		mojo.getLog().info(content);
	}

	@Override
	public void error(String content, Exception error) {
		mojo.getLog().error(content, error);
	}

}

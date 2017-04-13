package net.jcs.jboildown.compatibility.maven;

import java.io.File;
import java.util.List;

import net.jcs.jboildown.MavenGenerator;
import net.jcs.jboildown.compatibility.BuildAdapter;
import net.jcs.jboildown.compatibility.LogAdapter;

public class MavenBuildAdapter implements BuildAdapter {

	private MavenGenerator mojo;
	private LogAdapter logAdapter;
	private File outputDirectory;

	public MavenBuildAdapter(MavenGenerator mojo, File outputDirectory) {
		this.mojo = mojo;
		this.outputDirectory = outputDirectory;
		logAdapter = new MavenLogAdapter(mojo);
	}

	@Override
	public LogAdapter getLog() {
		return logAdapter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCompileSourceRoots() {
		return mojo.getProject().getCompileSourceRoots();
	}

	@Override
	public void refresh(File file) {
		mojo.getBuildContext().refresh(file);
	}

	@Override
	public File getOutputDirectory() {
		return outputDirectory;
	}

}

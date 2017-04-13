package net.jcs.jboildown.compatibility.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import net.jcs.jboildown.compatibility.BuildAdapter;
import net.jcs.jboildown.compatibility.LogAdapter;

public class GradleBuildAdapter implements BuildAdapter {

	private Project project;
	private LogAdapter logAdapter;
	private JboildownExtension extension;

	public GradleBuildAdapter(Project project, JboildownExtension extension) {
		this.project = project;
		this.extension = extension;
		logAdapter = new GradleLogAdapter();
	}

	@Override
	public LogAdapter getLog() {
		return logAdapter;
	}

	@Override
	public List<String> getCompileSourceRoots() {
		List<String> sourceRoots = new ArrayList<>();
		SourceSetContainer sources= (SourceSetContainer) project.getProperties().get("sourceSets");
		for (SourceSet sourceSet : sources) {
			Set<File> srcDirs = sourceSet.getJava().getSrcDirs();
			for (File srcDir : srcDirs) {
				sourceRoots.add(srcDir.getAbsolutePath());
			}
		}
		return sourceRoots;
	}

	@Override
	public void refresh(File file) {
		// empty action at the moment
	}

	@Override
	public File getOutputDirectory() {
		return new File(project.getBuildDir(), extension.getOutputDirectory());
	}

}

package net.jcs.jboildown;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

import net.jcs.jboildown.compatibility.gradle.GenerateTask;
import net.jcs.jboildown.compatibility.gradle.JboildownExtension;

public class GradleGenerator implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getExtensions().create("jboildownExt", JboildownExtension.class);
		project.getTasks().create("generate", GenerateTask.class);

		SourceSetContainer sources = (SourceSetContainer) project.getProperties().get("sourceSets");
		sources.getByName("main").getJava().srcDir(new File(project.getBuildDir(), "jboildownSrc"));
	}
}

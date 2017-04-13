package net.jcs.jboildown.compatibility.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import net.jcs.jboildown.GetterSetterGenerator;
import net.jcs.jboildown.compatibility.BuildAdapter;

public class GenerateTask extends DefaultTask {

	@TaskAction
	public void generate() {
		JboildownExtension extension = getProject().getExtensions().findByType(JboildownExtension.class);
		if (extension == null) {
			extension = new JboildownExtension();
		}

		BuildAdapter build = new GradleBuildAdapter(getProject(), extension);
		build.getLog().info("start generator");

		GetterSetterGenerator getterSetterGenerator = new GetterSetterGenerator(build);
		getterSetterGenerator.execute();
		build.getLog().info("generation completed");
		// SourceSetContainer sources = (SourceSetContainer) getProject().getProperties().get("sourceSets");
		// sources.getByName("main").getJava().srcDir(build.getOutputDirectory());
	}
}

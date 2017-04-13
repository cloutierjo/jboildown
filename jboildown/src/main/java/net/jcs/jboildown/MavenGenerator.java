package net.jcs.jboildown;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

import net.jcs.jboildown.compatibility.maven.MavenBuildAdapter;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class MavenGenerator extends AbstractMojo {

	/**
	 * @parameter property="project"
	 * @required
	 * @readonly
	 * @since 1.0
	 */
	private MavenProject project;

	/** @component */
	private BuildContext buildContext;

	/**
	 * @parameter default-value="target/generated-sources/"
	 * @required
	 */
	private File outputDirectory;

	@Override
	public void execute() {
		getLog().info("start generator");
		if (hasDelta()) {
			getLog().info("File change, run generator");
			GetterSetterGenerator getterSetterGenerator = new GetterSetterGenerator(new MavenBuildAdapter(this, outputDirectory));
			getterSetterGenerator.execute();
			getLog().info("generation completed");
		} else {
			getLog().info("no file changed");
		}
		getProject().addCompileSourceRoot(outputDirectory.getAbsolutePath());
	}

	private boolean hasDelta() {
		for (Object srcPath : getProject().getCompileSourceRoots()) {
			getLog().info((String) srcPath);
			if (!((String) srcPath).contains("target") && getBuildContext().hasDelta(new File((String) srcPath))) {
				return true;
			}
		}
		return false;
	}

	public MavenProject getProject() {
		return project;
	}

	public BuildContext getBuildContext() {
		return buildContext;
	}
}

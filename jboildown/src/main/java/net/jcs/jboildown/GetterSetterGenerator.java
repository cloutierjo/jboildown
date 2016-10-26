package net.jcs.jboildown;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaPackage;

import net.jcs.jboildown.annotation.Getter;
import net.jcs.jboildown.annotation.Setter;
import net.jcs.jboildown.data.Data;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class GetterSetterGenerator extends AbstractMojo {

	/**
	 * @parameter property="project"
	 * @required
	 * @readonly
	 * @since 1.0
	 */
	MavenProject project;

	/** @component */
	private BuildContext buildContext;

	/**
	 * @parameter default-value="target/generated-sources/"
	 * @required
	 */
	File outputDirectory;

	Map<String, List<FieldObserver>> fieldObservers = new HashMap<>();
	Map<String, List<ClassObserver>> classObservers = new HashMap<>();
	List<DataExtractor> dataExtrators = new ArrayList<>();

	@Override
	public void execute() {
		getLog().info("start generator");
		if (hasDelta()) {
			getLog().info("File change, run generator");
			Data data = new Data();
			addFieldObserver(new GetterExtractor(), Getter.class);
			addFieldObserver(new SetterExtractor(), Setter.class);
			doParsing(data);
			getLog().info("generation completed");
		} else {
			getLog().info("no file changed");
		}
		project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
	}

	private void doParsing(Data data) {
		try {
			JavaProjectBuilder builder = new JavaProjectBuilder();

			for (Object srcPath : project.getCompileSourceRoots()) {
				builder.addSourceTree(new File((String) srcPath));
			}

			for (JavaClass javaClass : builder.getClasses()) {
				boolean doGenerate = false;
				data.clear();
				for (DataExtractor dataExtractor : dataExtrators) {
					dataExtractor.clear();
				}
				doGenerate |= signalClass(javaClass);
				for (JavaField javaField : javaClass.getFields()) {
					doGenerate |= signalField(javaField, builder);
				}

				if (doGenerate) {
					String packageName = javaClass.getPackageName();
					File pd = new File(outputDirectory, packageName.replaceAll("\\.", "/"));
					pd.mkdirs();

					File file = new File(pd, javaClass.getName() + "_jbd.aj");
					try (FileWriter out = new FileWriter(file)) {

						data.setClassName(javaClass.getName());
						data.setPackageName(javaClass.getPackageName());

						Velocity.setProperty("input.encoding", "UTF-8");
						Velocity.setProperty("output.encoding", "UTF-8");
						Velocity.setProperty("resource.loader", "class");
						Velocity.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
						Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

						Velocity.init();
						VelocityContext context = new VelocityContext();
						for (DataExtractor dataExtractor : dataExtrators) {
							context.put("name", dataExtractor.getClass().getSimpleName());
							context.put(dataExtractor.getClass().getSimpleName(), dataExtractor.getData());
							data.addAllImport(dataExtractor.getImports());
						}
						context.put("baseData", data);

						Velocity.getTemplate("template/main.vm").merge(context, out);

						out.flush();
					}
					buildContext.refresh(file);
					getLog().info(file.getCanonicalPath() + " generated");
				}
			}
		} catch (Exception e) {
			getLog().error("General error", e);
		}
	}

	public void addFieldObserver(FieldObserver fieldObserver, Class<?> annotation) {
		String annotationCanonicalName = annotation.getCanonicalName();
		List<FieldObserver> fieldObserversList = fieldObservers.get(annotationCanonicalName);
		if (fieldObserversList == null) {
			fieldObserversList = new ArrayList<>();
			fieldObservers.put(annotationCanonicalName, fieldObserversList);
		}
		fieldObserversList.add(fieldObserver);
		dataExtrators.add(fieldObserver);
	}

	public void addClassObserver(ClassObserver classObserver, Class<?> annotation) {
		String annotationCanonicalName = annotation.getCanonicalName();
		List<ClassObserver> classObserversList = classObservers.get(annotationCanonicalName);
		if (classObserversList == null) {
			classObserversList = new ArrayList<>();
			classObservers.put(annotationCanonicalName, classObserversList);
		}
		classObserversList.add(classObserver);
		dataExtrators.add(classObserver);
	}

	private boolean signalClass(JavaClass javaClass) {
		boolean doGenerate = false;
		for (JavaAnnotation javaAnnotation : javaClass.getAnnotations()) {
			List<ClassObserver> classAnnotationObservers = classObservers.get(javaAnnotation.getType().getCanonicalName());
			if (classAnnotationObservers != null) {
				for (ClassObserver classAnnotationObserver : classAnnotationObservers) {
					doGenerate |= classAnnotationObserver.notify(javaClass);
				}
			}
		}
		for (JavaAnnotation javaAnnotation : javaClass.getPackage().getAnnotations()) {
			List<ClassObserver> classAnnotationObservers = classObservers.get(javaAnnotation.getType().getCanonicalName());
			if (classAnnotationObservers != null) {
				for (ClassObserver classAnnotationObserver : classAnnotationObservers) {
					doGenerate |= classAnnotationObserver.notify(javaClass);
				}
			}
		}
		return doGenerate;
	}

	private boolean signalField(JavaField javaField, JavaProjectBuilder builder) {
		boolean doGenerate = false;
		for (JavaAnnotation javaAnnotation : javaField.getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getCanonicalName());
			if (fieldAnnotationObservers != null) {
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		for (JavaAnnotation javaAnnotation : javaField.getDeclaringClass()
				.getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getCanonicalName());
			if (fieldAnnotationObservers != null) {
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		JavaPackage packageByName = builder.getPackageByName(javaField.getDeclaringClass().getPackage().getName());
		for (JavaAnnotation javaAnnotation : packageByName.getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getFullyQualifiedName());
			if (fieldAnnotationObservers != null) {
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		return doGenerate;
	}

	private boolean hasDelta() {
		for (Object srcPath : project.getCompileSourceRoots()) {
			getLog().info((String) srcPath);
			if (!((String) srcPath).contains("target")) {
				if (buildContext.hasDelta(new File((String) srcPath))) {
					return true;
				}
			}
		}
		return false;
	}
}

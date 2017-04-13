package net.jcs.jboildown;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaPackage;

import net.jcs.jboildown.annotation.Getter;
import net.jcs.jboildown.annotation.Setter;
import net.jcs.jboildown.compatibility.BuildAdapter;
import net.jcs.jboildown.data.Data;

public class GetterSetterGenerator {

	private BuildAdapter build;

	private Map<String, List<FieldObserver>> fieldObservers = new HashMap<>();
	private Map<String, List<ClassObserver>> classObservers = new HashMap<>();
	private List<DataExtractor> dataExtrators = new ArrayList<>();

	public GetterSetterGenerator(BuildAdapter build) {
		this.build = build;
	}

	public void execute() {
		Data data = new Data();
		addFieldObserver(new GetterExtractor(), Getter.class);
		addFieldObserver(new SetterExtractor(), Setter.class);
		doParsing(data);
	}

	private void doParsing(Data data) {
		try {
			JavaProjectBuilder builder = new JavaProjectBuilder();

			for (String srcPath : build.getCompileSourceRoots()) {
				builder.addSourceTree(new File(srcPath));
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
					File pd = new File(build.getOutputDirectory(), packageName.replaceAll("\\.", "/"));
					if (!pd.exists() && !pd.mkdirs()) {
						throw new Exception("Can't create output directory");
					}

					File file = new File(pd, javaClass.getName() + "_jbd.aj");
					try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {

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
					build.refresh(file);
					build.getLog().info(file.getCanonicalPath() + " generated");
				}
			}
		} catch (Exception e) {
			build.getLog().error("General error", e);
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
}

package net.jcs.jboildown;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcs.jboildown.annotation.Getter;
import net.jcs.jboildown.annotation.Setter;
import net.jcs.jboildown.data.Data;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class GetterSetterGenerator extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 * @since 1.0
	 */
	MavenProject project;

	/**
	 * @parameter default-value="target/generated-sources/"
	 * @required
	 */
	File outputDirectory;
	

	Map<String, List<FieldObserver>> fieldObservers = new HashMap<String, List<FieldObserver>>();
	Map<String, List<ClassObserver>> classObservers = new HashMap<String, List<ClassObserver>>();

	@Override
	public void execute() {
		Data data = new Data();
		addFieldObserver(new GSetterObserver(data.getters), Getter.class); 
		addFieldObserver(new GSetterObserver(data.setters), Setter.class); 
		doParsing(data);
	}

	private void doParsing(Data data) {
		try {
			JavaProjectBuilder builder = new JavaProjectBuilder();
			boolean doGenerate = false;
			
			for (Object srcPath : project.getCompileSourceRoots()) {
				builder.addSourceTree(new File((String)srcPath));
			}
			
			for (JavaClass javaClass : builder.getClasses()) {
				doGenerate |= signalClass(javaClass);
				for (JavaField javaField : javaClass.getFields()) {
					doGenerate |= signalField(javaField);
				}

				if(doGenerate){
					String packageName = "net.jcs.jboilerdowntest";
					File pd = new File(outputDirectory, packageName.replaceAll("\\.", "/"));
					pd.mkdirs();

					FileWriter out = new FileWriter(new File(pd, javaClass.getName()+"_jbd.aj"));
					
					data.className = javaClass.getName();
					data.packageName = javaClass.getPackageName();
					
					Velocity.setProperty("input.encoding", "UTF-8");
					Velocity.setProperty("output.encoding", "UTF-8");
					Velocity.setProperty("resource.loader", "class");
					Velocity.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
					Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//					Velocity.setProperty("runtime.introspector.uberspect", "org.apache.velocity.util.introspection.UberspectImpl, org.apache.velocity.util.introspection.UberspectPublicFields");
					
					Velocity.init();
					VelocityContext context = new VelocityContext();
					context.put("data", data);
					 
					Velocity.getTemplate("template/main.vm").merge(context, out);

					out.flush();
					out.close();
					
				}
			}
			
			
			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
		} catch (Exception e) {
			getLog().error("General error", e);
		}
	}
	
	public void addFieldObserver(FieldObserver fieldObserver, Class<?> annotation){
		String annotationCanonicalName = annotation.getCanonicalName();
		List<FieldObserver> fieldObserversList = fieldObservers.get(annotationCanonicalName);
		if(fieldObserversList==null){
			fieldObserversList = new ArrayList<FieldObserver>();
			fieldObservers.put(annotationCanonicalName, fieldObserversList);
		}
		fieldObserversList.add(fieldObserver);
	}
	
	public void addClassObserver(ClassObserver classObserver, Class<?> annotation){
		String annotationCanonicalName = annotation.getCanonicalName();
		List<ClassObserver> classObserversList = classObservers.get(annotationCanonicalName);
		if(classObserversList==null){
			classObserversList = new ArrayList<ClassObserver>();
			classObservers.put(annotationCanonicalName, classObserversList);
		}
		classObserversList.add(classObserver);
	}

	private boolean signalClass(JavaClass javaClass) {
		boolean doGenerate = false;
		for (JavaAnnotation javaAnnotation : javaClass.getAnnotations()) {
			List<ClassObserver> classAnnotationObservers = classObservers.get(javaAnnotation.getType().getCanonicalName());
			if(classAnnotationObservers!=null){
				for (ClassObserver classAnnotationObserver : classAnnotationObservers) {
					doGenerate |= classAnnotationObserver.notify(javaClass);
				}
			}
		}
		for (JavaAnnotation javaAnnotation : javaClass.getPackage().getAnnotations()) {
			List<ClassObserver> classAnnotationObservers = classObservers.get(javaAnnotation.getType().getCanonicalName());
			if(classAnnotationObservers!=null){
				for (ClassObserver classAnnotationObserver : classAnnotationObservers) {
					doGenerate |= classAnnotationObserver.notify(javaClass);
				}
			}
		}
		return doGenerate;
	}

	private boolean signalField(JavaField javaField) {
		boolean doGenerate = false;
		for (JavaAnnotation javaAnnotation : javaField.getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getCanonicalName());
			if(fieldAnnotationObservers!=null){
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		for (JavaAnnotation javaAnnotation : javaField.getDeclaringClass().getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getCanonicalName());
			if(fieldAnnotationObservers!=null){
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		for (JavaAnnotation javaAnnotation : javaField.getDeclaringClass().getPackage().getAnnotations()) {
			List<FieldObserver> fieldAnnotationObservers = fieldObservers.get(javaAnnotation.getType().getCanonicalName());
			if(fieldAnnotationObservers!=null){
				for (FieldObserver fieldAnnotationObserver : fieldAnnotationObservers) {
					doGenerate |= fieldAnnotationObserver.notify(javaField);
				}
			}
		}
		return doGenerate;
	}
}

package net.jcs.jboildown;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassObserver extends DataExtractor {

	boolean notify(JavaClass javaClass);

}

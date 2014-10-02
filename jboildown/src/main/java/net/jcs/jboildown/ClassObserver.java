package net.jcs.jboildown;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassObserver {

	boolean notify(JavaClass javaClass);

}

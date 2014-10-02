package net.jcs.jboildown;

import com.thoughtworks.qdox.model.JavaField;

public interface FieldObserver {

	boolean notify(JavaField javaField);

}

package net.jcs.jboildown;

import com.thoughtworks.qdox.model.JavaField;

public interface FieldObserver extends DataExtractor {

	boolean notify(JavaField javaField);

}

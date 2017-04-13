package net.jcs.jboildown.compatibility;

import java.io.File;
import java.util.List;

public interface BuildAdapter {

	LogAdapter getLog();

	List<String> getCompileSourceRoots();

	void refresh(File file);

	File getOutputDirectory();

}

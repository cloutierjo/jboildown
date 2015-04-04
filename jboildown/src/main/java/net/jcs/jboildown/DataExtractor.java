package net.jcs.jboildown;

import java.util.Collection;

public interface DataExtractor {

	public Collection<String> getImports();

	public Object getData();

	public void clear();
}

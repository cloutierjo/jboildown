package net.jcs.jboildown.data;

import java.util.Collection;
import java.util.HashSet;

public class Data {
	private String packageName;
	private String className;
	private Collection<String> imports;

	public Data() {
		imports = new HashSet<String>();
	}

	public String getPackageName() {
		return packageName;
	}

	public String getClassName() {
		return className;
	}

	public void clear() {
		packageName = "";
		className = "";
		imports.clear();
	}

	public Collection<String> getImports() {
		return imports;
	}

	public void setImports(Collection<String> imports) {
		this.imports = imports;
	}

	public boolean addImport(String e) {
		return imports.add(e);
	}

	public boolean addAllImport(Collection<? extends String> c) {
		return imports.addAll(c);
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
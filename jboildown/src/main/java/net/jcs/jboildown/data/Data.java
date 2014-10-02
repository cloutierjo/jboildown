package net.jcs.jboildown.data;

import java.util.HashSet;
import java.util.Set;

public class Data {
	public String packageName;
	public String className;
	public Set<GSetterData> getters = new HashSet<GSetterData>();
	public Set<GSetterData> setters = new HashSet<GSetterData>();

	public String getPackageName() {
		return packageName;
	}
	public String getClassName() {
		return className;
	}
	public Set<GSetterData> getGetters() {
		return getters;
	}
	
	public Set<GSetterData> getSetters() {
		return setters;
	}
	
}
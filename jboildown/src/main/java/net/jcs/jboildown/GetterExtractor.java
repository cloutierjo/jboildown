package net.jcs.jboildown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.jcs.jboildown.data.GSetterData;

import com.thoughtworks.qdox.model.JavaField;

public class GetterExtractor implements FieldObserver {

	private Collection<GSetterData> datas;
	private Collection<String> imports;

	public GetterExtractor() {
		datas = new ArrayList<GSetterData>();
		imports = new ArrayList<String>();
	}

	@Override
	public boolean notify(JavaField javaField) {
		List<String> modifiers = javaField.getModifiers();
		if (modifiers.contains("static")) {
			return false;
		}
		datas.add(new GSetterData(javaField));
		if (!javaField.getType().isPrimitive()
				&& !"java.lang".equals(javaField.getType().getPackageName())) {
			imports.add(javaField.getType().getCanonicalName());
		}

		return true;
	}

	@Override
	public Collection<String> getImports() {
		return imports;
	}

	@Override
	public Object getData() {
		return datas;
	}

	@Override
	public void clear() {
		datas.clear();
		imports.clear();
	}

}

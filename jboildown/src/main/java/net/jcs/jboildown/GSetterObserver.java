package net.jcs.jboildown;

import java.util.Collection;
import java.util.List;

import net.jcs.jboildown.data.GSetterData;

import com.thoughtworks.qdox.model.JavaField;

public class GSetterObserver implements FieldObserver {

	private Collection<GSetterData> datas;

	public GSetterObserver(Collection<GSetterData> datas) {
		this.datas = datas;
	}

	@Override
	public boolean notify(JavaField javaField) {
		datas.add(new GSetterData(javaField));
		 
		return true;
	}

}

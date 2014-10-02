package net.jcs.jboildown.data;

import com.thoughtworks.qdox.model.JavaField;

public class GSetterData {
	public String typeName;
	public String fieldName;
	private String javadoc;
	
	public GSetterData(JavaField javaField) {
		typeName = javaField.getType().getName();
		fieldName = javaField.getName();
		javadoc = javaField.getComment();
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldNamePascalCase() {
		return formatFieldNameToMethodSufix(fieldName);
	}
	public String getTypeName() {
		return typeName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getJavadoc() {
		return javadoc;
	}
	
	static String formatFieldNameToMethodSufix(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GSetterData))
			return false;
		GSetterData other = (GSetterData) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}
	
}
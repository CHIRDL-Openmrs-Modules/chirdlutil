package org.openmrs.module.chirdlutil.datatype;

import org.apache.commons.lang.StringUtils;
import org.openmrs.customdatatype.SerializingCustomDatatype;

/**
 * Datatype for integer, represented by java.lang.Integer
 * 
 * @author Steve McKee
 */
public class IntegerDatatype extends SerializingCustomDatatype<Integer> {

	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#serialize(java.lang.Object)
	 */
	@Override
	public String serialize(Integer typedValue) {
		if (typedValue == null) {
			return null;
		}
		
		return typedValue.toString();
	}

	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#deserialize(java.lang.String)
	 */
	@Override
	public Integer deserialize(String serializedValue) {
		if (StringUtils.isEmpty(serializedValue)) {
			return null;
		}
		
		return Integer.valueOf(serializedValue);
	}
	
}

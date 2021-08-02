package org.openmrs.module.chirdlutil.util;

/**
 * Bean containing the information provided for the flowsheet report file.
 * 
 * @author Steve McKee
 */
public class FlowsheetDescriptor {
	
	private String conceptName = null;
	private String conceptValueType = null;
	private String conceptDescription = null;
	private String code = null;
	private String display = null;
	
	/**
	 * Constructor Method
	 * Create a ConceptDescriptor with name, concept class, data type, description, parent concept, units, concept Id
	 * @param conceptName concept name
	 * @param conceptValueType the concept value type
	 * @param conceptDescription concept description
	 * @param description description of this concept
	 * @param code The flowsheet code
	 * @param units measurement unit
	 * @param display the flowsheet display value
	 */
	public FlowsheetDescriptor(
			String conceptName, String conceptValueType, String conceptDescription, String code, String display) {
		this.conceptName = conceptName;
		this.conceptValueType = conceptValueType;
		this.conceptDescription = conceptDescription;
		this.code = code;
		this.display = display;
	}
	
	/**
	 * Constructor method
	 */
	public FlowsheetDescriptor() {
		// Intentionally left empty.
	}
	
	/**
	 * @return the conceptName
	 */
	public String getConceptName() {
		return this.conceptName;
	}
	
	/**
	 * @param conceptName the conceptName to set
	 */
	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	/**
	 * @return the conceptValueType
	 */
	public String getConceptValueType() {
		return this.conceptValueType;
	}
	
	/**
	 * @param conceptValueType the conceptValueType to set
	 */
	public void setConceptValueType(String conceptValueType) {
		this.conceptValueType = conceptValueType;
	}
	
	/**
	 * @return the conceptDescription
	 */
	public String getConceptDescription() {
		return this.conceptDescription;
	}
	
	/**
	 * @param conceptDescription the conceptDescription to set
	 */
	public void setConceptDescription(String conceptDescription) {
		this.conceptDescription = conceptDescription;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return this.code;
	}
	
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the display
	 */
	public String getDisplay() {
		return this.display;
	}
	
	/**
	 * @param display the display to set
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.code == null) ? 0 : this.code.hashCode());
		result = prime * result + ((this.conceptDescription == null) ? 0 : this.conceptDescription.hashCode());
		result = prime * result + ((this.conceptName == null) ? 0 : this.conceptName.hashCode());
		result = prime * result + ((this.conceptValueType == null) ? 0 : this.conceptValueType.hashCode());
		result = prime * result + ((this.display == null) ? 0 : this.display.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowsheetDescriptor other = (FlowsheetDescriptor) obj;
		if (this.code == null) {
			if (other.code != null)
				return false;
		} else if (!this.code.equals(other.code))
			return false;
		if (this.conceptDescription == null) {
			if (other.conceptDescription != null)
				return false;
		} else if (!this.conceptDescription.equals(other.conceptDescription))
			return false;
		if (this.conceptName == null) {
			if (other.conceptName != null)
				return false;
		} else if (!this.conceptName.equals(other.conceptName))
			return false;
		if (this.conceptValueType == null) {
			if (other.conceptValueType != null)
				return false;
		} else if (!this.conceptValueType.equals(other.conceptValueType))
			return false;
		if (this.display == null) {
			if (other.display != null)
				return false;
		} else if (!this.display.equals(other.display))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FlowsheetDescriptor [conceptName=" + this.conceptName + ", conceptValueType=" + this.conceptValueType
		        + ", conceptDescription=" + this.conceptDescription + ", code=" + this.code + ", display=" + this.display + "]";
	}
}

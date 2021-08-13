package org.openmrs.module.chirdlutil.tools;

import java.io.File;

/**
 * Class to hold arguments for the CreateFlowsheetConceptMappingTool.
 * 
 * @author Steve McKee
 */
public class FlowsheetConceptMappingArgs {
	private File input;
	private File output;
	private String location;
	
	/**
	 * @return the input
	 */
	public File getInput() {
		return this.input;
	}
	
	/**
	 * @param input the input to set
	 */
	public void setInput(File input) {
		this.input = input;
	}
	
	/**
	 * @return the output
	 */
	public File getOutput() {
		return this.output;
	}
	
	/**
	 * @param output the output to set
	 */
	public void setOutput(File output) {
		this.output = output;
	}
	
	/**
	 * @return the location
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.input == null) ? 0 : this.input.hashCode());
		result = prime * result + ((this.location == null) ? 0 : this.location.hashCode());
		result = prime * result + ((this.output == null) ? 0 : this.output.hashCode());
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
		FlowsheetConceptMappingArgs other = (FlowsheetConceptMappingArgs) obj;
		if (this.input == null) {
			if (other.input != null)
				return false;
		} else if (!this.input.equals(other.input))
			return false;
		if (this.location == null) {
			if (other.location != null)
				return false;
		} else if (!this.location.equals(other.location))
			return false;
		if (this.output == null) {
			if (other.output != null)
				return false;
		} else if (!this.output.equals(other.output))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FlowsheetConceptMappingArgs [input=" + this.input + ", output=" + this.output + ", location=" 
				+ this.location + ", displayConceptSource=" + "]";
	}
}

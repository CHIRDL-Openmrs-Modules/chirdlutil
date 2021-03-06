/**
 * 
 */
package org.openmrs.module.chirdlutil;

import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;

/**
 * @author tmdugan
 * 
 */
public class SSNValidator implements IdentifierValidator
{
	private static final String ALLOWED_CHARS = "0123456789-";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.patient.IdentifierValidator#getAllowedCharacters()
	 */
	public String getAllowedCharacters()
	{
		return ALLOWED_CHARS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.patient.IdentifierValidator#getName()
	 */
	public String getName()
	{
		return "SSN validation Algorithm";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.patient.IdentifierValidator#getValidIdentifier(java.lang.String)
	 */
	public String getValidIdentifier(String undecoratedIdentifier)
			throws UnallowedIdentifierException
	{
		checkAllowedIdentifier(undecoratedIdentifier);

		return undecoratedIdentifier;
	}

	protected void checkAllowedIdentifier(String undecoratedIdentifier)
			throws UnallowedIdentifierException
	{
		if (undecoratedIdentifier == null)
			throw new UnallowedIdentifierException(
					"Identifier can not be null.");
		if (undecoratedIdentifier.length() == 0)
			throw new UnallowedIdentifierException(
					"Identifier must contain at least one character.");
		if (undecoratedIdentifier.contains(" "))
			throw new UnallowedIdentifierException(
					"Identifier may not contain white space.");
		for (int i = 0; i < undecoratedIdentifier.length(); i++)
		{
			if (getAllowedCharacters().indexOf(undecoratedIdentifier.charAt(i)) == -1)
				throw new UnallowedIdentifierException("\""
						+ undecoratedIdentifier.charAt(i)
						+ "\" is an invalid character.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.patient.IdentifierValidator#isValid(java.lang.String)
	 */
	public boolean isValid(String identifier)
			throws UnallowedIdentifierException
	{
		checkAllowedIdentifier(identifier);

		String identNoDashes = identifier.replaceAll("-", "");
		
		//ssn must be exactly 9 digits long
		if(identNoDashes.length()!=9){
			return false;
		}
		
		//ssn cannot be all 9's

		if (identNoDashes  == null||identNoDashes.length()==0
				|| identNoDashes.equals("000000000")
				|| identNoDashes.equals("111111111")
				|| identNoDashes.equals("222222222")
				|| identNoDashes.equals("333333333")
				|| identNoDashes.equals("444444444")
				|| identNoDashes.equals("555555555")
				|| identNoDashes.equals("666666666")
				|| identNoDashes.equals("777777777")
				|| identNoDashes.equals("888888888")
				|| identNoDashes.equals("999999999")){
			return false;
		}
		
		return true;

	}

}

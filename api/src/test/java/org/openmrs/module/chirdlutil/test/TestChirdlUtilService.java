package org.openmrs.module.chirdlutil.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

public class TestChirdlUtilService extends BaseModuleContextSensitiveTest
{
	/**
	 * Make sure that all methods in the service layer have the @Authorized annotation
	 */
	@Test
	@SkipBaseSetup
	public void test_checkForAuthorizedAnnotations()
	{
		Method[] methods = ChirdlUtilService.class.getDeclaredMethods(); // Use reflection to get a list of all methods (including public, private, etc.)
		
		for(Method method : methods)
		{
			if(Modifier.isPublic(method.getModifiers()))
			{
				Authorized authorized = method.getAnnotation(Authorized.class);
				Assert.assertNotNull("Service methods must include the Authorized annotation. Authorized annotation not found on method " + method.getName(), authorized);
			}
		}
	}
}

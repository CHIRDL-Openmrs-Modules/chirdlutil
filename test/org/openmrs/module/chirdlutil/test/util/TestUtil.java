package org.openmrs.module.chirdlutil.test.util;

import org.openmrs.module.chirdlutil.util.Util;
import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;

/**
 * @author davely
 */
public class TestUtil {

	@Test
	public void unitTestGetAgeInUnits()
	{
		// 1. Test case - Person is born on leap day, and comes in on February 28th of non-leap year
		// Person is 1 year old because they are 365 days old, 
		// but 1 day short if you go by calendar date
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12, 52, 365);
		
		// 2. Test case - DOB is after the current date
		// NOTE: Joda-Time returns -1 for days old, but code was added added to the getAgeInUnits() method 
		// to maintain original functionality which returned 0 if the birth date is after the current date
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2015, Calendar.DECEMBER, 21);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2015, Calendar.DECEMBER, 22);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0, 0, 0, 0);
		
		// 3. Test case - Person is born on leap day, current date is March 1st on non-leap year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.MARCH, 1);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12, 52, 366);
		
		// 4. Test case - Person is born on March 1st of a leap year 
		// and comes in the day before their birthday on non-leap year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.MARCH, 1);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0, 11, 52, 364);
		
		// 5. Test case - Person is born on March 1st on a non-leap year
		// and comes in on leap day
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2012, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2011, Calendar.MARCH, 1);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0, 11, 52, 365);
		
		// 6. Test case - Scenario listed in CHICA-588 Patient is born on October 29th of a leap year 
		// and comes in on their birthday 3 years later on a non-leap year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2015, Calendar.OCTOBER, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.OCTOBER, 29);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 3, 36, 156, 1095);
		
		// 7. Test case - Person is born before leap day and comes in on their birthday
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 28);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12, 52, 366);
		
		// 8. Test case - Person is born on February 28th and comes in on leap day the following year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2012, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2011, Calendar.FEBRUARY, 28);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12, 52, 366);
		
		// 9. Test case - Person is born on leap day and comes in on leap day
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2016, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 4, 48, 208, 1461);
		
		// 10. Test case - Person is born on leap day and comes in on leap day, 2 leap years have occurred
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2020, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 8, 96, 417, 2922);
	}
	
	/**
	 * Runs tests for all age units (Years, Months, Weeks, and Days)
	 * 
	 * @param birthdate - person's date of birth
	 * @param today - date to calculate age from
	 * @param expectedYears - number of years that we expect Util.getAgeInUnits to return
	 * @param expectedMonths - number of months that we expect Util.getAgeInUnits to return
	 * @param expectedWeeks - number of weeks that we expect Util.getAgeInUnits to return
	 * @param expectedDays - number of days that we expect Util.getAgeInUnits to return
	 */
	private static void runGetAgeInUnitsForAllUnits(Date birthdate, Date today, int expectedYears, int expectedMonths, int expectedWeeks, int expectedDays)
	{
		System.out.println("Beginning test case...");
		
		// Years old
		int ageInUnits = Util.getAgeInUnits(birthdate, today, Util.YEAR_ABBR);
		System.out.println("Age in units using original functionality: " + 
				testOriginalGetAgeInUnits(birthdate, 
						today, Util.YEAR_ABBR) + " " + Util.YEAR_ABBR);
		System.out.println("Age in units using Joda-Time: " + ageInUnits + " " + Util.YEAR_ABBR);
		assertEquals("testGetAgeInUnits failed - " + Util.YEAR_ABBR + " units", expectedYears, ageInUnits);

		// Months old
		ageInUnits = Util.getAgeInUnits(birthdate, today, Util.MONTH_ABBR);
		System.out.println("Age in units using original functionality: " + 
				testOriginalGetAgeInUnits(birthdate, 
						today, Util.MONTH_ABBR) + " " + Util.MONTH_ABBR);
		System.out.println("Age in units using Joda-Time: " + ageInUnits + " " + Util.MONTH_ABBR);
		assertEquals("testGetAgeInUnits failed - " + Util.MONTH_ABBR + " units", expectedMonths, ageInUnits);

		// Weeks old
		ageInUnits = Util.getAgeInUnits(birthdate, today, Util.WEEK_ABBR);
		System.out.println("Age in units using original functionality: " + 
				testOriginalGetAgeInUnits(birthdate, 
						today, Util.WEEK_ABBR) + " " + Util.WEEK_ABBR);
		System.out.println("Age in units using Joda-Time: " + ageInUnits + " " + Util.WEEK_ABBR);
		assertEquals("testGetAgeInUnits failed - " + Util.WEEK_ABBR + " units", expectedWeeks, ageInUnits);

		// Days old
		ageInUnits = Util.getAgeInUnits(birthdate, today, Util.DAY_ABBR);
		System.out.println("Age in units using original functionality: " + 
				testOriginalGetAgeInUnits(birthdate, 
						today, Util.DAY_ABBR) + " " + Util.DAY_ABBR);
		System.out.println("Age in units using Joda-Time: " + ageInUnits + " " + Util.DAY_ABBR);
		assertEquals("testGetAgeInUnits failed - " + Util.DAY_ABBR + " units", expectedDays, ageInUnits);
		
		System.out.println("Finished test case...\n\n");
	}
	
	/**
	 * This is the original method that I am keeping around for comparison purposes only
	 * This method can be removed after testing
	 * 
	 * @param birthdate
	 * @param today
	 * @param unit
	 * @return
	 */
	private static int testOriginalGetAgeInUnits(Date birthdate, Date today, String unit)
	{
		if (birthdate == null)
		{
			return 0;
		}

		if (today == null)
		{
			today = new Date();
		}
		
		int diffMonths = 0;
		int diffDayOfMonth = 0;
		int diffDayOfYear = 0;
		int years = 0;
		int months = 0;
		int days = 0;

		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.setTime(birthdate);
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.setTime(today);

		// return 0 if the birthdate is after today
		if (birthdate.compareTo(today) > 0)
		{
			return 0;
		}

		years = todayCalendar.get(Calendar.YEAR)
				- birthdateCalendar.get(Calendar.YEAR);
		
		diffMonths = todayCalendar.get(Calendar.MONTH)
				- birthdateCalendar.get(Calendar.MONTH);
		diffDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR)
				- birthdateCalendar.get(Calendar.DAY_OF_YEAR);

		diffDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH)
				- birthdateCalendar.get(Calendar.DAY_OF_MONTH);

		months = years * 12;
		months += diffMonths;

		days = years * 365;
		days += diffDayOfYear;

		if (unit.equalsIgnoreCase(Util.YEAR_ABBR))
		{
			if (diffMonths < 0)
			{
				years--;
			}
			else if (diffMonths == 0 && diffDayOfYear < 0)
			{
				years--;
			}
			return years;
		}

		if (unit.equalsIgnoreCase(Util.MONTH_ABBR))
		{
			if (diffDayOfMonth < 0)
			{
				months--;
			}
			return months;
		}

		if (unit.equalsIgnoreCase(Util.WEEK_ABBR))
		{
			return days/7;
		}

		if (days < 0)
		{
			days = 0;
		}
		return days;
	}
	
	@Test
	public void unitTestGetFractionalAgeInUnits()
	{
		// 1. Test case - Person is born on leap day, and comes in on February 28th of non-leap year
		// The difference between the original method and new method is due to the patient being
		// born on leap day, the original did not consider this patient to be a year old
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1.0, 12.0, 52.142857142857146);

		// 2. Test case - DOB is after the current date
		// NOTE: Joda-Time returns -1 for days old, but code was added added to the getAgeInUnits() method 
		// to maintain original functionality which returned 0 if the birth date is after the current date
		// The difference between the new method and the old for all values in this test case is 
		// that the old method did not correctly handle the fact that this patient hasn't been born yet.
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2015, Calendar.DECEMBER, 21);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2015, Calendar.DECEMBER, 22);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0, 0, 0);

		// 3. Test case - Person is born on leap day, current date is March 1st on non-leap year
		// The original method considered this person to be exactly 1 year old
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.MARCH, 1);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1.0027397260273974, 12.035714285714286, 52.285714285714285);

		// 4. Test case - Person is born on March 1st of a leap year 
		// and comes in the day before their birthday on non-leap year
		// The difference in years is that the original method considered this patient to be 362 days old
		// but the new method considers this patient to be 364 days old
		// The difference in weeks between the new method has to do again with leap year
		// I have verified that the new method is correct compared to the old
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.MARCH, 1);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0.9972602739726028, 11.964285714285714, 52);

		// 5. Test case - Person is born on March 1st on a non-leap year
		// and comes in on leap day
		// The original method would return 0.0 because it used the day of year to determine the difference in age, 
		// in this case the day of year would be the same, but in this case, the patient is not 0.0 years old, the new
		// method has been verified to be correct in this case
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2012, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2011, Calendar.MARCH, 1);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0.9972677595628415, 11.96551724137931, 52.142857142857146);

		// 6. Test case - Scenario listed in CHICA-588 Patient is born on October 29th of a leap year 
		// and comes in on their birthday 3 years later on a non-leap year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2015, Calendar.OCTOBER, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.OCTOBER, 29);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 3.0, 36.0, 156.42857142857142);

		// 7. Test case - Person is born before leap day and comes in on their birthday
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2013, Calendar.FEBRUARY, 28);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 28);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12.0, 52.285714285714285);

		// 8. Test case - Person is born on February 28th and comes in on leap day the following year
		// New method has been verified to be correct compared to the old method
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2012, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2011, Calendar.FEBRUARY, 28);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1.0027397260273974, 12.03448275862069, 52.285714285714285);

		// 9. Test case - Person is born on leap day and comes in on leap day
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2016, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 4, 48.0, 208.71428571428572);

		// 10. Test case - Person is born on leap day and comes in on leap day, 2 leap years have occurred
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2020, Calendar.FEBRUARY, 29);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2012, Calendar.FEBRUARY, 29);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 8, 96.0, 417.42857142857144);
		
		// 11. Test case - Including this test case because it was originally included in the TestUtil in the dss module
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2008, Calendar.JUNE,1);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2007, Calendar.SEPTEMBER, 9);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0.7287671232876712, 8.741935483870968, 38.0);

		// 13. Test case - Including this test case because it was originally included in the TestUtil in the dss module
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2008, Calendar.JUNE,27);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2007, Calendar.SEPTEMBER, 9);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 0.8, 9.6, 41.714285714285715);
		
		// 14. Test case - Person is born on March 1st on a non-leap year
		// and comes in on March 1st of a leap year
		todayCalendar = Calendar.getInstance();
		todayCalendar.set(2012, Calendar.MARCH, 1);
		birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.set(2011, Calendar.MARCH, 1);
		runGetFractionalAgeInUnitsForAllUnits(birthdateCalendar.getTime(), todayCalendar.getTime(), 1, 12, 52.285714285714285);
	}
	
	/**
	 * Runs tests for all age units (Years, Months, and Weeks)
	 * 
	 * @param birthdate - person's date of birth
	 * @param today - date to calculate age from
	 * @param expectedYears - number of years that we expect Util.getFractionalAgeInUnits to return
	 * @param expectedMonths - number of months that we expect Util.getFractionalAgeInUnits to return
	 * @param expectedWeeks - number of weeks that we expect Util.getFractionalAgeInUnits to return
	 */
	private static void runGetFractionalAgeInUnitsForAllUnits(Date birthdate, Date today, double expectedYears, double expectedMonths, double expectedWeeks)
	{	
		double fractAge = Util.getFractionalAgeInUnits(birthdate, 
				today, Util.YEAR_ABBR);
		double originalFractAge = testOriginalGetFractionalAgeInUnits(birthdate, 
				today, Util.YEAR_ABBR);
		System.out.println("Years: " + fractAge + " vs " + originalFractAge);
		assertEquals(expectedYears, fractAge, .00001);
		
		
		fractAge = Util.getFractionalAgeInUnits(birthdate, 
				today, Util.MONTH_ABBR);
		originalFractAge = testOriginalGetFractionalAgeInUnits(birthdate, 
				today, Util.MONTH_ABBR);
		System.out.println("Months: " + fractAge + " vs " + originalFractAge);
		assertEquals(expectedMonths, fractAge, 0.00001);
		
		fractAge = Util.getFractionalAgeInUnits(birthdate, 
				today, Util.WEEK_ABBR);
		originalFractAge = testOriginalGetFractionalAgeInUnits(birthdate, 
				today, Util.WEEK_ABBR);
		System.out.println("Weeks:" + fractAge + " vs " + originalFractAge);
		assertEquals(expectedWeeks, fractAge, 0.00001);
	}
	
	/**
	 * This combines the original getFractionalAgeInUnits() method with a call to the TestUtil.testOriginalGetAgeInUnits()
	 * This method can be removed after testing
	 * 
	 * @param birthdate
	 * @param today
	 * @param unit
	 * @return
	 */
	private static double testOriginalGetFractionalAgeInUnits(Date birthdate, Date today, String unit)
	{
		int ageInUnits = TestUtil.testOriginalGetAgeInUnits(birthdate,today,unit);
		Calendar birthdateCalendar = Calendar.getInstance();
		birthdateCalendar.setTime(birthdate);
		Calendar todayCalendar = Calendar.getInstance();
		todayCalendar.setTime(today);
		
		if (unit.equalsIgnoreCase(Util.MONTH_ABBR))
		{
			int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
			int birthdateDayOfMonth = birthdateCalendar.get(Calendar.DAY_OF_MONTH);
			
			double dayDiff = todayDayOfMonth - birthdateDayOfMonth;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			double daysInMonth = 0;
			
			if(dayDiff > 0)
			{
				daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}else{
				todayCalendar.add(Calendar.MONTH, -1);
				daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				dayDiff = daysInMonth+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInMonth);
		}
		if (unit.equalsIgnoreCase(Util.YEAR_ABBR))
		{
			int todayDayOfYear = todayCalendar.get(Calendar.DAY_OF_YEAR);
			int birthdateDayOfYear = birthdateCalendar.get(Calendar.DAY_OF_YEAR);
			
			double dayDiff = todayDayOfYear - birthdateDayOfYear;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			//code to handle leap years
			Integer daysInYear = 365; 
			if(birthdateCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365||
					todayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)>365){
				dayDiff--;
			}
			
			if(dayDiff < 0)
			{
				todayCalendar.add(Calendar.YEAR, -1);
				dayDiff = daysInYear+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInYear);
		}
		if (unit.equalsIgnoreCase(Util.WEEK_ABBR))
		{
			int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);
			int birthdateDayOfWeek = birthdateCalendar.get(Calendar.DAY_OF_WEEK);
			
			// DWE CHICA-588 Changed dayDiff to double because it was causing a loss of precision in our division below
			double dayDiff = todayDayOfWeek - birthdateDayOfWeek;
			
			if(dayDiff == 0)
			{
				return ageInUnits;
			}
			
			int daysInWeek = 0;
			
			if(dayDiff > 0)
			{
				daysInWeek = todayCalendar.getActualMaximum(Calendar.DAY_OF_WEEK);
			}else{
				todayCalendar.add(Calendar.WEEK_OF_YEAR, -1);
				daysInWeek = todayCalendar.getActualMaximum(Calendar.DAY_OF_WEEK);
				dayDiff = daysInWeek+dayDiff;
			}
			return ageInUnits+(dayDiff/daysInWeek);
		}
		return ageInUnits;
	
	}
}

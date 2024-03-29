/**
 * 
 */
package org.openmrs.module.chirdlutil.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Field;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.SecondaryForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

/**
 * This class contains several utility methods
 * and other modules that depend on it.
 * 
 * @author Tammy Dugan
 */
public class Util
{
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    
    public static final String MEASUREMENT_LB = "lb";
    public static final String MEASUREMENT_IN = "in";
    public static final String MEASUREMENT_CM = "cm";
    public static final String MEASUREMENT_KG = "kg";
    public static final String MEASUREMENT_CELSIUS = "celsius";
    public static final String MEASUREMENT_FAHRENHEIT = "fahrenheit";
    
    // DWE CHICA-635 IUH sends DegC, also adding one for "cel" since this is the official ISO standard
    // Epic is sending "C"
    public static final String MEASUREMENT_DEG_C = "DegC";
    public static final String MEASUREMENT_CEL = "cel";
    public static final String MEASUREMENT_CELSIUS_C = "C";
        
    public static final String YEAR_ABBR = "yo";
    public static final String MONTH_ABBR = "mo";
    public static final String WEEK_ABBR = "wk";
    public static final String DAY_ABBR = "do";
    
    private static ServerConfig serverConfig = null;
    private static long lastUpdatedServerConfig = System.currentTimeMillis();
    private static final long SERVER_CONFIG_UPDATE_CYCLE = 900000; // fifteen minutes
    private static DaemonToken daemonToken;
    
    /**
     * Converts specific measurements in English units to metric
     * 
     * @param measurement measurement to be converted
     * @param measurementUnits units of the measurement
     * @return double metric value of the measurement
     */
    public static double convertUnitsToMetric(double measurement,
            String measurementUnits)
    {
        if (measurementUnits == null)
        {
            return measurement;
        }

        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_IN))
        {
            measurement = measurement * 2.54; // convert inches to centimeters
        }

        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_LB))
        {
            measurement = measurement * 0.45359237; // convert pounds to kilograms
        }
        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_FAHRENHEIT))
        {
            measurement = (measurement - 32)*(5/9.0); // convert fahrenheit to celsius
        }
        return measurement; // truncate to one decimal
                                                  // place
    }
    
    /**
     * Converts specific measurements in metric units to English
     * 
     * @param measurement measurement to be converted
     * @param measurementUnits units of the measurement
     * @return double English value of the measurement
     */
    public static double convertUnitsToEnglish(double measurement,
            String measurementUnits)
    {
        if (measurementUnits == null)
        {
            return measurement;
        }

        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_CM))
        {
            measurement = measurement * 0.393700787; // convert centimeters to inches
        }

        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_KG))
        {
            measurement = measurement * 2.20462262; // convert kilograms to pounds
        }
        
        if (measurementUnits.equalsIgnoreCase(MEASUREMENT_CELSIUS) || measurementUnits.equalsIgnoreCase(MEASUREMENT_DEG_C) || measurementUnits.equalsIgnoreCase(MEASUREMENT_CEL) || measurementUnits.equalsIgnoreCase(MEASUREMENT_CELSIUS_C)) // DWE CHICA-635 Added DegC and cel
        {
            measurement = (measurement *  (9/5.0)) + 32; // convert celsius to fahrenheit
        }
        return measurement; // truncate to one decimal
                                                  // place
    }
    
    /**
     * Returns the numeric part of a string input as a string
     * @param input alphanumeric input
     * @return String all numeric
     */
    public static String extractIntFromString(String input)
    {
        if(input == null)
        {
            return null;
        }
        String[] tokens = Pattern.compile("\\D").split(input);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < tokens.length; i++)
            result.append(tokens[i]);
        return result.toString();
    }
    
    /**
     * Adds period if necessary to a package prefix
     * @param packagePrefix a java package prefix
     * @return String formatted package prefix
     */
    public static String formatPackagePrefix(String packagePrefix)
    {
        if (packagePrefix!=null&&!packagePrefix.endsWith("."))
        {
            packagePrefix += ".";
        }
        return packagePrefix;
    }
    
    /**
     * Parses a giving string into a list of package prefixes based on the delimiter provided.  This will also 
     * add a period (if necessary) to each of the package prefixes.  This will not return null.
     * 
     * @param packagePrefixes one or more java package prefix
     * @param delimiter the delimiter that separates the package prefixes in the packagePrefixes parameter.
     * @return List of Strings containing formatted package prefixes
     */
    public static List<String> formatPackagePrefixes(String packagePrefixes, String delimiter)
    {
        List<String> packagePrefixList = new ArrayList<String>();
        if (packagePrefixes == null) {
            return packagePrefixList;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(packagePrefixes, delimiter, false);
        while (tokenizer.hasMoreTokens()) {
            String packagePrefix = tokenizer.nextToken().trim();
            if (packagePrefix.length() == 0) {
                continue;
            }
            
            packagePrefix = formatPackagePrefix(packagePrefix);            
            packagePrefixList.add(packagePrefix);
        }
        
        return packagePrefixList;
    }
    
    public static String toProperCase(String str)
    {
        if(str == null || str.length()<1)
        {
            return str;
        }
        
        StringBuffer resultString = new StringBuffer();
        String delimiter = " ";
        
        StringTokenizer tokenizer = new StringTokenizer(str,delimiter,true);
        
        String currToken = null;
        
        while(tokenizer.hasMoreTokens())
        {
            currToken = tokenizer.nextToken();
            
            if(!currToken.equals(delimiter))
            {
                if(currToken.length()>0)
                {
                    currToken = currToken.substring(0, 1).toUpperCase()
                        + currToken.substring(1).toLowerCase();
                }
            }
            
            resultString.append(currToken);
        }
        
        return resultString.toString();
    }
    
    public static double getFractionalAgeInUnits(Date birthdate, Date today, String unit)
    {
        // Return 0 if the birthdate is after today
        if (birthdate.compareTo(today) > 0)
        {
            return 0;
        }
        
        int ageInUnits = getAgeInUnits(birthdate,today,unit);
        
        // DWE CHICA-588 Create Joda LocalDate objects using java Date objects
        LocalDate localBirthDate = new LocalDate(birthdate);
        LocalDate localToday = new LocalDate(today);
        
        Calendar birthdateCalendar = Calendar.getInstance();
        birthdateCalendar.setTime(birthdate);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(today);
        
        if (unit.equalsIgnoreCase(MONTH_ABBR))
        {
            // DWE CHICA-588 Use Joda-Time to get the difference in days
            // since it correctly handles leap year, which is needed since 
            // this method calls getAgeInUnits which now handles leap year.
            // Get the time period represented in the number of years old with any difference in days
            // Example: 1 year, 2 months, 6 days
            Period p = new Period(localBirthDate, localToday, PeriodType.yearMonthDay());
            int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
            int birthdateDayOfMonth = birthdateCalendar.get(Calendar.DAY_OF_MONTH);
            
            double dayDiff = p.getDays();
            
            if(dayDiff == 0)
            {
                return ageInUnits;
            }
            
            // Now we need to check to see if the current day of month 
            // is after the birth date day of month. If not, use the previous month
            // to determine the number of days in the month
            double daysInMonth = 0;
            if(todayDayOfMonth > birthdateDayOfMonth)
            {
                daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }else{
                todayCalendar.add(Calendar.MONTH, -1);
                daysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            return ageInUnits+(dayDiff/daysInMonth);
        }
        if (unit.equalsIgnoreCase(YEAR_ABBR))
        {
            // DWE CHICA-588 Use Joda-Time to get the difference in days
            // since it correctly handles leap year, which is needed since 
            // this method calls getAgeInUnits which now handles leap year.
            // Get the time period represented in the number of years old with any difference in days
            // Example: 1 year, 6 days
            Period p = new Period(localBirthDate, localToday, PeriodType.yearDay());
            
            double dayDiff = p.getDays();
            
            if(dayDiff == 0)
            {
                return ageInUnits;
            }
            
            Integer daysInYear = 365; 
            if(dayDiff == 365) // dayDiff will be 365 if patient is born on March 1 and comes in on leap day
            {
                daysInYear = todayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            return ageInUnits+(dayDiff/daysInYear);
        }
        if (unit.equalsIgnoreCase(WEEK_ABBR))
        {
            int todayDayOfWeek = todayCalendar.get(Calendar.DAY_OF_WEEK);
            int birthdateDayOfWeek = birthdateCalendar.get(Calendar.DAY_OF_WEEK);
            
            // DWE CHICA-588 Changed dayDiff to double because it was causing a loss of precision in our division below
            double dayDiff = (double)todayDayOfWeek - (double)birthdateDayOfWeek;
            
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
    
    /**
     * Returns a person's age in the specified units (days, weeks, months,
     * years)
     * 
     * DWE CHICA-588 Updated this method to use Joda-Time
     * This method will convert java.util.Date objects into joda.time.LocalDate objects 
     * and use the Between() methods in the Years, Months, Weeks, and Days classes to determine a person's age
     * in days, weeks, months, or years. This method will correctly handle leap years, for example, 
     * DOB is 02/29/2012 and the current date is 02/28/2013, the person is 365 days old, 
     * but is not exactly 1 year old based on the calendar. The person is still 1 year old, 
     * so this method will return a value of 1
     * 
     * @param birthdate person's date of birth
     * @param today date to calculate age from
     * @param unit unit to calculate age in (days, weeks, months, years)
     * @return int age in the given units
     */
    
    public static int getAgeInUnits(Date birthdate, Date today, String unit)
    {
        if (birthdate == null)
        {
            return 0;
        }

        if (today == null)
        {
            today = new Date();
        }
        
        // return 0 if the birthdate is after today
        if (birthdate.compareTo(today) > 0)
        {
            return 0;
        }
        
        int ageInUnits = 0;
        LocalDate localBirthDate = new LocalDate(birthdate);
        LocalDate localToday = new LocalDate(today);
        
        switch(unit)
        {
        case YEAR_ABBR:
            Years years = Years.yearsBetween(localBirthDate, localToday);
            ageInUnits = years.getYears();
            break;
        case MONTH_ABBR:
            Months months = Months.monthsBetween(localBirthDate, localToday);
            ageInUnits = months.getMonths();
            break;
        case WEEK_ABBR:
            Weeks weeks = Weeks.weeksBetween(localBirthDate, localToday);
            ageInUnits = weeks.getWeeks();
            break;
            default:
                // Default to days, which was the previously existing default
                Days days = Days.daysBetween(localBirthDate, localToday);
                ageInUnits = days.getDays();
                break;
        }
        
        return ageInUnits;
    }
    
    public static Double round(Double value,int decimalPlaces)
    {
        if(decimalPlaces<0||value == null)
        {
            return value;
        }
        
        double intermVal = value*Math.pow(10, decimalPlaces);
        intermVal = Math.round(intermVal);
        return intermVal/(Math.pow(10, decimalPlaces));
    }
    
    public static String getStackTrace(Exception x) {
        OutputStream buf = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(buf);
        x.printStackTrace(p);
        return buf.toString();
    }
    
    public static String archiveStamp()
    {
        Date currDate = new java.util.Date();
        String dateFormat = "yyyyMMdd-kkmmss-SSS";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String formattedDate = formatter.format(currDate);
        return formattedDate;
    }
    
    public static boolean isToday(Date date) {
        if (date != null) {
            Calendar today = Calendar.getInstance();
            Calendar dateToCompare = Calendar.getInstance();
            dateToCompare.setTime(date);
            return (today.get(Calendar.ERA) == dateToCompare.get(Calendar.ERA)
                    && today.get(Calendar.YEAR) == dateToCompare.get(Calendar.YEAR) && today
                    .get(Calendar.DAY_OF_YEAR) == dateToCompare
                    .get(Calendar.DAY_OF_YEAR));

        }
        return false;
    }
    
    public static String removeTrailingZeros(String str1)
    {

        char[] chars = str1.toCharArray();
        int index = chars.length-1;
        for (; index >=0; index--)
        {
            if (chars[index] != '0')
            {
                break;
            }
        }
        if (index > -1)
        {
            return str1.substring(0,index+1);
        }
        return str1;
    }
    
    public static String removeLeadingZeros(String mrn)
    {

        char[] chars = mrn.toCharArray();
        int index = 0;
        for (; index < chars.length; index++)
        {
            if (chars[index] != '0')
            {
                break;
            }
        }
        if (index > -1)
        {
            return mrn.substring(index);
        }
        return mrn;
    }
    
    public static Obs saveObs(Patient patient, Concept currConcept, int encounterId, String value,
                              Date obsDatetime) {
        if (value == null || value.length() == 0) {
            return null;
        }
        
        ObsService obsService = Context.getObsService();
        Obs obs = new Obs();
        String datatypeName = currConcept.getDatatype().getName();
        
        if (datatypeName.equalsIgnoreCase("Numeric")) {
            try {
                obs.setValueNumeric(Double.parseDouble(value));
            }
            catch (NumberFormatException e) {
                log.error("Could not save value: {} to the database for concept {}", value, currConcept.getName().getName());
            }
        } else if (datatypeName.equalsIgnoreCase("Coded")) {
            ConceptService conceptService = Context.getConceptService();
            Concept answer = conceptService.getConceptByName(value);
            if (answer == null) {
                log.error("{} is not a valid concept name. {} will be stored as text.", value, value);
                obs.setValueText(value);
            } else {
                obs.setValueCoded(answer);
            }
        } else if (datatypeName.equalsIgnoreCase("Date")) {
            Date valueDatetime = new Date(Long.valueOf(value));
            obs.setValueDatetime(valueDatetime);
        } else {
            obs.setValueText(value);
        }
        
        EncounterService encounterService = Context.getService(EncounterService.class);
        Encounter encounter = encounterService.getEncounter(encounterId);
        
        Location location = encounter.getLocation();
        
        obs.setPerson(patient);
        obs.setConcept(currConcept);
        obs.setLocation(location);
        obs.setEncounter(encounter);
        obs.setObsDatetime(obsDatetime);
        obsService.saveObs(obs, null);
        return obs;
    }
    
    /**
     * Calculates age to a precision of days, weeks, months, or years based on a
     * set of rules
     * 
     * @param birthdate patient's birth date
     * @param cutoff date to calculate age from
     * @return String age with units 
     */
    public static String adjustAgeUnits(Date birthdate, Date cutoff)
    {
        int years = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, YEAR_ABBR);
        int months = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, MONTH_ABBR);
        int weeks = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, WEEK_ABBR);
        int days = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, DAY_ABBR);

        if (years >= 2)
        {
            return years + " " + YEAR_ABBR;
        }

        if (months >= 2)
        {
            return months + " " + MONTH_ABBR;
        }

        if (days > 30)
        {
            return weeks + " " + WEEK_ABBR;
        }

        return days + " " + DAY_ABBR;
    }
    
    public static String computeMD5(String strToMD5) throws DigestException
    {
        try
        {
            //get md5 of input string
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(strToMD5.getBytes());
            byte[] bytes = md.digest();
            
            //convert md5 bytes to a hex string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++)
            {
                hexString.append(Integer.toHexString(0xFF & bytes[i]));
            }
            return hexString.toString();
        } catch (Exception e)
        {
            throw new DigestException("couldn't make digest of partial content");
        }
    }
    
    /**
     * Returns an array of barcodes found in the specified PDF file.
     * 
     * @param pdfFile The PDF document to search for barcodes.
     * @param hints Map of barcode types to find.  One key should be DecodeHintType.POSSIBLE_FORMATS.  The value should be a 
     * Collection of possible format types found in the BarcodeFormat class.  Another key that is optional is 
     * DecodeHintType.TRY_HARDER with a boolean value.
     * @param regexPattern A pattern used to match barcodes.  If this parameter is specified, only barcodes that match this 
     * pattern will be returned.  All others will be ignored.
     * @return Array of barcodes found in a PDF document.
     * @throws Exception
     */
    public static String[] getPdfFormBarcodes(File pdfFile, Map<DecodeHintType, Object> hints, String regexPattern) 
    throws Exception {    
        if (pdfFile == null || !pdfFile.exists() || !pdfFile.canRead()) {
            throw new IllegalArgumentException("Please specify a valid PDF file.  Make sure the file is readable as well.");
        }
        
        BufferedImage image;     
        image = convertPdfPageToImage(pdfFile, 0, 0.0f);     
        
        if (image == null) {       
            throw new IllegalArgumentException("Could not decode image from PDF file " + pdfFile.getAbsolutePath() + ".");  
        }
        
        LuminanceSource source = new BufferedImageLuminanceSource(image);     
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));     
        MultiFormatReader barcodeReader = new MultiFormatReader();
        MultipleBarcodeReader reader = new GenericMultipleBarcodeReader(barcodeReader);
        Result[] results;     
        Set<String> matches =  new HashSet<String>();     
        if (hints != null && ! hints.isEmpty()) {            
            results = reader.decodeMultiple(bitmap, hints); 
        }
        else {       
            results = reader.decodeMultiple(bitmap); 
        }
        
        if (results != null) {
            for (Result result : results) {
                String text = result.getText();
                if (regexPattern != null) {
                    if (!text.matches(regexPattern)) {
                        continue;
                    }
                }
                
                matches.add(text);
            }
        }
        
        String[] returnArry = new String[matches.size()];
        return matches.toArray(returnArry);
    }
    
    /**
     * Creates a BufferedImage of a particular page in a PDF document.
     * 
     * @param pdfFile The PDF document containing the page for which the image will be created.
     * @param pageNumber The page of the PDF document that will be generated into an image. This
     *            counter is zero based.
     * @param rotation The rotation of the image. If null it will be set to no rotation (0.0f).
     * @return BufferedImage object containing the specified PDF page as an image.
     */
    public static BufferedImage convertPdfPageToImage(File pdfFile, int pageNumber, Float rotation) throws Exception {
        if (rotation == null) {
            rotation = 0.0f;
        }
        
        // open the file
        Document document = new Document();
        try {
            document.setFile(pdfFile.getAbsolutePath());
        }
        catch (PDFException ex) {
            log.error("Error parsing PDF document", ex);
            throw ex;
        }
        catch (PDFSecurityException ex) {
            log.error("Error encryption not supported", ex);
            throw ex;
        }
        catch (FileNotFoundException ex) {
            log.error("Error file not found", ex);
            throw ex;
        }
        catch (IOException ex) {
            log.error("Error IOException", ex);
            throw ex;
        }
        
        // save page capture to file.
        float scale = 1.5f;
        String scaleStr = Context.getAdministrationService().getGlobalProperty("chirdlutil.pdfToImageScaleValue");
        if (scaleStr != null && scaleStr.trim().length() > 0) {
            try {
                scale = Float.parseFloat(scaleStr);
            }
            catch (NumberFormatException e) {
                log.error("Invalid value for global property chirdlutil.pdfToImageScaleValue.  Default value of "
                        + "1.5f will be used");
                scale = 1.5f;
            }
        } else {
            log.error("Value for global property chirdlutil.pdfToImageScaleValue is not set.  Default value of "
                    + "1.5f will be used");
        }
        
        // Paint the page content to an image
        BufferedImage image = (BufferedImage) document.getPageImage(pageNumber, GraphicsRenderingHints.PRINT,
            Page.BOUNDARY_CROPBOX, rotation, scale);
        
        // clean up resources
        document.dispose();
        
        return image;
    }
    
    /**
     * Returns the server configuration.
     * 
     * @return ServerConfig object.
     * @throws JiBXException
     * @throws FileNotFoundException
     */
    public static ServerConfig getServerConfig() throws JiBXException, FileNotFoundException {
        long currentTime = System.currentTimeMillis();
        if (serverConfig == null || (currentTime - lastUpdatedServerConfig) > SERVER_CONFIG_UPDATE_CYCLE) {
            lastUpdatedServerConfig = currentTime;
            String configFileStr = Context.getAdministrationService().getGlobalProperty("chirdlutil.serverConfigFile");
            if (configFileStr == null) {
                log.error("You must set a value for global property: chirdlutil.serverConfigFile");
                return null;
            }
            
            File configFile = new File(configFileStr);
            if (!configFile.exists()) {
                log.error("The file location specified for the global property "
                    + "chirdlutil.serverConfigFile does not exist.");
                return null;
            }
            
            IBindingFactory bfact = BindingDirectory.getFactory(ServerConfig.class);
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            serverConfig = (ServerConfig)uctx.unmarshalDocument(new FileInputStream(configFile), null);
        }
        
        return serverConfig;
    }
    
    /**
     * Method to return the JSP page associated with a specific form.
     * 
     * @param formId The form ID.
     * @return The page URL for the form or null if one can't be found.
     */
    public static String getFormUrl(Integer formId) {
        String url = null;
        try {
            ServerConfig config = Util.getServerConfig();
            if (config == null) {
                return null;
            }
            
            String username = Context.getAuthenticatedUser().getUsername();
            MobileClient client = config.getMobileClient(username);
            if (client == null) {
                return null;
            }
            
            Form form = Context.getFormService().getForm(formId);
            String formName = form.getName();
            String primaryFormId = client.getPrimaryFormId();
            MobileForm primaryForm = config.getMobileFormById(primaryFormId);
            if (primaryForm != null && formName.equals(primaryForm.getName())) {
                return primaryForm.getPageUrl();
            }
            
            SecondaryForm[] secondaryForms = client.getSecondaryForms();            
            for (SecondaryForm secondaryForm : secondaryForms) {
                String secondaryFormId = secondaryForm.getId();
                MobileForm mobileForm = config.getMobileFormById(secondaryFormId);
                if (mobileForm != null && formName.equals(mobileForm.getName())) {
                    return mobileForm.getPageUrl();
                }
            }
            
            return null;
        }
        catch (FileNotFoundException e) {
            log.error("Error finding server config file", e);
        }
        catch (JiBXException e) {
            log.error("Error parsing server config file", e);
        }
        
        return url;
    }
    
    /**
     * Creates a new concept.
     * 
     * @param conceptName The name of the concept.
     * @param conceptClass The concept class.
     * @param conceptDatatype The concept data type.
     * @return Newly created Concept object.
     */
    public static Concept createNewConcept(String conceptName, String conceptClass, String conceptDatatype) {
        Concept concept = new Concept();
        concept.setDateCreated(new Date());
        concept.setUuid(UUID.randomUUID().toString());
        
        ConceptName conceptNameObj = new ConceptName();
        conceptNameObj.setLocale(Context.getLocale());
        conceptNameObj.setName(conceptName);
        conceptNameObj.setDateCreated(new Date());
        conceptNameObj.setUuid(UUID.randomUUID().toString());
        concept.addName(conceptNameObj);
        
        ConceptService conceptService = Context.getConceptService();
        ConceptClass conceptClassObj = conceptService.getConceptClassByName(conceptClass);
        concept.setConceptClass(conceptClassObj);
        
        ConceptDatatype conceptDatatypeObj = conceptService.getConceptDatatypeByName(conceptDatatype);
        concept.setDatatype(conceptDatatypeObj);
        
        return conceptService.saveConcept(concept);
    }
    
    /**
     * Decrypts an encrypted value with the provided key.
     * 
     * @param encryptedValue The value to decrypt
     * @param base64DecodeValue if true, the encryptedValue will be decoded before being decrypted
     * @param key The key used to decrypt the value
     * @return The decrypted value or null if it can't be decrypted
     */
    public static String decryptValue(String encryptedValue, boolean base64DecodeValue, String key) {
        // Decrypt the password
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ChirdlUtilConstants.ENCRYPTION_AES);
            byte[] keyBytes = key.getBytes(ChirdlUtilConstants.ENCODING_UTF8);
            keyBytes = Arrays.copyOf(keyBytes, 16);
            Key secretKey = new SecretKeySpec(keyBytes, ChirdlUtilConstants.ENCRYPTION_AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            if (base64DecodeValue) {
                return new String(cipher.doFinal(Base64.decodeBase64(encryptedValue.getBytes())));
            } else {
                return new String(cipher.doFinal(encryptedValue.getBytes()));
            }
        }
        catch (NoSuchAlgorithmException e) {
            log.error("Error creating {} Cipher instance", ChirdlUtilConstants.ENCRYPTION_AES, e);
            return null;
        }
        catch (NoSuchPaddingException e) {
            log.error("Error creating {} Cipher instance", ChirdlUtilConstants.ENCRYPTION_AES, e);
            return null;
        }
        catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding: {}", ChirdlUtilConstants.ENCODING_UTF8, e);
            return null;
        }
        catch (InvalidKeyException e) {
            log.error("Invalid Cipher Key", e);
            return null;
        }
        catch (IllegalBlockSizeException e) {
            log.error("Illegal Block Size", e);
            return null;
        }
        catch (BadPaddingException e) {
            log.error("Bad Padding", e);
            return null;
        }
    }
    
    /**
     * Encrypts a value with the provided key.
     * 
     * @param value The value to encrypt
     * @param base64EncodeValue if true, the value will be encoded after being encrypted
     * @param key The key used to encrypt the value
     * @return The encrypted value or null if there was a problem encrypting the value
     */
    public static String encryptValue(String value, boolean base64EncodeValue, String key) {
        byte[] keyBytes;
        try {
            keyBytes = key.getBytes(ChirdlUtilConstants.ENCODING_UTF8);
            keyBytes = Arrays.copyOf(keyBytes, 16);  // Reduce to 128 bit
            Key secretKey = new SecretKeySpec(keyBytes, ChirdlUtilConstants.ENCRYPTION_AES);
        
            Cipher cipher = Cipher.getInstance(ChirdlUtilConstants.ENCRYPTION_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(ChirdlUtilConstants.ENCODING_UTF8));
            if (base64EncodeValue) {
                return new String(Base64.encodeBase64(encryptedBytes));
            }
            
            return new String (encryptedBytes);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding: {}", ChirdlUtilConstants.ENCODING_UTF8, e);
            return null;
        }
        catch (NoSuchAlgorithmException e) {
            log.error("Error creating {} Cipher instance", ChirdlUtilConstants.ENCRYPTION_AES, e);
            return null;
        }
        catch (NoSuchPaddingException e) {
            log.error("Error creating {} Cipher instance", ChirdlUtilConstants.ENCRYPTION_AES, e);
            return null;
        }
        catch (InvalidKeyException e) {
            log.error("Invalid Cipher Key", e);
            return null;
        }
        catch (IllegalBlockSizeException e) {
            log.error("Illegal Block Size", e);
            return null;
        }
        catch (BadPaddingException e) {
            log.error("Bad Padding", e);
            return null;
        }
        
        
    }
    
    /**
     * DWE CHICA-761 
     * Formats the patient identifier
     * 
     * @param patient
     * @return
     */
    public static String formatMRN(Patient patient)
    {
        String formattedMRN = "";
        PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
        if(patientIdentifier != null)
        {
            formattedMRN = ChirdlUtilConstants.GENERAL_INFO_NUMBER_SIGN + patientIdentifier.getIdentifier();
        }
        return formattedMRN;
    }
    
    /**
     * TMD CHICA-852
     * Displays mrn stored in MRN_EHR
     * If MRN_EHR value is empty, it displays MRN_OTHER value
     * Allows for correct display of check digits in MRNs
     * 
     * @param patient
     * @return
     */
    public static String getDisplayMRN(Patient patient)
    {
        PatientIdentifier mrn = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR);
        
        if(mrn == null){
            mrn = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
        }
        if(mrn != null){
            return mrn.getIdentifier();
        }
        return null;
    }
    
    /**
     * CHICA-221
     * Gets the provider that has the "Attending Provider" role for the provided encounter
     * There should be only one provider with this role for the encounter
     * 
     * @param encounter
     * @return
     */
    public static org.openmrs.Provider getProviderByAttendingProviderEncounterRole(org.openmrs.Encounter encounter)
    {
        org.openmrs.Provider provider = null;
        
        if(encounter != null)
        {
            EncounterService es = Context.getEncounterService();
            EncounterRole encounterRole = es.getEncounterRoleByName(ChirdlUtilConstants.ENCOUNTER_ROLE_ATTENDING_PROVIDER);
            Set<org.openmrs.Provider> providers = encounter.getProvidersByRole(encounterRole);
            
            if(providers != null && providers.size() > 0) // We should only have one encounter provider with the "Attending Provider" role
            {
                Iterator<org.openmrs.Provider> iter = providers.iterator();
                if(iter.hasNext())
                {
                    provider = iter.next();
                    
                    if(iter.hasNext())
                    {
                        log.error("More than one provider was found for encounter: {}", encounter.getEncounterId());
                    }
                }
            }
        }
        
        return provider;
    }

    /**
     * Utility method to build a map of form field name to a Field object.
     * 
     * @param form The Form used to build the map
     * @return Map of form field name to Field objects
     */
    public static Map<String, Field> createFormFieldMap(Form form) {
        Map<String, Field> formFieldMap = new HashMap<String, Field>();
        Set<FormField> formFields = form.getFormFields();
        Iterator<FormField> iter = formFields.iterator();
        while (iter.hasNext()) {
            FormField formField = iter.next();
            Field field = formField.getField();
            formFieldMap.put(field.getName(), field);
        }
        
        return formFieldMap;
    }
    
    /**
     * CHICA-1063
     * Method used to hash a string using SHA-256
     * SHA-256 is used because it creates a string with less characters than SHA-512
     * This is needed specifically to keep the url provided to Glooko shorter
     * 
     * @param strToHash
     * @param base64EncodeValue - true to return a Base64 encoded version
     * @return hashed value
     */
    public static String hashStringSHA256(String strToHash, boolean base64EncodeValue)
    {
        MessageDigest md;
        byte[] input;
        try {
            md = MessageDigest.getInstance(ChirdlUtilConstants.SHA_256);
            input = strToHash.getBytes(ChirdlUtilConstants.ENCODING_UTF8);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding: {}", ChirdlUtilConstants.ENCODING_UTF8, e);
            return null;
        }
        catch (NoSuchAlgorithmException e) {
            log.error("System cannot find encryption algorithm: {}", ChirdlUtilConstants.SHA_256, e);
            return null;
        }
       
        byte[] hashed = md.digest(input);
        if(base64EncodeValue){
            return new String(Base64.encodeBase64(hashed));
        }
        
        return new String (hashed);    
    }
    
    /**
     * CHICA-1063
     * Generate salt - OpenMRS has a method for this, but doesn't use SecureRandom
     * @return
     */
    public static byte[] generateSecureRandomToken()
    {
        byte[] salt = new byte[16];
        Random random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * CHICA-1063 Moved this existing method into Util class
     * DWE CHICA-633
     * 
     * Store an encounter attribute value
     * 
     * @param encounter
     * @param attributeName - the name of the encounter attribute
     * @param valueText - the value to store in the value_text field
     */
    public static void storeEncounterAttributeAsValueText(org.openmrs.Encounter encounter, String attributeName, String valueText)
    {
        ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

        try
        {
            EncounterAttribute encounterAttribute = chirdlutilbackportsService.getEncounterAttributeByName(attributeName);
            EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByAttribute(encounter.getEncounterId(), encounterAttribute, false);
            
            boolean existingVoided = false;
            if(encounterAttributeValue != null && StringUtils.isNotEmpty(encounterAttributeValue.getValueText()) && StringUtils.isNotEmpty(valueText) && !encounterAttributeValue.getValueText().equalsIgnoreCase(valueText))
            {         
                // Attribute already exists, void the old one, and create a new one
                encounterAttributeValue.setVoided(true);
                encounterAttributeValue.setVoidedBy(Context.getAuthenticatedUser());
                encounterAttributeValue.setVoidReason(ChirdlUtilConstants.ATTR_VALUE_VOID_REASON + valueText);
                encounterAttributeValue.setDateVoided(new Date());
                
                chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
                existingVoided = true;
            }
            
            if(encounterAttributeValue == null || existingVoided) // Create a new attribute if one didn't exist or if we voided an existing one
            {
                encounterAttributeValue = new EncounterAttributeValue(encounterAttribute, encounter.getEncounterId(), valueText);
                encounterAttributeValue.setCreator(encounter.getCreator());
                encounterAttributeValue.setDateCreated(new Date());
                encounterAttributeValue.setUuid(UUID.randomUUID().toString());

                chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
            }
        }
        catch(Exception e)
        {
            log.error("Error storing encounter attribute value encounterId: {} attributeName: {}", encounter.getEncounterId(), attributeName, e);
        }
    }
            
    public static void storeEncounterAttributeAsValueDate(org.openmrs.Encounter encounter, String attributeName, Date valueDateTime)
    {
        ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

        try
        {
            EncounterAttribute encounterAttribute = chirdlutilbackportsService.getEncounterAttributeByName(attributeName);
            EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByAttribute(encounter.getEncounterId(), encounterAttribute, false);
            
            boolean existingVoided = false;
            if(encounterAttributeValue != null)
            {    
            	Date existingEncounterDateTime = encounterAttributeValue.getValueDateTime();
            	if (existingEncounterDateTime != null && !existingEncounterDateTime.equals(valueDateTime)){
            		 // Attribute already exists, void the old one, and create a new one
                    encounterAttributeValue.setVoided(true);
                    encounterAttributeValue.setVoidedBy(Context.getAuthenticatedUser());
                    encounterAttributeValue.setVoidReason(ChirdlUtilConstants.ATTR_VALUE_VOID_REASON +  valueDateTime.toString());
                    encounterAttributeValue.setDateVoided(new Date());
                    chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
                    existingVoided = true;
            	}
            }
            
            if(encounterAttributeValue == null || existingVoided) // Create a new attribute if one didn't exist or if we voided an existing one
            {
                encounterAttributeValue = new EncounterAttributeValue(encounterAttribute, encounter.getEncounterId(), valueDateTime);
                encounterAttributeValue.setCreator(encounter.getCreator());
                encounterAttributeValue.setDateCreated(new Date());
                encounterAttributeValue.setUuid(UUID.randomUUID().toString());
                chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
            }    
        }
        catch(Exception e)
        {
            log.error("Error storing encounter attribute value encounterId: {} attributeName: {}", encounter.getEncounterId(), attributeName, e);
        }
    }
    
    /**
     * Return the non-formatted version of the MRN.
     * 
     * @param patient The patient used to retrieve the MRN.
     * @return The MRN with no formatting or null if the MRN cannot be located.
     */
    public static String getMedicalRecordNoFormatting(Patient patient) {
        if (patient != null) {
            PatientIdentifier patientIdentifier = patient.getPatientIdentifier();
            if(patientIdentifier != null) {
                String identifier = patientIdentifier.getIdentifier();
                if(identifier != null){
                    return identifier.replaceAll(ChirdlUtilConstants.GENERAL_INFO_DASH, ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
                }
            }
        }
        return null;
    }
    
    /**
     * CHICA-1125/CHICA-1169 Moved code into util method
     * 
     * Util method to find a patient by MRN_OTHER
     * This method will remove leading zeros from the received mrn
     * Checks to see if the received mrn contains a "-", if not, it will add one before the last character
     * If a match is not found by using the received mrn without leading zeros, a search will be performed by adding 1 leading zero
     * 
     * Example, when the received mrn is 0000999995, a search will be performed using 99999-5, if a match is not found, this method
     * will try 099999-5
     * 
     * @param mrn
     * @return
     */
    public static Patient getPatientByMRNOther(String mrn)
    {
        PatientService patientService = Context.getService(PatientService.class);
        Patient patient = null;
        
        if (StringUtils.isNotEmpty(mrn)) {
            PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
            List<PatientIdentifierType> identifierTypes = new ArrayList<>();
            identifierTypes.add(identifierType);

            List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
            if (!patients.isEmpty()) {
                patient = patients.get(0);
            } else {
            	String updatedMrn = Util.removeLeadingZeros(mrn);
                if (!updatedMrn.contains(ChirdlUtilConstants.GENERAL_INFO_DASH) && updatedMrn.length() > 1) {
                	updatedMrn = updatedMrn.substring(0, updatedMrn.length() - 1) 
                			+ ChirdlUtilConstants.GENERAL_INFO_DASH + updatedMrn.substring(updatedMrn.length()-1);
                }
                
                patients = patientService.getPatientsByIdentifier(null, updatedMrn, identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
                if (patients.isEmpty()){
                    patients = patientService.getPatientsByIdentifier(null, "0" + updatedMrn, identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
                }

                if (!patients.isEmpty()) {
                    patient = patients.get(0);
                }
            }
        }
        
        return patient;
    }
    
    /**
     * CHICA-1234 Moved code from chica module
     * Retrieves the form type for PrimaryPatientForm and PrimaryPhysicianForm
     * @param formId
     * @param locationTagId
     * @param locationId
     * @return Form Type
     */
    public static String getFormType(Integer formId, Integer locationTagId, Integer locationId) {
        
        ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
        FormAttributeValue primaryPatientFormfav = null;
        FormAttributeValue primaryPhysicianFormfav = null;
        if (formId != null && locationId != null && locationTagId != null) {
            primaryPatientFormfav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PATIENT_FORM, locationTagId, locationId);
            primaryPhysicianFormfav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PHYSICIAN_FORM, locationTagId, locationId);
        }
        if (primaryPatientFormfav != null && StringUtils.isNotBlank(primaryPatientFormfav.getValue()) && 
                ChirdlUtilConstants.FORM_ATTR_VAL_TRUE.equalsIgnoreCase(primaryPatientFormfav.getValue())) { 
            return ChirdlUtilConstants.PATIENT_FORM_TYPE;
        } else if (primaryPhysicianFormfav != null && StringUtils.isNotBlank(primaryPhysicianFormfav.getValue()) && 
                ChirdlUtilConstants.FORM_ATTR_VAL_TRUE.equalsIgnoreCase(primaryPhysicianFormfav.getValue())) {
            return ChirdlUtilConstants.PHYSICIAN_FORM_TYPE;
        }
        return null;
    }

    /**
     * Get the patient's EHR MRN (MRN_EHR) identifier.  This will look at the MRN_EHR identifier first and default to the 
     * preferred identifier if it does not exist.
     * 
     * @param patient The patient for identifier lookup
     * @return patient identifier
     */
    public static String getPatientEHRMRNIdentifier(Patient patient) {
        // We ideally want to return the EHR identifier because this will match the EHR identifier better
        PatientIdentifier pi = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR);
        if (pi != null && StringUtils.isNotBlank(pi.getIdentifier())) {
            return pi.getIdentifier();
        }
        
        // Return the preferred identifier
        pi =  patient.getPatientIdentifier();
        return pi == null ? ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING : pi.getIdentifier();
    }
    
    /**
     * Convenience method to lookup form attribute values.
     * 
     * @param locationId The location identifier.
     * @param locationTagId The location tag identifier.
     * @param attributeName The name of the attribute.
     * @param formName The name of the form being accessed.
     * @return The form attribute value or null.
     */
    public static String getFormAttributeValue(
            Integer locationId, Integer locationTagId, String attributeName, String formName) {
        if (StringUtils.isNotBlank(formName)) {
            Form form = Context.getFormService().getForm(formName);
            if (form != null) {
                ChirdlUtilBackportsService chirdlutilbackportsService = 
                		Context.getService(ChirdlUtilBackportsService.class);
                FormAttributeValue formAttributeValue = chirdlutilbackportsService.getFormAttributeValue(
                    form.getFormId(), attributeName, locationTagId, locationId);
                if (formAttributeValue != null) { 
                    return formAttributeValue.getValue();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves the last encounter for a patient or null if one does not exist.
     * 
     * @param patient The patient used to find the encounter.
     * @return Encounter object or null if one does not exist.
     */
    public static Encounter getLastEncounter(Patient patient) {
        List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
        if (encounters == null || encounters.size() == 0) {
            return null;
        }
        
        return encounters.get(encounters.size() - 1);
    }
    
    /**
     * Compares the equality of two objects.
     * @param firstObject The first object to compare.
     * @param secondObject The second object to compare.
     * 
     * @return True if the objects are equal, false otherwise.
     */
    public static boolean compareObjectEquality(Object firstObject, Object secondObject) {
        if (firstObject == null) {
            if (secondObject != null) {
                return false;
            }
        } else if (!firstObject.equals(secondObject)) {
            return false;
        }
        
        return true;
    }
    
    /**
	 * @return the daemonToken
	 */
	public static DaemonToken getDaemonToken() {
		return daemonToken;
	}
	
	/**
	 * @param daemonToken the daemonToken to set
	 */
	public static void setDaemonToken(DaemonToken daemonToken) {
		Util.daemonToken = daemonToken;
	}
	
	/**
	 * Returns the newest numeric value from an observation from the list of results.
	 * 
	 * @param resultList The list of results containing observations
	 * @return Double object or null if one is not found
	 */
	public static Double getLatestNumericValue(List<org.openmrs.logic.result.Result> resultList) {
		Obs obs = getLatestObs(resultList);
		Double value = null;
		if (obs != null) {
			value = obs.getValueNumeric();
		}
		
		return value;
	}
	
	/**
	 * Returns the newest  observation from the list of results.
	 * 
	 * @param resultList The list of results containing observations
	 * @return Obs object or null if one is not found
	 */
	public static Obs getLatestObs(List<org.openmrs.logic.result.Result> resultList) {
		List<Obs> obsList = new ArrayList<>();
		for (org.openmrs.logic.result.Result result : resultList) {
			Object resultObj = result.getResultObject();
			if (resultObj instanceof Obs) {
				Obs obs = (Obs)resultObj;
				if (obs.getValueNumeric() != null) {
					obsList.add((Obs)resultObj);
				}
			}
		}
		
		if (obsList.isEmpty()) {
			return null;
		}
		
		if (obsList.size() == 1) {
			return obsList.get(0);
		}
		
		// Sort the weights so the latest is first
		Collections.sort(obsList, new ObsDateComparator());
		Collections.reverse(obsList);
		
		return obsList.get(0);
	}
	

	/**
	 * Recursive method to delete a directory and all its files and sub-directories.
	 * @param fileOrDirectory The file or directory to be deleted
	 * @return true if the file or directory is successfully deleted
	 */
	public static boolean deleteDirectory(File fileOrDirectory) {
		File[] contents = fileOrDirectory.listFiles();
		if (contents != null) {
			for (File file : contents) {
				deleteDirectory(file);
			}
		}
		return fileOrDirectory.delete();
	}
}
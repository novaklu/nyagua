package nyagua;

/*
 * locUtil library, is a component of Ny-libs: a set of useful libraries 
 *    born as a spin-off of Nyagua - Aquarium Manager - project
 * 
 *    Copyright (C) 2012 Rudi Giacomini Pilon
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */



import java.text.*;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import javax.swing.JOptionPane;
import nyagua.data.Setting;


/**
 * locUtil library allow easy localisation and internalization of programs
 * with a set of functions that allow to validate a manage numbers and dates
 * that user input in languages (locale) that use different formats.
 * Near that standard java formatted numbers and dates can be easily displayed 
 * in user language format.
 * 
 *
 * @author Rudi Giacomini Pilon
 */
public class LocUtil {
    
    private static String defaultDateFormat;
    
    /**
     * set a user defined date format to use 
     * as default as date format for functions that need it
     *      * 
     * @param df    a string with date format
     */
    public static void setDefaultDateFormat(String df){
        defaultDateFormat=df;
    }
    
    
     public static String getCustomCaption (String key) {
        Setting s=Setting.getInstance();
        String customLabel = key;
        
        if (customLabel.equalsIgnoreCase("DENS")) {
            customLabel = "DENS";
        } 
        if (customLabel.equalsIgnoreCase("COND")) {
            customLabel = "COND";
        } 
        if (customLabel.equalsIgnoreCase("KH")) {
            customLabel = "KH";
        } 
        if (customLabel.equalsIgnoreCase("TEMP")) {
            customLabel = "TEMP";
        } 
        if (customLabel.equalsIgnoreCase("salinity")) {
            customLabel = "Salinity";
        } 
        
        
        try {
            customLabel = java.util.ResourceBundle.getBundle(
                "nyagua/Bundle").getString(customLabel); // NOI18N;
        }
        catch (MissingResourceException e) {           
            return key;
        }
        
        String tmpLabel = null;
       
       if (key.equalsIgnoreCase("DENS")) { // NOI18N
           tmpLabel = s.getDensCustomLabel();
       }
       else if (key.equalsIgnoreCase("COND")) { // NOI18N
           tmpLabel = s.getCondCustomLabel();
       }
       else if (key.equalsIgnoreCase("KH")) { // NOI18N
           tmpLabel = s.getKHCustomLabel();
       }
       else if (key.equalsIgnoreCase("TEMP")) { // NOI18N
           tmpLabel = s.getTempCustomLabel();
       }
       else if (key.equalsIgnoreCase("salinity")) { // NOI18N
           tmpLabel = s.getSalinityCustomLabel();
       }
       else {
           return key;
       }
       
       
       if ((tmpLabel != null) && !tmpLabel.isEmpty()) {
           customLabel = tmpLabel;
       }
              
       return customLabel;
    }

    /**
     * 
     * gets a list of strings representing 
     * the short, medium and long date formats for current 
     * locale (the user language/nation settings)
     * 
     * @return the string for short, medium and long date format
     */
    public static String [] getDateFormats (){
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        String sdf0=sdf.toLocalizedPattern();
        sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
        String sdf1=sdf.toLocalizedPattern();
        sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.LONG);
        String sdf2=sdf.toLocalizedPattern();
        String [] df={sdf0,sdf1,sdf2};
        return df;
    }
    
    /**
     * Convert a string representing a (double) number
     * into a localized string with correct decimal separator
     * 
     * @param   toConvert the value to convert
     * @return  a string with converted value
     */
    public static String localizeDouble (String toConvert){
        if (isNumber (toConvert)){
            NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.getDefault());
            DecimalFormat df = (DecimalFormat)nf;
            String pattern="###,###.###";// NOI18N
            Double value=Double.valueOf(toConvert);
            df.applyPattern(pattern);
            return df.format(value);
        } else {
            return toConvert;
        }
    }
    
    /**
     * Convert a string representing a (double) number
     * into a localized string with correct decimal separator
     * 
     * @param   toConvert the value to convert
     * @return  a string with converted value
     */
    public static String localizeCurrency (String toConvert){
        if (isNumber (toConvert)){
            NumberFormat nf = NumberFormat.getCurrencyInstance(
                    java.util.Locale.getDefault());
           
            Double value=Double.valueOf(toConvert);
            return nf.format(value);
        } else {
            return toConvert;
        }
    }
    

    /**
     * Convert a (double) number 
     * into a localized string with correct decimal separator
     * 
     * @param toConvert the value to convert
     * @return  string converted value
     */
    public static String localizeDouble (Double toConvert){
        NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.getDefault());
        DecimalFormat df = (DecimalFormat)nf;
        String pattern="###,###.###";// NOI18N
        df.applyPattern(pattern);
        if (toConvert==null) {
            return "";
        }
        return df.format(toConvert);
    }

    /**
     * Converts a string representing a localized 
     * double or integer into string representing a 
     * number in  standard java numeric format 
     * if all goes wrong it return the original string
     * to avoid to lose data....
     * 
     * @param   sDouble   the number to be converted
     * @return  converted string (the number in java format) or original string
     */
    public static String delocalizeDouble (String sDouble){
        Number n;
        double d;
        int    i;
        if (sDouble==null) {
            sDouble="";
        }
        String sInt =sDouble;
        NumberFormat nfDLocal = NumberFormat.getNumberInstance(),
                nfILocal = NumberFormat.getIntegerInstance();
        ParsePosition pp;
        //String  sDInfo,sIInfo;
        nfDLocal.setMinimumFractionDigits( 1 );
        pp = new ParsePosition( 0 );

        pp.setIndex( 0 );
        n = nfILocal.parse( sInt, pp );
        if( sInt.length() != pp.getIndex()  ||n == null ){ //if not integer
            pp.setIndex( 0 );
            n = nfDLocal.parse( sDouble, pp );
            if( sDouble.length() != pp.getIndex() || n == null ){ //and not double
               //return null;
                //if all goes wrong it's better to return the original string
                //than to lose data....
                return sDouble;

            } else {    //is double
                d = n.doubleValue();
                return Double.toString(d);
            }

        } else  {   //is integer
          i = n.intValue();
          return Integer.toString(i);
        }
      }
    
    /**
     * Converts a string representing a localized 
     * double or integer into string representing a 
     * number in  standard java numeric format 
     * if all goes wrong it return the original string
     * to avoid to lose data....
     * 
     * @param   sDouble   the number to be converted
     * @return  converted string (the number in java format) or original string
     */
    public static String delocalizeCurrency (String sDouble){
        Locale locale = java.util.Locale.getDefault();
        Currency currency= Currency.getInstance(locale);
        String symbol = currency.getSymbol();
        
        String replaceAll = sDouble.replaceAll(symbol, "");
        
        System.out.println("rep=" + replaceAll + "  sdouble=" +sDouble);
        return delocalizeDouble(replaceAll.trim());
           
      }
    

    /**
     * Converts a string representing a date into
     * a localized date string with locale format and separators
     * if it's impossible to format the date then the original string
     * is return to avoid to lose it
     * 
     * @param inDate    date to convert
     * @return          a string with localized date or original string
     */
    public static String localizeDate (String inDate){
        if (inDate==null) {
            return "";
        }        
        DateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");// NOI18N
        Date day = null;        
        try {
            day = (Date) dateFormatter.parse(inDate);
        } catch (ParseException ex) {
        }
        if (defaultDateFormat!=null){
            dateFormatter = new SimpleDateFormat(defaultDateFormat);
        }
        
        if (day!=null){
            return dateFormatter.format(day);
        } else {
            //if it's impossible to format the date then
            //it's better to return the original string
            //to avoid to lose it
            return inDate;
        }
    }

    /**
     * Converts a string representing a date into
     * a localized date string with locale format and separators
     * 
     * @param inDate    date to convert
     * @return          localized date
     */
    public static Date localizeAsDate (String inDate){
        if (inDate==null) {
            return null;
        }   
        DateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");// NOI18N
        Date day = null;
        try {
            day = (Date) dateFormatter.parse(inDate);
        } catch (ParseException ex) {
            if (!(inDate.isEmpty()) ||  !(inDate.matches(""))){
                String str = JOptionPane.showInputDialog( 
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID-DATE"), inDate);
                if(str != null){
                    try {
                        day = (Date) dateFormatter.parse(str);
                    } catch (ParseException err) {
                        System.out.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WRONG-DATE")+": "+str);
                    }
                }
            }
            
        }
        return day;        
    }
    
    /**
     * gets the current date
     *
     * @return a string formatted date with default format or (if unspecified) the format yyyy/MM/dd HH:mm:ss
     */
    public static String getCurrentlocalizedDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//NOI18N
        Date d = new java.util.Date();
        dateFormat = new SimpleDateFormat(defaultDateFormat);
        return dateFormat.format(d);
    }
    
    /**
     * returns the current date
     * @return today date
     */
    public static Date getToday() {
        Date d=new java.util.Date();
        return d;
    }

    /**
     *  Converts a string representing a 
     * localized date into 
     * a standard date string
     * 
     * @param inDate    localized date
     * @return  standard date
     */
    public static String delocalizeDate (String inDate){
        DateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat(defaultDateFormat);
        Date day = null;
        try {
            day = dateFormatter.parse(inDate);
        } catch (ParseException ex) {
        }
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");// NOI18N
        return dateFormatter.format(day);
    }
    
    /**
     *  Converts a  
     * localized date into
     * a standard date string
     * 
     * @param inDate    localized date
     * @return  standard date
     */
    public static String delocalizeDate (Date inDate){
        DateFormat dateFormatter;        
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");// NOI18N
        try {
            return dateFormatter.format(inDate);
        } catch (NullPointerException er){
            return "";
        }
        
    }
    
    /**
     * check if value is a double (or int)
     * 
     * @param in    string that represent a number
     * @return true if is a valid number
     */
    public static boolean isNumber(String in) {
        if (in==null) {
            return false;
        }
        try {
            Double.parseDouble(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Check if the string passed represents a valid date
     * in choosed date format
     * 
     * @param  date     a string representing a date
     * @return true if valid date
     */
    public static boolean isValidDate(String date) {
        //DateFormat sdf =  DateFormat.getDateInstance(DateFormat.SHORT,java.util.Locale.getDefault());
        if (date.isEmpty() || date == null || date.matches("")) {
            return true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(defaultDateFormat);
        // our converted string
        Date testDate;
        try {
            testDate = sdf.parse(date);
        } catch (ParseException e){
            return false;
        }
        // dateformat.parse will accept any date as long as it's in the format
        // you defined, it simply rolls dates over, for example, december 32
        // becomes jan 1 and december 0 becomes november 30
        // This statement will make sure that once the string
        // has been checked for proper formatting that the date is still the
        // date that was entered, if it's not, we assume that the date is invalid
           /* System.out.println("Second test");
            System.out.println("Checking:" + date);
            System.out.println("Against:" + sdf.format(testDate));*/
        if (!sdf.format(testDate).equals(date))
        {
         //errorMessage = "The date that you provided is invalid.";
          return false;
        }
        return true;
    } // end isValidDate

    /**
     * Check if the date passed represents a valid date
     * in choosed date format
     * 
     * @param date
     * @return true if valid date
     */
    public static boolean isValidDate(Date date) {        
        if (date == null) {
            return true;
        }
        try {
            Date copyDate = date;
        if (date != copyDate){
            return false;
            } 
        }finally {
        }
        return true;
    } // end isValidDate
    
    

    /**
     * Parse the new (custom) date format and  check if it's sintax is valid.
     * Following strings can be used to build the new format but note that not 
     * all combination are valid.
     * 
     * G 	Era designator 	Text 	AD
     * y 	Year 	Year 	1996; 96
     * M 	Month in year 	Month 	July; Jul; 07
     * w 	Week in year 	Number 	27
     * W 	Week in month 	Number 	2
     * D 	Day in year 	Number 	189
     * d 	Day in month 	Number 	10
     * F 	Day of week in month 	Number 	2
     * E 	Day in week 	Text 	Tuesday; Tue
     * a 	Am/pm marker 	Text 	PM
     * H 	Hour in day (0-23) 	Number 	0
     * k 	Hour in day (1-24) 	Number 	24
     * K 	Hour in am/pm (0-11) 	Number 	0
     * h 	Hour in am/pm (1-12) 	Number 	12
     * m 	Minute in hour 	Number 	30
     * s 	Second in minute 	Number 	55
     * S 	Millisecond 	Number 	978
     * z 	Time zone 	General time zone 	Pacific Standard Time; PST; GMT-08:00
     * Z 	Time zone 	RFC 822 time zone 	-0800
     * 
     * 
     * @param df the new date format
     * @return  true for a valid format
     */
    public static boolean validateDateFormat (String df){        
        Date today=new Date();        
        try {
            SimpleDateFormat newDateFormatter = new SimpleDateFormat(df);
            String newDay=newDateFormatter.format(today);
            
        } catch (IllegalArgumentException e) {
            return false;
        }   
        return true;
    }

}

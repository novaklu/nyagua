/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon *
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
 */


package nyagua;

import java.awt.Color;

/**
 * This class provides global variables
 * 
 * @author Rudi Giacomini Pilon
 * @version 5.0
 * 
 * 
 */
public class Global {
     /** Application work directory*/
    public static String WorkDir;    
    /** Database File name with fullpath*/ 
    public static String FullFileName;   
    /** KH  units */
    public static String khunit;
    /** lenght  units */
    public static String lenghtunit;
    /** vol  units */
    public static String volunit;
    /** temperature  units */
    public static String temperatureunit;    
     /** Density custom  units */
    public static String densCustomUnit;
     /** Conductivity custom  units */
    public static String condCustomUnit;
     /** Salinity Custom  units */
    public static String salinityCustomUnit;
    /** selected acquarium */
    public static int AqID;
    /**Query Starting date*/
    public static String dFrom="";//NOI18N
    /**Query Ending date*/
    public static String dTo="";//NOI18N    
    /**Decimal separator*/
    //public static char DEC_SEPARATOR= '.';
    /**Date Format*/
    public static String dateFormat;//NOI18N
    /**filter state*/
    public static boolean filterState=false;
    //*Button standard Background color*/
    public static final Color BUTTON_GREY= new Color(238,238,238);
    public static final Color BUTTON_RED=Color.red;
    /** use saved units in forms **/
    public static boolean useUnitsInForms;
    /** use saved units in reports **/
    public static boolean useUnitsInReports;
    /** generate all reports when aquarium report is generated **/
    public static boolean generateAllReports;
    /** include expenses report in all report generation and on links 
     ** default to false for privacy when pages could be published on web :-)     **/
    public static boolean includeExpensesReport;
    /** report pie charts size **/ 
    public static int reportChartWidth;
    public static int reportChartHeight;
    /** true only during first program startup **/ 
    public static boolean firstStartup = false;
    
    public static boolean isSchedulerEnabled=true;
    /**export record separator**/
    public static String RECSEPARATOR="|:|";
}

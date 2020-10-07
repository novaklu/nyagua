/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon
 *    Copyright (C) 2010 Tom Judge <tom(at)tomjudge.com>
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

package nyagua.data;

import java.io.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import nyagua.Application;
import nyagua.Global;
import nyagua.LocUtil;
import nyagua.XMLFilter;

/**
 * Implements preferences
 * saving and retrieving operations
 *
 * @author Rudi Giacomini Pilon
 */
public class Setting {    
    
    //Settings settings
    private final static String SETTING_VERSION = "setting_version";// NOI18N
    private final static double DEFAULT_SETTING_VERSION=0.00;
    private final static double CURRENT_SETTING_VERSION=1.00;
    
    //General settings
    private final static String BROWSER = "browser";// NOI18N
    private final static String DEFAULT_BROWSER = "firefox";// NOI18N
    private final static String KWCOST ="kwcost";// NOI18N
    private final static double DEFAULT_KWCOST=0.00;
    private final static String USE_GNUPLOT="gnuplot";// NOI18N
    private final static boolean DEFAULT_USE_GNUPLOT=false;
    //win and tree positions
    private final static String WIN_POS_SAVE="save_win_pos";// NOI18N
    private final static boolean DEFAULT_WIN_POS_SAVE=false;
    private final static String WIN_POS_X="x";// NOI18N
    private final static int DEFAULT_WIN_POS_X=0;   
    private final static String WIN_POS_Y="y";// NOI18N
    private final static int DEFAULT_WIN_POS_Y=0;
    private final static String WIN_POS_WIDTH="width";// NOI18N
    private final static int DEFAULT_WIN_POS_WIDTH=741;
    private final static String WIN_POS_HEIGHT="height";// NOI18N
    private final static int DEFAULT_WIN_POS_HEIGHT=624;
    private final static String TREE_POS_WIDTH="tree_width";// NOI18N
    private final static int DEFAULT_TREE_POS_WIDTH=120;
    //Plotting settings
    private final static String PL_CONFIGURATIONS="pl_configurations";// NOI18N
    private final static String PL_CONFIGURATION="pl_configuration";// NOI18N
    private final static String DEFAULT_PL_CONFIGURATION="";// NOI18N
    
    private final static String PL_OUTPUT="pl_output";// NOI18N
    private final static String DEFAULT_PL_OUTPUT="video";// NOI18N
    private final static String PL_GRID ="pl_grid";// NOI18N
    private final static boolean DEFAULT_PL_GRID=true;
    private final static String PL_NO2 ="pl_NO2";// NOI18N
    private final static boolean DEFAULT_PL_NO2=true;
    private final static String PL_TEMP ="pl_temp";// NOI18N
    private final static boolean DEFAULT_PL_TEMP=true;
    private final static String PL_NO3 ="pl_NO3";// NOI18N
    private final static boolean DEFAULT_PL_NO3=true;
    private final static String PL_NH ="pl_NH";// NOI18N
    private final static boolean DEFAULT_PL_NH=true;
    private final static String PL_KH ="pl_KH";// NOI18N
    private final static boolean DEFAULT_PL_KH=true;
    private final static String PL_GH ="pl_GH";// NOI18N
    private final static boolean DEFAULT_PL_GH=true;
    private final static String PL_FE ="pl_FE";// NOI18N
    private final static boolean DEFAULT_PL_FE=true;
    private final static String PL_PH ="pl_PH";// NOI18N
    private final static boolean DEFAULT_PL_PH=true;
    private final static String PL_KEY ="pl_Key";// NOI18N
    private final static String DEFAULT_PL_KEY="K";// NOI18N
    private final static String PL_GRIDSTEP ="pl_Gridstep";// NOI18N
    private final static String DEFAULT_PL_GRIDSTEP="1";
    private final static String PL_CO2 ="pl_CO2";// NOI18N
    private final static boolean DEFAULT_PL_CO2=true;
    private final static String PL_COND ="pl_Cond";// NOI18N
    private final static boolean DEFAULT_PL_COND=true;
    private final static String PL_CA ="pl_CA";// NOI18N
    private final static boolean DEFAULT_PL_CA=true;
    private final static String PL_MG ="pl_MG";// NOI18N
    private final static boolean DEFAULT_PL_MG=true;
    private final static String PL_CU ="pl_CU";// NOI18N
    private final static boolean DEFAULT_PL_CU=true;
    private final static String PL_PO4 ="pl_PO4";// NOI18N
    private final static boolean DEFAULT_PL_PO4=true;
    private final static String PL_O2 ="pl_O2";// NOI18N
    private final static boolean DEFAULT_PL_O2=true;
    private final static String PL_DENS ="pl_DENS";// NOI18N
    private final static boolean DEFAULT_PL_DENS=true;
    private final static String PL_NH3 ="pl_NH3";// NOI18N
    private final static boolean DEFAULT_PL_NH3=true;
    private final static String PL_I2 ="pl_I2";// NOI18N
    private final static boolean DEFAULT_PL_I2=true;
    private final static String PL_NA ="pl_NA";// NOI18N
    private final static boolean DEFAULT_PL_NA=true;
    private final static String DATE_FORMAT="Date_Format";// NOI18N
    private final static String DEFAULT_DATE_FORMAT="MM-dd-yy";// NOI18N
    //path settings
    private final static String DATA_FILE_PATH="File_Path";// NOI18N
    private final static String DEFAULT_DATA_FILE_PATH= Global.FullFileName;
    //report settings
    private final static String LOGO = "logo";// NOI18N
    private final static String DEFAULT_LOGO="";// NOI18N
    private final static String UTF = "utf8";// NOI18N
    private final static boolean DEFAULT_UTF=true;// NOI18N
    private final static String TITLECOLOR ="titleColor";// NOI18N
    private final static String DEFAULT_TITLECOLOR="3AC4FF";
    private final static String THEME="Theme";//NOI18N
    public final static String THEME_STANDARD="Standard";//NOI18N
    public final static String THEME_SHADOW="Shad";//NOI18N
    public final static String THEME_GREEN="Green";//NOI18N
    public final static String THEME_ELEGANT="Elegant";//NOI18N
    private final static String FOOTER ="footer";// NOI18N
    private final static String DEFAULT_FOOTER="";// NOI18N
    private final static String USER_CSS = "user_css";// NOI18N
    private final static String DEFAULT_USER_CSS="";// NOI18N
    private final static String CHART_WIDTH="ReportChartWidth";// NOI18N
    private final static int    DEFAULT_CHART_WIDTH=500;
    private final static String CHART_HEIGHT="ReportChartHeight";// NOI18N
    private final static int    DEFAULT_CHART_HEIGHT=300;
    private final static String GENERATE_COMPLETE_REPORT = "generate_complete";// NOI18N
    private final static boolean DEFAULT_GENERATE_COMPLETE_REPORT=true;// NOI18N
    private final static String INCLUDE_EXPENSES_REPORT = "include_expns";// NOI18N
    private final static boolean DEFAULT_INCLUDE_EXPENSES_REPORT=false;// NOI18N
    
    //Scheduler settings
    private final static String STARTUP_FORM = "StartupForm";// NOI18N
    private final static int DEFAULT_S_FORM=0;// NOI18N  //default to aquarium form
    private final static String ASK_FOR_S_FORM = "AskStartupForm";// NOI18N
    private final static boolean DEFAULT_ASK_S_FORM=true;// NOI18N
    private final static String SCHEDULER_ENABLED = "scheduler_disabled";// NOI18N
    private final static boolean DEFAULT_SCHEDULER_ENABLED=true;// NOI18N
    
    //compatibility settings
    private final static String COMP_CONFIGURATIONS="comp_configurations";// NOI18N
    private final static String COMP_CONFIGURATION="comp_configuration";// NOI18N
    private final static String DEFAULT_COMP_CONFIGURATION="";// NOI18N
    
    private final static String COMP_OUTPUT="comp_output";// NOI18N
    private final static String DEFAULT_COMP_OUTPUT="video";// NOI18N
    private final static String COMP_GRID ="comp_grid";// NOI18N
    private final static boolean DEFAULT_COMP_GRID=false;
    private final static String COMP_GRIDSTEP ="comp_Gridstep";// NOI18N
    private final static String DEFAULT_COMP_GRIDSTEP="1";
    private final static String COMP_KH ="comp_KH";// NOI18N
    private final static boolean DEFAULT_COMP_KH=true;
    private final static String COMP_PH ="comp_PH";// NOI18N
    private final static boolean DEFAULT_COMP_PH=true;
    private final static String COMP_TEMP ="comp_temp";// NOI18N
    private final static boolean DEFAULT_COMP_TEMP=true;
    private final static String COMP_VALUE="comp_value";// NOI18N
    private final static String DEFAULT_COMP_VALUE="last";// NOI18N
    private final static String COMP_SUBJ="comp_subject";// NOI18N
    private final static String DEFAULT_COMP_SUBJ="fish";// NOI18N
    private final static String COMP_KEY ="comp_Key";// NOI18N
    private final static String DEFAULT_COMP_KEY="K";// NOI18N
    //tables sizes
    private final static String COL_WIDTH="_column_width_coln_";// NOI18N
    private final static String TABLE_NAME="table_";// NOI18N
    private final static int    DEFAULT_COL_WIDTH=40;
    //units
    private final static String UNITS_IN_FORMS ="units_in_forms";// NOI18N
    private final static boolean DEFAULT_UNITS_IN_FORMS=true;    
    private final static String UNITS_IN_REPORTS ="units_in_reports";// NOI18N
    private final static boolean DEFAULT_UNITS_IN_REPORTS=true;
    private final static String UNITS_WATER_HARDNESS ="units_w_hard";// NOI18N
    private final static String DEFAULT_UNITS_WATER_HARDNESS="degree";// NOI18N
    //private final static String NON_DEFAULT_UNITS_WATER_HARDNESS="ppm";// NOI18N    
    private final static String UNITS_TEMP ="units_temp";// NOI18N
    private final static String DEFAULT_UNITS_TEMP="C";// NOI18N
    //private final static String NON_DEFAULT_UNITS_TEMP="F";// NOI18N
    private final static String UNITS_VOL ="units_vol";// NOI18N
    private final static String DEFAULT_UNITS_VOL="l";// NOI18N
    //private final static String NON_DEFAULT_UNITS_VOL="usGal";// NOI18N
    private final static String UNITS_LENGHT ="units_lenght";// NOI18N
    private final static String DEFAULT_UNITS_LENGHT="cm";// NOI18N
    //private final static String NON_DEFAULT_UNITS_LENGHT="inch";// NOI18N
    
    private final static String FISHBASE_SITE ="fishbase_site";// NOI18N
    private final static String DEFAULT_FISHBASE_SITE="www.fishbase.org";// NOI18N    
    private final static String FISHBASE_TIMEOUT="fishbase_timeout";// NOI18N
    private final static int    DEFAULT_FISHBASE_TIMEOUT=40;
    
    private static final String ENABLE_EXPENSES_PRESETS = "enable_expenses_presets"; //NOI18N
    private final static boolean DEFAULT_EXPENSES_PRESETS_ENABLED=true;// NOI18N
    private static final String ENABLE_MAINTENANCE_PRESETS = "enable_maintenance_presets"; //NOI18N
    private final static boolean DEFAULT_MAINTENANCE_PRESETS_ENABLED=true;// NOI18N
    
    private static final String CUSTOM_LABEL_DENS="custom_label_for_dens";// NOI18N
    private static final String CUSTOM_LABEL_COND="custom_label_for_cond";// NOI18N
    private static final String CUSTOM_LABEL_KH="custom_label_for_kh";// NOI18N
    private static final String CUSTOM_LABEL_TEMP="custom_label_for_temp";// NOI18N
    private static final String CUSTOM_LABEL_NA="custom_label_for_salinity";// NOI18N
    
    private static final String CUSTOM_UNITS_DENS="custom_units_for_dens";// NOI18N
    private static final String CUSTOM_UNITS_COND="custom_units_for_cond";// NOI18N
    private static final String CUSTOM_UNITS_NA="custom_units_for_salinity";// NOI18N
    
    private final Preferences prefs;

    private final static Setting INSTANCE = new Setting();
    
    ResourceBundle rBundle = java.util.ResourceBundle.getBundle("nyagua/Bundle");

    private Setting() {
        prefs = Preferences.userNodeForPackage(Setting.class);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Settings Version Methods">
    
    
    public double getSettingVersion(){
        return prefs.getDouble(SETTING_VERSION, DEFAULT_SETTING_VERSION);
    }

    
    public void setSettingVersion(){
        prefs.putDouble(SETTING_VERSION, CURRENT_SETTING_VERSION);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Compatibility Settings Access Methods">
    
    /**
     * gets default compatibility configuration setting
     *
     * @return configuration name
     */
     public String getCompConfiguration(){
        return prefs.get(COMP_CONFIGURATION , DEFAULT_COMP_CONFIGURATION);
    }

     /**
      * set default compatibility configuration
      *
      * @param configuration name
      */
    public void setCompConfiguration(String configuration){         
        prefs.put(COMP_CONFIGURATION, configuration);
    }
    
    /**
     * gets default compatibility configuration setting
     *
     * @return configurations names
     */
     public String []  getCompConfigurations(){
         String configurations =  prefs.get(
                 COMP_CONFIGURATIONS , DEFAULT_COMP_CONFIGURATION);
         if (!configurations.isEmpty()) {
             return configurations.split("#!");
         }
        return null;
    }

     /**
      * set default compatibility configuration
      *
     * @param configurations
      */
    public void setCompConfigurations(String [] configurations){         
        if (configurations.length > 0) {
            StringBuilder sb = new StringBuilder(configurations[0]);
            for (int i = 1; i < configurations.length; i++) {
                sb.append("#!");
                sb.append(configurations[i]);
            }
            prefs.put(COMP_CONFIGURATIONS, sb.toString());
        }
    }

    /**
     * get compatibility subject to compare
     *
     * @param configuration
     * @return [fish|plants|both]
     */
    public String getCompSubj(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(COMP_SUBJ + configuration,DEFAULT_COMP_SUBJ);
    }

    /**
     * * set compatibility subject to compare
     *
     * @param c_subj [fish|plants]
     * @param configuration
     */
    public  void setCompSubj(String c_subj, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(COMP_SUBJ + configuration,c_subj);
    }

    /**
     * get compatibility value to compare
     *
     * @param configuration
     * @return [selected|last|medium]
     */
    public String getCompValue(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(COMP_VALUE + configuration,DEFAULT_COMP_VALUE);
    }

    /**
     * * set compatibility value to compare
     *
     * @param c_value [selected|last|medium]
     * @param configuration
     */
    public  void setCompValue(String c_value,String configuration){
         if (configuration == null) configuration ="";
        prefs.put(COMP_VALUE + configuration,c_value);
    }

    /**
     * get compatibility output device
     *
     * @param configuration
     * @return [video|png|ps file]
     */
    public String getCompOutput(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(COMP_OUTPUT + configuration,DEFAULT_COMP_OUTPUT);
    }

    /**
     * * set compatibility output device
     *
     * @param output [video|png|ps file]
     * @param configuration
     */
    public  void setCompOutput(String output, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(COMP_OUTPUT + configuration,output);
    }

    /**
     * Get if grid is showed
     *
     * @param configuration
     * @return true if showed
     */
    public boolean getComp_Grid(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(COMP_GRID + configuration, DEFAULT_COMP_GRID);
    }

    /**
     * Set if grid is showed
     *
     * @param comp_Grid [true | false]
     * @param configuration
     */
    public void setComp_Grid(boolean comp_Grid, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(COMP_GRID + configuration, comp_Grid);
    }

    public String getComp_Key(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(COMP_KEY + configuration, DEFAULT_COMP_KEY);
    }

    public void setComp_Key(String comp_Key, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(COMP_KEY + configuration, comp_Key);
    }

    /**
     * get grid step in compatibility plot
     *
     * @param configuration
     * @return  the step size
     */
    public String getComp_Gridstep(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(COMP_GRIDSTEP + configuration, DEFAULT_COMP_GRIDSTEP);
    }

    /**
     * set grid step in compatibility plot
     *
     * @param comp_Gridstep
     * @param configuration
     */
    public void setComp_Gridstep(String comp_Gridstep,String configuration){
         if (configuration == null) configuration ="";
        prefs.put(COMP_GRIDSTEP + configuration, comp_Gridstep);
    }

    public boolean getComp_KH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(COMP_KH + configuration, DEFAULT_COMP_KH);
    }

    public void setComp_KH(boolean comp_KH,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(COMP_KH + configuration, comp_KH);
    }

    public boolean getComp_PH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(COMP_PH + configuration, DEFAULT_COMP_PH);
    }

    public void setComp_PH(boolean comp_PH,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(COMP_PH + configuration, comp_PH);
    }

    public boolean getComp_Temp(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(COMP_TEMP + configuration, DEFAULT_COMP_TEMP);
    }

    public void setComp_Temp(boolean comp_TEMP, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(COMP_TEMP + configuration, comp_TEMP);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Appearance Settings Access Methods">
    /**
     * retrieve the table columns width
     * 
     * @param tableName table string identifier
     * @param columns number of columns
     * @return array of column widths
     */
    public int [] getTableWidths(String tableName, int columns) {        
        int [] widths=new int [columns];
        for (int y=0;y<columns; y++){ // for each column
                String setting=TABLE_NAME+tableName+COL_WIDTH+Integer.toString(y);                
                widths[y]=prefs.getInt(setting, DEFAULT_COL_WIDTH);
            } 
        return widths;
    }
    
   
    /**
     * save Column widths of a tables passed as parameter
     * 
     * @param tableName the string that identify table
     * @param table    table pointer
     */
    public void setTableWidths(String tableName, JTable table) {        
        for (int y=0;y<table.getColumnCount(); y++){ // for each column
            String setting=TABLE_NAME+tableName+COL_WIDTH+Integer.toString(y);
            TableColumn col = table.getColumnModel().getColumn(y);
            prefs.putInt(setting, col.getWidth());
            //System.out.println("Saving " + setting + " val:" + col.getWidth());
        }         
    }
     // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Global Settings Access Methods">
    /**
     * get Browser setting 
     * 
     * @return browser path
     */
    public String getBrowser() {
        return prefs.get(BROWSER,DEFAULT_BROWSER);
    }

    /**
     * set browser setting
     * @param browser path
     */
    public void setBrowser(String browser) {
        prefs.put(BROWSER, browser);
    }
    
    /**
     * get info about to use gnuplot 
     * or internal library to plot
     * 
     * @return true if user prefer to use gnuplot
     */
    public boolean getUsegnuplot(){
        return prefs.getBoolean(USE_GNUPLOT, DEFAULT_USE_GNUPLOT);
    }
    
    /**
     * set if user want to use gnuplot
     * or internal  library to plot
     * 
     * @param useit (true = gnuplot|false = internal library)
     */
    public void setUsegnuplot (boolean useit) {
        prefs.putBoolean(USE_GNUPLOT,useit);
    }
    
    /**
     * get info about to use UTF format for reports
     * 
     * @return true if user prefer to use utf8
     */
    public boolean getUseUTFFormat(){
        return prefs.getBoolean(UTF, DEFAULT_UTF);
    }
    
    /**
     * set if user want to use use UTF format for reports
     * 
     * @param useit (true = utf8|false = no pre encoding)
     */
    public void setUseUTFFormat (boolean useit) {
        prefs.putBoolean(UTF,useit);
    }
    
    /**
     * check if user want to save 
     * main window's position and size
     * 
     * @return true=want to save| false=don't want save
     */
    public boolean getSaveWinPosition(){
        return prefs.getBoolean(WIN_POS_SAVE, DEFAULT_WIN_POS_SAVE);
    }
    
    /**
     * set if user want to save 
     * main window's position and size
     * 
     * @param useit [true if want to save| false don't save]
     */
    public void setSaveWinPosition (boolean useit) {
        prefs.putBoolean(WIN_POS_SAVE,useit);
    }
    
    /**
     * Save the main window position and size
     * 
     * @param x     upper left corner x
     * @param y     upper left corner y
     * @param width     window width
     * @param height    window height
     */
    public void saveWinPosition (int x, int y, int width, int height){
        prefs.putInt(WIN_POS_X, x);
        prefs.putInt(WIN_POS_Y, y);
        prefs.putInt(WIN_POS_WIDTH, width);
        prefs.putInt(WIN_POS_HEIGHT, height);
    }
    
    /**
     * Load main window's position and size
     * 
     * @return position array (0=x;1=y;2=width;3=height)
     */
    public int[] loadWinPosition(){
        int [] position=new int[4];
        position[0]=prefs.getInt(WIN_POS_X, DEFAULT_WIN_POS_X);
        position[1]=prefs.getInt(WIN_POS_Y, DEFAULT_WIN_POS_Y);
        position[2]=prefs.getInt(WIN_POS_WIDTH, DEFAULT_WIN_POS_WIDTH);
        position[3]=prefs.getInt(WIN_POS_HEIGHT, DEFAULT_WIN_POS_HEIGHT);
        return position;
    }
    
    /**
     * Save the main tree width
     * 
     * @param w     width
     */
    public void setTreeWidth (int w){
        prefs.putInt(TREE_POS_WIDTH, w);
    }
    
    /**
     * Retrieve the main tree width
     * 
     * @return  an int representing width
     */
    public int getTreeWidth () {
        return prefs.getInt(TREE_POS_WIDTH, DEFAULT_TREE_POS_WIDTH);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Report Settings Access Methods">
    /***
     * Get logo settings
     * @return  logo path
     */
    public String getLogo() {
        return prefs.get(LOGO,DEFAULT_LOGO);
    }

    /**
     * set logo setting
     * @param logo path
     */
    public void setLogo(String logo) {
        prefs.put(LOGO, logo);
    }

     /***
     * Get report theme settings
     * @return  theme name
     */
    public String getTheme() {
        return prefs.get(THEME,THEME_STANDARD);
    }

    /**
     * set report theme setting
     * @param theme name
     */
    public void setTheme(String theme) {
        prefs.put(THEME, theme);
    }

    /**
     * get title color as hex string
     * @return title color
     */
    public String getTitleColor(){
        return prefs.get(TITLECOLOR, DEFAULT_TITLECOLOR);
    }

    /**
     * set title color as hex string
     *
     * @param titleColor the color to use for title 
     */
    public void setTitleColor(String titleColor){
        prefs.put(TITLECOLOR, titleColor);
    }
    
    /**
     * get default report footer
     * @return the string to use as report footer
     */
    public String getFooter(){
        return prefs.get(FOOTER, DEFAULT_FOOTER);
    }

    /**
     * set default report footer
     *
     * @param footer the string used as report footer 
     */
    public void setFooter(String footer){
        prefs.put(FOOTER, footer);
    }
    
    /**
     * get default report Css file
     * @return the string to use as Css file path
     */
    public String getCss(){
        return prefs.get(USER_CSS, DEFAULT_USER_CSS);
    }

    /**
     * set default report Css file
     *
     * @param css the string used as Css file path 
     */
    public void setCss(String css){
        prefs.put(USER_CSS, css);
    }
    
    /**
     * retrieve the report chart width
     * 
     * @return int width
     */
    public int getChartWidth() {
        int chartWidth=prefs.getInt(CHART_WIDTH, DEFAULT_CHART_WIDTH);
        return chartWidth;
    }
    
    /**
     *  save the report chart width
     *
     * @param chartWidth The chart width
     */
    public void setChartWidth(int chartWidth) {  
        prefs.putInt(CHART_WIDTH,chartWidth);
    }
    
     /**
     * retrieve the report chart height
     * 
     * @return int height
     */
    public int getChartHeight() {
        int chartHeight=prefs.getInt(CHART_HEIGHT, DEFAULT_CHART_HEIGHT);
        return chartHeight;
    }
    
    /**
     *  save the report chart height
     *
     * @param chartHeight The chart height
     */
    public void setChartHeight(int chartHeight) {  
        prefs.putInt(CHART_HEIGHT,chartHeight);
    }
    
    /**
     * set if user want to generate all reports with aquarium reports 
     * and include related links
     * 
     * @param generate  
     */
    public void setGenerateCompleteReport (boolean generate) {
        prefs.putBoolean(GENERATE_COMPLETE_REPORT,generate);
    }
    
    /**
     * get info about user want to generate all reports with aquarium reports 
     * 
     * @return true if yes
     */
    public boolean getGenerateCompleteReport(){
        return prefs.getBoolean(
                GENERATE_COMPLETE_REPORT, DEFAULT_GENERATE_COMPLETE_REPORT);
    }
    
    /**
     * set if user want to generate also expenses report with aquarium report 
     * and include related links
     * 
     * @param generate  
     */
    public void setIncludeExpensesReport (boolean generate) {
        prefs.putBoolean(INCLUDE_EXPENSES_REPORT,generate);
    }
    
    /**
     * get info about user want to generate also expenses report 
     * with aquarium report generation
     * 
     * @return true if yes
     */
    public boolean getIncludeExpensesReport(){
        return prefs.getBoolean(
                INCLUDE_EXPENSES_REPORT, DEFAULT_INCLUDE_EXPENSES_REPORT);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Path e var Settings Access Methods">

    /**
     * get DataFile Path setting
     *
     * @return Data File  full path
     */
    public String getDataFilePath() {
        return prefs.get(DATA_FILE_PATH,DEFAULT_DATA_FILE_PATH);
    }

    /**
     * set DataFilePath setting
     * @param DataFilePath - Data File full path
     */
    public void setDataFilePath(String DataFilePath) {
        prefs.put(DATA_FILE_PATH, DataFilePath);
    }

    /**
     * get KW/h cost in currency
     * @return KW/H cost
     */
    public double getKwCost(){
        return prefs.getDouble(KWCOST, DEFAULT_KWCOST);
    }

    /**
     * set KW/h cost in currency
     * 
     * @param kwCost
     */
    public void setKwCost(double kwCost){
        prefs.putDouble(KWCOST, kwCost);
    }

     /**
     * get Date Format setting
     *
     * @return Date Format String
     */
    public String getDateFormat() {
        String [] defLocale = LocUtil.getDateFormats();
        String df=prefs.get(DATE_FORMAT,defLocale[0]);
        if (df==null) {
            df=DEFAULT_DATE_FORMAT;
        }
        return df;
    }

    /**
     * set Date Format  setting
     * @param dateFormat string
     */
    public void setDateFormat(String dateFormat) {
        if (dateFormat==null) {
            dateFormat=DEFAULT_DATE_FORMAT;
        }
        prefs.put(DATE_FORMAT, dateFormat);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Scheduler Access Methods">
    /**
     * Get if to ask which form to show by default
     *
     * @return true if question showed
     */
    public boolean getAskForDefaultForm(){
        return prefs.getBoolean(ASK_FOR_S_FORM, DEFAULT_ASK_S_FORM);
    }

    /**
     * Set if to ask which form to show by default
     *
     * @param ask [true | false]
     */
    public void setAskForDefaultForm(boolean ask){
        prefs.putBoolean(ASK_FOR_S_FORM, ask);
    }
    
    /**
     * get startup form
     * @return int 0=aquarium | 1=scheduler
     */
    public int getStartupForm(){
        return prefs.getInt(STARTUP_FORM, DEFAULT_S_FORM);
    }

    /**
     * set startup form
     * 
     * @param form  0=aquarium | 1=scheduler
     */
    public void setStartupForm(int form){
        prefs.putInt(STARTUP_FORM, form);
    }
    
    /**
     * verify if scheduler is enabled
     *
     * @return true if enabled
     */
    public boolean isSchedulerEnabled() {
        return prefs.getBoolean(SCHEDULER_ENABLED, DEFAULT_SCHEDULER_ENABLED);
    }

    /**
     * Enable and disable scheduler
     *
     * @param enable [true | false]
     */
    public void setSchedulerEnabled(boolean enable){
        prefs.putBoolean(SCHEDULER_ENABLED, enable);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fishbase Access Methods">
   
    /**
     * 
     * @return 
     */
    public String getFishbaseSite(){
        return prefs.get(FISHBASE_SITE, DEFAULT_FISHBASE_SITE);
    }

    /**
     * 
     * @param hostname 
     */
    public void setFishbaseSite(String  hostname){
        prefs.put(FISHBASE_SITE, hostname);
    }
    
    /**
     * 
     * @return timeout
     */
    public int getTimeout(){
        return prefs.getInt(FISHBASE_TIMEOUT, DEFAULT_FISHBASE_TIMEOUT);
    }
    
    /**
     * 
     * 
     * @param timeout 
     */
    public void setTimeout(int timeout){
        prefs.putInt(FISHBASE_TIMEOUT, timeout);
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Plot Settings Access Methods">

    /**
     * gets default plot configuration setting
     *
     * @return configuration name
     */
     public String getPlConfiguration(){
        return prefs.get(PL_CONFIGURATION , DEFAULT_PL_CONFIGURATION);
    }

     /**
      * set default plot configuration
      *
      * @param configuration name
      */
    public void setPlConfiguration(String configuration){         
        prefs.put(PL_CONFIGURATION, configuration);
    }
    
    /**
     * gets default plot configuration setting
     *
     * @return configurations names
     */
     public String []  getPlConfigurations(){
         String configurations =  prefs.get(
                 PL_CONFIGURATIONS , DEFAULT_PL_CONFIGURATION);
         if (!configurations.isEmpty()) {
             return configurations.split("#!");
         }
        return null;
    }

     /**
      * set default plot configuration
      *
     * @param configurations
      */
    public void setPlConfigurations(String [] configurations){         
        if (configurations.length > 0) {
            StringBuilder sb = new StringBuilder(configurations[0]);
            for (int i = 1; i < configurations.length; i++) {
                sb.append("#!");
                sb.append(configurations[i]);
            }
            prefs.put(PL_CONFIGURATIONS, sb.toString());
        }
    }
    
    /**
     * gets default plot output setting
     *
     * @param configuration
     * @return video | png | postscript
     */
     public String getPl_Output(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(PL_OUTPUT + configuration, DEFAULT_PL_OUTPUT);
    }

     /**
      * set default plot output
      *
      * @param pl_Output [video | png | postscript]
     * @param configuration
      */
    public void setPl_Output(String pl_Output, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(PL_OUTPUT + configuration, pl_Output);
    }
    
    /**
     * Get if grid is showed setting
     *
     * @param configuration
     * @return true if showed
     */
    public boolean getPl_Grid(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_GRID + configuration, DEFAULT_PL_GRID);
    }

    /**
     * Set if grid is showed setting
     *
     * @param pl_Grid [true | false]
     * @param configuration
     */
    public void setPl_Grid(boolean pl_Grid, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_GRID + configuration, pl_Grid);
    }

    public boolean getPl_NO2(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_NO2 + configuration, DEFAULT_PL_NO2);
    }

    public void setPl_NO2(boolean pl_NO2, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_NO2 + configuration, pl_NO2);
    }
    
    public boolean getPl_I2(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_I2 + configuration, DEFAULT_PL_I2);
    }

    public void setPl_I2(boolean pl_I2, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_I2 + configuration, pl_I2);
    }
    
     public boolean getPl_NA(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_NA + configuration, DEFAULT_PL_NA);
    }

    public void setPl_NA(boolean pl_NA, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_NA + configuration, pl_NA);
    }

    public boolean getPl_Temp(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_TEMP + configuration, DEFAULT_PL_TEMP);
    }

    public void setPl_Temp(boolean pl_Temp, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_TEMP + configuration, pl_Temp);
    }
    
    public boolean getPl_NO3(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_NO3 + configuration, DEFAULT_PL_NO3);
    }

    public void setPl_NO3(boolean pl_NO3,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_NO3 + configuration, pl_NO3);
    }
    
    public boolean getPl_NH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_NH + configuration, DEFAULT_PL_NH);
    }

    public void setPl_NH(boolean pl_NH, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_NH + configuration, pl_NH);
    }
    
     public boolean getPl_NH3(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_NH3 + configuration, DEFAULT_PL_NH3);
    }

    public void setPl_NH3(boolean pl_NH3,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_NH3 + configuration, pl_NH3);
    }

    public boolean getPl_KH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_KH+configuration, DEFAULT_PL_KH);
    }

    public void setPl_KH(boolean pl_KH,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_KH + configuration, pl_KH);
    }
    
    public boolean getPl_GH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_GH + configuration, DEFAULT_PL_GH);
    }

    public void setPl_GH(boolean pl_GH,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_GH + configuration, pl_GH);
    }
    
    public boolean getPl_FE(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_FE + configuration, DEFAULT_PL_FE);
    }

    public void setPl_FE(boolean pl_FE, String configuration){
         if (configuration == null) configuration ="";         
        prefs.putBoolean(PL_FE + configuration, pl_FE);
    }

    public boolean getPl_PH(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_PH + configuration, DEFAULT_PL_PH);
    }

    public void setPl_PH(boolean pl_PH,String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_PH + configuration, pl_PH);
    }

    public String getPl_Key(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(PL_KEY + configuration, DEFAULT_PL_KEY);
    }

    public void setPl_Key(String pl_Key, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(PL_KEY + configuration, pl_Key);
    }

    public String getPl_Gridstep(String configuration){
         if (configuration == null) configuration ="";
        return prefs.get(PL_GRIDSTEP + configuration, DEFAULT_PL_GRIDSTEP);
    }

    public void setPl_Gridstep(String pl_Gridstep, String configuration){
         if (configuration == null) configuration ="";
        prefs.put(PL_GRIDSTEP + configuration, pl_Gridstep);
    }

    public boolean getPl_CO2(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_CO2 + configuration, DEFAULT_PL_CO2);
    }

    public void setPl_CO2(boolean pl_CO2, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_CO2 + configuration, pl_CO2);
    }

    public boolean getPl_Cond(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_COND + configuration, DEFAULT_PL_COND);
    }

    public void setPl_Cond(boolean pl_Cond, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_COND + configuration, pl_Cond);
    }

    public boolean getPl_CA(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_CA + configuration, DEFAULT_PL_CA);
    }

    public void setPl_CA(boolean pl_CA, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_CA + configuration, pl_CA);
    }

    public boolean getPl_MG(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_MG + configuration, DEFAULT_PL_MG);
    }

    public void setPl_MG(boolean pl_MG, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_MG + configuration, pl_MG);
    }

    public boolean getPl_CU(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_CU + configuration, DEFAULT_PL_CU);
    }

    public void setPl_CU(boolean pl_CU, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_CU + configuration, pl_CU);
    }
    
    public boolean getPl_PO4(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_PO4 + configuration, DEFAULT_PL_PO4);
    }

    public void setPl_PO4(boolean pl_PO4, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_PO4 + configuration, pl_PO4);
    }
    
    public boolean getPl_O2(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_O2 + configuration, DEFAULT_PL_O2);
    }

    public void setPl_O2(boolean pl_O2, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_O2 + configuration, pl_O2);
    }
    
    public boolean getPl_DENS(String configuration){
         if (configuration == null) configuration ="";
        return prefs.getBoolean(PL_DENS + configuration, DEFAULT_PL_DENS);
    }

    public void setPl_DENS(boolean pl_DENS, String configuration){
         if (configuration == null) configuration ="";
        prefs.putBoolean(PL_DENS + configuration, pl_DENS);
    }

    
     // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Presets Settings Access Methods">
    
    /**
     * return expenses presets enabled configuration setting
     *
     * @return true=Enabled
     */    
     public boolean isExpensesPresetsEnabled(){
        return prefs.getBoolean(
                ENABLE_EXPENSES_PRESETS , DEFAULT_EXPENSES_PRESETS_ENABLED);        
    }

     /**
      * set enable expenses presets configuration
      *
     * @param enable true=enabled
      */
    public void setEnableExpensesPresetsConfiguration(boolean enable){ 
        prefs.putBoolean(ENABLE_EXPENSES_PRESETS, enable);
    }
    /**
     * return expenses presets enabled configuration setting
     *
     * @return true=Enabled
     */    
     public boolean isMaintenancePresetsEnabled(){
        return prefs.getBoolean(
            ENABLE_MAINTENANCE_PRESETS , DEFAULT_MAINTENANCE_PRESETS_ENABLED);        
    }

     /**
      * set enable expenses presets configuration
      *
     * @param enable true=enabled
      */
    public void setEnableMaintenancePresetsConfiguration(boolean enable){ 
        prefs.putBoolean(ENABLE_MAINTENANCE_PRESETS, enable);
    }
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Units Settings Access Methods">
    
    /**
     * Gets the settings about use units labels in forms
     * 
     * @return true to use units || false not to use
     */
    public boolean getUseUnits(){
        return prefs.getBoolean(UNITS_IN_FORMS, DEFAULT_UNITS_IN_FORMS);
    }
    /**
     * Sets the settings about use units labels in forms
     * 
     * @param useIt  boolean [to use units || false not to use]
     */
    public void setUseUnits(boolean useIt) {
        prefs.putBoolean(UNITS_IN_FORMS, useIt);
    }
    
    /**
     * Gets the settings about use units labels in reports
     * 
     * @return true to use units || false not to use
     */
    public boolean getUseReportUnits(){
        return prefs.getBoolean(UNITS_IN_REPORTS, DEFAULT_UNITS_IN_REPORTS);
    }
    /**
     * Sets the settings about use units labels in reports
     * 
     * @param useIt  boolean [to use units || false not to use]
     */
    public void setUseReportUnits(boolean useIt) {
        prefs.putBoolean(UNITS_IN_REPORTS, useIt);
    }
    
    /**
     * get unit for water hardness
     *
     * @return [degree|ppm]
     */
    public String getUnitWHardness() {
        return prefs.get(UNITS_WATER_HARDNESS,DEFAULT_UNITS_WATER_HARDNESS);
    }
    /**
     * * set unit for water hardness
     *
     * @param unit [degree|ppm]
     */
    public  void setUnitWHardness(String unit) {
        prefs.put(UNITS_WATER_HARDNESS,unit);
    }
    
    /**
     * get unit for Temp
     *
     * @return [C|F]
     */
    public String getUnitTemp() {
        return prefs.get(UNITS_TEMP,DEFAULT_UNITS_TEMP);
    }
    /**
     * * set unit for Temp
     *
     * @param unit [C|F]
     */
    public  void setUnitTemp(String unit) {
        prefs.put(UNITS_TEMP,unit);
    }
    
    /**
     * get unit for volume
     *
     * @return [l|gal]
     */
    public String getUnitVolume() {
        return prefs.get(UNITS_VOL,DEFAULT_UNITS_VOL);
    }
    /**
     * * set unit for Temp
     *
     * @param unit [l|gal]
     */
    public  void setUnitVolume(String unit) {
        prefs.put(UNITS_VOL,unit);
    }
    
    /**
     * get unit for lenght
     *
     * @return [cm|inch]
     */
    public String getUnitLenght() {
        return prefs.get(UNITS_LENGHT,DEFAULT_UNITS_LENGHT);
    }
    /**
     * * set unit for lenght
     *
     * @param unit [cm|inch]
     */
    public  void setUnitLenght(String unit) {
        prefs.put(UNITS_LENGHT,unit);
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Custom Labels Settings Access Methods">
    
    /**
     * get custom label for 'Dens'
     * @return custom Label
     */
    public String getDensCustomLabel() {        
        return prefs.get(CUSTOM_LABEL_DENS, rBundle.getString("Ny.readingsDensLabel.text"));
    }
    
    /**
     *  set unit custom label for 'Dens'
     * @param newLabel
     */
    public  void setDensCustomLabel(String newLabel) {
        if (newLabel.endsWith(":")) {
            newLabel = newLabel.substring(0, newLabel.lastIndexOf(":"));
        }
        prefs.put(CUSTOM_LABEL_DENS,newLabel);
    }
    
    /**
     * get custom Units for 'Dens'
     * @return custom Units
     */
    public String getDensCustomUnits() {        
        return prefs.get(CUSTOM_UNITS_DENS, rBundle.getString("_rho"));
    }
    
    /**
     *  set unit custom Units for 'Dens'
     * @param newUnits
     */
    public  void setDensCustomUnits(String newUnits) {
        prefs.put(CUSTOM_UNITS_DENS,newUnits);
    }
    
    /**
     * get custom label for 'Cond'
     * @return custom Label
     */
    public String getCondCustomLabel() {
        return prefs.get(CUSTOM_LABEL_COND, rBundle.getString("Ny.readingsCondLabel.text"));
    }
    
    /**
     *  set unit custom label for 'Cond'
     * @param newLabel
     */
    public  void setCondCustomLabel(String newLabel) {
        if (newLabel.endsWith(":")) {
            newLabel = newLabel.substring(0, newLabel.lastIndexOf(":"));
        }
        prefs.put(CUSTOM_LABEL_COND,newLabel);
    }
    
    /**
     * get custom units for 'Cond'
     * @return custom Units
     */
    public String getCondCustomUnits() {
        return prefs.get(CUSTOM_UNITS_COND, rBundle.getString("microS_cm"));
    }
    
    /**
     *  set unit custom units for 'Cond'
     * @param newUnits
     */
    public  void setCondCustomUnits(String newUnits) {
        prefs.put(CUSTOM_UNITS_COND,newUnits);
    }
    
    /**
     * get custom label for 'KH'
     * @return custom Label
     */
    public String getKHCustomLabel() {
        return prefs.get(CUSTOM_LABEL_KH, rBundle.getString("Ny.readingsKHLabel.text"));
    }
    
    /**
     *  set unit custom label for 'KH'
     * @param newLabel
     */
    public  void setKHCustomLabel(String newLabel) {
        if (newLabel.endsWith(":")) {
            newLabel = newLabel.substring(0, newLabel.lastIndexOf(":"));
        }
        prefs.put(CUSTOM_LABEL_KH,newLabel);
    }
    
    /**
     * get custom label for 'Temp'
     * @return custom Label
     */
    public String getTempCustomLabel() {
        return prefs.get(CUSTOM_LABEL_TEMP, rBundle.getString("Ny.readingsTempLabel.text"));
    }
    
    /**
     *  set unit custom label for 'Temp'
     * @param newLabel
     */
    public  void setTempCustomLabel(String newLabel) {
        if (newLabel.endsWith(":")) {
            newLabel = newLabel.substring(0, newLabel.lastIndexOf(":"));
        }
        prefs.put(CUSTOM_LABEL_TEMP,newLabel);
    }
    
    /**
     * get custom label for 'Salinity'
     * @return custom Label
     */
    public String getSalinityCustomLabel() {
        return prefs.get(CUSTOM_LABEL_NA, rBundle.getString("Ny.readingsSalinityLabel.text"));
    }
    
    /**
     *  set unit custom label for 'Salinity'
     * @param newLabel
     */
    public  void setSalinityCustomLabel(String newLabel) {
        if (newLabel.endsWith(":")) {
            newLabel = newLabel.substring(0, newLabel.lastIndexOf(":"));
        }
        prefs.put(CUSTOM_LABEL_NA,newLabel);
    }
    
    /**
     * get custom Units for 'Salinity'
     * @return custom Units
     */
    public String getSalinityCustomUnits() {
        return prefs.get(CUSTOM_UNITS_NA, rBundle.getString("ppt"));
    }
    
    /**
     *  set unit custom Units for 'Salinity'
     * @param newUnits
     */
    public  void setSalinityCustomUnits(String newUnits) {
        prefs.put(CUSTOM_UNITS_NA,newUnits);
    }
    
    // </editor-fold>
    
    
    /**
     *  clear all settings
     */
    public void clear () {
        String msg=ResourceBundle.getBundle("nyagua/Bundle").getString("ASK_DELETION");
        int a = JOptionPane.showConfirmDialog(null,msg,
                    ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING"), JOptionPane.YES_NO_OPTION);
            if ( a== JOptionPane.YES_OPTION){
                try {
                    prefs.clear();
                } catch (BackingStoreException ex) {
                    _log.log(Level.SEVERE, null, ex);
                }
            }

    }

    /**
     * Exports settings in a file
     * (the file is fixed in work dir - setting.xml)
     * 
     */
    public void exportSettings(){
        String prefsFile = Global.WorkDir +  Application.FS + "settings.xml";//NOI18N
        try {
            FileOutputStream fos = new FileOutputStream(prefsFile);
            try {
                prefs.exportSubtree(fos);
                String msgType=ResourceBundle.getBundle("nyagua/Bundle").getString("EXPORT_SETTINGS");
                String msg=ResourceBundle.getBundle("nyagua/Bundle").getString("SETTINGS_SAVED_IN_FILE") + prefsFile;
                JOptionPane.showMessageDialog( null, msg,msgType,JOptionPane.INFORMATION_MESSAGE );
            } catch (    IOException | BackingStoreException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("static-access")
    public void importSettings() throws IOException, InvalidPreferencesFormatException{
        File prefsFile =  XMLFilter.getXMLFile();
        if (prefsFile==null) {
            return;
        }
         //is = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(prefsFile.getAbsolutePath()));
            prefs.importPreferences(is);
            String msgType=ResourceBundle.getBundle("nyagua/Bundle").getString("IMPORT_SETTINGS");
            String msg=ResourceBundle.getBundle("nyagua/Bundle").getString("SETTINGS_LOADED");
            JOptionPane.showMessageDialog( null, msg,msgType,JOptionPane.INFORMATION_MESSAGE );
        }  catch (FileNotFoundException e) {
            String msgType=ResourceBundle.getBundle("nyagua/Bundle").getString("IMPORT_SETTINGS");
            String msg=ResourceBundle.getBundle("nyagua/Bundle").getString("FILE_NOT_FOUND.");
            JOptionPane.showMessageDialog( null, msg,msgType,JOptionPane.INFORMATION_MESSAGE );
        }
    }

    /**
     *  Singleton Pattern (thread safe implemet.)
     *
     * @return setting instance
     */
    public static Setting getInstance() {
        return INSTANCE;
    }

    static final Logger _log = Logger.getLogger(Setting.class.getName());
            
}

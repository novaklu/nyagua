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

/**
 * Report.java *
 *
 * Created on 29-set-2010, 12.23.22
 *
 * Manage and show reports
 */
package nyagua;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import nyagua.data.Expense;
import nyagua.data.Fish;
import nyagua.data.Invertebrates;
import nyagua.data.Plant;
import nyagua.data.Reading;
import nyagua.data.Recipe;
import nyagua.data.Setting;
import nyagua.data.Solutions;
import pieChart.Pie;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class Report {
    
    private static final int CURR_REPORT_AQUARIUM=0;
    private static final int CURR_REPORT_FISH=1;
    private static final int CURR_REPORT_INVERT=2;
    private static final int CURR_REPORT_PLANTS=3;
    private static final int CURR_REPORT_READINGS=4;
    private static final int CURR_REPORT_EXPENSES=5;

    private String reportHtmlContent;    //the report to print
    private String reportName;      //the report name
    private String reportTitle;     //the report title
    private String reportDir;       //the report working directory

    public Report(){
        try {
            checkWorkDir();
            StyleSheetReport();  //generates stylesheet if not exist            
        } catch (IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="private basic methods">

    /**
     * Checks and (if necessary) create
     * the reports working directory
     * 
     */
    private void checkWorkDir(){
        reportDir=Global.WorkDir + Application.FS + "reports";// NOI18N
        if (! (new File(reportDir).exists())){
            //if not exist create it
            boolean success = (new File(reportDir)).mkdir();
            if (success != true) {
              System.err.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + reportDir);
            }
        }
    }

    /**
     * Returns starndard html footer fot all reports
     *
     * @return footer string
     */
    private String htmFooter(){ 
        Setting s=Setting.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("\n<hr><p class='the_footer'> ");//NOI18N
        if (!(s.getFooter().isEmpty())) {
            sb.append(s.getFooter());
        }
        else {
            sb.append(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GENERATE_BY_HTM"));//NOI18N
            sb.append(" <a href='http://nyagua.sourceforge.net'>");//NOI18N
            sb.append(Application.NAME);//NOI18N
            sb.append("</a>");//NOI18N
        }
        
        sb.append( "</p>\n</body></html>");//NOI18N
        return sb.toString();
    }
    
    /**
     * Replaces apostrof in orig. string
     * with the correct HTML entity
     * 
     * @param orig  the original string
     * @return      the new string
     */
    private static String replaceApos (String orig){
        if (orig==null) {
            return "";
        }
        orig=orig.replaceAll("'", "&#39;");
        return orig;
    }
    

    /**
     * Creates standard style sheet
     *
     * @throws IOException
     */
    private void StyleSheetReport() throws IOException{
       //check for exixsting working dir and create if necessary        
       String  sshFileName=reportDir+Application.FS+"user_style.css";//NOI18N
        //In case of user.css it should not be overwrite time by time.
        //A user may have change it to his own style
        boolean success = (new File(sshFileName)).exists();
        if (! success){
            try {
                success = (new File(sshFileName)).createNewFile();
            } catch (IOException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(sshFileName))) {
                out.println("");
            }
       }
       sshFileName=Global.WorkDir + Application.FS + "reports"+Application.FS+"style.css";//NOI18N
        //In case of admin css it should be overwrite time by time.
        success = (new File(sshFileName)).exists();
        if (success){
            success=(new File(sshFileName)).delete();
        }
        try {
            success = (new File(sshFileName)).createNewFile();
        } catch (IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (success!=true){
            JOptionPane.showMessageDialog (null,
                      java.util.ResourceBundle.getBundle("miniMaga/Bundle").getString("CAN_T_CREATE_THEREPORT_FILE") + sshFileName,java.util.ResourceBundle.getBundle("miniMaga/Bundle").getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
              return;
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(sshFileName))) {
            Setting s=Setting.getInstance();
            String titleColor="3AC4FF";
            if (! s.getTitleColor().isEmpty()){
                titleColor=s.getTitleColor();
            }
            //global classes
            out.println("h1 {");//NOI18N
            out.println("\tcolor : #"+ titleColor +";");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("p.the_footer {");//NOI18N
            out.println("\tfont-style : italic;");//NOI18N
            out.println("\tfont-size : 90%;");//NOI18N
            out.println("}\n\n" );//NOI18N        
            out.println("p.pron{\n\tfont-style : italic;\n\tfont-size : 90%;\n}\n\n");//NOI18N

            // report classes
            out.println("img.logo{\n\t"//NOI18N
                    +"border-style: solid;\n\t"//NOI18N
                    +"border-width : 0px;\n\t"//NOI18N
                    +"border-color : #000000;\n}\n\n");//NOI18N        
            out.println("table, th, td {");//NOI18N
            out.println("\t border-bottom-style : solid;");//NOI18N
            out.println("\t border-left-style : solid;");//NOI18N
            out.println("\t border-right-style : solid;");//NOI18N
            out.println("\t  border-top-style : solid;");//NOI18N
            out.println("\t  border-collapse: collapse;");//NOI18N
            out.println("\t  border-spacing: 0px;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("table, th {");//NOI18N
            out.println("\t  border-width : 2px;");//NOI18N
            out.println("\t  padding: 5px;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("td {");//NOI18N
            out.println("\t  padding: 5px;");//NOI18N
            out.println("\t border-width : 1px;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("div.filt {");//NOI18N
            out.println("\tfont-style : italic;");//NOI18N
            out.println("\tfont-size : 80%;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println(".aq_img img{");//NOI18N
            out.println("\tdisplay: block;");//NOI18N
            out.println("\tmargin-left: auto;");//NOI18N
            out.println("\tmargin-right: auto;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("div.f_img, div.p_img, div.i_img{");//NOI18N
            out.println("\tfloat:right;");//NOI18N
            out.println("\tmargin-left: auto;");//NOI18N
            out.println("\tmargin-right: 0;");//NOI18N
            out.println("}\n\n");//NOI18N    
            out.println("div.pie_img{");//NOI18N
            out.println("\tfloat:right;");//NOI18N
            out.println("\tposition: absolute;");//NOI18N
            out.println("\tmargin-left: 30%;");//NOI18N      
            out.println("\tmargin-bottom: 40px;");//NOI18N
            out.println("\tmargin-top: 40px;");//NOI18N
            out.println("}\n\n");//NOI18N 
            out.println("div.imgbaseline{ ");//NOI18N
            out.println("\t border-style:none;");//NOI18N
            out.println("\t display:inline;");//NOI18N
            out.println("\t vertical-align:top;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("div.warning{ ");//NOI18N
            out.println("\t color:red;");//NOI18N
            out.println("\t display:inline;");//NOI18N        
            out.println("}\n\n");//NOI18N   
             out.println("div.WARNING{ ");//NOI18N
            out.println("\t color:yellow;");//NOI18N
            out.println("\t display:inline;");//NOI18N        
            out.println("}\n\n");//NOI18N
             out.println("div.Alarm{ ");//NOI18N
            out.println("\t color:orange;");//NOI18N
            out.println("\t display:inline;");//NOI18N        
            out.println("}\n\n");//NOI18N
            out.println("div.Toxic{ ");//NOI18N
            out.println("\t color:red;");//NOI18N
            out.println("\t display:inline;");//NOI18N        
            out.println("}\n\n");//NOI18N
            out.println("div.Deadly{ ");//NOI18N
            out.println("\t color:magenta;");//NOI18N
            out.println("\t display:inline;");//NOI18N        
            out.println("}\n\n");//NOI18N
            
            //recipe
            out.println(".recipe .caption {");//NOI18N
            out.println("\t font-weight: bold;");//NOI18N
            out.println("}\n\n");//NOI18N
            out.println("#recipesummary {");//NOI18N
            out.println("\t border: 1px solid black;");//NOI18N
            out.println("\t border-style: solid;");//NOI18N
            out.println("\t border-width: 1px;");//NOI18N
            out.println("\t padding: 10px;");//NOI18N
            out.println("\t margin-top: 20px;");//NOI18N
            out.println("}\n\n");//NOI18N
            
            if (s.getTheme().equals(Setting.THEME_SHADOW)){
                 out.println("body{");//NOI18N
                out.println("\t margin: 0px auto;");//NOI18N
                out.println("\t padding: 5%;");//NOI18N
                out.println("\t width: 75%;");//NOI18N 
                out.println("\t ");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("hr {");//NOI18N
                out.println("\t margin-top: 60px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("h1 {");//NOI18N
                out.println("\t color : #363636;");//NOI18N
                out.println("\t text-shadow: #6374AB 16px 10px 2px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("h2.aqname, h3 {");//NOI18N
                out.println("\t font-style : italic;");//NOI18N
                out.println("\t text-shadow: #6374AB 16px 10px 2px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("p.item {");//NOI18N
                out.println("border-bottom-style:solid;");//NOI18N
                out.println("border-bottom-width:1px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_commonName {");//NOI18N
                out.println("\t text-shadow: #6374AB 16px 10px 2px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.p_name{");//NOI18N
                out.println("\t text-shadow: #6374AB 16px 10px 2px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("img{");//NOI18N
                out.println("\tbox-shadow: 10px 10px 5px #888888;");//NOI18N
                out.println("}\n\n");//NOI18N
                 out.println("div.f_img, div.p_img, div.i_img {");//NOI18N
                out.println("\t margin-left: 10%;");//NOI18N
                out.println("\t margin-right: 15%;");//NOI18N
                out.println("\t float: right;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("table, th, td {");//NOI18N
                out.println("\t border-bottom-style : solid;");//NOI18N
                out.println("\t border-left-style : hidden;");//NOI18N
                out.println("\t border-right-style : hidden;");//NOI18N
                out.println("\t border-top-style : solid;");//NOI18N
                out.println("\t border-width: 1px;");//NOI18N
                out.println("\t border-collapse: collapse;");//NOI18N
                out.println("\t border-spacing: 0px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println(".aq_img img{\n");//NOI18N
                out.println("\t border: 1;\n");//NOI18N
                out.println("\t display: block;\n");//NOI18N
                out.println("\t margin-left: auto;\n");//NOI18N
                out.println("\t margin-right: auto;\n");//NOI18N
                out.println("\t -webkit-box-reflect: below 0px -webkit-gradient(linear, left top, left bottom, from(transparent), color-stop(50%, transparent), to(rgba(255,255,255,0.2)));\n");//NOI18N
                out.println("\t box-shadow: 10px 10px 5px #888888;\n");//NOI18N
                out.println("}\n\n");//NOI18N
                
            } else if (s.getTheme().equals(Setting.THEME_GREEN)){
                out.println("body{");//NOI18N
                out.println("\t background-color:  #B2FCAF;");//NOI18N
                out.println("\t color: #2605FF;");//NOI18N
                out.println("\t margin: 0px auto;");//NOI18N
                out.println("\t width: 75%;");//NOI18N 
                out.println("}\n\n");//NOI18N
                out.println("h1 {");//NOI18N
                out.println("\t color: #3AC4FF;");//NOI18N 
                out.println("\t text-align: center;");//NOI18N 
                out.println("\t background: #FFFF7D;");//NOI18N 
                out.println("\t background: linear-gradient(to right, orange, #FFFF7D, yellow);");//NOI18N 
                out.println("}\n\n");//NOI18N
                out.println("h2, h3 {");//NOI18N
                out.println("\t background-color: #F6FF53;");//NOI18N
                out.println("\t font-style: italic;");//NOI18N
                out.println("\t font-variant: small-caps;");//NOI18N
                out.println("\t font-weight: bolder;");//NOI18N  
                out.println("\t display: table;");//NOI18N
                out.println("\t padding-top: 4px;");//NOI18N
                out.println("\t padding-right: 10px;");//NOI18N
                out.println("\t padding-bottom: 4px;");//NOI18N
                out.println("\t padding-left: 10px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.exp_total {");//NOI18N
                out.println("\t font-size: medium;");//NOI18N
                out.println("\t padding: inherit;");//NOI18N
                out.println("\t margin-top: inherit;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println(".aqname {");//NOI18N
                out.println("\t display: initial");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_commonName, div.p_name{");//NOI18N
                out.println("\t border-top: solid;");//NOI18N
                out.println("\t border-top-width: 1px;");//NOI18N
                out.println("\t border-top-color: #F6FF53;");//NOI18N
                out.println("\t border-left: solid;");//NOI18N
                out.println("\t border-left-width: 1px;");//NOI18N
                out.println("\t border-left-color: #F6FF53;");//NOI18N
                out.println("}\n\n");//NOI18N
            }else if (s.getTheme().equals(Setting.THEME_ELEGANT)){
                out.println("body{");//NOI18N
                out.println("\t border-left: solid;");//NOI18N
                out.println("\t border-left-color: #AEAEAE;");//NOI18N
                out.println("\t border-left-width:45px;");//NOI18N
                out.println("\t border-spacing: 15px;");//NOI18N
                out.println("\t padding-left:15px;");//NOI18N
                out.println("\t font-family: URW Chancery L;");//NOI18N
                out.println("\t font-style: italic;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_img, div.p_img, div.i_img{");//NOI18N
                out.println("\t margin-right: 15px;");//NOI18N
                out.println("\t padding: 10px;");//NOI18N
                out.println("}\n\n");//NOI18N
            }else if (s.getTheme().equals(Setting.THEME_STANDARD)){
                out.println("body{");//NOI18N
                out.println("\t margin: 0px auto;");//NOI18N
                out.println("\t padding: 5%;");//NOI18N
                out.println("\t border-width : 1px;");//NOI18N
                out.println("\t border-style : solid;");//NOI18N
                out.println("\t border-color: grey;");//NOI18N
                out.println("\t width: 75%;");//NOI18N 
                out.println("\t ");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_img, div.p_img, div.i_img {");//NOI18N
                out.println("\t margin-left: auto;");//NOI18N
                out.println("\t margin-right: 20px;");//NOI18N
                out.println("\t float: left;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_commonName, div.p_name {");//NOI18N
                out.println("\t font-size: larger;");//NOI18N
                out.println("\t color: blue;");//NOI18N
                out.println("\t padding: 10px;");//NOI18N
                out.println("}\n\n");//NOI18N
                out.println("div.f_id, div.p_id {");//NOI18N
                out.println("\t color: blue;");//NOI18N
                out.println("\t margin-top: 20px;");//NOI18N
                out.println("}\n\n");//NOI18N
                
            }
            out.flush();
        }
    }



    /**
     * Creates a standard header using the reportTitle
     */
    private void createHeader(){
        //title
        reportHtmlContent="<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 " +//NOI18N
                "Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>" + //NOI18N
                "\n<html> \n <head> \n <title>"  + reportTitle+ "</title> \n" ;// NOI18N
        
        Setting s=Setting.getInstance();
        if (s.getUseUTFFormat()){ //uses utf8
            reportHtmlContent=reportHtmlContent +"<meta http-equiv='content-type' content='text/html; charset=utf-8' /> \n"; //NOI18N
        }
        //stylesheet
        String userCss=s.getCss();
        if (userCss.isEmpty() || userCss.matches("")){
            userCss="user_style.css";
        }
        reportHtmlContent=reportHtmlContent +"<link rel=styleSheet href='style.css' type='text/css' media='screen'>\n" +//NOI18N
                "<link rel=styleSheet href='"+userCss+//NOI18N
                "' type='text/css' " +//NOI18N
                "media='screen'> \n </head>\n";//NOI18N        
    }

    private void createLogo(){
        Setting s=Setting.getInstance();
        if (!s.getLogo().isEmpty()){
            reportHtmlContent=reportHtmlContent +" <p> <img src='"+//NOI18N
                    s.getLogo() + "' alt='logo' class='logo'" +//NOI18N
                    "></p>\n";//NOI18N
        }
    }

    /**
     * Creates a standard h1 title at top of body
     * using reportTitle
     */
    private void createTitle(){
        reportHtmlContent=reportHtmlContent  +//NOI18N
                "<h1 class=title >" + reportTitle + "</h1>\n";//NOI18N
    }


    /**
     * Creates a row with timestamp
     */
    private void htmPrintedOn() {
        reportHtmlContent=reportHtmlContent + "<p class=pron>" //NOI18N
                + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PRINTED_ON_")
                + " " + LocUtil.localizeDate(LocUtil.getCurrentlocalizedDate())//NOI18N
                + "</p><hr>\n"; //NOI18N
    }
    
    
//    private void createNavbar( String baseLink, int currReport) {
//        
//        String prevLink="";
//        String nextLink="";
//        
//        switch (currReport) {
//            case 1:
//                prevlink=
//                        nextLink=
//                break;
//            
//            case 2:
//                break;
//            
//            case 3:
//                break;
//            
//            case 4:
//                break;
//            
//            case 5:
//                break;
//        }
//        
//        StringBuilder sb = new StringBuilder();
//        sb.append("<div class=prev_page_link >");
//        sb.append("<a href='");
//        
////        + baseLink + "inverts_data.html'>";)
//    }

     /**
     * Call the browser defined in settings
     * (default for firefox) and show the given file
     *
     * @param fileName  the file to be shown
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    private void browseReport(String fileName){
        //String cmd = null;
        Setting s=Setting.getInstance();
        String browser = s.getBrowser();
        //String browser = DB.getSettings("def_browser","firefox");//NOI18N
        if (Util.OS_Detect().matches("mac")){
            //cmd="open "+browser + " " + fileName;
            String [] cmd={"open ",browser,fileName};
                try {
                //Process p = Runtime.getRuntime().exec(cmd);
                Process p = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
        }else{
            //cmd=browser + " " + fileName;
            String [] cmd={browser,fileName};    
            try {
                //Process p = Runtime.getRuntime().exec(cmd);
                Process p = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
        }     
    }
    

    // </editor-fold>

    /**
     * set report file name
     *
     * @param name
     */
    public void setName (String name){
        reportName=name;
    }

    /**
     * Sets the report title
     *
     * @param title
     */
    public void setTitle (String title){
        reportTitle=title;
    }

    /**
     * creates header and title
     */
    public void open(){
        //create content
        this.createHeader();
        //start body
        reportHtmlContent=reportHtmlContent +" <body>\n" ;//NOI18N
        this.createLogo();
        this.createTitle();
        this.htmPrintedOn();
    }

    /**
     * add some code to body
     *
     * @param htmlCode  the code to add
     */
    public void addToBody(String htmlCode){
        reportHtmlContent=reportHtmlContent+htmlCode;
    }
    
    /**
     * add footer and close report
     */
    public void close () {
        reportHtmlContent=reportHtmlContent+this.htmFooter();
    }

    /**
     * Output the report to file and show it
     * @param showItnow
     */
    public void out (boolean showItnow) { 
        PrintWriter out= null;        
        String htmFileName = reportDir + Application.FS + reportName + ".html"; // NOI18N
        boolean success = (new File(htmFileName)).exists();
        if (success) {
            success = (new File(htmFileName)).delete();
        }
        try {
            success = (new File(htmFileName)).createNewFile();            
            if (success != true) {
                JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("miniMaga/Bundle").getString("CAN_T_CREATE_THEREPORT_FILE") + htmFileName, java.util.ResourceBundle.getBundle("miniMaga/Bundle").getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            //get setting for utf8 or not            
            Setting s=Setting.getInstance();
            if (s.getUseUTFFormat()){   //uses new utf8 format
                out = new PrintWriter(new File(htmFileName), "UTF-8");//NOI18N
            }else{      //uses old undefined format
                out = new PrintWriter(new FileWriter(htmFileName));
            }            
            out.println(reportHtmlContent);
            out.flush();
            out.close();
            if (showItnow) {
                browseReport(htmFileName);
            }
        } catch (IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * deletes a report file 
     * (useful for temporary reports)
     */
    public void reportDelete (){
        String htmFileName = Global.WorkDir + Application.FS + "reports" // NOI18N
                + Application.FS + reportName + ".html"; // NOI18N
        boolean success = (new File(htmFileName)).exists();
        if (success) {
            success = (new File(htmFileName)).delete();
        }
    }
    
    /**
     * Count elements returned from a query
     * 
     * @param tableAndFilter the table name and filter for query
     * @param distinct
     * @return  number of elements
     */
    public int countElements(String tableAndFilter,boolean distinct){
        int elements =0;
        String qry = "SELECT COUNT(*) AS cont FROM " + tableAndFilter; // NOI18N
        if (distinct){
            qry = "SELECT COUNT(DISTINCT name) AS cont FROM " + tableAndFilter; // NOI18N
        }
        qry = qry + ";"; //NOI18N
                   
        DB.openConn();
        ResultSet rs;
        try {
            rs = DB.getQuery(qry);
            elements = rs.getInt("cont"); // NOI18N
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }               
        return elements;
    }
    
    /**
     * Gets an image from source code internal resources
     * and add it to report
     * @param img
     * @param resource 
     * @param altTitle  [image alt string]
     * @param type
     */

    public void addImageFromCode (BufferedImage img,String resource, String altTitle,int type){
        final int TYPE_JPG=0;
        final int TYPE_PNG=1;
        //check for exixsting working dirs and create if necessary
        String imgDirName = Global.WorkDir + Application.FS + "reports" +//NOI18N
                Application.FS + "nyagua-" + this.reportName + "-images";//NOI18N
        String imgDirLink="nyagua-" + this.reportName + "-images";//NOI18N 
        if (! (new File(imgDirName).exists())){
            //if not exist create it
            boolean success = (new File(imgDirName)).mkdir();
            if (success != true) {
              System.err.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName);
              JOptionPane.showMessageDialog (null,
                      java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
              return;
            }
        }
       //save the image and add the link
        if (img!=null){
            if (type==TYPE_JPG){
                Util.SaveImage(img, imgDirName+Application.FS+resource+".jpg");//NOI18N
            reportHtmlContent=reportHtmlContent +"<img class='picture' src='"//NOI18N
                    +imgDirLink+Application.FS+resource+".jpg' "//NOI18N                    
                    + " alt='"+ altTitle +"' title='"+ altTitle + "'/>";//NOI18N
            }else{
                Util.SaveImagePNG(img, imgDirName+Application.FS+resource+".png");//NOI18N
            reportHtmlContent=reportHtmlContent +"<img class='picture' src='"//NOI18N
                    +imgDirLink+Application.FS+resource+".png' "//NOI18N                    
                    + " alt='"+ altTitle +"' title='"+ altTitle + "'/>";//NOI18N
            }
            
        }
    }

    /**
     * Gets an image from DB and add it to report
     * 
     * @param tableName table name in which image is used
     * @param id        id of element of tablename to which image is referred
     * @param altTitle  [image alt string]
     */

    public void addImageFromDB (String tableName, String id, String altTitle){
        //check for exixsting working dirs and create if necessary
        String imgDirName = Global.WorkDir + Application.FS + "reports" +//NOI18N
                Application.FS + "nyagua-" + this.reportName + "-images";//NOI18N  
        String imgDirLink="nyagua-" + this.reportName + "-images";//NOI18N 
        if (! (new File(imgDirName).exists())){
            //if not exist create it
            boolean success = (new File(imgDirName)).mkdir();
            if (success != true) {
              System.err.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName);
              JOptionPane.showMessageDialog (null,
                      java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
              return;
            }
        }
        //load the image
        BufferedImage img=null;
        try {
            img = DB.DBLoadImage(tableName, id); //NOI18N
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        //save the image and add the link
        if (img!=null){
            Util.SaveImage(img, imgDirName+Application.FS+id+".jpg");//NOI18N            
            reportHtmlContent=reportHtmlContent +"<img class='picture' src='"//NOI18N
                    +imgDirLink+Application.FS+id+".jpg'"//NOI18N                   
                    + " alt='"+ altTitle +"' title='"+ altTitle + "'/>";//NOI18N
        }
    }
    
    /**
     * Gets an image from source code internal resources
     * and add it to report
     * 
     * @param resource 
     * @param altTitle  [image alt string]
     */

    public void addImageFromResources (String resource, String altTitle){
        //check for exixsting working dirs and create if necessary
        String imgDirName = Global.WorkDir + Application.FS + "reports" +//NOI18N
                Application.FS + "nyagua-" + this.reportName + "-images";//NOI18N
        String imgDirLink="nyagua-" + this.reportName + "-images";//NOI18N 
        if (! (new File(imgDirName).exists())){
            //if not exist create it
            boolean success = (new File(imgDirName)).mkdir();
            if (success != true) {
              System.err.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName);
              JOptionPane.showMessageDialog (null,
                      java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CAN_T_CREATE_THE_WORKING_DIRECTORY") + imgDirName,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
              return;
            }
        }
        //load the image     
        
        ImageIcon ii=new javax.swing.ImageIcon(getClass().getResource("/icons/"+resource)); // NOI18N
        Image source = ii.getImage();
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D)img.getGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        
        //save the image and add the link
        Util.SaveImage(img, imgDirName+Application.FS+resource+".jpg");//NOI18N
        reportHtmlContent=reportHtmlContent +"<img class='picture' src='"//NOI18N
                +imgDirLink+Application.FS+resource+".jpg' "//NOI18N                    
                + " alt='"+ altTitle +"' title='"+ altTitle + "'/>";//NOI18N
       
    }
    
     /**
     * Return an array from a table
     * 
     * @param i the query to get elements from db
     * @return  an array from table
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public String[][] getTable (String i) throws ClassNotFoundException, SQLException {
        String qry=i;
        String table;
        int start=i.indexOf("FROM")+5;
        int end = i.indexOf("WHERE")-1;
        table=i.substring(start, end);
        String filter=i.substring(end);
        end=table.indexOf(",");
        if (end > 0) {
            table=table.substring(0,end);
        }
        boolean distinct=false;
        if (i.indexOf("DISTINCT") >0 ) {
            distinct=true;
        }
        DB.openConn();
        
        int elements=countElements(table + filter ,distinct);
        //String qry= "SELECT * FROM " + i;// NOI18N
        //qry=qry +" ORDER BY id DESC;";// NOI18N
        
        ResultSet rs = DB.getQuery(qry);
        int x=0;
        //***
        ResultSetMetaData metadata = rs.getMetaData();
        int columns=metadata.getColumnCount();
        String [][] tableData = new String[elements][columns];
        while (rs.next()) {            
            for (int y = 0; y < columns; y++){
                tableData[x][y]= rs.getString(y+1);
            }
            x++;
        }
        DB.closeConn();
        return tableData;       
    }

    /**
     * Creates a list from a query
     * 
     * @param qry the query that retrieves data
     * @param ordered o=ordered list / u=unordered list
     * 
     */
    public void createList (String qry, char ordered){
        if (ordered !='u' && ordered !='o') {
            ordered='u';
        }//default to unordered list
        String htmList="<" + ordered + "l>"; //open list
        String[][] tb;
        try {
            tb = getTable(qry);
            for (String[] tb1 : tb) {
                htmList=htmList + "<li>";// NOI18N
                for (int y = 0; y <tb [0].length; y++) {
                    htmList = //NOI18N
                            htmList + replaceApos(tb1[y]) // NOI18N
                     + " "; // NOI18N
                }
                htmList=htmList + "</li>\n";// NOI18N
            }
        } catch (ClassNotFoundException | SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        htmList=htmList +"</" + ordered + "l>"; //close list
        reportHtmlContent=reportHtmlContent+htmList;
    }

    
    
    // <editor-fold defaultstate="collapsed" desc="Specific application reports">
    
    private static void addFilter (Report rep){
        if (Global.filterState){
           rep.addToBody("<p style='color:#FF0000'><br><i>"//NOI18N
                   + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FILTERED-DATA")
                   +"</i></p>");//NOI18N
        }
    }
    
    private static void addItemFilter (Report rep, String filter){
        if (!filter.isEmpty()){
           rep.addToBody("<p style='color:#FF0000'><br><i>"//NOI18N
                   + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on_data") +
                   " " + filter
                   +" </i></p>");//NOI18N
        }
    }

    /**
     *
     * Creates a report for aquarium
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void AquariumReport () throws IOException, ClassNotFoundException, SQLException { 
        //**creates report
        Report rep=new Report();
        rep.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQUARIUM_DATA"));
        rep.setName("aquarium_data");
        rep.open();
        String id=Integer.toString( Global.AqID);
        String htmout="";
        String lenghtUnits="";//NOI18N
        String volumeUnits="";//NOI18N
        if (Global.useUnitsInReports){
            lenghtUnits=" "+Global.lenghtunit;//NOI18N
            volumeUnits=" "+Global.volunit;//NOI18N
        }
        
        //links
        String baseLink = rep.reportDir + Application.FS ; // NOI18N + repname" + ".html"
        
        //out aquarium data
        String qry="SELECT * FROM Aquarium ";//NOI18N
        qry=qry + "WHERE id='" + id  + "';";//NOI18N
        DB.openConn();
        ResultSet rs = DB.getQuery(qry);
        //output image before data
        rep.addToBody("<div class='aq_img'>");
        rep.addImageFromDB("Aquarium", id, replaceApos(rs.getString("Name")));
        rep.addToBody("</div>");
        //now data
        while (rs.next()) {
            htmout= "\n<h2 class='aqname'>"+ replaceApos(rs.getString("Name"));//NOI18N
            htmout=htmout + "</h2>\n<p class='aqtype'><b>";//NOI18N
            htmout=htmout + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TYPE") +": </b>"//NOI18N
                    + replaceApos(rs.getString("Type"));//NOI18N
            htmout=htmout + "</p>\n<p class='aqbottom'><b>";//NOI18N
            htmout=htmout + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BOTTOM")+": </b>"//NOI18N
                    + replaceApos(rs.getString("Bottom"));//NOI18N
            htmout=htmout + "</p>\n<p class='aqoc'>";//NOI18N
            if (rs.getString("o_c") != null) {// NOI18N
                if (rs.getString("o_c").matches("true")) {// NOI18N
                    htmout=htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("OPEN_TOP_AQUARIUM"));
                } else {
                    htmout=htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQUARIUM_WITH_COVER"));
                }
            }
            htmout=htmout + "</p>\n<p class='aqsize'> <b>";//NOI18N
            htmout=htmout + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("_W_")//NOI18N
                    +": </b>" + LocUtil.localizeDouble(rs.getString("Width"))//NOI18N
                    +lenghtUnits;//NOI18N
            htmout=htmout + " x <b>"//NOI18N
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("D")
                    +": </b>" + LocUtil.localizeDouble(rs.getString("Deep"))//NOI18N
                    +lenghtUnits;//NOI18N
            htmout=htmout + " x <b>"//NOI18N
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("H")
                    +": </b>"+LocUtil.localizeDouble(rs.getString("Height"))//NOI18N
                    +lenghtUnits;//NOI18N
            String gt=rs.getString("Glass_thick");  
            if (gt!=null && gt.length()>0){
                htmout=htmout + " ( x <b>"//NOI18N
                    +"Ø: </b>"+LocUtil.localizeDouble(gt)//NOI18N
                    +lenghtUnits +" )";//NOI18N
            }
            
            htmout=htmout + "<br>\n <b>"//NOI18N
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TOTAL_VOLUME")
                    +": </b> "+ LocUtil.localizeDouble(rs.getString("Tank_vol"))//NOI18N
                    +volumeUnits+ "<br> <b>"//NOI18N
                    + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WATER_VOLUME")
                    +": </b> "+ LocUtil.localizeDouble(rs.getString("Water_vol"))//NOI18N
                    +volumeUnits;//NOI18N
            htmout = htmout + "<br>\n <b>" + 
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Water_change_interval")//NOI18N
                    +": </b> "+ LocUtil.localizeDouble(rs.getString("Water_change"));//NOI18N
            htmout=htmout + "</p>\n<p class='start'><b>"//NOI18N
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("STARTED_ON")
                    +": </b>" + LocUtil.localizeDate(rs.getString("Start_date"))+ "<br><b>"// NOI18N
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ENDED_ON")
                    +": </b>" + LocUtil.localizeDate(rs.getString("End_date"))+"</p>";//NOI18N
            htmout=htmout  +"<p><b>"+ java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES")+": </b>"//NOI18N
                    + replaceApos(rs.getString("Notes"))+"</p><br>\n";//NOI18N

          }
          rs.close();
        rep.addToBody(htmout);
          //out devices
        htmout="\n<div class='aqdevices'><h3 class='subtitle'>";//NOI18N
        htmout=htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEVICES"));
        htmout=htmout  +"</h3>\n";//NOI18N
        rep.addToBody(htmout);

        //htmout="<ul>");//NOI18N
        qry="SELECT CASE WHEN Qty IS NULL  THEN '1' " +
"         ELSE Qty END AS Qty,' x ' AS per,Device,Brand,' - ' AS separator, W,'watt' as watt FROM Devices ";//NOI18N
        qry=qry + "WHERE AqID='" + id  + "';";//NOI18N
        
        
        rep.createList(qry, 'u');
        rep.addToBody("</div>");

        //out fishes
        
        htmout="\n<div class='aqfishes'><h3 class='subtitle'>";//NOI18N
        if (Global.generateAllReports) {
            htmout = htmout + "<a href='" + baseLink + "fish_data.html'>";
        }
        htmout=htmout +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISHES_"));
        if (Global.generateAllReports) {
            htmout = htmout + "</a>";
        }            
        htmout=htmout  +"</h3>\n";//NOI18N
        rep.addToBody(htmout);
        qry="SELECT DISTINCT Name FROM Fishes ";//NOI18N
        qry=qry + "WHERE AqID='" + id  + "';";//NOI18N
        rep.createList(qry, 'u');
        rep.addToBody("</div>");
        
        //out invertebrates
        htmout="\n<div class='aqinverts'><h3 class='subtitle'>";//NOI18N
        if (Global.generateAllReports) {
            htmout = htmout + "<a href='" + baseLink + "inverts_data.html'>";
        }
        htmout=htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVERTS_"));
        if (Global.generateAllReports) {
            htmout = htmout + "</a>";
        }            
        htmout=htmout +"</h3>\n";//NOI18N
        rep.addToBody(htmout);
        qry="SELECT DISTINCT Name FROM Inverts ";//NOI18N
        qry=qry + "WHERE AqID='" + id  + "';";//NOI18N
        rep.createList(qry, 'u');
        rep.addToBody("</div>");

       //out plants
        htmout="\n<div class='aqplants'><h3 class='subtitle'>";//NOI18N
        if (Global.generateAllReports) {
            htmout = htmout + "<a href='" + baseLink + "plants_data.html'>";
        }
        htmout=htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTS_"));
        if (Global.generateAllReports) {
            htmout = htmout + "</a>";
        }            
        htmout=htmout +"</h3>\n";//NOI18N
        rep.addToBody(htmout);        
        qry="SELECT DISTINCT Name FROM Plants ";//NOI18N
        qry=qry + "WHERE AqID='" + id  + "';";//NOI18N
        rep.createList(qry, 'u');
        rep.addToBody("</div>");
        
        //Out links for expenses and readings
        if (Global.generateAllReports) {
            htmout="\n<div class='otherlinks'>";//NOI18N
            htmout = htmout + "<a href='" + baseLink + "readings_data.html'>";
            htmout = htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("this.aquarium.readings"));
            htmout = htmout + "</a><br><br>";//NOI18N
            if (Global.includeExpensesReport) {
                htmout = htmout + "\n<div class='otherlinks'>";//NOI18N
                htmout = htmout + "<a href='" + baseLink + "expense_data.html'>";
                htmout=  htmout + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("this.aquarium.expenses"));
                htmout = htmout + "</a><br>\n";//NOI18N
            }
             htmout = htmout + "</div>";
            rep.addToBody(htmout);
        }
        
        rep.close();
        rep.out(true);
        
        if (Global.generateAllReports) {
            ReadingsReport(null,false);
            PlantBaseReport(null,false);
            FishBaseReport(null,false);
            InvertsBaseReport(null,false);
            if (Global.includeExpensesReport) {
                ExpensesReport(false);
            }
        }
    }
    
    private static String [] buildUnits() {
        int arrSize = Reading.CAPTIONS.length;
        String [] units = new String[arrSize];
        ResourceBundle bundle = ResourceBundle.getBundle("nyagua/Bundle");
        for (int x = 0; x < arrSize; x++) {
            switch (x) {
                case 6: //KH
                    if (Global.khunit.matches("degree")){
                        units [x] = bundle.getString("degree");            
                    } else {
                       units [x] = bundle.getString("ppm");          
                    }
                    break;
                
                case 8: //temp                    
                    if (Global.temperatureunit.matches("C")){
                      units [x] = bundle.getString("_C");  
                    } else {
                      units [x] = bundle.getString("_F");  
                    }
                    break;
                    
                case 12: //cond
                    if (!Global.condCustomUnit.isEmpty()) {
                        units [x] = Global.condCustomUnit;
                    }
                    else {
                        units [x] = bundle.getString("Ny.readingsCondLabel.text");
                    }
                    break;
                    
                case 18: //dens
                    if (!Global.densCustomUnit.isEmpty()) {
                        units [x] = Global.densCustomUnit;
                    }
                    else {
                        units [x] = bundle.getString("_rho");
                    }
                    break;
                    
                case 21: //salinity
                    if (!Global.salinityCustomUnit.isEmpty()) {
                        units [x] = Global.salinityCustomUnit;
                    }
                    else {
                        units [x] = bundle.getString("ppt");
                    }
                    break;
                    
                default:                    
                    units [x] = bundle.getString("ppm");
                    
            }
                
        }
        
        return units;
    }
    
    /**
     * Creates a report/analisys for readings
     * 
     * @param id id of selected record (if any) or medium values
     * @param showItNow
     * @throws SQLException  
     */
    
    public static void ReadingsReport(String id, boolean showItNow) throws SQLException{
        Report rep=new Report();
        rep.setTitle(replaceApos(ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.title")));// NOI18N
        rep.setName("readings_data");//NOI18N
        rep.open();
        String htmout=""; // NOI18N
        String qry="SELECT  avg(id) AS id,";
        if ((id==null || id.matches(""))) { // NOI18N
            qry=qry+ "avg(Date) AS Date,";// NOI18N
        } else {
            qry=qry+ "Date,";// NOI18N
        }
        qry=qry+" avg(Time) AS Time"+// NOI18N
                " , avg(NO2) AS NO2, avg(NO3) AS NO3, avg(GH) AS GH, avg(KH) " +// NOI18N
                " AS KH, avg(PH) AS PH, avg(temp) AS temp, avg(FE) AS FE, avg(NH)" +// NOI18N
                " AS NH,avg(CO2) AS CO2, avg(Cond) AS Cond,   avg(CA) AS CA," +// NOI18N
                " avg(MG) AS MG, avg(CU) AS CU, avg(PO4) AS PO4, avg(O2) AS O2," +// NOI18N
                "avg(dens), avg(NH3) AS NH3, avg(iodine) AS I2, avg(salinity) AS Na " +// NOI18N
                "FROM Measures  WHERE AqID ='"// NOI18N
                + Global.AqID +"' "+  Util.getPeriod();// NOI18N
       if ((id==null || id.matches(""))) { // NOI18N
            qry=qry+";";// NOI18N
        } else {
            qry=qry+" AND id='" + id + "';";// NOI18N
        }
        DB.openConn();
        //out  data
        ResultSet rs = DB.getQuery(qry);
        htmout=htmout+"\n<div class='readings'><h3>"+replaceApos(ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.readingsPanel.TabConstraints.tabTitle_1"))+"</h3>";// NOI18N
        
        if ((id==null || id.matches(""))) { // NOI18N
            htmout=htmout+"\n<div class='average'><i>"+replaceApos(ResourceBundle.getBundle("nyagua/Bundle").getString("AVERAGE_MEASURES"))+"</i>";// NOI18N
        } else {
            htmout=htmout+"\n<div class='f_id'><i>"+ResourceBundle.getBundle("nyagua/Bundle").getString("ID_HTM")+":</i>";// NOI18N            
            htmout=htmout+ id;
        }
        htmout=htmout+"<br></div>\n";// NOI18N
        while (rs.next()) {
            String [] hdr=Reading.CAPTIONS;
            String [] units = buildUnits();
            
             if ((id==null || id.matches(""))){// NOI18N
             } else { // NOI18N
                 htmout=htmout+ "<b>"+ResourceBundle.getBundle("nyagua/Bundle").getString("DATE")+":</b>";// NOI18N
                 htmout=htmout+ LocUtil.localizeDate(rs.getString("Date"))+"<br>\n";// NOI18N
             }
             for (int y=3; y<(hdr.length); y++){
                 htmout = htmout + "<div class='read'> <b> "+ hdr[y] + ":</b>"// NOI18N
                         + "  ";  // NOI18N
                 
                 String value = rs.getString(y+1);
                 if (value != null) {
                     htmout = htmout +  LocUtil.localizeDouble(value) + " &nbsp;" + units [y];
                 }
                 else {
                     htmout = htmout +  " - ";
                 }
                 
                 htmout = htmout + "</div>\n";// NOI18N
             }
             rep.addToBody(htmout);
        }
        htmout="</div>\n<div class='read_analisis'><h3>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.title")+"</h3>\n";// NOI18N
        rep.addToBody(htmout);
        
        //Data Analysis
        
        //TODO I2 and Na+ ??
        
        rs = DB.getQuery(qry); //restart rs
        //NH3
//      * * NH3 level             PPM       PPM
//     * safe                 0.000     0.020
//     * alert                0.020     0.050
//     * alarm                0.050     0.200
//     * toxic                0.200     0.500
//     * deadly               0.500+  
        if (rs.getString("NH3")!=null){// NOI18N
            double NH3 = rs.getDouble("NH3");
            String level="";//NOI18N
            String alertMsg="";//NOI18N
            if (NH3 > 0.02){//>0.02ppm // NOI18N
                if (NH3 < 0.05) {
                    level = "WARNING"; 
                    alertMsg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toonh");
                }
                if (NH3 >= 0.05 && NH3 < 0.2) {
                    level = "Alarm"; 
                    alertMsg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.alarmnh");
                }
                if (NH3 >= 0.2 && NH3 < 0.5) {
                    level = "Toxic"; 
                    alertMsg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toxicnh");
                }
                if (NH3 >= 0.5) {
                    level = "Deadly"; 
                    alertMsg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.deadlynh");
                }
                
                htmout="<div class='" + level + "'>";
                htmout=htmout+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(level)) + "!";// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NH3");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("NH3"));// NOI18N  
                htmout=htmout+ " " + alertMsg;// NOI18N
                htmout=htmout+ "<br>\n";// NOI18N
                rep.addToBody(htmout);
            }else{
                htmout= " <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NH")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        //NO2
        if (rs.getString("NO2")!=null){// NOI18N
            if (rs.getDouble("NO2")>0.50){//>0.5ppm // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO2");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("NO2"));// NOI18N  
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toono2");// NOI18N
                htmout=htmout+ "<br>\n";// NOI18N
                rep.addToBody(htmout);
            }else{
                htmout= " <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO2")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        //NO3
        if (rs.getString("NO3")!=null){// NOI18N
            if (rs.getDouble("NO3")>20.00){//>20ppm // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO3");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("NO3"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toono3");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else{
                htmout=" <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO3")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        //CO2
        if (rs.getString("CO2")!=null){// NOI18N
            double CO2=rs.getDouble("CO2");
            if (CO2 >= 30.00){//>30ppm // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Toxic")) +"!";// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("CO2"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.tooco2");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else if (CO2<12.00){//<12ppm // NOI18N
                htmout="<div class='Alarm'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("CO2"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.lowco2");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else if (CO2>=12.00 && CO2 < 20){//<12ppm // NOI18N
                htmout="<div class='WARNING'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("CO2"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.stilllowco2");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else{
                htmout= " <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        //PH
        if (rs.getString("PH")!=null){// NOI18N
            if (rs.getDouble("PH")<4.50 ||  rs.getDouble("PH")>9.00 ){//out of range 4.5-9 // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("PH"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.wrongph");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else if (rs.getDouble("PH")>8.50 ){//>8.5 marine only // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("PH"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.highph");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
                
            }else if (rs.getDouble("PH")<5.0 ){//<5.0 some species // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("PH"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.lowph");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
                
            }else{
                htmout= " <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        
        //FE
        if (rs.getString("FE")!=null){// NOI18N
            if (rs.getDouble("FE")>0.20){//>0.2ppm // NOI18N
                htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                htmout=htmout+ "</div> " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FE");// NOI18N
                htmout=htmout+ "="+LocUtil.localizeDouble(rs.getString("FE"));  // NOI18N
                htmout=htmout+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toofe");// NOI18N
                htmout=htmout+ "<br>";// NOI18N
                rep.addToBody(htmout);
            }else{
                htmout=" <div class='a_read'>" + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FE")+// NOI18N
                        "="+"<div class='imgbaseline'>";// NOI18N
                rep.addToBody(htmout);                    
                rep.addImageFromResources("ok.png","OK");
                htmout= "</div></div>\n";// NOI18N
                rep.addToBody(htmout);
            }
        }
        
        //Redfield Ratio
        if (rs.getString("PO4")!=null && rs.getString("NO3")!=null){// NOI18N
            double po4=rs.getDouble("PO4");
            double no3=rs.getDouble("NO3");
            if (po4>0 && no3>0) {
                double rr=no3*0.625/po4;
                if ( rr >22){//green algae // NOI18N
                    htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                    htmout=htmout+ "</div> Reidfeld ratio";// NOI18N
                    htmout=htmout+ "= <span id='ga' style=color:green;> "+LocUtil.localizeDouble(rr);  // NOI18N
                    htmout=htmout+ " " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.toorr"));// NOI18N
                    htmout=htmout+ "</span><br>";// NOI18N
                    rep.addToBody(htmout);
                }if ( rr <10){//blue algae // NOI18N
                    htmout="<div class='warning'>"+ replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!"));// NOI18N
                    htmout=htmout+ "</div> Reidfeld ratio";// NOI18N
                    htmout=htmout+ "= <span id='ga' style=color:blue;> "+LocUtil.localizeDouble(rr);  // NOI18N
                    htmout=htmout+ " " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("report.readings.lowrr"));// NOI18N
                    htmout=htmout+ "</span><br>";// NOI18N
                    rep.addToBody(htmout);
                }else{
                    htmout=" <div class='a_read'> Reidfeld Ratio"+// NOI18N
                            "="+"<div class='imgbaseline'>";// NOI18N
                    rep.addToBody(htmout);                    
                    rep.addImageFromResources("ok.png","OK");
                    htmout= "</div></div>\n";// NOI18N
                    rep.addToBody(htmout);
                }
            }       
            
        }
          
        htmout="</div>";
        rep.addToBody(htmout);
        DB.closeConn();
        addFilter(rep);
        rep.close();
        rep.out(showItNow); 
    }

    /**
     * Creates a report for plants
     *
     * @param id selected record if any
     * @param showItNow
     * @throws SQLException
     */
    public static void PlantBaseReport (String id, boolean showItNow) throws SQLException{
        Report rep=new Report();
        rep.setTitle(replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANT_DATA")));
        rep.setName("plants_data"); // NOI18N
        rep.open();
        String filter;
        filter=Plant.getFilter();         
        //htmout=""; // NOI18N
        String qry="SELECT DISTINCT Plants.id as id,Plants.Date,Plants.Name,Plants.Qty," +// NOI18N
                "Plants.Init_Status,Plants.Notes,Plants.AqID,PlantsBase.Family, " +// NOI18N
                "PlantsBase.id AS pbid,PlantsBase.Distribution,PlantsBase.Height,PlantsBase.Width," +// NOI18N
                "PlantsBase.Light, PlantsBase.Growth, PlantsBase.Demands, " +// NOI18N
                "PlantsBase.PHMin, PlantsBase.PHMax, PlantsBase.DHMin, PlantsBase.DHMax," +// NOI18N
                "PlantsBase.placement, PlantsBase.aquatic, PlantsBase.note, " +// NOI18N
                "PlantsBase.Aka, PlantsBase.CO2,  " +// NOI18N
                "PlantsBase.t_Min,PlantsBase.t_Max FROM Plants,PlantsBase WHERE AqID ='"+ // NOI18N                
                Global.AqID + "'" + Util.getPeriod()+" " + filter+ " AND Plants.Name=PlantsBase.Name";// NOI18N
        if ((id==null || id.matches(""))) { // NOI18N
            qry=qry+";";// NOI18N
        } else {
            qry=qry+" AND Plants.id='" + id + "';";// NOI18N
        }
        //System.htmout=qry);
        DB.openConn();
        //out plantsbase data
        ResultSet rs = DB.getQuery(qry);
        while (rs.next()) {
            String recId=rs.getString("pbid");// NOI18N
            String htmout="\n<div class='item'>";// NOI18N
            htmout=htmout+"\n<div class='p_id'><i>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID_HTM")+":</i>"+recId+"<br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_name'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME_HTM")+": </b>"//NOI18N
                    + replaceApos(rs.getString("Name"))+" <br></div>";// NOI18N
            //htmout=htmout+"<div class='plantImage'>";// NOI18N
            rep.addToBody(htmout);
            rep.addToBody("\n<div class='p_img'>");
            rep.addImageFromDB("PlantsBase", recId, replaceApos(rs.getString("Name")));// NOI18N
            rep.addToBody("</div>");
            //htmout=htmout+"</div>";// NOI18N
            htmout=""+"\n<div class='p_aka'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Aka")+": </b>"+ replaceApos(rs.getString("Aka"))+" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_family'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FAMILY_HTM")+": </b>"+ replaceApos(rs.getString("Family"))+" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_distrib'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DISTRIBUTION_HTM")+": </b>"+ replaceApos(rs.getString("Distribution")) +" <br></div>";// NOI18N
            if (Boolean.parseBoolean(rs.getString("Aquatic"))) {
                htmout=htmout+"\n<div class='p_aquatic'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("True_Aquatic")+". </b> <br></div>";// NOI18N
            }
            htmout=htmout+"\n<div class='p_height'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("placement")+": </b>" + replaceApos(rs.getString("Placement")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_height'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("HEIGHT_HTM")+": </b>" + replaceApos(rs.getString("Height")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_width'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WIDTH_HTM")+": </b>" + replaceApos(rs.getString("Width")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_light'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("LIGHT_HTM")+": </b>" + replaceApos(rs.getString("Light")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_growt'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GROWTH_HTM")+": </b>" + replaceApos(rs.getString("Growth")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_demands'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEMANDS_HTM")+": </b>" + replaceApos(rs.getString("Demands")) + " <br></div>";// NOI18N
            if (Boolean.parseBoolean(rs.getString("CO2"))) {
                htmout=htmout+"\n<div class='p_co2'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2.Required")+" </b> <br></div>";// NOI18N
            }
            htmout=htmout+"\n<div class='p_ph'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("PHMin"))+" - "// NOI18N
                    +LocUtil.localizeDouble(rs.getString("PHMax"))+"  <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_dh'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DH_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("DHMin"))+" - "// NOI18N
                    +LocUtil.localizeDouble(rs.getString("DHMax"))+"  <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_temp'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TEMP_C_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("t_Min"))+" - "//NOI18N
                    +LocUtil.localizeDouble(rs.getString("t_Max"))+"  <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_demands'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES_HTM")+": </b>" + replaceApos(rs.getString("Note")) + " <br></div>";// NOI18N

            htmout=htmout+"\n<div class='p_ttile'><br><b><i> "+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IN_THIS_TANK_HTM")+": </i></b><br></div>";
            htmout=htmout+"\n<div class='p_qty'>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("N._")+ " "+// NOI18N
                                rs.getString("Qty")+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTS_HTM")+"<br></div>";
            htmout=htmout+"\n<div class='p_date'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INTRODUCED_ON_HTM")+": </b>"
                            +LocUtil.localizeDate(rs.getString("Date")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='p_status'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INITIAL_STATUS_HTM")+": </b>"
                            +replaceApos(rs.getString("Init_Status"))+" <br></div> ";// NOI18N
            htmout=htmout+"\n<div class='p_note'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES_HTM")+": </b>"+
                            replaceApos(rs.getString("Notes"))+" <br></div>";// NOI18N
            htmout=htmout+"</div>";// NOI18N
            htmout= htmout+"<br>\n\n" ;// NOI18N
            rep.addToBody(htmout);

          }
        DB.closeConn();
        addFilter(rep);  
        addItemFilter(rep, filter);
        rep.close();
        rep.out(showItNow); 
    }

    /**
     * Creates a report for fishes
     *
     * @param id    selected record if any
     * @param showItNow
     * @throws SQLException
     */
    public static void FishBaseReport (String id, boolean showItNow) throws  SQLException{
        Report rep=new Report();
        rep.setTitle(replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISH_DATA")));
        rep.setName("fish_data"); // NOI18N
        rep.open();
        String filter;
        filter=Fish.getFilter();  
        String qry="SELECT DISTINCT Fishes.id as id, Fishes.Date, Fishes.Name, " + // NOI18N
                "Fishes.Males_qty, Fishes.Females_Qty, Fishes.Notes, " + // NOI18N
                " FishBase.id as fbid, FishBase.CommonName, FishBase.Class, " + // NOI18N
                "FishBase.Distribution, FishBase.Diagnosis, FishBase.Biology, " + // NOI18N
                "FishBase.Maxsize, FishBase.Environment, FishBase.Climate, " + // NOI18N
                "FishBase.Dangerous, FishBase.PHMin, FishBase.PHMax, FishBase.DHMin, " + // NOI18N
                "FishBase.DHMax, FishBase.t_Min, FishBase.t_Max, FishBase.swimLevel, FishBase.lifeSpan, FishBase.Aka " + // NOI18N
                "FROM Fishes,FishBase WHERE AqID ='"+ // NOI18N
                Global.AqID +"'" + Util.getPeriod()+" " +filter+ "  AND Fishes.Name=FishBase.Name";// NOI18N
        if ((id==null || id.matches(""))) { // NOI18N
            qry=qry+";";// NOI18N
        } else {
            qry=qry+" AND Fishes.id='" + id + "';";// NOI18N
        }
        //System.htmout=qry);
        DB.openConn();
        
        ResultSet rs = DB.getQuery(qry);
        while (rs.next()) {
            String recId=rs.getString("fbid");// NOI18N
            String htmout="\n<div class='item'>";// NOI18N
            htmout=htmout+"\n<div class='f_id'><i>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID_HTM")+": </i>"+recId+"<br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_commonName'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("COMMON_NAME_HTM")+": </b>"//NOI18N
                    + replaceApos(rs.getString("CommonName"))+" <br></div>";// NOI18N
            //htmout=htmout+"<div class='fishImage'>";// NOI18N
            rep.addToBody(htmout);
            rep.addToBody("\n<div class='f_img'>");
            rep.addImageFromDB("FishBase", recId, replaceApos(rs.getString("CommonName")));// NOI18N
            rep.addToBody("</div>");
            //htmout=htmout+"</div>";// NOI18N

            htmout=""+"\n<div class='f_class'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLASS_HTM")+": </b>"+ replaceApos(rs.getString("Class"))+" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_name'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME_HTM")+": </b>"+ replaceApos(rs.getString("Name")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_aka'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Aka")+": </b>"+ replaceApos(rs.getString("Aka")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_distr'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DISTRIBUTION_HTM")+": </b>"+ replaceApos(rs.getString("Distribution")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_diag'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DIAGNOSIS_HTM")+": </b>" + replaceApos(rs.getString("Diagnosis")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_biol'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BIOLOGY_HTM")+": </b>" + replaceApos(rs.getString("Biology")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_clim'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLIMATE_HTM")+": </b>" + replaceApos(rs.getString("Climate")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_danger'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DANGEROUS_HTM")+": </b>" + replaceApos(rs.getString("Dangerous")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_maxsize'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MAXSIZE_HTM")+": </b>" + replaceApos(rs.getString("Maxsize")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_envir'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ENVIRONMENT_HTM")+": </b>" + replaceApos(rs.getString("Environment")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_swimlev'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("SWIM_LEVEL_HTM")+": </b>"+ replaceApos(rs.getString("swimLevel")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_lifespan'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("LIFE_SPAN_HTM")+": </b>"+ replaceApos(rs.getString("lifeSpan")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_ph'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("PHMin"))+" - "// NOI18N
                    +LocUtil.localizeDouble(rs.getString("PHMax"))+"  <br></div>";//NOI18N
            htmout=htmout+"\n<div class='f_dh'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DH_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("DHMin"))+" - "// NOI18N
                    +LocUtil.localizeDouble(rs.getString("DHMax"))+"  <br></div>";//NOI18N
            htmout=htmout+"\n<div class='f_temp'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TEMP_C_HTM")+": </b>"
                    +LocUtil.localizeDouble(rs.getString("t_Min"))+" - "// NOI18N
                    +LocUtil.localizeDouble(rs.getString("t_Max"))+"  <br></div>";//NOI18N

            htmout=htmout+"\n<div class='f_ttile'><br><b><i> "+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IN_THIS_TANK_HTM")+": </i></b><br></div>";
            htmout=htmout+"\n<div class='f_maqty'>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("N._")+ " "+// NOI18N
                                rs.getString("Males_qty") + " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MALES_HTM")+" <br></div>";
            htmout=htmout+"\n<div class='f_femqty'>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("N._")+ " "+// NOI18N
                                rs.getString("Females_Qty")+ " "  + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FEMALES_HTM")+" <br></div>";
            htmout=htmout+"\n<div class='f_date'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INTRODUCED_ON_HTM")+": </b>"
                            +LocUtil.localizeDate(rs.getString("Date")) +" <br>"+"</div>";// NOI18N
            htmout=htmout+"\n<div class='f_notes'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES_HTM")+": </b>"+
                            replaceApos(rs.getString("Notes"))+" <br>"+"</div>";// NOI18N
            htmout=htmout+"</div>";// NOI18N
            htmout= htmout+"<br>\n\n" ;// NOI18N
            rep.addToBody(htmout);

          }
        DB.closeConn();
        addFilter(rep);
        addItemFilter(rep, filter);
        rep.close();
        rep.out(showItNow); 
    }

    /**
     * Creates a report for Invertebrates
     *
     * @param id    selected record if any
     * @param showItNow
     * @throws SQLException
     */
    public static void InvertsBaseReport (String id, boolean showItNow) throws  SQLException{
        Report rep=new Report();
        rep.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVERTS_DATA"));
        rep.setName("inverts_data"); // NOI18N
        rep.open();
        String filter;
        filter=Invertebrates.getFilter();
        String qry="SELECT DISTINCT Inverts.id as id, Inverts.Date, Inverts.Name, " + // NOI18N
                "Inverts.Males_qty, Inverts.Females_Qty, Inverts.Notes, " + // NOI18N
                " InvBase.id as ibid, InvBase.CommonName, InvBase.Class, " + // NOI18N
                "InvBase.Distribution, InvBase.Diagnosis, InvBase.Biology, " + // NOI18N
                "InvBase.Maxsize, InvBase.Environment, InvBase.Climate, " + // NOI18N
                "InvBase.Dangerous, InvBase.PHMin, InvBase.PHMax, InvBase.DHMin, " + // NOI18N
                "InvBase.DHMax, InvBase.t_Min, InvBase.t_Max, InvBase.swimLevel, " +
                 "InvBase.lifeSpan, InvBase.Aka, InvBase.TDSmin, InvBase.TDSmax " + // NOI18N
                "FROM Inverts,InvBase WHERE AqID ='"+ // NOI18N
                Global.AqID +"'" + Util.getPeriod()+" " + filter+ "  AND Inverts.Name=InvBase.Name";// NOI18N
        if ((id==null || id.matches(""))) { // NOI18N
            qry=qry+";";// NOI18N
        } else {
            qry=qry+" AND Inverts.id='" + id + "';";// NOI18N
        }
        //System.htmout=qry);
        DB.openConn();
        
        ResultSet rs = DB.getQuery(qry);
        while (rs.next()) {
            String recId=rs.getString("ibid");// NOI18N
            String htmout="\n<div class='item'>";// NOI18N
            htmout=htmout+"\n<div class='f_id'><i>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID_HTM")+": </i>"+recId+"<br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_commonName'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("COMMON_NAME_HTM")//NOI18N
                    +": </b>"+ replaceApos(rs.getString("CommonName"))+" <br></div>";// NOI18N
            //htmout=htmout+"<div class='fishImage'>";// NOI18N
            rep.addToBody(htmout);
            rep.addToBody("\n<div class='i_img'>");
            rep.addImageFromDB("InvBase", recId, replaceApos(rs.getString("CommonName")));// NOI18N
            rep.addToBody("</div>");
            //htmout=htmout+"</div>";// NOI18N

            htmout=""+"\n<div class='f_class'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLASS_HTM")+": </b>"+ replaceApos(rs.getString("Class"))+" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_name'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME_HTM")+": </b>"+ replaceApos(rs.getString("Name")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_aka'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Aka")+": </b>"+ replaceApos(rs.getString("Aka")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_distr'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DISTRIBUTION_HTM")+": </b>"+ replaceApos(rs.getString("Distribution")) +" <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_diag'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DIAGNOSIS_HTM")+": </b>" + replaceApos(rs.getString("Diagnosis")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_biol'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BIOLOGY_HTM")+": </b>" + replaceApos(rs.getString("Biology")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_clim'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLIMATE_HTM")+": </b>" + replaceApos(rs.getString("Climate")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_danger'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DANGEROUS_HTM")+": </b>" + replaceApos(rs.getString("Dangerous")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_maxsize'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MAXSIZE_HTM")+": </b>" + replaceApos(rs.getString("Maxsize")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_envir'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ENVIRONMENT_HTM")+": </b>" + replaceApos(rs.getString("Environment")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_swimlev'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("SWIM_LEVEL_HTM")+": </b>"+ replaceApos(rs.getString("swimLevel")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_lifespan'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("LIFE_SPAN_HTM")+": </b>"+ replaceApos(rs.getString("lifeSpan")) + " <br></div>";// NOI18N
            htmout=htmout+"\n<div class='f_ph'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH_HTM")+": </b>"
                    + LocUtil.localizeDouble(rs.getString("PHMin"))+" - "// NOI18N
                    + LocUtil.localizeDouble(rs.getString("PHMax"))+"  <br></div>";//NOI18N
            htmout=htmout+"\n<div class='f_dh'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DH_HTM")+": </b>"
                    + LocUtil.localizeDouble(rs.getString("DHMin"))+" - "// NOI18N
                    + LocUtil.localizeDouble(rs.getString("DHMax"))+"  <br></div>";//NOI18N 
            String tdsMin =""; //NOI18N
            String tdsMax=""; //NOI18N
            if (LocUtil.localizeDouble(rs.getString("TDSmin"))!=null) tdsMin = LocUtil.localizeDouble(rs.getString("TDSmin"));
            if (LocUtil.localizeDouble(rs.getString("TDSmax"))!=null) tdsMax = LocUtil.localizeDouble(rs.getString("TDSmax"));
            htmout=htmout+"\n<div class='f_dh'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TDS")+": </b>"
                    + tdsMin +" - "// NOI18N
                    + tdsMax +"  <br></div>";//NOI18N 
            htmout=htmout+"\n<div class='f_temp'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TEMP_C_HTM")+": </b>"
                    + LocUtil.localizeDouble(rs.getString("t_Min"))+" - "// NOI18N
                    + LocUtil.localizeDouble(rs.getString("t_Max"))+"  <br></div>";//NOI18N

            htmout=htmout+"\n<div class='f_ttile'><br><b><i> "+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IN_THIS_TANK_HTM")+": </i></b><br></div>";
            htmout=htmout+"\n<div class='f_maqty'>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("N._")+ " "+// NOI18N
                                rs.getString("Males_qty")+ " "  + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MALES_HTM")+" <br></div>";
            htmout=htmout+"\n<div class='f_femqty'>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("N._")+ " "+// NOI18N
                                rs.getString("Females_Qty")+ " "  + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FEMALES_HTM")+" <br></div>";
            htmout=htmout+"\n<div class='f_date'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INTRODUCED_ON_HTM")+": </b>"
                            +LocUtil.localizeDate(rs.getString("Date")) +" <br>"+"</div>";// NOI18N
            htmout=htmout+"\n<div class='f_notes'><b>"+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES_HTM")+": </b>"+
                            replaceApos(rs.getString("Notes"))+" <br>"+"</div>";// NOI18N
            htmout=htmout+"</div>";// NOI18N
            htmout= htmout+"<br>\n\n" ;// NOI18N
            rep.addToBody(htmout);

          }
        DB.closeConn();
        addFilter(rep);
        addItemFilter(rep, filter);
        rep.close();
        rep.out(showItNow);        
    }


    /**
     * Creates a report for expenses
     *
     * @param showItNow
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void ExpensesReport (boolean showItNow) throws ClassNotFoundException, SQLException{
        if (Global.AqID==0){
            AppUtil.msgSelectAquarium();
            return;
        }
        Report rep=new Report();
        rep.setTitle(replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("EXPENSES")));//NOI18N
        rep.setName("expense_data"); // NOI18N
        rep.open();
        String filter;
        filter=Expense.getFilter();  
        String htmout=""; // NOI18N
        //out aquarium data
        String qry="SELECT Name FROM Aquarium ";//NOI18N
        qry=qry + "WHERE id='" + Global.AqID  + "';";//NOI18N
        DB.openConn();
        ResultSet rs = DB.getQuery(qry);       
        while (rs.next()) {
            htmout= "\n<h2 class='aqname'>"+ replaceApos(rs.getString("Name")) + "</h2><br>\n";//NOI18N
        }
        rep.addToBody(htmout);
        //out expenses data
        rep.addToBody("<div  class='exp_total'><h3> " + replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TOTAL"))+" </h3>\n");//NOI18N
        qry="select  total(Price) as tot FROM Expenses  WHERE AqID ='"+//NOI18N
                Global.AqID + "' " + Util.getPeriod()+" " + filter+ ";";//NOI18N
        //DB.openConn();
        rs = DB.getQuery(qry);
        while (rs.next()) {
           htmout=LocUtil.localizeCurrency(rs.getString("tot"))+"</div><br><br>\n" ;//NOI18N
        }
        rep.addToBody(htmout);
        //Yearly Total
        htmout="<h3> " 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_YEARLY_TOTALS"))
                +" </h3>\n" ;//NOI18N
        //Prebuild table to get also pie values
        String expTable="";        
        int count=0;
        qry="select substr(Date,1,4) As y, total(Price) as tot from Expenses "+//NOI18N
                "WHERE AqID ='"+ Global.AqID + "'" + Util.getPeriod()+" " + filter+ " group by y;";//NOI18N
        rs = DB.getQuery(qry);
        while (rs.next()) {
            expTable=expTable+" \n<tr><td>"+//NOI18N
                    rs.getString("y")+"</td><td align='right'>"+//NOI18N
                    LocUtil.localizeCurrency(rs.getString("tot"))+"</td></tr>";//NOI18N
            count ++;
        }
        if (count>0){
            String[] pieLabels=new String [count];
            double[] pieValues=new double[count];
            rs = DB.getQuery(qry);
            count=0;
            while (rs.next()) {
                pieLabels[count]=rs.getString("y");//NOI18N
                pieValues[count]=Double.valueOf(rs.getString("tot"));//NOI18N
                count ++;
            }
            doPie(pieValues, pieLabels, "", 
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_YEARLY_TOTALS"), 
                    rep,"year_total");
        }
        
        
        htmout=htmout+"\n<div class='table'> <table border='1' cellpadding='3' cellspacing='0' summary='" 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_YEARLY_TOTALS"))
                +"'>\n";//NOI18N
        htmout=htmout+"<tr><td><b>"
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_YEAR"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_PRICE"))
                +" </b></td></tr>\n";
        htmout=htmout+expTable+"\n</table></div>";
        rep.addToBody(htmout);      
        htmout="<hr>\n";//NOI18N
        rep.addToBody(htmout);
        
        //Monthly total
        htmout="<h3> " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_MONTLY_DETAIL"))+" </h3>\n";//NOI18N
        htmout=htmout+"\n<div class='table'> <table border='1' cellpadding='3' cellspacing='0'  summary='" 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_MONTLY_DETAIL"))
                +"'>\n";//NOI18N
        htmout=htmout+"<tr><td><b>"
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_YEAR"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_MONTH"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_PRICE"))
                +" </b></td></tr>\n";
        qry="select substr(Date,1,4) As y, substr(Date,6,2) As m, total(Price) " +//NOI18N
                "as tot from Expenses WHERE AqID ='"+ Global.AqID + "'" + Util.getMidPeriod() + " " + filter+   " group by y,m;";//NOI18N
        rs = DB.getQuery(qry);
        while (rs.next()) {
            htmout=htmout+"\n <tr><td>"+rs.getString("y")+"</td><td>"+//NOI18N
                    rs.getString("m")+"</td><td align='right'>"+//NOI18N
                    LocUtil.localizeCurrency(rs.getString("tot"))+"</td></tr>";//NOI18N
        }
        htmout=htmout+"\n</table></div><hr>\n";//NOI18N
        rep.addToBody(htmout);
        
        //Detail
        htmout="<h3> " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_EXPENSES_DETAIL"))+" </h3>\n";//NOI18N
        htmout=htmout+"\n<div class='table'> <table border='1' cellpadding='4' cellspacing='0'  summary='" 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_EXPENSES_DETAIL"))
                +"'>\n";// NOI18N
        htmout=htmout+"<tr><td><b> "+
                replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_DATE"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_ITEM"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_PRICE"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_NOTES"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TYPE"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_SHOP"))
                +" </b></td></tr>\n";
        qry="SELECT * FROM Expenses  WHERE AqID ='"+ Global.AqID + "'";//NOI18N
        if (Util.getPeriod().isEmpty() || Util.getPeriod().matches("")){//NOI18N
            //add nothing
            qry=qry+" " + filter;
        }else{
            qry=qry + " " + Util.getPeriod()+" " + filter;//NOI18N
        }
        qry = qry + "ORDER BY id";//NOI18N       
        rs = DB.getQuery(qry);
        while (rs.next()) {
            htmout=htmout+"\n <tr><td>"+rs.getString("Date")+"</td><td>"+//NOI18N
                    rs.getString("Item")+"</td><td align='right'>"+//NOI18N
                    LocUtil.localizeCurrency(rs.getString("Price"))+"</td><td>"+//NOI18N
                    rs.getString("Notes")+"</td><td>"+//NOI18N
                    rs.getString("Type")+"</td><td>"+//NOI18N
                    rs.getString("Shop")+"</td></tr>";//NOI18N
        }
        htmout=htmout+"\n</table></div><hr>\n";//NOI18N
        rep.addToBody(htmout);
        //Type totals
        htmout="<h3> " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TYPE_TOTALS"))+" </h3>\n";//NOI18N
        //Prebuild table to get also pie values
        expTable="";        
        count=0;
        qry="SELECT Type, total(Price) as tot from Expenses WHERE AqID ='"+//NOI18N
                Global.AqID + "'" + Util.getPeriod()+" " + filter+  " group by Type;";//NOI18N
        
        rs = DB.getQuery(qry);
        while (rs.next()) {
            expTable=expTable+"\n <tr><td>"+rs.getString("Type")+"</td><td align='right'>"+//NOI18N
                    LocUtil.localizeCurrency(rs.getString("tot"))+"</td></tr>";//NOI18N
            count ++;
        }
        if (count>0){
            String[] pieLabels=new String [count];
            double[] pieValues=new double[count];
            rs = DB.getQuery(qry);
            count=0;
            while (rs.next()) {
                pieLabels[count]=rs.getString("type");//NOI18N
                pieValues[count]=Double.valueOf(rs.getString("tot"));//NOI18N
                count ++;
            }
            doPie(pieValues, pieLabels, "", 
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TYPE_TOTALS"), 
                    rep,"type_total");
        }
        
        htmout=htmout+"\n<div class='table'> <table border='1' cellpadding='3' cellspacing='0' summary='" 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TYPE_TOTALS"))
                +"'>\n";//NOI18N
        htmout=htmout+"<tr><td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_TYPE"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_PRICE"))
                +" </b></td></tr>\n";
        htmout=htmout+expTable+"\n</table></div><hr>\n";//NOI18N
        rep.addToBody(htmout);
        //Shop totals
        htmout="<h3> " +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_SHOP_TOTALS"))+" </h3>\n";//NOI18N
        //Prebuild table to get also pie values
        expTable="";        
        count=0;
        qry="SELECT Shop, total(Price) as tot from Expenses WHERE AqID ='"+//NOI18N
                Global.AqID + "'" + Util.getPeriod()+" " + filter+  " group by Shop;";//NOI18N
        
        rs = DB.getQuery(qry);
        while (rs.next()) {
            expTable=expTable+"\n <tr><td>"+rs.getString("Shop")+"</td><td align='right'>"+//NOI18N
                    LocUtil.localizeCurrency(rs.getString("tot"))+"</td></tr>";//NOI18N
         count ++;
        }
        if (count>0){
            String[] pieLabels=new String [count];
            double[] pieValues=new double[count];
            rs = DB.getQuery(qry);
            count=0;
            while (rs.next()) {
                pieLabels[count]=rs.getString("Shop");//NOI18N
                pieValues[count]=Double.valueOf(rs.getString("tot"));//NOI18N
                count ++;
            }
            doPie(pieValues, pieLabels, "", 
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_SHOP_TOTALS"), 
                    rep,"shop_total");
        }     
        
        htmout=htmout+"\n<div class='table'> <table border='1' cellpadding='3' cellspacing='0' summary='" 
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_SHOP_TOTALS"))
                +"'>\n";//NOI18N
        htmout=htmout+"<tr><td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_SHOP"))
                +" </b></td>";
        htmout=htmout+"<td><b> "
                +replaceApos(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("REP_HTM_PRICE"))
                +" </b></td></tr>\n";
        htmout=htmout+expTable+"</table></div>\n";//NOI18N
        rep.addToBody(htmout);
        //close
        DB.closeConn();
        addFilter(rep);
        addItemFilter(rep, filter);
        rep.close();        
        rep.out(showItNow);
    }
    
    public static void recipeReport (
            String recipeName, String element, String[] elements, 
            double [] values, String result ) {
        
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle");
        
        if (recipeName.isEmpty() || recipeName.equalsIgnoreCase("---")) {
            String msg = bundle.getString("Select.recipe.before");
            Util.showErrorMsg(msg);
            return;
        }
        
        Report rep=new Report();
        rep.setTitle(replaceApos(bundle.getString("Recipe")));//NOI18N
        rep.setName("Recipe"); // NOI18N
        rep.open();      
        
        Recipe recipe = Recipe.getByRecipeName(recipeName);
        if (recipe == null ) {
            rep.addToBody("<div class='error'> ");//NOI18N
            rep.addToBody(bundle.getString("Null.or.invalid.recipe"));//NOI18N
        }
        else {
            rep.addToBody("<div class='recipe'> \n");//NOI18N  //the global body div
            
            String htmout= "";
            htmout  = htmout + "\t<div id='aquarium'> \n"; //NOI18N           
            htmout  = htmout + "\t\t<span class='caption'> "; //NOI18N 
            htmout  = htmout + bundle.getString("Aquarium.water.volume");//NOI18N            
            htmout  = htmout +  "</span> <span class='value'>";//NOI18N
            htmout  = htmout + recipe.getWaterVolume();
            htmout  = htmout + " " + recipe.getWaterUnits();//NOI18N
            htmout  = htmout +  "</span><br> \n ";//NOI18N
            
            htmout  = htmout + "\t\t<span class='caption'> "; //NOI18N
            htmout  = htmout + bundle.getString("solutions.selected.method");//NOI18N
            htmout  = htmout +  "</span> <span class='value'>";//NOI18N
            String method = "";
            int methodNumber=Integer.parseInt(recipe.getMethod());
            switch (methodNumber) {
                case Solutions.METHOD_TARGET://NOI18N
                    method=bundle.getString("solutions.method.1.target");   //NOI18N                         
                    break;                
                case Solutions.METHOD_EI:
                    method=bundle.getString("solutions.method.3.ei"); //NOI18N                           
                    break;                
                case Solutions.METHOD_EID:
                    method=bundle.getString("solutions.method.4.eid"); //NOI18N                           
                    break;                
                case Solutions.METHOD_EIW:
                    method=bundle.getString("solutions.method.5.eiw");//NOI18N                            
                    break;                
                case Solutions.METHOD_PPS:
                    method=bundle.getString("solutions.method.6.pps"); //NOI18N                           
                    break;                
                case Solutions.METHOD_PMDD:
                    method=bundle.getString("solutions.method.7.pmdd");//NOI18N                            
                    break;
            }
            htmout  = htmout + method;
            htmout  = htmout +  "</span><br> \n \t </div>"; //NOI18N 
            //method targets            
            htmout  = htmout + "\n\t<div id='targets'> \n\t\t";//NOI18N
            htmout  = htmout + bundle.getString("method.targets") + "<br> \n\t\t";//NOI18N
            Solutions sol = new Solutions();
            double [][][] methods_parameters = sol.getMethodParameters();
            for (int i=0; i<methods_parameters.length; i++) {
                htmout  = htmout + elements[i] + "=";//NOI18N
                htmout  = htmout + LocUtil.localizeDouble(methods_parameters[i][methodNumber][0]);
                htmout  = htmout + "; ";//NOI18N
            }
            
            htmout  = htmout + " \n \t</div> \n";//NOI18N
            rep.addToBody(htmout);
        }
        
        //recipe part
        String htmout= "\t<div id='recipesummary'> \n";   //NOI18N    
        htmout = htmout + "\t\t<span class='caption'> "; //NOI18N
        htmout = htmout + bundle.getString("target.element");
        htmout = htmout +  "</span> <span class='value'> ";//NOI18N
        htmout = htmout + element;
        htmout  = htmout +  "</span><br> \n"; //NOI18N 
        if ((recipe != null) && (recipe.getForm().equalsIgnoreCase("solution"))){//NOI18N
            htmout = htmout + "\t\t<span class='caption'> "; //NOI18N
            htmout = htmout + bundle.getString("dissolve");
            htmout = htmout +  "</span> <span class='value'> ";//NOI18N
            htmout = htmout + result ;
            htmout  = htmout +  "</span><br> \n"; //NOI18N 
            
            htmout = htmout + "\t\t<span class='caption'> "; //NOI18N
            htmout = htmout + bundle.getString("in.a.recipient.of");
            htmout = htmout +  "</span> <span class='value'> ";//NOI18N 
            htmout = htmout + recipe.getSolutionVolume();
            htmout  = htmout + " ml";//NOI18N
            htmout  = htmout +  "</span><br> \n"; //NOI18N
            
            htmout = htmout + "\t\t<span class='caption'> "; //NOI18N
            htmout = htmout + bundle.getString("and.then.dose");
            htmout = htmout +  "</span> <span class='value'> ";//NOI18N
            htmout = htmout + recipe.getDoseVolume();
            htmout  = htmout + " ml";//NOI18N
            htmout  = htmout +  "</span><br> \n"; //NOI18N
            
            htmout  = htmout + "\n\t\t<div id='targets2'> \n\t\t\t";//NOI18N
            htmout  = htmout + bundle.getString("this.will.add") + "<br> \n\t\t\t";//NOI18N
            for (int i=0; i<elements.length; i++) {
                if (values[i] > 0) {                    
                    htmout  = htmout + elements[i] + "=";//NOI18N
                    htmout  = htmout + LocUtil.localizeDouble(values[i]);
                    htmout  = htmout + "; ";//NOI18N
                }
            }
            
            htmout  = htmout + " \n \t\t</div> \n";//NOI18N           
            
        } 
        else { //powder
            htmout = htmout + "\t\t<span class='caption'> "; //NOI18N
            htmout = htmout + bundle.getString("dissolve");
            htmout = htmout +  "</span> <span class='value'> ";//NOI18N
            htmout = htmout + result ;
            htmout  = htmout +  "</span><br> \n"; //NOI18N
            
            htmout = htmout + "\t\t<span> "; //NOI18N
            htmout = htmout + bundle.getString("directly.in.aquarium");
            htmout  = htmout +  "</span><br> \n"; //NOI18N
            
            htmout  = htmout + "\n\t\t<div id='targets2'> \n\t\t\t";//NOI18N
            htmout  = htmout + bundle.getString("this.will.add") + "<br> \n\t\t\t";//NOI18N
            for (int i=0; i<elements.length; i++) {
                if (values[i] > 0) {                    
                    htmout  = htmout + elements[i] + "=";//NOI18N
                    htmout  = htmout + LocUtil.localizeDouble(values[i]);
                    htmout  = htmout + "; ";//NOI18N
                }
            }
            
            htmout  = htmout + " \n \t\t</div> \n";//NOI18N    
            
        }
        
        htmout  = htmout + " \n \t</div> \n";//NOI18N
        rep.addToBody(htmout);
        
        
        rep.addToBody("</div>"); //NOI18N   //the global body div
        rep.close();        
        rep.out(true);
        
    }


   /**
    * Get average measures from Measures table
    * for the current selected acquarium and
    * convert it in HTML
    *
    * @return the html code for all infos
    * @throws ClassNotFoundException
    * @throws SQLException
    */
    public static String getAvgHtm() throws ClassNotFoundException, SQLException{
        //DS ds = new DS();
        String infos="<html><h1 align='center'>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AVERAGE_MEASURES")+
                "</h1><hr><br><p><table>";// NOI18N
        int count=0;
        String qry="SELECT count(*) as cont FROM Measures WHERE AqID='"// NOI18N
                + Global.AqID +"' ;";// NOI18N
        DB.openConn();
        ResultSet rs = DB.getQuery(qry);
        while (rs.next()) {
            count = rs.getInt("cont"); // NOI18N
        }
        DB.closeConn();
        if (count>0){//NOI18N            
             String [] hdr=Reading.CAPTIONS;
             String [] flds= {"id", "Date", "Time", "NO2", "NO3", "GH", "KH", // NOI18N
                 "PH", "temp", "FE", "NH", "CO2", "Cond", "CA", "MG", "CU", // NOI18N
                 "PO4","O2","dens","NH3","iodine","salinity"
             };// NOI18N
             for (int y=3; y<(hdr.length); y++){
                 infos=infos+ "<tr><td style='padding 0 10'> "+ hdr[y] + ":</td>"// NOI18N
                         +"<td style='padding 0 10'> "  // NOI18N                         
                         + LocUtil.localizeDouble(getMedia(flds[y]))
                         + "</td></tr>";// NOI18N             
             }                        
             //DB.closeConn();
             infos=infos+"</table></p></br>";//NOI18N
             if (Global.filterState){
                 infos=infos+"<p><br><i>";//NOI18N
                 infos=infos+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FILTERED-DATA");
                 infos=infos+"</p></i>";//NOI18N
             }
             infos=infos+"</html>";// NOI18N
        }
        return infos;
    }
    
    /**
     * Calculates the average values of a measure
     * taking care the exclusion of null values
     * 
     * @param measure to get avrg value
     * @return a double with real avg
     */
    public static double getMedia (String measure){
        double media=0;
        double validElements=0;
        double sum=0;
        DB.openConn();
        String qry="SELECT sum (" + measure + ") as thisSum FROM Measures WHERE AqID ='"// NOI18N
                + Global.AqID +"' "+  Util.getPeriod() + // NOI18N
                     " AND (" + measure + " <>null OR " + measure + " <> '' );";// NOI18N
             ResultSet rs=DB.getQuery(qry);
            try {
                sum =rs.getDouble("thisSum");// NOI18N
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
             
             
         qry="SELECT count (" + measure + ") as count FROM Measures WHERE AqID ='"// NOI18N
            + Global.AqID +"' "+  Util.getPeriod() + // NOI18N
                 " AND (" + measure + " <>null OR " + measure + " <> '' );";// NOI18N
         rs=DB.getQuery(qry);
        try {
            validElements=rs.getDouble("count");// NOI18N
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        DB.closeConn();
        if (validElements>0){
            media=sum/validElements;
        }
        return media;
    }
 

   /**
    * Gets Consumption statistics from devices table
    * for the current selected acquarium and
    * converts data in html
    *
    * @return the html code for all infos
    * @throws ClassNotFoundException
    * @throws SQLException
    */
    public static String getConsHtm() throws ClassNotFoundException, SQLException{
        String infos="<html><h1 align=center>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CONSUMPTION_STATISTICS")+
                "</h1><hr><br>"+// NOI18N
                "<p><table><thead><tr><th>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEVICE")+
                "</th><th>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("W/DAY")+
                "</th><th>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("W/YEAR")+
                "</th></tr> </thead><tbody>";// NOI18N
        String qry="SELECT Device, Brand, W, OnPeriod, Qty FROM Devices WHERE AqID ='"// NOI18N
                + Global.AqID + "';";// NOI18N
        double wattperday;
        double wattperyear;
        double totwattperd=0;
        double totwattpery=0;
        DB.openConn();
        ResultSet rs = DB.getQuery(qry);
        while (rs.next()) {
            if (rs.getDouble("W")!=0 && rs.getDouble("OnPeriod")!=0){// NOI18N
                wattperday = rs.getDouble("W") * rs.getDouble("OnPeriod");// NOI18N
                wattperyear = wattperday * 0.365; //in KW
                double qty = rs.getDouble("Qty");
                if (qty > 0 ){
                    wattperday = wattperday * qty;
                    wattperyear = wattperyear * qty;
                }
                totwattperd=totwattperd+wattperday;
                totwattpery=totwattpery+wattperyear;
                String device=rs.getString("Device");// NOI18N
                if (device.length()>20){
                    device=device.substring(0, 20);
                }
                String brand=rs.getString("Brand");// NOI18N
                if (brand.length()>20){
                    brand=brand.substring(0, 20);
                }
                infos=infos+"<tr><td>"+device + " " + brand + "</td>"// NOI18N
                        +"<td align='right' style='padding 0 10'>"+
                        LocUtil.localizeDouble(wattperday)// NOI18N
                        +"</td><td align='right' style='padding 0 10'>"// NOI18N
                        +LocUtil.localizeDouble(wattperyear)+"</td></tr>";// NOI18N
            }

         }
        DB.closeConn();
        totwattperd=totwattperd/1000;
        infos=infos+"<tr><td></td><td></td><td></td></tr>";// NOI18N
        infos=infos+"<tr><td>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TOTAL_DAILY_CONSUMPTION")+
                "</td><td> -></td><td align='right'>"+// NOI18N
                LocUtil.localizeDouble(totwattperd) +
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("KW/H")+
                "</td></tr><tr><td>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TOTAL_YEAR_CONSUMPTION")+
                "</td><td> -></td><td  align='right'>"+
                LocUtil.localizeDouble(totwattpery)+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("KW/H") +
                "</td></tr>";// NOI18N
        Setting s=Setting.getInstance();
        //double dv=Double.valueOf( DB.getSettings("def_kwcost", "0"));// NOI18N
        double dv=s.getKwCost();
        double totcost =dv * totwattpery;
        infos=infos+"<tr><td>"+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TOTAL_YEAR_COST_AT_") +
                " "+ LocUtil.localizeDouble(dv) + " " +
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("/H") +
                "</td><td> -></td><td  align='right'>"+// NOI18N
                LocUtil.localizeDouble(totcost) + " "+// NOI18N
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CUR_")+
                "</td></tr></tbody></table></p></br></html>";// NOI18N
        return infos;
    }


    // </editor-fold>
    
    private static void doPie (double [] values,String [] labels,String footer, String header, Report rep,String id){        
        //buld pie
        int count=values.length; 
        double total=0;
        for (int x=0;x<count;x++){
            total=total+values[x];//total; // divide to get %              
        }
        for (int x=0;x<count;x++){
            values[x]=values[x]/total;// get %  
        }
        Pie pie;
        pie = new Pie(values,labels, footer,header);
        pie.setSize(Global.reportChartWidth,Global.reportChartHeight);
        //JPanel cnv=new JPanel();
        //pie.setBackground(Color.white);        
        pie.repaint();
        rep.addToBody("<div class='pie_img'>");
        rep.addImageFromCode(Util.imageFromComponent(pie), id, header,1);
        rep.addToBody("</div>");
        
    }
    
    static final Logger _log = Logger.getLogger(Report.class.getName());
}

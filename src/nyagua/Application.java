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

import java.io.File;

/**
 * Application related variables
 * 
 * @author Rudi Giacomini Pilon
 * @version 1.0
 */
public class Application { 
    /** Application name */
    static final String NAME = "Nyagua";// NOI18N
    /**Application home page*/
    static final String HOMEPAGE="http://nyagua.sourceforge.net/";//NOI18N
    /** Major version of program */
    static final int MAJOR_VER = 5;
    /** Minor version of program */
    static final int MINOR_VER = 2;
    /** Sub version of program */
    static final int SUB_VER = 0;
    /** Db filename */ 
    static final String DATA_FILENAME="nyagua.db";// NOI18N
    /** OS file separator*/
    static public String FS =System.getProperty("file.separator");// NOI18N
    /** Application Icon */
   // static public Image APP_ICON =null;
    //Startup
    static final int STARTING=1;    //application is starting
    static final int STARTED=0;     //application started
    private static int startup;
    static final int DEFAULT_FORM_AQUARIUM=0;   //show aquarium on startup
    static final int DEFAULT_FORM_SCHEDULER=1;   //show scheduler on startup
    private static int defaultForm;
   
   /**
    * Set application startup condition
    * 
    * @param condition [1=STARTING || 0=STARTED]
    * 
    */
   public void setStartup (int condition){
       startup=condition;
   }
   
   /**
    * get application startup condition
    * 
    * @return [false=STARTING || true=STARTED]
    */
   public static boolean isStarted (){
        return startup==STARTED;
   }
   
   /**
    * Set a default (session) form for startup
    * 
    * @param form 
    */
   public static void setDefaultForm(int form){
       defaultForm=form;
   }
   
   /**
    * get current (session) default form to startup
    * @return 
    */
   public static int getDefaultForm (){
       return defaultForm;
   }
    /**
     * 
     * @return Program version: in the form Major ver . Minor ver
     */
    public String getVersion () {
        return Application.MAJOR_VER + "." + Application.MINOR_VER+ "." + Application.SUB_VER;// NOI18N
    }
    
    /**
     * 
     * @return sHomeDir: the user home directory (works in all OS)
     */
    private String getUserDir () {
        String sHomeDir;
        sHomeDir = System.getProperty("user.home");// NOI18N
        return  sHomeDir;            
    }    
    
    /**
     * Checks for existence of work diretory
     * @param sWD the directory to check
     * @return true if exists
     */
    private static boolean checkWorkDir (String sWD) {
        boolean exists = (new File(sWD).exists());
        return exists;
    }
    
    /**
     * Initialises the working directory, the application name
     * and the data file name
     */
    Application(){        
        Global.WorkDir = this.getUserDir()+ Application.FS + Application.NAME;
        //test if Working dir exist
        if (Application.checkWorkDir(Global.WorkDir) != true){
            Global.WorkDir = this.getUserDir();
        }
        //Global.khunit="degree";// NOI18N
    }

    /**
     * Show credits dialog box
     * 
     */
    public static void ShowCredits() {
        // Display credits
        InfoDisplay A = new InfoDisplay();
        A.setTitle("Credits");//NOI18N
        String infos = "<html><h1>Credits</h1><hr><p><br>"; //NOI18N
        infos=infos
                + "Rudi Giacomini Pilon - <i>developer and mantainer </i> - rudigiacomini[at]users.sourceforge.net <br>"//NOI18N
                + "Giacomo Fantozzi	    - <i>tests and bug reporting</i> <br>"//NOI18N
                + "Rodrigo Lacerda      - <i>brazilian portoguese translation</i> <br>"//NOI18N
                + "Tom Judge	    - <i>bug reporting/fixing</i> <br>"//NOI18N
                + "Horst-Peter Koopmann - <i>german translation</i> <br>"//NOI18N
                + " David Cooley	    - <i>bug reporting</i> <br>"//NOI18N
                + " Michael Martinson 	    - <i>bug reporting</i> <br>"//NOI18N
                + " Winfried Bergmann 	    - <i>bug reporting/fixing</i> <br>"//NOI18N
                + " Alessio 'nolith' Caiazza 	    - <i>bug reporting/fixing</i> <br>"//NOI18N
                + "Tayfun Kalyoncu       - <i>turkish translation</i> <br>"//NOI18N  
                + "Slavko       - <i>slovak translation and Debian packaging</i> - linux[at]slavino.sk <br>"//NOI18N
                + "Daniel Pęcak    - <i>polish translation</i> <br>"//NOI18N 
                + "Thierry Gorlier    - <i>french translation</i> - corniman[at]gmail.com <br>"//NOI18N 
                + "Meo Bogliolo    - <i>bug reporting</i> - mail[at]meo.bogliolo.name <br>"//NOI18N 
                + "Iain Bonnes - <i>bug reporting, alfa testing</i>  <br>"//NOI18N 
                + "Jaroslav Hauzr    - <i>czech translation</i> <br>"//NOI18N    
                + "Leandro Lima      - <i>brazilian portoguese translation</i> <br>"//NOI18N 
                + "Heimen Stoffels      - <i>dutch translation</i> - vistausss[at]outlook.com <br>"//NOI18N 
                + "Michael Izban - <i>german translation</i> - mizban[at]web.de <br>"//NOI18N
                
                + "</p></html>";//NOI18N

        A.displayInformations(infos);
        A.setVisible(true);
    }

}
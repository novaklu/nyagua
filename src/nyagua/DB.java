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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Setting;


/**
 * Database definition and operations
 * 
 * @author Rudi Giacomini Pilon
 * @version 1.0
 */
public class DB {
    
    public final static  String BLANK = ""; //NOI18N

    private static final String VERSION_0_98 = "0.98";// NOI18N
    private static final String VERSION_1_0 = "1.0";// NOI18N
    private static final String VERSION_1_1 = "1.1";// NOI18N
    private static final String VERSION_1_2 = "1.2";// NOI18N
    private static final String VERSION_1_3 = "1.3";// NOI18N
    private static final String VERSION_1_4 = "1.4";// NOI18N
    private static final String VERSION_1_5 = "1.5";// NOI18N
    private static final String VERSION_1_6 = "1.6";// NOI18N
    private static final String VERSION_1_7 = "1.7";// NOI18N
    private static final String VERSION_1_8 = "1.8";// NOI18N
    private static final String VERSION_1_9 = "1.9";// NOI18N
    private static final String VERSION_2_0 = "2.0";// NOI18N
    private static final String VERSION_2_1 = "2.1";// NOI18N
    private static final String VERSION_2_2 = "2.2";// NOI18N
    private static final String CURRENT_VERSION = VERSION_2_2;

    /** The connection between application and DB */
    static private Connection conn;
    
    /** DB Tables Names*/
    static String [] TablesNames  = {"Measures","settings","Maintenance" , // NOI18N
                "Expenses", "Devices", "FishBase", "Images", "Fishes","Plants", // NOI18N
                "PlantsBase", "Version","Aquarium","InvBase","Inverts","Schedule",
                "History", "Recipes","Plans"}; // NOI18N

    
     /** Fields of each DB table */
    public static String [][] TablesFields = {
                {"id", "Date", "Time", "NO2", "NO3", "GH", "KH", "PH", "temp", // NOI18N
                    "FE", "NH", "CO2", "Cond", "CA", "MG", "CU","AqID","PO4","O2",
                    "dens","NH3","iodine","salinity"}, // NOI18N    //measures
                {"var","value", "desc"}, // NOI18N      //preferences
                {"id", "Date", "Time", "Event", "Units", "Notes", "Warnings", // NOI18N
                 "AqID" },// NOI18N     //Maintenance
                {"id", "Date", "Item", "Price", "Notes", "Shop","AqID","Type" },// NOI18N     //Expenses
                {"id", "Device", "Brand", "W", "Notes", "OnPeriod","AqID","Qty" },// NOI18N     //Devices
                {"id", "CommonName", "Class", "Name", "Distribution", "Diagnosis", // NOI18N
                "Biology", "Maxsize", "Environment", "Climate", "Dangerous", // NOI18N
                "PHMin", "PHMax", "DHMin", "DHMax", "t_Min", "t_Max","swimLevel","lifeSpan","Aka"},// NOI18N     //fishbase
                {"id", "joined_table", "joined_id", "picture","AqID" },// NOI18N     //images
                {"id", "Date", "Name", "Males_qty", "Females_Qty", "Notes","AqID"},// NOI18N     //fishes
                {"id", "Date", "Name", "Qty", "Init_Status", "Notes","AqID"},// NOI18N     //plants
                {"id", "Name", "Family", "Distribution", "Height", "Width",  // NOI18N
                "Light", "Growth", "Demands", "PHMin", "PHMax", "DHMin", "DHMax", // NOI18N
                "t_Min", "t_Max","Placement","Aquatic","Note","Aka","CO2"},// NOI18N     //plantsbase
                {"ver"},// NOI18N     //Version
                {"id","Name","Deep","Width","Height","Water_vol","Type", // NOI18N
                         "Start_date","End_date","o_c","Tank_vol","Bottom",// NOI18N
                         "Glass_thick","Notes","Water_change"},// NOI18N     //aquarium
                {"id", "CommonName", "Class", "Name", "Distribution", "Diagnosis", // NOI18N
                "Biology", "Maxsize", "Environment", "Climate", "Dangerous", // NOI18N
                "PHMin", "PHMax", "DHMin", "DHMax", "t_Min", "t_Max","swimLevel",
                "lifeSpan","Aka","TDSmin","TDSmax"},// NOI18N     //invbase
                {"id", "Date", "Name", "Males_qty", "Females_Qty", "Notes","AqID"},// NOI18N     //inverts
                {"id","Event","Notes","Reminder","Date","RecursionType","Recursion",
                    "EndDate","Every","Status","Snoozed","AqID"},// NOI18N     //Schedule
                {"id", "Date", "Time", "Event", "AqID" },// NOI18N     //History
                {"RecipeName","Method","WaterVolume","WaterUnits",
                    "Product","Form","SolutionVolume","DoseVolume","Target",
                    "Added","Units","AqID"},//NOI18N    //Recipes
                {"id", "Name", "Method", "Recipe", "AqID","Fert_int","Fert_delay" },// NOI18N     //Plans //TODO
    }; 
    
    /** Types for fields of each DB table */
    public static String [][] FieldsTypes = {
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text","text","text",  // NOI18N
        "text", "text", "text", "text", "text", "text", "text","text", "text",  // NOI18N
        "text","text","text","text","text","text","text"}, // NOI18N      //measures
        {"text PRIMARY KEY", "text","text"}, // NOI18N      //preferences
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text",  // NOI18N
        "text"},// NOI18N     //Maintenance
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text","text"},// NOI18N     //Expenses
        {"INTEGER PRIMARY KEY", "text",  "text", "text", "text", "text", "text","text"},// NOI18N     //Devices
        {"INTEGER PRIMARY KEY", "text",  "text", "text", "text", "text", "text", // NOI18N
         "text", "text", "text", "text", "text", "text", "text", "text", "text", // NOI18N
         "text","text","text", "text"},// NOI18N     //fishbase
        {"INTEGER PRIMARY KEY", "text", "text", "blob", "text"  },// NOI18N       //images
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text" },// NOI18N     //fishes
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text" },// NOI18N     //plants
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text", // NOI18N
        "text", "text", "text", "text", "text", "text", "text", "text", "text", 
        "text", "text", "text", "text"},// NOI18N     //plantsbase
        {"text"}, // NOI18N //version
        {"INTEGER PRIMARY KEY","text","text","text","text", "text", "text",  // NOI18N
         "text", "text","text","text","text","text","text","text"},// NOI18N     //aquarium
        {"INTEGER PRIMARY KEY", "text",  "text", "text", "text", "text", "text", // NOI18N
         "text", "text", "text", "text", "text", "text", "text", "text", "text", // NOI18N
         "text","text","text", "text", "text", "text" },// NOI18N     //invbase
        {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text"},// NOI18N     //inverts
        {"INTEGER PRIMARY KEY","text","text","text","text","text","text",
                    "text","text","text","text","text"},// NOI18N     //Schedule
         {"INTEGER PRIMARY KEY", "text", "text", "text", "text"},// NOI18N     //History
         {"TEXT PRIMARY KEY", "text","text","text", "text",// NOI18N
             "text","text","text","text","text","text","text"},// NOI18N        //Recipes
         {"INTEGER PRIMARY KEY", "text", "text", "text", "text", "text", "text"},// NOI18N              //Plans 
    }; 
    
    /**
     * to initialize DB it need to verify if it exists or not.
     * if yes needs to check version 
     * if not to create DB is needed
     */
    DB()  {
        boolean goodDB=false;
        boolean exists;
        //get db name from settings
        Setting s= Setting.getInstance();
        Global.FullFileName=s.getDataFilePath();        
        if (Global.FullFileName != null) {
             exists = (new File(Global.FullFileName).exists());
            if (exists) {
                goodDB=checkValidDB();
            }
        }
        //if settings has a no valid path or no good db 
        //then we wil use default path
        if (!goodDB){
            Global.FullFileName=Global.WorkDir+Application.FS
                +Application.DATA_FILENAME;
            exists = (new File(Global.FullFileName).exists());
            if (exists) {
                goodDB=checkValidDB();
            }
        }
        //if no db in default path then show dialog
        while (!goodDB) {
            DBSelector dbs=new DBSelector(null, true); 
            dbs.setVisible(true);
            int result=dbs.getReturnStatus();
            if (result == DBSelector.RET_CANCEL) {
                //If every db operation is loosed
                System.out.println(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_VALID_DB_CHOOSED_OR_CREATED._BYE."));
                System.exit(0);
            }
            exists = (new File(Global.FullFileName).exists());
            if (exists) {
                goodDB=checkValidDB();
            }
        }
        updateVersion();
        setCurrent();
    }
    
    /**
     * setAll global variables to current db
     */
    public static void setCurrent () {
        Setting s = Setting.getInstance();
        s.setDataFilePath(Global.FullFileName); 
        int endIndex=Global.FullFileName.lastIndexOf(Application.FS);
        Global.WorkDir=Global.FullFileName.substring(0, endIndex);
    }

    /**
     * Gets the db filename
     *
     * @return db filename
     */
    public static String getCurrent (){
        Setting s = Setting.getInstance();
        s.setDataFilePath(Global.FullFileName);
        int startIndex=Global.FullFileName.lastIndexOf(Application.FS);
        int endIndex=Global.FullFileName.lastIndexOf(".");
        if (endIndex <= startIndex) {
            return "";
        }
        return Global.FullFileName.substring(startIndex, endIndex);
    }

       
    /**
     * Execute a query (connection must be already open) 
     * which return a recordset.
     * 
     * @param qry to be executed
     * @return the query resulting recordset
     */
    static public ResultSet getQuery (String qry ) {                
        try {
            Statement stat = DB.conn.createStatement();
            ResultSet rs = stat.executeQuery(qry);
            return rs;
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Execute a query (connection must be already open) 
     * which don't return a recordset.
     * 
     * @param qry to be executed    
     */
    public static void execQuery (String qry ) {                
        try {
            Statement stat = DB.conn.createStatement();
            stat.executeUpdate(qry);
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    /**
     * Opens a connection (global)
     * 
     */
    public static void openConn ()  {
        try {
            String connString = "jdbc:sqlite:" + Global.FullFileName; // NOI18N
            Class.forName("org.sqlite.JDBC"); // NOI18N
            try {
                // NOI18N
                DB.conn = DriverManager.getConnection(connString);
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
   /**
     * Allow accessing current connection
     * 
     * @return the Db connection
     */
    public  static Connection getConn(){
       return DB.conn;
   }
    
    /**
     * Close the global connection to db
     */
    public static void closeConn() {
        try {
            DB.conn.close();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Update DB version if needed
     */
    public static void updateVersion(){
        try {
            String version=getVersion();
            if (!version.matches(CURRENT_VERSION)){ // NOI18N
                String warn = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!")
                        + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("YDB")
                        + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GTB")
                        + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("YWP");
                JOptionPane.showMessageDialog (null,warn
                        ,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"),JOptionPane.WARNING_MESSAGE);
                Util.backupFile(null);
                updateDB(); // NOI18N
            }
        } catch (ClassNotFoundException | SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Checks the db version and if it's not correct then
     * update the db (connection must be already open)
     * 
     * @return version string
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static  String getVersion () throws ClassNotFoundException, SQLException  {
        String version="0.98";   // NOI18N
        DB.openConn();                
        String qry="SELECT name FROM (SELECT * FROM sqlite_master UNION ALL ";// NOI18N
        qry=qry+"SELECT * FROM sqlite_temp_master) WHERE type='table' ORDER BY name";// NOI18N
        ResultSet rs= DB.getQuery(qry);
        while (rs.next()) {
            String tableName = rs.getString("name");// NOI18N
            if ((tableName.matches("Version"))) {// NOI18N
                version = "1.0"; // NOI18N    //version 1.0 or higher
            }             
        }         
        rs.close();
        if (version.matches("1.0")) { // NOI18N
            //get exact version from table
            qry="SELECT ver FROM Version;";// NOI18N
            rs= DB.getQuery(qry);
            while (rs.next()) {
                version = rs.getString("ver"); // NOI18N
            }
        }
        rs.close();
        DB.closeConn();       
        return version;
    }

    /**
     * Set the current db version
     *
     * @param version
     * @throws SQLException
     */
    public static void setVersion(String version) throws SQLException {
        DB.openConn();
        String query = "UPDATE Version SET ver='" + version + "';";// NOI18N
        execQuery(query);
        DB.closeConn();
    }

    /**
     * Cecks tables names in DB to see if
     * it's a valid Nyagua DB
     *
     * @return true if at least 9 tables matches
     */
    public static boolean checkValidDB () {
        try {
            int tablesChecked = 0;
            DB.openConn();
            //select tables names from db
            String qry = "SELECT name FROM (SELECT * FROM sqlite_master UNION ALL "; // NOI18N
            qry = qry + "SELECT * FROM sqlite_temp_master) WHERE type='table' ORDER BY name"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            //compares the db tables names with the names defined in this class
            while (rs.next()) {
                String tableName = rs.getString("name"); // NOI18N
                for (String TablesName : TablesNames) {
                    if (TablesName.matches(tableName)) {
                        tablesChecked++;
                        break;
                    }
                }
            }
            DB.closeConn();
            return tablesChecked >= 9;
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
            return false;
        }
    }


    /**
     * Creates a new DB and set version number
     * @return true=success; false=failure
     */
    public static boolean createNewDB () {
        String query;
        for (int y = 0 ;y<DB.TablesNames.length ; y++){   //for each table
            try {
                //for each table
                int numFields = DB.TablesFields[y].length;
                query = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
                for (int x = 0; x < numFields; x++) {
                    query = query + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
                    query = query + DB.FieldsTypes[y][x];
                    if (x < (numFields) - 1) {
                        query = query + ","; // NOI18N
                    } else {
                        query = query + ")"; // NOI18N
                    }
                }
                DB.openConn();
                Statement stat = conn.createStatement();
                stat.executeUpdate(query);
                DB.closeConn();
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
                return false;
            }
        }
        //Set version
        DB.openConn();  
        String qry="SELECT COUNT (ver) AS cnt FROM Version;";// NOI18N
        ResultSet rs;
        int count=0;
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                count=rs.getInt("cnt");// NOI18N
            }
        } catch (SQLException ex) {
        }           
        if (count<=0) {
            query= "INSERT INTO Version values (\"" + CURRENT_VERSION + "\");"; // NOI18N
            DB.execQuery(query);
        }
                   
        DB.closeConn();
        return true;
    }
    
    /**
     * Updates DB from previous versions
     *      
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    private static void updateDB () throws ClassNotFoundException,
            SQLException {
        String fromVersion = getVersion();
        if (fromVersion != null) {
            double requiredVersion = Double.valueOf(CURRENT_VERSION);
            double oldVersion = Double.valueOf(fromVersion);
            if (oldVersion > requiredVersion) {
                String warn = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING!");
                warn += " ";
                warn += java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DB_VERSION");
                JOptionPane.showMessageDialog (null,warn
                        ,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"),JOptionPane.WARNING_MESSAGE);
                System.out.println(warn);
            }
        }        
        //update from ver 0.98 to 1.8
        while (!fromVersion.equals(CURRENT_VERSION)) {
            //update from ver 0.98 to 1.0
            if (fromVersion.matches(VERSION_0_98)) {   // NOI18N
                update0_98to1_0();
            } else if (fromVersion.equals(VERSION_1_0)) {
                update1_0to1_1();
            }else if (fromVersion.equals(VERSION_1_1)) {
                update1_1to1_2();
            }else if (fromVersion.equals(VERSION_1_2)) {
                update1_2to1_3();
            }else if (fromVersion.equals(VERSION_1_3)) {
                update1_3to1_4();
            }else if (fromVersion.equals(VERSION_1_4)) {
                update1_4to1_5();
            }else if (fromVersion.equals(VERSION_1_5)) {
                update1_5to1_6();
            }else if (fromVersion.equals(VERSION_1_6)) {
                update1_6to1_7();
            }else if (fromVersion.equals(VERSION_1_7)) {
                update1_7to1_8();
            }else if (fromVersion.equals(VERSION_1_8)) {
                update1_8to1_9();
            }else if (fromVersion.equals(VERSION_1_9)) {
                update1_9to2_0();
            }else if (fromVersion.equals(VERSION_2_0)) {
                update2_0to2_1();
            }else if (fromVersion.equals(VERSION_2_1)) {
                update2_1to2_2();
            }
            
            fromVersion = getVersion();
            
        }
    }

    /**
     * Specific updates from v. 0.98 to 1.0
     * 
     * @deprecated this function now return an
     * error message
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void update0_98to1_0 () throws ClassNotFoundException, SQLException {
        Util.showErrorMsg("Version 0.98 DB files are no more supported.");
        /*DB.createNewDB();     // create all not existing tables
            // create default  aquarium data
            String query= "INSERT INTO Aquarium  ('Name')" +// NOI18N
                    " values (' -- default -- ');";// NOI18N
            DB.openConn();
            execQuery(query);
            DB.closeConn();
            //get aquarium id
            int id = DB.DBLastId("Aquarium");// NOI18N

            //Add fields and set default value
            String [] tablesToAlter = {"Measures","Maintenance" ,"Expenses",// NOI18N
            "Devices","Images", "Fishes","Plants"}; // NOI18N
            DB.openConn();
            for (int y=0; y<tablesToAlter.length;y++) {
                query = "ALTER TABLE "+ tablesToAlter[y] +" ADD COLUMN AqID text;";// NOI18N
                execQuery(query);
                query = "UPDATE "+ tablesToAlter[y] + " SET AqID = " // NOI18N
                        + id + " WHERE 1=1";            // NOI18N
                execQuery(query);
            }
            DB.closeConn();
            DB.DBUpdImgVersion();
            query = "INSERT INTO Version (ver) VALUES ('" + VERSION_1_0 + "';";// NOI18N
            execQuery(query);*/
    }

    /**
     *  Specific updates from v. 1.0 to 1.1
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void update1_0to1_1() throws ClassNotFoundException, SQLException{
        Setting s = Setting.getInstance();
        DB.openConn();
        try (ResultSet rs = getQuery("SELECT * FROM settings;")) {
            String var;
            while (rs.next()) {
                 var=rs.getString("var");// NOI18N
                switch (var) {
                    case "def_browser": // NOI18N
                        s.setBrowser(rs.getString("value"));// NOI18N
                        break;
                    case "def_kwcost": // NOI18N
                        s.setKwCost(Double.valueOf(rs.getString("value")) );// NOI18N
                        break;
                    case "pl_def_output":// NOI18N
                        s.setPl_Output(rs.getString("value"),null);// NOI18N
                        break;
                    case "pl_def_grid": // NOI18N
                        s.setPl_Grid(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_NO2": // NOI18N
                        s.setPl_NO2(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_temp":// NOI18N
                        s.setPl_Temp(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_NO3":// NOI18N
                        s.setPl_NO3(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_NH": // NOI18N
                        s.setPl_NH(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_KH":  // NOI18N
                        s.setPl_KH(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_GH": // NOI18N
                        s.setPl_GH(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_FE":  // NOI18N
                        s.setPl_FE(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_PH":  // NOI18N
                        s.setPl_PH(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_Key":   // NOI18N
                        s.setPl_Key(rs.getString("value"),null);// NOI18N
                        break;
                    case "pl_def_Gridstep": // NOI18N
                        s.setPl_Gridstep(rs.getString("value"),null);// NOI18N
                        break;
                    case "pl_def_CO2":  // NOI18N
                        s.setPl_CO2(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_Cond": // NOI18N
                        s.setPl_Cond(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_CA": // NOI18N
                        s.setPl_CA(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_MG": // NOI18N
                        s.setPl_MG(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                    case "pl_def_CU":  // NOI18N
                        s.setPl_CU(rs.getString("value").equals("1"),null);// NOI18N
                        break;
                }
            }
        }
        String query = "DROP TABLE settings;";// NOI18N
        execQuery(query);
        DB.closeConn();
        setVersion(VERSION_1_1);
    }

    private static void update1_1to1_2() throws ClassNotFoundException, SQLException{
        //add bottom and tank_vol field in acquarium
        DB.openConn();
        String query = "ALTER TABLE Aquarium ADD COLUMN Tank_vol text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE Aquarium ADD COLUMN Bottom text;";// NOI18N
        execQuery(query);
        DB.closeConn();
        setVersion(VERSION_1_2);
    }
  
    private static void update1_2to1_3() throws ClassNotFoundException, SQLException{
        //add swimLevel field in fishbase
        DB.openConn();
        String query = "ALTER TABLE FishBase ADD COLUMN swimLevel text;";// NOI18N
        execQuery(query);
        DB.closeConn();
        setVersion(VERSION_1_3);
    }
    
    private static void update1_3to1_4() throws ClassNotFoundException, SQLException{
        //add swimLevel field in fishbase
        DB.openConn();
        String query = "ALTER TABLE FishBase ADD COLUMN lifeSpan text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE Measures ADD COLUMN PO4 text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE Measures ADD COLUMN O2 text;";// NOI18N        
        execQuery(query);
        query = "ALTER TABLE Measures ADD COLUMN dens text;";// NOI18N
        execQuery(query);
        DB.closeConn();
        setVersion(VERSION_1_4);
    }
    
    private static void update1_4to1_5() throws ClassNotFoundException, SQLException{
        //Add invbase & inverts tables
        String query; 
        for (int y = 12 ;y<14 ; y++){   //for each of two tables            
            int numFields = DB.TablesFields[y].length;
            query = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
            for (int x = 0; x < numFields; x++) {
                query = query + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
                query = query + DB.FieldsTypes[y][x];
                if (x < (numFields) - 1) {
                    query = query + ","; // NOI18N
                } else {
                    query = query + ")"; // NOI18N
                }
            }
            DB.openConn();
            Statement stat = conn.createStatement();
            stat.executeUpdate(query);
            DB.closeConn();
        }
        setVersion(VERSION_1_5);
    }
    
    private static void update1_5to1_6() throws ClassNotFoundException, SQLException{
        //add type field in expenses
        DB.openConn();
        String query = "ALTER TABLE Expenses ADD COLUMN Type text;";// NOI18N
        execQuery(query);
        query = "UPDATE Expenses SET Type='' WHERE TYPE is null;";// NOI18N
        execQuery(query);
        DB.closeConn();
        setVersion(VERSION_1_6);
    }
    
    private static void update1_6to1_7() throws ClassNotFoundException, SQLException{
        //add type field in expenses
        String query; 
        int y=14;   //schedule table              
        int numFields = DB.TablesFields[y].length;
        query = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
        for (int x = 0; x < numFields; x++) {
            query = query + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
            query = query + DB.FieldsTypes[y][x];
            if (x < (numFields) - 1) {
                query = query + ","; // NOI18N
            } else {
                query = query + ")"; // NOI18N
            }
        }
        DB.openConn();
        Statement stat = conn.createStatement();
        stat.executeUpdate(query);
        DB.closeConn();          
        setVersion(VERSION_1_7);
    }
    
     private static void update1_7to1_8() throws ClassNotFoundException, SQLException{
        //add type field in expenses
       String query; 
        int y=15;   //history table              
        int numFields = DB.TablesFields[y].length;
        query = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
        for (int x = 0; x < numFields; x++) {
            query = query + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
            query = query + DB.FieldsTypes[y][x];
            if (x < (numFields) - 1) {
                query = query + ","; // NOI18N
            } else {
                query = query + ")"; // NOI18N
            }
        }
        DB.openConn();
        Statement stat = conn.createStatement();
        stat.executeUpdate(query);
        //new fields
        query = "ALTER TABLE Aquarium ADD COLUMN Glass_thick text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE Aquarium ADD COLUMN Notes text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE PlantsBase ADD COLUMN Placement text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE PlantsBase ADD COLUMN Aquatic text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE PlantsBase ADD COLUMN Note text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE Measures ADD COLUMN NH3 text;";// NOI18N        
        execQuery(query);
        DB.closeConn();          
        setVersion(VERSION_1_8);
    }
     
    private static void update1_8to1_9() 
            throws ClassNotFoundException, SQLException {
        
        //add type field in expenses
       String query; 
        int y=16;   //Recipes table              
        int numFields = DB.TablesFields[y].length;
        query = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
        for (int x = 0; x < numFields; x++) {
            query = query + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
            query = query + DB.FieldsTypes[y][x];
            if (x < (numFields) - 1) {
                query = query + ","; // NOI18N
            } else {
                query = query + ")"; // NOI18N
            }
        }
        String query2; 
        y=17;   //Plans table              
        numFields = DB.TablesFields[y].length;
        query2 = "CREATE TABLE IF NOT EXISTS " + DB.TablesNames[y] + " ("; // NOI18N
        for (int x = 0; x < numFields; x++) {
            query2 = query2 + DB.TablesFields[y][x] + " "; // NOI18N// NOI18N
            query2 = query2 + DB.FieldsTypes[y][x];
            if (x < (numFields) - 1) {
                query2 = query2 + ","; // NOI18N
            } else {
                query2 = query2 + ")"; // NOI18N
            }
        }
        DB.openConn();
        Statement stat = conn.createStatement();
        stat.executeUpdate(query);
        stat.executeUpdate(query2);
        DB.closeConn();          
        setVersion(VERSION_1_9);
    } 
    
    private static void update1_9to2_0() 
            throws ClassNotFoundException, SQLException {
        
        String query; 
        DB.openConn();
        //new fields
        query = "ALTER TABLE PlantsBase ADD COLUMN Aka text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE PlantsBase ADD COLUMN CO2 text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE FishBase ADD COLUMN Aka text;";// NOI18N
        execQuery(query);
        query = "ALTER TABLE InvBase ADD COLUMN Aka text;";// NOI18N        
        execQuery(query);
        DB.closeConn(); 
        setVersion(VERSION_2_0);
    }
    
    private static void update2_0to2_1() 
            throws ClassNotFoundException, SQLException {
        
        String query; 
        DB.openConn();        //new fields        
        query = "ALTER TABLE InvBase ADD COLUMN TDSmin text;";// NOI18N        
        execQuery(query);
        query = "ALTER TABLE InvBase ADD COLUMN TDSmax text;";// NOI18N        
        execQuery(query);
        DB.closeConn(); 
        setVersion(VERSION_2_1);
    }

    private static void update2_1to2_2() 
            throws ClassNotFoundException, SQLException {
        
        String query; 
        DB.openConn();        //new fields        
        query = "ALTER TABLE Measures ADD COLUMN iodine text;";// NOI18N        
        execQuery(query);
        query = "ALTER TABLE Measures ADD COLUMN salinity text;";// NOI18N        
        execQuery(query);
        query = "ALTER TABLE Aquarium ADD COLUMN Water_change text;";// NOI18N        
        execQuery(query);
        query = "ALTER TABLE Devices ADD COLUMN Qty text;";// NOI18N        
        execQuery(query);  
        query = "ALTER TABLE Plans ADD COLUMN Fert_int text;";// NOI18N        
        execQuery(query); 
        query = "ALTER TABLE Plans ADD COLUMN Fert_delay text;";// NOI18N        
        execQuery(query);         
        DB.closeConn(); 
        setVersion(VERSION_2_2);        
    }
    
    /**
     * Gets last id from a table
     * 
     * @param TableName
     * @return last id number
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static int DBLastId (String TableName) throws ClassNotFoundException, SQLException{
        int LastId=0;
        String query = "SELECT MAX(id) AS max FROM " + TableName;// NOI18N
        DB.openConn();
        ResultSet rs= DB.getQuery(query);
        while (rs.next()) {
            LastId = rs.getInt("max");// NOI18N
        }
        DB.closeConn();
        return LastId;
    }
    
    
    /**
     * Gets last id from a table for the current aquarium
     * 
     * @param TableName
     * @return last id number
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static int DBLastIdInAquarium (String TableName) throws ClassNotFoundException, SQLException{
        int LastId=0;
        String query = "SELECT MAX(id) AS max FROM " + TableName + // NOI18N
                " WHERE AqID ='" + Global.AqID + "' ";// NOI18N
        DB.openConn();
        ResultSet rs= DB.getQuery(query);
        while (rs.next()) {
            LastId = rs.getInt("max");// NOI18N
        }
        DB.closeConn();
        return LastId;
    }
    
    /**
     * Gets records count from a table
     * 
     * @param TableName
     * @return number of records
     * @throws SQLException 
     */
    public static int DBCount (String TableName) throws SQLException {
        int elements=0;
        String query = "Select count(*) as tot FROM " + TableName;// NOI18N
        DB.openConn();
        ResultSet rs= DB.getQuery(query);
        while (rs.next()) {
            elements = rs.getInt("tot");// NOI18N
        }
        DB.closeConn();
        return elements;        
    }
    
    /**
     * Gest records count from a related table for a given value
     * 
     * @param Value         value searched
     * @param TableName     table to seach in
     * @param TableField    field to search in
     * @return              amount of related records
     * @throws SQLException 
     */
    public static int DBCountRelated (String Value, String TableName, String TableField) throws SQLException{
        int elements=0;
        String query = "Select count(*) as tot FROM " + TableName +// NOI18N
                " WHERE " + TableField + "='" + Value + "';";// NOI18N
        DB.openConn();
        ResultSet rs= DB.getQuery(query);
        while (rs.next()) {
            elements = rs.getInt("tot");// NOI18N
        }
        DB.closeConn();
        return elements; 
        
    }

    /**
     * Delete a row from a table
     * @param dbTable affected table
     * @param recID the id of record to delete
     * @return 
     *
     */
    public static boolean DbDelRow (String dbTable, String recID){ 
        boolean result=false;
        if (recID.isEmpty() || recID.matches("")){// NOI18N
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_ITEM_SELECTED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"), JOptionPane.OK_OPTION);
                    return result;
        }else{
            int a = JOptionPane.showConfirmDialog(null,
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DO_YOU_REALLY_WANT_TO_DELETE_ITEM_N.") + recID,
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING"), JOptionPane.YES_NO_OPTION);
            if ( a== JOptionPane.YES_OPTION){
                String qry = "DELETE FROM " +  dbTable + " WHERE id="+recID+";";// NOI18N
                    
                DB.openConn();
                DB.execQuery(qry);
                DB.closeConn();
                return true;
            }else{
                return result;
            }
        }
    }
    
     /**
     * Delete a row from a table
     * @param dbTable affected table
     * @param columnName
     * @param recID the id of record to delete
     * @return 
     *
     */
    public static boolean DbDelRowByPK (String dbTable, String columnName, String recID){ 
        boolean result=false;
        if (recID.isEmpty() || recID.matches("")){// NOI18N
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_ITEM_SELECTED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"), JOptionPane.OK_OPTION);
                    return result;
        }else{
            int a = JOptionPane.showConfirmDialog(null,
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DO_YOU_REALLY_WANT_TO_DELETE_ITEM") + recID,
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING"), JOptionPane.YES_NO_OPTION);
            if ( a== JOptionPane.YES_OPTION){
                String qry = "DELETE FROM " +  dbTable + " WHERE " + columnName + "='"+recID+"';";// NOI18N
                    
                DB.openConn();
                DB.execQuery(qry);
                DB.closeConn();
                return true;
            }else{
                return result;
            }
        }
    }

    /**
     * Deletes an image from db
     * 
     * @param tableName the related table
     * @param joinedId  the related id
     */
    public static void DBDeleteImage (String tableName,String joinedId) {
        String qry = "DELETE FROM Images WHERE joined_table='"+ tableName // NOI18N
                        + "' AND  joined_id='" + joinedId + "' AND  AqID='"// NOI18N
                        + Global.AqID +"';";// NOI18N        
        DB.openConn();
        DB.execQuery(qry);
        DB.closeConn();
    }

    /**
     * Store an image in database
     * @param img the image to store
     * @param tableName  the name of the table where the image is related
     * @param joinedId  the id of record to which image is related
     *
     */
    public static void DBSaveImage (BufferedImage img,String tableName,String joinedId) {        
        if(img != null) {
            try {                
                byte[] buffer = Util.image_byte_data(img);
                Blob blob_img= null;
                blob_img=new SerialBlob(buffer); 
                String qry="SELECT id FROM Images WHERE joined_table='"+ tableName // NOI18N
                        + "' AND  joined_id='" + joinedId + "' ";// NOI18N                        
                if (tableName.equalsIgnoreCase("FishBase") || // NOI18N
                        tableName.equalsIgnoreCase("PlantsBase") || // NOI18N
                        tableName.equalsIgnoreCase("InvBase")){// NOI18N
                    qry=qry+";";// NOI18N
                }else{
                    qry=qry+"AND  AqID='"+ Global.AqID +"';";// NOI18N
                }
                DB.openConn();
                ResultSet rs = DB.getQuery(qry);
                String id=null;
                while (rs.next()) {
                        id=rs.getString("id");
                }
                PreparedStatement prep = conn.prepareStatement("INSERT OR " +// NOI18N
                        "REPLACE INTO Images  VALUES (?,?,?,?,?);");// NOI18N
                prep.setString(1,id);
                prep.setString(2, tableName);
                prep.setString(3,joinedId);
                prep.setBytes(4, buffer);
                if (tableName.equalsIgnoreCase("FishBase") || // NOI18N
                        tableName.equalsIgnoreCase("PlantsBase") || // NOI18N
                        tableName.equalsIgnoreCase("InvBase")){// NOI18N
                    prep.setString(5,"0");// NOI18N
                }else{
                    prep.setString(5,Integer.toString(Global.AqID));
                }
                prep.addBatch();
                conn.setAutoCommit(false);
                    prep.executeBatch();
                conn.setAutoCommit(true);
                DB.closeConn();

                /*DB.openConn();
                DB.execQuery(qry);
                DB.closeConn();*/            
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Load an image from database
     * @param tableName  the name of the table where the image is related
     * @param joinedId  the id of record to which image is related
     * @return the selected image
     * @throws ClassNotFoundException
     * @throws SQLException 
     * @throws IOException  
     *
     */
    public static BufferedImage DBLoadImage (String tableName,String joinedId)
            throws ClassNotFoundException, SQLException, IOException {
         BufferedImage img=null;
         byte[] blob_img;         
         String qry="SELECT * FROM Images WHERE joined_table='"+ tableName// NOI18N
                        + "' AND  joined_id='" + joinedId + "' ";// NOI18N
         if (tableName.equalsIgnoreCase("FishBase") || // NOI18N
                 tableName.equalsIgnoreCase("PlantsBase") || // NOI18N
                 tableName.equalsIgnoreCase("InvBase")){// NOI18N
             qry=qry+";";// NOI18N
         }else{
             qry=qry+"AND  AqID='"+ Global.AqID +"';";// NOI18N
         }
                 
         DB.openConn();
         ResultSet rs = DB.getQuery(qry);         
         while (rs.next()) {             
             blob_img=rs.getBytes("picture");// NOI18N
             img = Util.byte_image_data(blob_img);
         }
         DB.closeConn();         
         return img;
     }
    
    /**
     * Test if exist an image for a table-row
     * @param tableName the table related
     * @param joinedId  the related record
     * @return true if found otherwise false
     */
    public static boolean DbTestImagePresence (String tableName,String joinedId) {
         //BufferedImage img=null;
         //byte[] blob_img=null;
         boolean result=false;
         String qry="SELECT count(*) as cont FROM Images WHERE joined_table='"+ tableName// NOI18N
                        + "' AND  joined_id='" + joinedId + "'";// NOI18N
         if (tableName.equalsIgnoreCase("FishBase") || // NOI18N
                 tableName.equalsIgnoreCase("InvBase") || // NOI18N
                 tableName.equalsIgnoreCase("PlantsBase")){// NOI18N
             qry=qry+";";// NOI18N
         }else{
             qry=qry+"AND  AqID='"+ Global.AqID +"';";// NOI18N
         }
         DB.openConn();
         ResultSet rs = DB.getQuery(qry);
         int records=0;
        try {
            while (rs.next()) {
                records = rs.getInt("cont"); // NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
         DB.closeConn(); 
         if (records>0){
            result=true;
         }                 
         return result;
    }


    /**
     * Check and validate db 
     * files interface
     * 
     * @param mode
     * @return true on valid DB
     */
    public static boolean fileDBOperations (int mode){
        String oldFullFilePath=Global.FullFileName;
        DBSelector dbs=new DBSelector(null, true);
        dbs.setMode(mode);
        dbs.setVisible(true);
        int result=dbs.getReturnStatus();
        if (result == DBSelector.RET_CANCEL) {
            Global.FullFileName=oldFullFilePath;
            return false;
        }
        boolean exists = (new File(Global.FullFileName).exists());
        if (exists) {
            if (DB.checkValidDB()){
                setCurrent();
                return true;
            }else {
                Global.FullFileName=oldFullFilePath;
                Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR._WRONG_DB_FORMAT"));
                return false;
            }
        } else {
            Global.FullFileName=oldFullFilePath;
            Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR._FILE_NOT_FOUND"));
            return false;
        }
    }

    /**
     * Empties a jTable assigning null model
     * 
     * @param diplayData
     */
    public static void emptyTable (JTable diplayData){
        DefaultTableModel dm = new DefaultTableModel();
        String tableData[][] = {{null}};
        String[] nameHeader = {java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_SELECTION")};
        dm.setDataVector(tableData, nameHeader);
        diplayData.setModel(dm);
    }
    
        
    /**
     * Create a Filter for query based on populated Text Fields
     * relating TextFields with db Fields on array order basis
     * the counter parameter is used to add WHERE or AND related
     * to other filters condition
     *
     * @param jTF
     * @param dbFields
     * @return String the filter
     */
    public static String createFilter (JTextField [] jTF, String [] dbFields){
        String filter="";
        for (int x=0;x < jTF.length;x++){
            if (!(jTF[x].getText().isEmpty())){
                filter =filter+" AND ";//NOI18N                
                filter=filter + dbFields[x] + " LIKE '%" + jTF[x].getText() +"%' " ;//NOI18N
            }
        }
        return filter;
    }
    
    /**
     * Create a Filter for query based on populated Text Fields
     * relating TextFields with db Fields on array order basis
     * the counter parameter is used to add WHERE or AND related
     * to other filters condition
     *
     * @param jTF
     * @param dbFields
     * @return String  the filter
     */
    public static String createFilter (JTextArea [] jTF, String [] dbFields){
        String filter="";
        for (int x=0;x < jTF.length;x++){
            if (!(jTF[x].getText().isEmpty())){
                filter =filter+" AND ";//NOI18N                
                filter=filter + dbFields[x] + " LIKE '%" + jTF[x].getText() +"%' " ;//NOI18N
            }
        }
        return filter;
    }
    
    /**
     * Create a Filter for query based on populated Text Fields
     * relating TextFields with db Fields on array order basis
     * the counter parameter is used to add WHERE or AND related
     * to other filters condition
     *
     * @param jTF
     * @param dbFields
     * @return String  the filter
     */
    public static String createFilter (JComboBox [] jTF, String [] dbFields){
        String filter="";
        for (int x=0;x < jTF.length;x++){
            String fieldText=jTF[x].getSelectedItem().toString();
            if (fieldText.matches("---")) {
                fieldText="";
            }
            if (!(fieldText.isEmpty())){
                filter =filter+" AND ";//NOI18N                
                filter=filter + dbFields[x] + " LIKE '%" + jTF[x].getSelectedItem().toString() +"%' " ;//NOI18N
            }
        }
        return filter;
    }
    
    /**
     * Create a Filter for query based on populated Text Fields
     * relating TextFields with db Fields on array order basis
     * the counter parameter is used to add WHERE or AND related
     * to other filters condition
     *
     * @param jTF
     * @param dbFields
     * @return String the filter
     */
    public static String createNumericFilter (JTextField [] jTF, String [] dbFields){
        String filter="";
        for (int x=0;x < jTF.length;x++){
            if (!(jTF[x].getText().isEmpty())){
                filter =filter+" AND ";//NOI18N                
                filter=filter + dbFields[x] + " LIKE '%" + LocUtil.delocalizeDouble(jTF[x].getText()) +"%' " ;//NOI18N
            }
        }
        return filter;
    }
    
    /**
     * compact DB cleaning waste space
     */
    public static void vacuumDb () {
        String qry="VACUUM;";
        DB.openConn();
        DB.execQuery(qry);
        DB.closeConn();
    }
    
    public static String exportTable(String tablename) {
        
        int index =  0;
        for (int i = 0; i < TablesNames.length; i++) {
            if (tablename.equalsIgnoreCase(TablesNames[i])) {
                index = i;
                break;
            }
        }
        if (index == 0) {
            _log.log(Level.SEVERE,"wrong table name.");
        }        
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < TablesFields[index].length; i++  ) {
            
            String value = TablesFields[index][i];
            if (value == null) value = BLANK;
            sb.append(value);
            if (i < (TablesFields[index].length-1)) {
                sb.append(FIELD_SEP);
            }
        }
        sb.append(LINE_TERM + NL);
        
        try {    
           DB.openConn();
            String  qry = "SELECT * FROM " + tablename + " ORDER BY id DESC;"; // NOI18N
            ResultSet rs =  DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {    
                 for (int i = 1; i < TablesFields[index].length; i++  ) {
                    sb.append(rs.getString(i));
                    if (i < (TablesFields[index].length-1)) {
                        sb.append(FIELD_SEP);
                    }
                 }
                sb.append(LINE_TERM + NL);
            }
        }
        catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
         
        }
        finally {
            DB.closeConn();
        }

        return sb.toString();
            
    }
    
    /**
     *
     * @param tablename
     * @return  list of images id related to table
     */
    public static List<String> getImagesIds (String tablename ){
        
        int index =  0;
        for (int i = 0; i < TablesNames.length; i++) {
            if (tablename.equalsIgnoreCase(TablesNames[i])) {
                index = i;
                break;
            }
        }
        if (index == 0) {
            _log.log(Level.SEVERE,"wrong table name.");
        } 
        List<String> imgList = new ArrayList<>();
        
        try {    
           DB.openConn();
            String  qry = "SELECT DISTINCT joined_id FROM images where joined_table='" + tablename // NOI18N
                    + "' AND joined_id IN (SELECT id FROM " + tablename + ");"; // NOI18N
//             System.out.print("query:"+qry);
            ResultSet rs =  DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                
                imgList.add(rs.getString("joined_id"));
//                 System.out.print(rs.getString("id") + Global.RECSEPARATOR);               
                
            }
        }
        catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
         
        }
        finally {
            DB.closeConn();
        }      
        
        return imgList;
    } 
    
    static final String NL="\n";
    static final String FIELD_SEP="||";
    static final String LINE_TERM="#|";
    static final Logger _log = Logger.getLogger(DB.class.getName());
    
}   
    

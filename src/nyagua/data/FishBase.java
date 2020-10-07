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

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import nyagua.DB;
import nyagua.LocUtil;
import nyagua.Util;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class FishBase {
    public static final String TABLE = "FishBase";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("COMMONNAME"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLASS"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DISTRIBUTION"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DIAGNOSIS"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BIOLOGY"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MAXSIZE"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ENVIRONMENT"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CLIMATE"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DANGEROUS"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PHMIN"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PHMAX"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DHMIN"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DHMAX"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("T_MIN"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("T_MAX"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.fbSwimLevelLabel.text"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("LIFE_SPAN"),
                 java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Aka")
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String name;
    private String commonName;
    private String type;
    private String distribution;
    private String diagnosis;
    private String biology;
    private String maxSize;
    private String environment;
    private String climate;
    private String dangerous;
    private String phMin;
    private String phMax;
    private String dhMin;
    private String dhMax;
    private String tempMin;
    private String tempMax;
    private String swimLevel;
    private String lifeSpam;
    private String Aka;
    private boolean imagePresent;
    private BufferedImage specImage;
    
    private static final int[] colWidth = new int[CAPTIONS.length];
        
    private static String filter="";
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Data Access Methods">

    /**
     * @return the id
     */
    public int getId(){
        return id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(int id){
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    /**
     * @return the alias
     */
    public String getAKA() {
        return Aka;
    }

    /**
     * @param aka   set alias
     */
    public void setAka(String aka) {
        this.Aka = aka;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the distribution
     */
    public String getDistribution() {
        return distribution;
    }

    /**
     * @param distribution the distribution to set
     */
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    /**
     * @return the diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * @param diagnosis the diagnosis to set
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * @return the biology
     */
    public String getBiology() {
        return biology;
    }

    /**
     * @param biology the biology to set
     */
    public void setBiology(String biology) {
        this.biology = biology;
    }

    /**
     * @return the maxSize
     */
    public String getMaxSize() {
        return maxSize;
    }

    /**
     * @param maxSize the maxSize to set
     */
    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @return the climate
     */
    public String getClimate() {
        return climate;
    }

    /**
     * @param climate the climate to set
     */
    public void setClimate(String climate) {
        this.climate = climate;
    }

    /**
     * @return the dangerous
     */
    public String getDangerous() {
        return dangerous;
    }

    /**
     * @param dangerous the dangerous to set
     */
    public void setDangerous(String dangerous) {
        this.dangerous = dangerous;
    }

    /**
     * @return the phMin
     */
    public String getPhMin() {
        return phMin;
    }

    /**
     * @param phMin the phMin to set
     */
    public void setPhMin(String phMin) {
        this.phMin = phMin;
    }

    /**
     * @return the phMax
     */
    public String getPhMax() {
        return phMax;
    }

    /**
     * @param phMax the phMax to set
     */
    public void setPhMax(String phMax) {
        this.phMax = phMax;
    }

    /**
     * @return the dhMin
     */
    public String getDhMin() {
        return dhMin;
    }

    /**
     * @param dhMin the dhMin to set
     */
    public void setDhMin(String dhMin) {
        this.dhMin = dhMin;
    }

    /**
     * @return the dhMax
     */
    public String getDhMax() {
        return dhMax;
    }

    /**
     * @param dhMax the dhMax to set
     */
    public void setDhMax(String dhMax) {
        this.dhMax = dhMax;
    }

    /**
     * @return the tempMin
     */
    public String getTempMin() {
        return tempMin;
    }

    /**
     * @param tempMin the tempMin to set
     */
    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    /**
     * @return the tempMax
     */
    public String getTempMax() {
        return tempMax;
    }

    /**
     * @param tempMax the tempMax to set
     */
    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public boolean hasImage() {
        return imagePresent;
    }

    public BufferedImage getImage(){
        return specImage;
    }

    public void setImage (BufferedImage img) {
        this.specImage=img;
        this.imagePresent=true;
    }
    
    /**
     * @return the swim level
     */
    public String getSwimLevel() {
        return swimLevel;
    }

    /**
     * @param swim_Level the swimLevel to set
     */
    public void setSwimLevel(String swim_Level) {
        this.swimLevel = swim_Level;
    }
    
    /**
     * @return the lifeSpam
     */
    public String getLifeSpam() {
        return lifeSpam;
    }

    /**
     * @param life_Spam the lifeSpam to set
     */
    public void setLifeSpam(String life_Spam) {
        this.lifeSpam = life_Spam;
    }
    
    /**
     * Sets table columns widths' array
     * 
     * @param cw array with the widths
     */
    public static void setColWidth (int [] cw){
        if (cw.length == colWidth.length) {
            System.arraycopy(cw, 0, colWidth, 0, colWidth.length);
        }
    }
    
    /**
     * @return the filter
     */
    public static String getFilter(){
        return filter;
    }

    /**
     *
     * @param filter the filter to set
     */
    public static void setFilter(String filter){
        FishBase.filter=filter;
    }
    // </editor-fold>

    /**
     * get FishBase from Db by id
     *
     * @param recId
     * @return FishBase
     */
    public static FishBase getById (int recId){
        FishBase specData = new FishBase();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                specData.id = recId;
                specData.commonName=rs.getString("CommonName"); // NOI18N
                specData.type = rs.getString("Class"); // NOI18N
                specData.name = rs.getString("Name"); // NOI18N
                specData.distribution = rs.getString("Distribution"); // NOI18N
                specData.diagnosis = rs.getString("Diagnosis"); // NOI18N
                specData.biology = rs.getString("Biology"); // NOI18N
                specData.maxSize = rs.getString("Maxsize"); // NOI18N
                specData.environment = rs.getString("Environment"); // NOI18N
                specData.climate = rs.getString("Climate"); // NOI18N
                specData.dangerous = rs.getString("Dangerous"); // NOI18N
                specData.phMin = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                specData.phMax = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                specData.dhMin = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                specData.dhMax = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                specData.tempMin = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                specData.tempMax = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                specData.swimLevel=rs.getString("swimLevel");// NOI18N
                specData.lifeSpam=LocUtil.localizeDouble(rs.getString("lifeSpan"));// NOI18N
                specData.Aka = rs.getString("Aka"); // NOI18N

            }
            DB.closeConn();

        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        //get image if present
        String sId=Integer.toString(specData.id);
        try {
            specData.imagePresent = DB.DbTestImagePresence(TABLE, sId); // NOI18N
            if (specData.imagePresent == true) {
                specData.specImage = DB.DBLoadImage(TABLE, sId);// NOI18N
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return specData;
    }
    
    /**
     * get FishBase from Db by id
     *
     * @param name
     * @return FishBase
     */
    public static FishBase getByName (String name){
        FishBase specData = new FishBase();
        String qry = "SELECT * FROM " + TABLE + " WHERE Name='" + name + "' ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                specData.id = rs.getInt("id");
                specData.commonName=rs.getString("CommonName"); // NOI18N
                specData.type = rs.getString("Class"); // NOI18N
                specData.name = rs.getString("Name"); // NOI18N
                specData.distribution = rs.getString("Distribution"); // NOI18N
                specData.diagnosis = rs.getString("Diagnosis"); // NOI18N
                specData.biology = rs.getString("Biology"); // NOI18N
                specData.maxSize = rs.getString("Maxsize"); // NOI18N
                specData.environment = rs.getString("Environment"); // NOI18N
                specData.climate = rs.getString("Climate"); // NOI18N
                specData.dangerous = rs.getString("Dangerous"); // NOI18N
                specData.phMin = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                specData.phMax = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                specData.dhMin = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                specData.dhMax = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                specData.tempMin = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                specData.tempMax = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                specData.swimLevel=rs.getString("swimLevel");// NOI18N
                specData.lifeSpam=LocUtil.localizeDouble(rs.getString("lifeSpan"));// NOI18N
                specData.Aka = rs.getString("Aka"); // NOI18N

            }
            DB.closeConn();

        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        //get image if present
        String sId=Integer.toString(specData.id);
        try {
            specData.imagePresent = DB.DbTestImagePresence(TABLE, sId); // NOI18N
            if (specData.imagePresent == true) {
                specData.specImage = DB.DBLoadImage(TABLE, sId);// NOI18N
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return specData;
    }

    /**
     * Save FishBase to db
     *
     * @param specData FishBase to save
     */
    public static void save(FishBase specData){
        int currID=specData.id;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,"// NOI18N
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,specData.commonName);
            prep.setString(3,specData.type);
            prep.setString(4,specData.name);
            prep.setString(5,specData.distribution);
            prep.setString(6,specData.diagnosis);
            prep.setString(7,specData.biology);
            prep.setString(8,specData.maxSize);
            prep.setString(9,specData.environment);
            prep.setString(10,specData.climate);
            prep.setString(11,specData.dangerous);
            prep.setString(12,specData.phMin);
            prep.setString(13,specData.phMax);
            prep.setString(14,specData.dhMin);
            prep.setString(15,specData.dhMax);
            prep.setString(16,specData.tempMin);
            prep.setString(17,specData.tempMax);  
            prep.setString(18,specData.swimLevel);
            prep.setString(19,specData.lifeSpam);
            prep.setString(20,specData.Aka);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        //end changes
        //DB.execQuery(qry);
        DB.closeConn();

        //try to save or update image
        try {
            if (specData.imagePresent) {
                if (specData.id == 0) {
                    specData.id = DB.DBLastId(TABLE); // NOI18N
                }
                DB.DBSaveImage(specData.specImage, TABLE, Integer.toString(specData.id)); // NOI18N
            }  else {
                if (currID != 0 ){
                    DB.DBDeleteImage(TABLE, Integer.toString(specData.id));// NOI18N
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delete a record from table by id
     *
     * @param recId
     * @param Name
     */
    public static void deleteById (String recId, String Name){
        int records=0;
        try {//check for related records
            records=DB.DBCountRelated(Name, "Fishes", "Name");//NOI18N            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (records == 0){
            if (DB.DbDelRow(TABLE, recId)){//if record is deleted then delete related image
                DB.DBDeleteImage(TABLE, recId);// NOI18N
            }            
        } else {
            String msg=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CANT_DELETE")+ " " ;//NOI18N
            msg=msg+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CANT_DELETE_ARE") + " " + records+ " " ;//NOI18N
            msg=msg+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CANT_DELETE_RELATED")+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISH");//NOI18N
            Util.showErrorMsg(msg);
        }     
    }
    
    /**
      * populate a table with datafrom DB
      *
      * @param displayData the table to populate
      */
    public static void populateTable (JTable displayData){
        try {
            if (displayData.getColumnCount()>1){ //save col width
                FishBase.setColWidth(Util.getColSizes(displayData));
            }
            DefaultTableModel dm = new DefaultTableModel();
            displayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE ; // NOI18N
            if (!filter.isEmpty()){
                qry=qry + " WHERE 1=1 "+ filter;
            }
            qry = qry +";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE; // NOI18N
            if (!filter.isEmpty()){
                qry=qry + " WHERE 1=1 "+ filter;
            }
            qry = qry + " ORDER BY id DESC;"; // NOI18N
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0] = rs.getString("id");// NOI18N
                tableData[x][1] = rs.getString("CommonName"); // NOI18N
                tableData[x][2] = rs.getString("Class"); // NOI18N
                tableData[x][3] = rs.getString("Name"); // NOI18N
                tableData[x][4] = rs.getString("Distribution"); // NOI18N
                tableData[x][5] = rs.getString("Diagnosis"); // NOI18N
                tableData[x][6] = rs.getString("Biology"); // NOI18N
                tableData[x][7] = rs.getString("Maxsize"); // NOI18N
                tableData[x][8] = rs.getString("Environment"); // NOI18N
                tableData[x][9] = rs.getString("Climate"); // NOI18N
                tableData[x][10] = rs.getString("Dangerous"); // NOI18N
                tableData[x][11] = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                tableData[x][12] = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                tableData[x][13] = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                tableData[x][14] = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                tableData[x][15] = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                tableData[x][16] = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                tableData[x][17] = rs.getString("swimLevel"); // NOI18N
                tableData[x][18] = LocUtil.localizeDouble(rs.getString("lifeSpan")); // NOI18N
                tableData[x][19] = rs.getString("Aka"); // NOI18N
                x++;
            }
            DB.closeConn();
            //tb1=LocUtil.localizeTableFieldDate(tb1, 1);
            dm.setDataVector(tableData,CAPTIONS);
            displayData.setModel(dm);
            // Set the first visible column to 40 pixels wide
            TableColumn col = displayData.getColumnModel().getColumn(0);
            int width = 40;
            col.setPreferredWidth(width);
            col = displayData.getColumnModel().getColumn(1);
            width = 150;
            col.setPreferredWidth(width);
            Util.setColSizes(displayData, colWidth);
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(displayData.getModel());
            displayData.setRowSorter(sorter);
            sorter.setComparator(0, new Comparator<String>() { 
                @Override
                public int compare(String s1, String s2) {
                    Integer val1 = Integer.parseInt(s1);
                    Integer val2 = Integer.parseInt(s2);
                    return val1.compareTo(val2);
                }
            });
            JTableHeader header = displayData.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * populate a list from db
     *
     * @param DisplayCombo
     */
    public static void populateCombo (JComboBox DisplayCombo){
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm); 
        int totElements=0;
        DB.openConn();
        String qry= "SELECT DISTINCT Name FROM " + TABLE + //NOI18N
                "  ORDER BY Name;";//NOI18N
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    dcm.addElement(rs.getString("Name"));//NOI18N
                    totElements ++;
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            if (totElements == 0) {
                dcm.addElement(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("fishtable_combobox_default"));
            } else {
                dcm.insertElementAt("---", 0);//NOI18N
                dcm.setSelectedItem(dcm.getElementAt(0));
            }

        DB.closeConn();
    }
    
    static final Logger _log = Logger.getLogger(FishBase.class.getName());

}

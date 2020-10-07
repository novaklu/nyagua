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
import java.util.List;
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
public class PlantBase {
    public static final String TABLE = "PlantsBase";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FAMILY"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DISTRIBUTION"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("HEIGHT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WIDTH"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("LIGHT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GROWTH"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEMANDS"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PHMIN"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PHMAX"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DHMIN"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DHMAX"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("T_MIN"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("T_MAX"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLACEMENT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQUATIC"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AKA"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2")            
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    private int id;
    private String name;
    private String family;
    private String distribution;
    private String height;
    private String width;
    private String light;
    private String growth;
    private String demands;
    private String phMin;
    private String phMax;
    private String dhMin;
    private String dhMax;
    private String tempMin;
    private String tempMax;
    private String placement;
    private boolean aquatic;
    private String note;
    private String Aka;
    private boolean co2required;
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
     * @return the family
     */
    public String getFamily() {
        return family;
    }

    /**
     * @param family the family to set
     */
    public void setFamily(String family) {
        this.family = family;
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
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the light
     */
    public String getLight() {
        return light;
    }

    /**
     * @param light the light to set
     */
    public void setLight(String light) {
        this.light = light;
    }

    /**
     * @return the growth
     */
    public String getGrowth() {
        return growth;
    }

    /**
     * @param growth the growth to set
     */
    public void setGrowth(String growth) {
        this.growth = growth;
    }

    /**
     * @return the demands
     */
    public String getDemands() {
        return demands;
    }

    /**
     * @param demands the demands to set
     */
    public void setDemands(String demands) {
        this.demands = demands;
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
    
    /**
     * @return the placement
     */
    public String getPlacement() {
        return placement;
    }

    /**
     * @param placement the placement to set
     */
    public void setPlacement(String placement) {
        this.placement = placement;
    }
    
    /**
     * @return the if aquatic
     */
    public boolean isAquatic() {
        return aquatic;
    }

    /**
     * @param aquatic the if plant is true aquatic or not
     */
    public void setAquatic(boolean aquatic) {
        this.aquatic = aquatic;
    }
    
    /**
     * @return the if is CO2 Required
     */
    public boolean isCO2Required() {
        return co2required;
    }

    /**
     * @param CO2Required set true if required 
     */
    public void setCO2Required(boolean CO2Required) {
        this.co2required = CO2Required;
    }
    
    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
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
     * Gets table columns widths' array
     * 
     * @return array with the widths
     */
    /*public static int[] getcolWidth (JTable displayTable){
        return Util.getColSizes(displayTable);        
    }*/
    
    
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
        PlantBase.filter=filter;
    }
    // </editor-fold>
    
    /**
     * get PlantBase from Db by id
     *
     * @param recId
     * @return PlantBase
     */
    public static PlantBase getById (int recId){
        PlantBase specData = new PlantBase();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                specData.id = recId;
                specData.name= rs.getString("Name"); // NOI18N
                specData.family = rs.getString("Family"); // NOI18N
                specData.distribution = rs.getString("Distribution"); // NOI18N
                specData.height = rs.getString("Height"); // NOI18N
                specData.width = rs.getString("Width"); // NOI18N
                specData.light = rs.getString("Light"); // NOI18N
                specData.growth = rs.getString("Growth"); // NOI18N
                specData.demands = rs.getString("Demands"); // NOI18N
                specData.phMin = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                specData.phMax = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                specData.dhMin = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                specData.dhMax = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                specData.tempMin = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                specData.tempMax = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                specData.placement = LocUtil.localizeDouble(rs.getString("placement")); // NOI18N
                specData.aquatic = Boolean.parseBoolean(rs.getString("aquatic")); // NOI18N
                specData.note = LocUtil.localizeDouble(rs.getString("note")); // NOI18N
                specData.Aka = rs.getString("Aka"); // NOI18N
                specData.co2required = Boolean.parseBoolean(rs.getString("CO2")); // NOI18N
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
     * get PlantBase from Db by id
     *
     * @param name
     * @return PlantBase
     */
    public static PlantBase getByName (String name){
        PlantBase specData = new PlantBase();
        String qry = "SELECT * FROM " + TABLE + " WHERE Name='" + name + "' ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                specData.id = rs.getInt("id");;
                specData.name= rs.getString("Name"); // NOI18N
                specData.family = rs.getString("Family"); // NOI18N
                specData.distribution = rs.getString("Distribution"); // NOI18N
                specData.height = rs.getString("Height"); // NOI18N
                specData.width = rs.getString("Width"); // NOI18N
                specData.light = rs.getString("Light"); // NOI18N
                specData.growth = rs.getString("Growth"); // NOI18N
                specData.demands = rs.getString("Demands"); // NOI18N
                specData.phMin = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                specData.phMax = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                specData.dhMin = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                specData.dhMax = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                specData.tempMin = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                specData.tempMax = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                specData.placement = LocUtil.localizeDouble(rs.getString("placement")); // NOI18N
                specData.aquatic = Boolean.parseBoolean(rs.getString("aquatic")); // NOI18N
                specData.note = LocUtil.localizeDouble(rs.getString("note")); // NOI18N
                specData.Aka = rs.getString("Aka"); // NOI18N
                specData.co2required = Boolean.parseBoolean(rs.getString("CO2")); // NOI18N
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
     * Save PlantBase to db
     *
     * @param specData PlantBase to save
     */
    public static void save(PlantBase specData){
        int currID=specData.id;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?,?,?,"// NOI18N
                    + "?, ?, ?, ?, ?, ?,?,?,?,?,?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,specData.name);
            prep.setString(3,specData.family);
            prep.setString(4,specData.distribution);
            prep.setString(5,specData.height);
            prep.setString(6,specData.width);
            prep.setString(7,specData.light);
            prep.setString(8,specData.growth);
            prep.setString(9,specData.demands);
            prep.setString(10,specData.phMin);
            prep.setString(11,specData.phMax);
            prep.setString(12,specData.dhMin);
            prep.setString(13,specData.dhMax);
            prep.setString(14,specData.tempMin);
            prep.setString(15,specData.tempMax);  
            prep.setString(16,specData.placement);  
            prep.setString(17,String.valueOf(specData.aquatic));  
            prep.setString(18,specData.note);  
            prep.setString(19,specData.Aka);
            prep.setString(20,String.valueOf(specData.co2required));
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
     * @param PlantName
     */
    public static void deleteById (String recId, String PlantName){
        int records=0;
        try {//check for related records
            records=DB.DBCountRelated(PlantName, "Plants", "Name");//NOI18N            
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
            msg=msg+java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CANT_DELETE_RELATED")+ " " + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTS_");//NOI18N
            Util.showErrorMsg(msg);
        }        
    }

    /**
      * populate a table with datafrom DB
      *
      * @param diplayData the table to populate
      */
    public static void populateTable (JTable diplayData){  
        try {  
            if (diplayData.getColumnCount()>1){ //save col width
                PlantBase.setColWidth(Util.getColSizes(diplayData));               
            }
            DefaultTableModel dm = new DefaultTableModel();
            diplayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE; // NOI18N
            if (!filter.isEmpty()){
                qry=qry + " WHERE 1=1 "+ filter;
            }
            qry = qry  +";"; // NOI18N
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
                tableData[x][1] = rs.getString("Name"); // NOI18N
                tableData[x][2] = rs.getString("Family"); // NOI18N
                tableData[x][3] = rs.getString("Distribution"); // NOI18N
                tableData[x][4] = rs.getString("Height"); // NOI18N
                tableData[x][5] = rs.getString("Width"); // NOI18N
                tableData[x][6] = rs.getString("Light"); // NOI18N
                tableData[x][7] = rs.getString("Growth"); // NOI18N
                tableData[x][8] = rs.getString("Demands"); // NOI18N
                tableData[x][9] = LocUtil.localizeDouble(rs.getString("PHMin")); // NOI18N
                tableData[x][10] = LocUtil.localizeDouble(rs.getString("PHMax")); // NOI18N
                tableData[x][11] = LocUtil.localizeDouble(rs.getString("DHMin")); // NOI18N
                tableData[x][12] = LocUtil.localizeDouble(rs.getString("DHMax")); // NOI18N
                tableData[x][13] = LocUtil.localizeDouble(rs.getString("t_Min")); // NOI18N
                tableData[x][14] = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                tableData[x][14] = LocUtil.localizeDouble(rs.getString("t_Max")); // NOI18N
                tableData[x][15] = LocUtil.localizeDouble(rs.getString("placement")); // NOI18N
                tableData[x][16] = LocUtil.localizeDouble(rs.getString("aquatic")); // NOI18N
                tableData[x][17] = LocUtil.localizeDouble(rs.getString("note")); // NOI18N
                tableData[x][18] = rs.getString("Aka"); // NOI18N
                tableData[x][19] = rs.getString("CO2"); // NOI18N

                x++;
            }
            DB.closeConn();
            //tb1=LocUtil.localizeTableFieldDate(tb1, 1);
            dm.setDataVector(tableData,CAPTIONS);
            diplayData.setModel(dm);
            // Set the first visible column to 40 pixels wide
            TableColumn col = diplayData.getColumnModel().getColumn(0);
            int width = 40;
            col.setPreferredWidth(width);
            col = diplayData.getColumnModel().getColumn(1);
            width = 150;
            col.setPreferredWidth(width);
            Util.setColSizes(diplayData, colWidth);
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(diplayData.getModel());
            diplayData.setRowSorter(sorter);
            sorter.setComparator(0, new Comparator<String>() { 
                @Override
                public int compare(String s1, String s2) {
                    Integer val1 = Integer.parseInt(s1);
                    Integer val2 = Integer.parseInt(s2);
                    return val1.compareTo(val2);
                }
            });
            JTableHeader header = diplayData.getTableHeader();
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
                dcm.addElement(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("plantstable_combobox_default"));
            } else {
                dcm.insertElementAt("---", 0);//NOI18N
                dcm.setSelectedItem(dcm.getElementAt(0));
            }

        DB.closeConn();
    }
    
     public static void populateCombo (JComboBox DisplayCombo, String dbField, List <String> defaults){
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm); 
        int totElements=0;
        //add default values
        for (String item : defaults){
            dcm.addElement(item);
            totElements ++;
        }
        
        DB.openConn();
        String qry= "SELECT DISTINCT " + dbField + " FROM " + TABLE + //NOI18N
                "  ORDER BY " + dbField + ";";//NOI18N
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    String fieldValue = rs.getString(dbField);
                    if (fieldValue != null) {
                        if (!defaults.contains(fieldValue) && !fieldValue.isEmpty() && !fieldValue.matches("---")){ //add only id not already in default
                            dcm.addElement(fieldValue);//NOI18N
                            totElements ++;
                        }  
                    }                                          
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            if (totElements == 0) {
                dcm.addElement(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("plantstable_combobox_default"));
            } else {
                dcm.insertElementAt("---", 0);//NOI18N
                dcm.setSelectedItem(dcm.getElementAt(0));
            }

        DB.closeConn();
    }
     
     static final Logger _log = Logger.getLogger(PlantBase.class.getName());

}

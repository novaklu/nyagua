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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import nyagua.DB;
import nyagua.Global;
import nyagua.LocUtil;
import nyagua.Util;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class Reading {
    public static final String TABLE = "Measures";

    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TIME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO2"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO3"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GH"),
        LocUtil.getCustomCaption("KH"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH"),
        LocUtil.getCustomCaption("TEMP"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NH"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CO2"),
        LocUtil.getCustomCaption("COND"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CA"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MG"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CU"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PO4"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("O2"),
        LocUtil.getCustomCaption("DENS"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NH3"),        
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Iodine"),
        LocUtil.getCustomCaption("Salinity")
        //java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQID")
    };
    
    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String date;
    private String time;
    private String no2;
    private String no3;
    private String gh;
    private String kh;
    private String ph;
    private String temp;
    private String fe;
    private String nh;
    private String co2;
    private String cond;
    private String ca;
    private String mg;
    private String cu;
    private Date newDate;
    private String po4;
    private String o2;
    private String dens;
    private String nh3;
    private String iodine;
    private String salinity;
    
    private static final int[] colWidth = new int[CAPTIONS.length];
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

    /**
     * @return the date
     */
    /*public String getDate() {
        return date;
    }*/
    
    public Date getDate(){
        return newDate;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the no2
     */
    public String getNo2() {
        return no2;
    }

    /**
     * @param no2 the no2 to set
     */
    public void setNo2(String no2) {
        this.no2 = no2;
    }

    /**
     * @return the no3
     */
    public String getNo3() {
        return no3;
    }

    /**
     * @param no3 the no3 to set
     */
    public void setNo3(String no3) {
        this.no3 = no3;
    }

    /**
     * @return the gh
     */
    public String getGh() {
        return gh;
    }

    /**
     * @param gh the gh to set
     */
    public void setGh(String gh) {
        this.gh = gh;
    }

    /**
     * @return the kh
     */
    public String getKh() {
        return kh;
    }

    /**
     * @param kh the kh to set
     */
    public void setKh(String kh) {
        this.kh = kh;
    }

    /**
     * @return the ph
     */
    public String getPh() {
        return ph;
    }

    /**
     * @param ph the ph to set
     */
    public void setPh(String ph) {
        this.ph = ph;
    }

    /**
     * @return the temp
     */
    public String getTemp() {
        return temp;
    }

    /**
     * @param temp the temp to set
     */
    public void setTemp(String temp) {
        this.temp = temp;
    }

    /**
     * @return the fe
     */
    public String getFe() {
        return fe;
    }

    /**
     * @param fe the fe to set
     */
    public void setFe(String fe) {
        this.fe = fe;
    }

    /**
     * @return the nh
     */
    public String getNh() {
        return nh;
    }

    /**
     * @param nh the nh to set
     */
    public void setNh(String nh) {
        this.nh = nh;
    }
    
     /**
     * @return the nh3
     */
    public String getNh3() {
        return nh3;
    }

    /**
     * @param nh3 the nh3 to set
     */
    public void setNh3(String nh3) {
        this.nh3 = nh3;
    }

    /**
     * @return the co2
     */
    public String getCo2() {
        return co2;
    }

    /**
     * @param co2 the co2 to set
     */
    public void setCo2(String co2) {
        this.co2 = co2;
    }

    /**
     * @return the cond
     */
    public String getCond() {
        return cond;
    }

    /**
     * @param cond the cond to set
     */
    public void setCond(String cond) {
        this.cond = cond;
    }

    /**
     * @return the ca
     */
    public String getCa() {
        return ca;
    }

    /**
     * @param ca the ca to set
     */
    public void setCa(String ca) {
        this.ca = ca;
    }

    /**
     * @return the mg
     */
    public String getMg() {
        return mg;
    }

    /**
     * @param mg the mg to set
     */
    public void setMg(String mg) {
        this.mg = mg;
    }

    /**
     * @return the cu
     */
    public String getCu() {
        return cu;
    }

    /**
     * @param cu the cu to set
     */
    public void setCu(String cu) {
        this.cu = cu;
    }
    
    /**
     * @return the po4
     */
    public String getPo4() {
        return po4;
    }

    /**
     * @param po4 the po4 to set
     */
    public void setPo4(String po4) {
        this.po4 = po4;
    }
    
    /**
     * @return the o2
     */
    public String getO2() {
        return o2;
    }

    /**
     * @param o2 the o2 to set
     */
    public void setO2(String o2) {
        this.o2 = o2;
    }
    
    /**
     * @return the o2
     */
    public String getSalinity() {
        return salinity;
    }

    /**
     * @param salinity the salinity to set
     */
    public void setSalinity(String salinity) {
        this.salinity = salinity;
    }
    
    /**
     * @return the iodine
     */
    public String getIodine() {
        return iodine;
    }

    /**
     * @param iodine the iodine to set
     */
    public void setIodine(String iodine) {
        this.iodine = iodine;
    }
    
    /**
     * @return the density
     */
    public String getDensity() {
        return dens;
    }

    /**
     * @param density the density to set
     */
    public void setDensity(String density) {
        this.dens = density;
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
    // </editor-fold>

    
    /**
     * get Reading from Db by id
     *
     * @param recId
     * @return Reading
     */
    public static Reading getById (int recId){
        Reading measure = new Reading();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                measure.id = recId;
                measure.date = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                measure.newDate=LocUtil.localizeAsDate(rs.getString("Date")); // NOI18N                        
                measure.time = rs.getString("Time"); // NOI18N
                measure.no2 = LocUtil.localizeDouble(rs.getString("NO2")); // NOI18N
                measure.no3 = LocUtil.localizeDouble(rs.getString("NO3")); // NOI18N
                measure.kh = LocUtil.localizeDouble(rs.getString("KH")); // NOI18N
                measure.gh = LocUtil.localizeDouble(rs.getString("GH")); // NOI18N
                measure.cond = LocUtil.localizeDouble(rs.getString("Cond")); // NOI18N
                measure.fe = LocUtil.localizeDouble(rs.getString("FE")); // NOI18N
                measure.temp = LocUtil.localizeDouble(rs.getString("temp")); // NOI18N
                measure.co2 = LocUtil.localizeDouble(rs.getString("CO2")); // NOI18N
                measure.ph = LocUtil.localizeDouble(rs.getString("PH")); // NOI18N
                measure.ca = LocUtil.localizeDouble(rs.getString("CA")); // NOI18N
                measure.cu = LocUtil.localizeDouble(rs.getString("CU")); // NOI18N
                measure.mg = LocUtil.localizeDouble(rs.getString("MG")); // NOI18N
                measure.nh = LocUtil.localizeDouble(rs.getString("NH")); // NOI18N
                measure.po4 = LocUtil.localizeDouble(rs.getString("PO4")); // NOI18N
                measure.o2 = LocUtil.localizeDouble(rs.getString("O2")); // NOI18N
                measure.dens = LocUtil.localizeDouble(rs.getString("dens")); // NOI18N
                measure.nh3 = LocUtil.localizeDouble(rs.getString("NH3")); // NOI18N
                measure.iodine = LocUtil.localizeDouble(rs.getString("iodine")); // NOI18N
                measure.salinity = LocUtil.localizeDouble(rs.getString("salinity")); // NOI18N
            }
            DB.closeConn();           
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return measure; 
    }

    /**
     * Save reading to db
     * 
     * @param measure Reading to save
     */
    public void save(Reading measure){
        int currID=measure.id;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?,?,?,"// NOI18N
                    + "?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,measure.date);
            prep.setString(3,measure.time);
            prep.setString(4,measure.no2);
            prep.setString(5,measure.no3);
            prep.setString(6,measure.gh);
            prep.setString(7,measure.kh);
            prep.setString(8,measure.ph);
            prep.setString(9,measure.temp);
            prep.setString(10,measure.fe);
            prep.setString(11,measure.nh);
            prep.setString(12,measure.co2);
            prep.setString(13,measure.cond);
            prep.setString(14,measure.ca);
            prep.setString(15,measure.mg);
            prep.setString(16,measure.cu);
            prep.setInt(17,Global.AqID);
            prep.setString(18,measure.po4);
            prep.setString(19,measure.o2);
            prep.setString(20,measure.dens);
            prep.setString(21,measure.nh3);
            prep.setString(22,measure.iodine);
            prep.setString(23,measure.salinity);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        //end changes
       // DB.execQuery(qry);
        DB.closeConn();
    }
    

 /**
  * populate a table with datafrom DB
  * 
  * @param diplayData the table to populate
  */
    public static void populateTable (JTable diplayData){
        try {
            if (diplayData.getColumnCount()>1){ //save col width
                Reading.setColWidth(Util.getColSizes(diplayData));                
            }
            DefaultTableModel dm = new DefaultTableModel();
            diplayData.setAutoCreateRowSorter(true);            
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' " + Util.getPeriod()+ ";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' "; // NOI18N
            qry = qry + Util.getPeriod()+ " ORDER BY Date DESC,id DESC;"; // NOI18N
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0]=rs.getString("id");// NOI18N
                tableData[x][1] = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                tableData[x][2] = rs.getString("Time"); // NOI18N
                tableData[x][3] = LocUtil.localizeDouble(rs.getString("NO2")); // NOI18N
                tableData[x][4] = LocUtil.localizeDouble(rs.getString("NO3")); // NOI18N
                tableData[x][5] = LocUtil.localizeDouble(rs.getString("GH")); // NOI18N
                tableData[x][6] = LocUtil.localizeDouble(rs.getString("KH")); // NOI18N
                tableData[x][7] = LocUtil.localizeDouble(rs.getString("PH")); // NOI18N
                tableData[x][8] = LocUtil.localizeDouble(rs.getString("temp")); // NOI18N
                tableData[x][9] = LocUtil.localizeDouble(rs.getString("FE")); // NOI18N
                tableData[x][10] = LocUtil.localizeDouble(rs.getString("NH")); // NOI18N
                tableData[x][11] = LocUtil.localizeDouble(rs.getString("CO2")); // NOI18N
                tableData[x][12] = LocUtil.localizeDouble(rs.getString("Cond")); // NOI18N
                tableData[x][13] = LocUtil.localizeDouble(rs.getString("CA")); // NOI18N
                tableData[x][14] = LocUtil.localizeDouble(rs.getString("MG")); // NOI18N
                tableData[x][15] = LocUtil.localizeDouble(rs.getString("CU")); // NOI18N
                tableData[x][16] = LocUtil.localizeDouble(rs.getString("PO4")); // NOI18N
                tableData[x][17] = LocUtil.localizeDouble(rs.getString("O2")); // NOI18N
                tableData[x][18] = LocUtil.localizeDouble(rs.getString("dens")); // NOI18N
                tableData[x][19] = LocUtil.localizeDouble(rs.getString("NH3")); // NOI18N
                tableData[x][20] = LocUtil.localizeDouble(rs.getString("iodine")); // NOI18N
                tableData[x][21] = LocUtil.localizeDouble(rs.getString("salinity")); // NOI18N
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
           sorter.setComparator(1, new Comparator<String>() { 
                @Override
                public int compare(String s1, String s2) {
                    try {
                        Date val1 = LocUtil.localizeAsDate(LocUtil.delocalizeDate(s1));
                        Date val2 = LocUtil.localizeAsDate(LocUtil.delocalizeDate(s2));
                        Long t1= val1.getTime();
                        Long t2= val2.getTime();
                        return t1.compareTo(t2);
                    }
                    catch (Exception e) {
                       _log.log(Level.SEVERE, null, e); 
                    }
                    return s1.compareTo(s2);
                }
            });
            JTableHeader header = diplayData.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
              
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delete a record from table by id
     *
     * @param recId
     */
    public static void deleteById (String recId){
        DB.DbDelRow(TABLE, recId);// NOI18N
    }
    
    static final Logger _log = Logger.getLogger(Reading.class.getName());

}

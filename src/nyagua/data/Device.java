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
public class Device {
    public static final String TABLE = "Devices";

    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEVICE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BRAND"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("W"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ONPERIOD"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("QTY")
        //java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQID")
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String name;
    private String brand;
    private String wattage;
    private String notes;
    private String onPeriod;
    private String qty;
    
    private static final int[] colWidth = new int[CAPTIONS.length];
    
    private static String filter="";
    //private int aquariumId;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWattage() {
        return wattage;
    }

    public void setWattage(String wattage) {
        this.wattage = wattage;
    }

    public String getOnPeriod() {
        return onPeriod;
    }

    public void setOnPeriod(String onPeriod) {
        this.onPeriod = onPeriod;
    }
    
    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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
        Device.filter=filter;
    }
    // </editor-fold>
    
    /**
     * get Device from Db by id
     *
     * @param recId
     * @return Device
     */
    public static Device getById (int recId){
        Device dev = new Device();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                dev.id = recId;
                dev.name = rs.getString("Device"); // NOI18N
                dev.brand = rs.getString("Brand"); // NOI18N
                dev.wattage = LocUtil.localizeDouble(rs.getString("W")); // NOI18N
                dev.notes = rs.getString("Notes"); // NOI18N
                dev.onPeriod = LocUtil.localizeDouble(rs.getString("OnPeriod")); // NOI18N
                dev.qty = LocUtil.localizeDouble(rs.getString("Qty")); // NOI18N
                
            }
            DB.closeConn();           
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return dev; 
    }

    /**
     * Save Device to db
     * 
     * @param dev Device to save
     */
    public void save(Device dev){
        int currID=dev.id;       
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,dev.name);
            prep.setString(3,dev.brand);
            prep.setString(4,dev.wattage);
            prep.setString(5,dev.notes);
            prep.setString(6,dev.onPeriod);
            prep.setInt(7,Global.AqID);
            prep.setString(8,dev.qty);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        //end changes
        //DB.execQuery(qry);
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
                Device.setColWidth(Util.getColSizes(diplayData));
            }
            DefaultTableModel dm = new DefaultTableModel();
            diplayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE + "  WHERE AqID ='" 
                    + Global.AqID + "' " + filter + ";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' "; // NOI18N
            qry = qry + " " + filter  + " ORDER BY id DESC;"; // NOI18N
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0] = rs.getString("id");// NOI18N
                tableData[x][1] = rs.getString("Device"); // NOI18N
                tableData[x][2] = rs.getString("Brand"); // NOI18N
                tableData[x][3] = LocUtil.localizeDouble(rs.getString("W")); // NOI18N
                tableData[x][4] = rs.getString("Notes"); // NOI18N
                tableData[x][5] = LocUtil.localizeDouble(rs.getString("OnPeriod")); // NOI18N
                tableData[x][6] = LocUtil.localizeDouble(rs.getString("Qty")); // NOI18N
                           
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
            width = 140;
            col.setPreferredWidth(width);
            col = diplayData.getColumnModel().getColumn(2);
            width = 140;
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
     * Delete a record from table by id
     *
     * @param recId
     */
    public static void deleteById (String recId){
        DB.DbDelRow(TABLE, recId);// NOI18N
    }
    
    static final Logger _log = Logger.getLogger(Device.class.getName());

}

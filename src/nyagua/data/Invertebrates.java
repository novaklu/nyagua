/*
 * Nyagua - Aquarium Manager
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
public class Invertebrates {
    public static final String TABLE = "Inverts";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("MALES_QTY"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FEMALES_QTY"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES"),
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String name;
    private String date;
    private String males;
    private String females;
    private String notes;
    private Date newDate;
    
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
     * @return the males
     */
    public String getMales() {
        return males;
    }

    /**
     * @param males the males to set
     */
    public void setMales(String males) {
        this.males = males;
    }

    /**
     * @return the females
     */
    public String getFemales() {
        return females;
    }

    /**
     * @param females the females to set
     */
    public void setFemales(String females) {
        this.females = females;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
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
        Invertebrates.filter=filter;
    }
    // </editor-fold>

    /**
     * get Fish from Db by id
     *
     * @param recId
     * @return Inverts
     */
    public static Invertebrates getById (int recId){
        Invertebrates specimen = new Invertebrates();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                specimen.id = recId;
                specimen.date = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                specimen.newDate = LocUtil.localizeAsDate(rs.getString("Date")); // NOI18N
                specimen.name = rs.getString("Name"); // NOI18N
                specimen.males = LocUtil.localizeDouble(rs.getString("Males_qty")); // NOI18N
                specimen.females = LocUtil.localizeDouble(rs.getString("Females_Qty")); // NOI18N
                specimen.notes = rs.getString("Notes"); // NOI18N
            }
            DB.closeConn();           
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return specimen; 
    }


    /**
     * Save Fish to db
     *
     * @param specimen Inverts to save
     */
    public void save(Invertebrates specimen){
        int currID=specimen.id;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,specimen.date);
            prep.setString(3,specimen.name);
            prep.setString(4,specimen.males);
            prep.setString(5,specimen.females);
            prep.setString(6,specimen.notes);
            prep.setInt(7,Global.AqID);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        //end changes
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
                Invertebrates.setColWidth(Util.getColSizes(diplayData));
            }
            DefaultTableModel dm = new DefaultTableModel();
            diplayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE + "  WHERE AqID ='" 
                    + Global.AqID + "' " + Util.getPeriod()+ " " + filter +  ";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' "; // NOI18N
            qry = qry + Util.getPeriod()+ " " + filter  +  " ORDER BY Date DESC, id DESC;"; // NOI18N
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0] = rs.getString("id");// NOI18N
                tableData[x][1] = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                tableData[x][2] = rs.getString("Name"); // NOI18N
                tableData[x][3] = LocUtil.localizeDouble(rs.getString("Males_qty")); // NOI18N
                tableData[x][4] = rs.getString("Females_Qty"); // NOI18N
                tableData[x][5] = LocUtil.localizeDouble(rs.getString("Notes")); // NOI18N
                
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
            col = diplayData.getColumnModel().getColumn(2);
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
    
    static final Logger _log = Logger.getLogger(Invertebrates.class.getName());
            
}

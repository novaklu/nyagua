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
public class History {
    public static final String TABLE = "History";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TIME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("EVENT"),
        //java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQID")
    };
    
    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String date;
    private String time;
    private String event;
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

    /**
     * @return the date
     */
      
    public Date getDate(){
        return newDate;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String  date) {
        this.date = date;
    }

    /**
     * @return the time
     */
    public String  getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String  time) {
        this.time = time;
    }

    /**
     * @return the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
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
    public static  void setFilter(String filter){
        History.filter=filter;
    }
    
    // </editor-fold>

    /**
     * 
     * get History event from Db by id
     * 
     * @param recId
     * @return Maintenance event
     */
    public static History getById (int recId){
        History event = new History();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                event.id = recId;
                event.date = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                event.newDate = LocUtil.localizeAsDate(rs.getString("Date")); // NOI18N
                event.time = rs.getString("Time"); // NOI18N
                event.event = rs.getString("Event"); // NOI18N
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return event;
    }



    /**
     * Save history to db
     *
     * @param event Reading to save
     * @param AqID Aquarium ID
     */
    public void save(History event, int AqID){
        int currID=event.id;
        DB.openConn();
        try {
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,event.date);
            prep.setString(3,event.time);
            prep.setString(4,event.event);
            if (AqID==0){
                prep.setInt(5,Global.AqID);
            }else{
                prep.setInt(5,AqID);
            }
            
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        } 
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
                History.setColWidth(Util.getColSizes(diplayData));
            }
            DefaultTableModel dm = new DefaultTableModel(); 
            diplayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE + " WHERE AqID ='" 
                    + Global.AqID + "' " + Util.getPeriod()+ " " + filter + ";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE + " WHERE AqID ='" + Global.AqID + "' "; // NOI18N
            qry = qry + Util.getPeriod() + " " + filter + " ORDER BY Date DESC,id DESC;"; // NOI18N
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0] = rs.getString("id");// NOI18N
                tableData[x][1] = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                tableData[x][2] = rs.getString("Time"); // NOI18N
                tableData[x][3] = rs.getString("Event"); // NOI18N

                x++;
            }
            DB.closeConn();
            //tb1=LocUtil.localizeTableFieldDate(tb1, 1);
            dm.setDataVector(tableData, CAPTIONS);
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
    
    static final Logger _log = Logger.getLogger(History.class.getName());

}

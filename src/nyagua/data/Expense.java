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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
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
public class Expense {
    public static final int COMBO_TYPE=1;
    public static final int COMBO_SHOP=2;
    public static final String TABLE = "Expenses";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ITEM"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PRICE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("SHOP"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TYPE"),
        //java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQID")
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String date;
    private String item;
    private String price;
    private String notes;
    private String shop;
    private String type;
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
     * @return the item
     */
    public String getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(String price) {
        this.price = price;
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
     * @return the shop
     */
    public String getShop() {
        return shop;
    }

    /**
     * @param shop the shop to set
     */
    public void setShop(String shop) {
        this.shop = shop;
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
        Expense.filter=filter;
    }
    // </editor-fold>


    /**
     * gets the total expenses for an aquarium
     *
     * @return the total (as sum)
     */
    public static String getTotalExpense(){
        String total="";
        String qry = "SELECT total(Price) as totale FROM " + TABLE//NOI18N
                + " WHERE AqID=" + Global.AqID + "  " + Util.getPeriod()+ " " + filter + ";";//NOI18N 
        
        try {
            DB.openConn();
            ResultSet rs = DB.getQuery(qry);        
            total=rs.getString("totale");
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        if (!total.isEmpty()) {
            total = String.format("%.2f", Double.valueOf(total));
        }
        return total;
    }

    /**
     * get Expense from Db by id
     *
     * @param recId
     * @return Expense
     */
    public static Expense getById (int recId){
        Expense exp = new Expense();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                exp.id = recId;
                exp.date = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                exp.newDate = LocUtil.localizeAsDate(rs.getString("Date")); // NOI18N
                exp.item = rs.getString("Item"); // NOI18N
                exp.price = LocUtil.localizeCurrency(rs.getString("Price")); // NOI18N
                exp.notes = rs.getString("Notes"); // NOI18N
                exp.shop = rs.getString("Shop"); // NOI18N
                exp.type = rs.getString("Type"); // NOI18N
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return exp;
    }
    
    /**
     * Save Expense to db
     *
     * @param exp Expense to save
     */
    public void save(Expense exp){
        int currID=exp.id;
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
            prep.setString(2,exp.date);
            prep.setString(3,exp.item);
            prep.setString(4,exp.price);
            prep.setString(5,exp.notes);
            prep.setString(6,exp.shop);
            prep.setInt(7,Global.AqID);
            prep.setString(8,exp.type);
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
                Expense.setColWidth(Util.getColSizes(diplayData));
            }            
            DefaultTableModel dm = new DefaultTableModel();
            diplayData.setAutoCreateRowSorter(true);
            DB.openConn();
            String qry = "SELECT COUNT(*) AS cont FROM " + TABLE + "  WHERE AqID ='" 
                    + Global.AqID + "' " + Util.getPeriod()+ " " + filter + ";"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            int elements = rs.getInt("cont"); // NOI18N
            int columns = CAPTIONS.length;
            String[][] tableData = new String[elements][columns];
            qry = "SELECT * FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' "; // NOI18N
            qry = qry + Util.getPeriod() + " " + filter + " ORDER BY Date DESC,id DESC;"; // NOI18N            
            rs = DB.getQuery(qry);
            int x = 0;
            while (rs.next()) {
                tableData[x][0] = rs.getString("id");// NOI18N
                tableData[x][1] = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                tableData[x][2] = rs.getString("Item"); // NOI18N
                tableData[x][3] = LocUtil.localizeCurrency(rs.getString("Price")); // NOI18N   
                tableData[x][4] = rs.getString("Notes"); // NOI18N
                tableData[x][5] = rs.getString("Shop"); // NOI18N                
                tableData[x][6] = rs.getString("Type"); // NOI18N             
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
            
            //align prices right
            col = diplayData.getColumnModel().getColumn(3);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(JLabel.RIGHT);
            col.setCellRenderer(renderer);
 
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
     * Populates type or shop combo
     * 
     * @param DisplayCombo the combo to populate
     * @param whichCombo
     */
    public static void populateCombo (JComboBox DisplayCombo, int whichCombo, List <String> defaults){
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm); 
        String qry;
        if (whichCombo == COMBO_TYPE){
            if (defaults != null) {
                //add default values
                for (String item : defaults){
                    dcm.addElement(item);
                } 
            }
                      
            
            qry= "SELECT DISTINCT Type AS element FROM " + TABLE + //NOI18N
            " WHERE AqID ='"+ Global.AqID + "' AND Type IS NOT NULL ORDER BY Type;";//NOI18N
        }else{
            qry= "SELECT DISTINCT Shop AS element FROM " + TABLE + //NOI18N
            " WHERE AqID ='"+ Global.AqID + "' AND Shop IS NOT NULL ORDER BY Shop;";//NOI18N        
        }
        
        DB.openConn();
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    String fieldValue = rs.getString("element");
                    if (whichCombo == COMBO_TYPE){
                        if (fieldValue != null) {
                            if (!defaults.contains(fieldValue) && !fieldValue.isEmpty() && !fieldValue.matches("---")){ //add only id not already in default
                                dcm.addElement(fieldValue);//NOI18N
                            }  
                        }   
                    }
                    else {
                        dcm.addElement(fieldValue);//NOI18N                    
                    }                   
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            dcm.insertElementAt("---", 0);//NOI18N
            dcm.setSelectedItem(dcm.getElementAt(0));
        DB.closeConn();
    }

    /**
     * Delete a record from table by id
     *
     * @param recId
     */
    public static void deleteById (String recId){
        DB.DbDelRow(TABLE, recId);// NOI18N
    }
    
    static final Logger _log = Logger.getLogger(Expense.class.getName());

}

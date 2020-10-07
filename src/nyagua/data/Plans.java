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
import java.util.ArrayList;
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
import nyagua.Global;
import nyagua.PlansPanel;
import nyagua.Util;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class Plans {
    public static final String TABLE = "Plans";

    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
//        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("METHOD"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Recipe"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FERT_INT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FERT_DELAY"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.dosevolume")
    };

    private static String getDoseFromRecipe(String recipeName) {
        if (recipeName.isEmpty()) {
            return "";
        }
        Recipe recipe = Recipe.getByRecipeName(recipeName);
        return recipe.getDoseVolume();
    }

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String name;
    private String method;
    private String recipe;
    private int fertInterval;
    private int fertDelay;
    private int aqId;
    
    private static final int[] COL_WIDTH = new int[CAPTIONS.length];
    
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getFertInterval() {
        return String.valueOf(fertInterval);
    }

    public void setFertInterval(String fertInterval) {        
        this.fertInterval = Integer.parseInt(fertInterval);
    }

    public String getFertDelay() {
        return String.valueOf(fertDelay);
    }

    public void setFertDelay(String fertDelay) {        
        this.fertDelay = Integer.parseInt(fertDelay);
    }

    public int getAqId() {
        return aqId;
    }

    public void setAqId(int aqId) {
        this.aqId = aqId;
    }
    
    public String getWaterChangeInterval() {
        //TODO
        return null;
    }

    
    /**
     * Sets table columns widths' array
     * 
     * @param cw array with the widths
     */
    public static void setColWidth (int [] cw){
        if (cw.length == COL_WIDTH.length) {
            System.arraycopy(cw, 0, COL_WIDTH, 0, COL_WIDTH.length);
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
        Plans.filter=filter;
    }
    // </editor-fold>
    
 
    /**
     * get Plan from Db by id
     *
     * @param recId
     * @return Plan
     */
    public static Plans getById (int recId){
        Plans plan = new Plans();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                plan.id = recId;
                plan.name = rs.getString("Name"); // NOI18N
                plan.method = rs.getString("Method"); // NOI18N
                plan.recipe = rs.getString("Recipe"); // NOI18N
                plan.fertInterval = rs.getInt("Fert_int"); // NOI18N  
                plan.fertDelay = rs.getInt("Fert_delay"); // NOI18N  
            }
            DB.closeConn();           
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return plan; 
    }

    /**
     * Save Plan to db
     * 
     * @param plan Plan to save
     */
    public void save(Plans plan){
        int currID=plan.id;       
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + // NOI18N
                    " VALUES (?, ?, ?, ?, ?, ?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,plan.name);
            prep.setString(3,plan.method);
            prep.setString(4,plan.recipe);
            prep.setInt(5,Global.AqID);
            prep.setInt(6,plan.fertInterval);
            prep.setInt(7,plan.fertDelay);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        //end changes
        //DB.execQuery(qry);
        DB.closeConn();
    }
    
    public static String getSelectedMethod() {
        if (Global.AqID <= 0) {
            return null;            
        }
        String result = null;
        try {
            DB.openConn();
            String qry = "SELECT DISTINCT Method FROM " + TABLE + // NOI18N
                    "  WHERE AqID ='" + Global.AqID + "' "; 
            qry = qry + " " + filter  + " ORDER BY id DESC;"; // NOI18N
            ResultSet rs = DB.getQuery(qry);
            while (rs.next()) {
                result = rs.getString("Method"); // NOI18N
            }
            
            DB.closeConn();
        }
        catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
            
        }
        
        return result;
    }
    
    /*
    * Populate recipes combo box from selected method
    *
    */
    public static void populateRecipes (
            JComboBox DisplayCombo, int method, Double waterVol) {
        
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm); 
        DB.openConn();
        String qry= "SELECT RecipeName,WaterVolume FROM Recipes ";  //NOI18N        
        if (method == 0) {
            qry = qry +  " ORDER BY RecipeName;";//NOI18N
        }
        else {
            qry = qry +  " WHERE Method = " + method;//NOI18N
            qry = qry +  " ORDER BY RecipeName;";//NOI18N
        }      
               
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    if (waterVol > 0) { //filter coherent recipes by water volume
                        String recipeWaterVol = rs.getString("WaterVolume");//NOI18N
                        if (recipeWaterVol !=null && !recipeWaterVol.isEmpty()) {
                            Double recWV = Double.parseDouble(recipeWaterVol);
                            if (Double.compare(waterVol, recWV) == 0) {
                                dcm.addElement(rs.getString("RecipeName"));//NOI18N
                            }
                        }
                    }
                    else {
                        dcm.addElement(rs.getString("RecipeName"));//NOI18N
                    }
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            dcm.insertElementAt("---", 0);//NOI18N
            dcm.setSelectedItem(dcm.getElementAt(0));

        DB.closeConn();
    }
    
    
    private static String getMethodById (String captionId) {
        if (captionId != null ) {
            int methodNumber = Integer.parseInt(captionId);
            if (methodNumber > 0) {
                String methodName = PlansPanel.methods[methodNumber];
                if (!methodName.isEmpty()) {

                    return methodName;
                }

            }
        }
        return captionId;
    }
     
    /**
      * populate a table with datafrom DB
      * 
      * @param displayData the table to populate
      */
    public static void populateTable (JTable displayData){
        try {    
            if (displayData.getColumnCount()>1){ //save col width
                Plans.setColWidth(Util.getColSizes(displayData));
            }
            DefaultTableModel dm = new DefaultTableModel();
            displayData.setAutoCreateRowSorter(true);
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
//                tableData[x][1] = rs.getString("Name"); // NOI18N
                tableData[x][1] = getMethodById(rs.getString("Method")); // NOI18N
                tableData[x][2] = rs.getString("Recipe"); // NOI18N
                tableData[x][3] = rs.getString("Fert_int"); // NOI18N    
                tableData[x][4] = rs.getString("Fert_delay"); // NOI18N
                x++;
            }
            DB.closeConn();
            
            for (int y = 0; y < x; y++ ) {                   
                tableData[y][5] = getDoseFromRecipe(tableData[y][2]);// NOI18N
            }
            //tb1=LocUtil.localizeTableFieldDate(tb1, 1);
            dm.setDataVector(tableData,CAPTIONS);
            displayData.setModel(dm);
            // Set the first visible column to 40 pixels wide
            TableColumn col = displayData.getColumnModel().getColumn(0);
            int width = 40;
            col.setPreferredWidth(width);
            col = displayData.getColumnModel().getColumn(1);
            width = 140;
            col.setPreferredWidth(width);
            col = displayData.getColumnModel().getColumn(2);
            width = 140;
            col.setPreferredWidth(width);
            Util.setColSizes(displayData, COL_WIDTH);
            
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
      * Get the list of current plans
      *
      * @return plans a list of Plans
      */
    public static List <Plans> getAllPlans (){
       
       List <Plans> plans = new ArrayList<>();
       
       String qry = "SELECT * FROM " + TABLE + "  WHERE AqID ='" + Global.AqID + "' "; // NOI18N
        qry = qry + " " + filter  + " ORDER BY id DESC;"; // NOI18N
        
       ResultSet rs;
       try {
            DB.openConn();  
            rs = DB.getQuery(qry);
            while (rs.next()) {
                Plans plan = new Plans();
                plan.id = rs.getInt("id");
                plan.name = rs.getString("Name"); // NOI18N
                plan.method = rs.getString("Method"); // NOI18N
                plan.recipe = rs.getString("Recipe"); // NOI18N
                plan.fertInterval = rs.getInt("Fert_int"); // NOI18N  
                plan.fertDelay = rs.getInt("Fert_delay"); // NOI18N  
                plans.add(plan);
               
            }
            DB.closeConn();
       } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
       
       return plans;    
    }
    
    /**
      * check if a Recipe is used in any plan
      *
      * @return plans a list of Plans
      */
    public static boolean isRecipeInUse (String recipeName){
        
        int count = 0;

        String qry = "SELECT count(recipe) as cont  FROM " + TABLE + "  WHERE recipe ='" + 
               recipeName + "' "; // NOI18N

        ResultSet rs;
        try {
            DB.openConn();  
            rs = DB.getQuery(qry);
            while (rs.next()) {
                count = rs.getInt("cont");               
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        
        if (count > 0) return true;

        return false;    
    }

    /**
     * Delete a record from table by id
     *
     * @param recId
     */
    public static void deleteById (String recId){
        DB.DbDelRow(TABLE, recId);// NOI18N
    }
    
    static final Logger _log = Logger.getLogger(Plans.class.getName());

}

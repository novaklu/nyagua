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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import nyagua.DB;
import nyagua.LocUtil;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class Recipe {
    public static final String TABLE = "Recipes";
    
    // <editor-fold defaultstate="collapsed" desc="fields">
    
    private String recipeName;
    private String method;
    private String waterVolume;
    private String waterUnits;
    private String product;
    private String form;
    private String solutionVolume;
    private String doseVolume;
    private String target;
    private String added;
    private String units;
    //AqID
        
     private static String filter="";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Access Methods">
    /**
     * @return the id
     */
//    public int getId(){
//        return id;
//    }

    /**
     *
     * @param id the id to set
     */
//    public void setId(int id){
//        this.id=id;
//    }

    /**
     * @return the recipe name
     */      
    public String getRecipeName(){
        return recipeName;
    }

    /**
     * @param recipeName the recipe name to set
     */
    public void setRecipeName(String  recipeName) {
        this.recipeName = recipeName;
    }
   
    /**
     * @return the choosen method 
     */      
    public String getMethod(){
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String  method) {
        this.method = method;
    }
    
    /**
     * @return the water Volume 
     */      
    public String getWaterVolume(){
        return waterVolume;
    }

    /**
     * @param waterVolume the water Volume amount
     */
    public void setWaterVolume(String  waterVolume) {
        this.waterVolume = waterVolume;
    }
    
    /**
     * @return the water Units 
     */      
    public String getWaterUnits(){
        return waterUnits;
    }

    /**
     * @param waterUnits  the water units [l || USGal]
     */
    public void setWaterUnits(String  waterUnits) {
        this.waterUnits = waterUnits;
    }
    
    /**
     * @return the product/compound 
     */      
    public String getProduct(){
        return product;
    }

    /**
     * @param product  the product/compound name
     */
    public void setProduct(String  product) {
        this.product = product;
    }
    
    /**
     * @return the product/compound form (solution || powder)
     */      
    public String getForm(){
        return form;
    }

    /**
     * @param form  the product/compound form (solution || powder)
     */
    public void setForm(String  form) {
        this.form = form;
    }
    
    /**
     * @return the solution volume
     */      
    public String getSolutionVolume(){
        return solutionVolume;
    }

    /**
     * @param solutionVolume  set the solution volume amount
     */
    public void setSolutionVolume(String  solutionVolume) {
        this.solutionVolume = solutionVolume;
    }
    
    /**
     * @return the dose volume
     */      
    public String getDoseVolume(){
        return doseVolume;
    }
   
    /**
     * @param doseVolume  set the dose volume amount
     */
    public void setDoseVolume(String  doseVolume) {
        this.doseVolume = doseVolume;
    }
    
    /**
     * @return the target value
     */      
    public String getTarget(){
        return target;
    }

    /**
     * @param target  set the target value
     */
    public void setTarget(String  target) {
        this.target = target;
    }
        
    /**
     * @return the added value
     */      
    public String getAdded(){
        return added;
    }

    /**
     * @param added  set the added value
     */
    public void setAdded(String  added) {
        this.added = added;
    }

    /**
     * @return units
     */      
    public String getUnits(){
        return units;
    }

    /**
     * @param units  set the units
     */
    public void setUnits(String  units) {
        this.units = units;
    }
        
//    /**
//     * Sets table columns widths' array
//     * 
//     * @param cw array with the widths
//     */
//    public static void setColWidth (int [] cw){
//        if (cw.length == colWidth.length) {
//            System.arraycopy(cw, 0, colWidth, 0, colWidth.length);
//        }        
//    }    
        
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
        Recipe.filter=filter;
    }
    
    // </editor-fold>

    /**
     * 
     * get Recipe  from Db by name
     * 
     * @param name
     * @return Recipe 
     */
    public static Recipe getByRecipeName (String name){
        Recipe rec = new Recipe();
        String qry = "SELECT * FROM " + TABLE + " WHERE RecipeName='" + name + "' ;"; // NOI18N
//                + " AND AqID=" + Global.AqID + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                rec.recipeName = name; // NOI18N
                rec.method = rs.getString("Method"); // NOI18N
                rec.waterVolume = LocUtil.localizeDouble(rs.getString("WaterVolume")); // NOI18N
                rec.waterUnits = rs.getString("WaterUnits"); // NOI18N
                rec.product = rs.getString("Product"); // NOI18N
                rec.form = rs.getString("Form"); // NOI18N
                rec.solutionVolume = LocUtil.localizeDouble(rs.getString("SolutionVolume")); // NOI18N
                rec.doseVolume = LocUtil.localizeDouble(rs.getString("DoseVolume")); // NOI18N
                rec.target = LocUtil.localizeDouble(rs.getString("Target")); // NOI18N
                rec.added = LocUtil.localizeDouble(rs.getString("Added")); // NOI18N
                rec.units = rs.getString("Units"); // NOI18N
//                rec. = rs.getString("AqID");// NOI18N
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return rec;
    }



    /**
     * Save recipe to db
     *
     * @param rec recipe to save
     */
    public void save(Recipe rec){
        String currID=rec.recipeName;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + 
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");// NOI18N
           
            prep.setString(1,currID);
            prep.setString(2,rec.method);
            prep.setString(3,rec.waterVolume);
            prep.setString(4,rec.waterUnits);
            prep.setString(5,rec.product);
            prep.setString(6,rec.form);
            prep.setString(7,rec.solutionVolume);
            prep.setString(8,rec.doseVolume);
            prep.setString(9,rec.target);
            prep.setString(10,rec.added);
            prep.setString(11,rec.units);
            prep.setString(12,"0");  //aqID            
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        DB.closeConn();
    }
    
    public static void populateCombo (JComboBox DisplayCombo, int method){
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm);
        DB.openConn();
        String qry= "SELECT DISTINCT RecipeName FROM " + TABLE + //NOI18N
            " WHERE RecipeName IS NOT NULL AND Method=" + method + 
                " ORDER BY RecipeName;";//NOI18N
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    dcm.addElement(rs.getString("RecipeName"));//NOI18N
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
     * @param name
     */
    public static void deleteById (String name){
        DB.DbDelRowByPK(TABLE, "RecipeName", name);// NOI18N
    }
    
    static final Logger _log = Logger.getLogger(Recipe.class.getName());

}

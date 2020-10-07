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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nyagua.DB;
import nyagua.Global;
import nyagua.LocUtil;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class Aquarium {
    public static final String TABLE = "Aquarium";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NAME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DEEP"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WIDTH"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("HEIGHT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WATER_VOL"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TYPE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("START_DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("END_DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("O_C"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("TANK_VOLUME"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BOTTOM"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("GLASS_THICKNESS"),   
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES"),   
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Water_change")    
    };

    // <editor-fold defaultstate="collapsed" desc="fields">
    //private String String;
    private int AqId;
    private String name;

    private String depth;
    private String width;
    private String height;
    private String waterVolume;
    private String type;
    //private String substrate;
    private String startDate;
    private String endDate;
    private Date newStartDate;
    private Date newEndDate;
    private boolean open;
    private boolean imagePresent;
    private BufferedImage aquariumImage;
    private String tankVolume;
    private String bottom;
    private String glassThick;
    private String notes;
    private String waterChange;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Access Methods">

    /**
     * @return the id
     */
    public int getId(){
        return AqId;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(int id){
        this.AqId=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVolume() {
        return waterVolume;
    }

    public void setVolume(String volume) {
        this.waterVolume = volume;
    }

    public String getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(String volume) {
        this.tankVolume = volume;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }
    
     public String getGlassTick() {
        return glassThick;
    }

    public void setGlassTick(String glassThick) {
        this.glassThick = glassThick;
    }

    public Date getStartDate(){
        return newStartDate;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    
    public Date getEndDate(){
        return newEndDate;
    }


    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getWaterChange() {
        return waterChange;
    }

    public void setWaterChange(String waterChange) {
        this.waterChange = waterChange;
    }
    
    public boolean hasImage() {
        return imagePresent;
    }

    public BufferedImage getImage(){
        return aquariumImage;
    }

    public void setImage (BufferedImage img) {
        this.aquariumImage=img;
        this.imagePresent=true;
    }


    // </editor-fold>

    /**
     * get Aquarium from Db by id
     *
     * @param recId
     * @return Aquarium
     */
    public static Aquarium getById (int recId){
        Aquarium tank = new Aquarium();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                tank.AqId = recId;
                tank.name = rs.getString("Name"); // NOI18N
                tank.depth = LocUtil.localizeDouble(rs.getString("Deep")); // NOI18N
                tank.width = LocUtil.localizeDouble(rs.getString("Width")); // NOI18N
                tank.height = LocUtil.localizeDouble(rs.getString("Height")); // NOI18N
                tank.waterVolume = LocUtil.localizeDouble(rs.getString("Water_vol")); // NOI18N
                tank.type = rs.getString("Type"); // NOI18N
                tank.startDate = LocUtil.localizeDate(rs.getString("Start_date")); // NOI18N
                tank.endDate = LocUtil.localizeDate(rs.getString("End_date")); // NOI18N
                tank.newStartDate = LocUtil.localizeAsDate(rs.getString("Start_date")); // NOI18N
                tank.newEndDate = LocUtil.localizeAsDate(rs.getString("End_date")); // NOI18N
                tank.open=Boolean.parseBoolean(rs.getString("o_c"));
                //tank.open = rs.getBoolean("o_c"); // NOI18N
                tank.tankVolume = LocUtil.localizeDouble(rs.getString("Tank_vol")); // NOI18N
                tank.bottom = rs.getString("Bottom"); // NOI18N
                tank.glassThick=LocUtil.localizeDouble(rs.getString("Glass_thick")); // NOI18N
                tank.notes = rs.getString("Notes"); // NOI18N
                tank.waterChange = LocUtil.localizeDouble(rs.getString("Water_change")); // NOI18N
               // "id","Name","Deep","Width","Height","Water_vol","Type","Start_date","End_date","o_c"

            }
            DB.closeConn();

        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        //get image if present
        String sAqId=Integer.toString(tank.AqId);
        try {
            tank.imagePresent = DB.DbTestImagePresence(TABLE, sAqId); // NOI18N
            if (tank.imagePresent == true) {
                try {
                    tank.aquariumImage = DB.DBLoadImage(TABLE, sAqId);// NOI18N
                } catch (        SQLException | IOException ex) {
                    _log.log(Level.SEVERE, null, ex);
                }
            }
        } catch (ClassNotFoundException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return tank;
    }

    /**
     * Save Aquarium to db
     *
     * @param tank - Aquarium to save
     */
    public static void save(Aquarium tank){
        int currID=tank.AqId;
        if (tank.name==null || tank.name.equalsIgnoreCase("")){
            tank.name="-" + tank.AqId+"-";//NOI18N
        }

        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,"// NOI18N
                    + "?, ?, ?, ?,?,?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }            
            prep.setString(2,tank.name);
            prep.setString(3,tank.depth);
            prep.setString(4,tank.width);
            prep.setString(5,tank.height);
            prep.setString(6,tank.waterVolume);
            prep.setString(7,tank.type);
            prep.setString(8,tank.startDate);
            prep.setString(9,tank.endDate);
            prep.setString(10,String.valueOf(tank.open));
            prep.setString(11,tank.tankVolume);
            prep.setString(12,tank.bottom);    
            prep.setString(13,tank.glassThick);   
            prep.setString(14,tank.notes);
            prep.setString(15,tank.waterChange);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }                
        DB.closeConn();
        
        //try to save or update image
        try {
            if (tank.imagePresent) {
                if (tank.AqId == 0) {
                    tank.AqId = DB.DBLastId("Aquarium") + 1; // NOI18N
                }
                Global.AqID=tank.AqId;
                DB.DBSaveImage(tank.aquariumImage, TABLE, Integer.toString(tank.AqId)); // NOI18N
            }  else {
                if (currID != 0 ){
                    DB.DBDeleteImage(TABLE, Integer.toString(tank.AqId));// NOI18N
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
    
     /**
     * get Aquariums id and name
     *
     * 
     * @return Aquarium array
     */
    public static Aquarium[] getAll (){
        Aquarium[] aquariums = null;
        
        String qry = "SELECT * FROM " + TABLE + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
             rs = DB.getQuery(qry);
            int count=0;
            while (rs.next()) {
                count++;
            }
            if (count==0) {
                return null;
            }
            rs = DB.getQuery(qry);
            aquariums=new Aquarium[count];
            count=0;
            while (rs.next()) {
                Aquarium tank = new Aquarium();
                tank.AqId = rs.getInt("id");
                tank.name = rs.getString("Name"); // NOI18N
                aquariums[count]=tank;
                count++;

            }
            DB.closeConn();

        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        
        return aquariums;
    }

    /**
     * Delete a record from table by id
     *
     * @param recId
     */
    public static void deleteById (String recId){
        DB.DbDelRow(TABLE, recId);// NOI18N
    }

    static final Logger _log = Logger.getLogger(Aquarium.class.getName());

}

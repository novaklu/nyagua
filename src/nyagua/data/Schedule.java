/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2014 Rudi Giacomini Pilon
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import nyagua.DB;
import nyagua.Global;
import nyagua.LocUtil;

/**
 *
 * @author Rudi Giacomini Pilon
 * 
 * Contains all informations about a scheduled event
 * 
 */
public class Schedule {
    public static final String TABLE = "Schedule";
    public static final String [] CAPTIONS = {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ID"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("EVENT"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NOTES"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Reminder"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DATE"),        
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("RecursionType"),        
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Recursion"),        
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("END_DATE"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Every"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("status"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Snoozed"),
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQID")
    };
    
    public static final String RECURSION_DAILY="D";
    public static final String RECURSION_WEEKLY="W";
    public static final String RECURSION_MONTHLY="M";
    public static final String RECURSION_YEARLY="Y";
    public static final String RECURSION_SINGLE="S";  
    
    public static final int STATUS_TODO=0;
    public static final int STATUS_CANCELED=1;
    public static final int STATUS_SNOOZED=2;
    public static final int STATUS_LATE=3;
    public static final int STATUS_CLOSED=4;
    
    private static boolean hideClosedTasksFilter =true;
    
    
    
    // <editor-fold defaultstate="collapsed" desc="fields">
    //private Date date;
    private int id;
    private String event;
    private String notes;    
    private boolean reminder;
    private String date;
    private String recursionType;
    private int recursion;
    private String endDate;
    private int every;
    private int status;
    private int snoozed;
    private Date newDate;
    private Date newEndDate;
    private int aqId;
    
    //private static int[] colWidth = new int[CAPTIONS.length];
    
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
     * @return the Reminder
     */
    public boolean getReminder() {
        return reminder;
    }

    /**
     * @param reminder the reminder to set
     */
    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }
    
    /**
     * @return the RecursionType
     */
    public String getRecursionType() {
        return recursionType;
    }

    /**
     * @param recursionType the recursionType to set 
     */
    public void setRecursionType(String recursionType) {
        this.recursionType = recursionType;
    }
    
     /**
     * @return the Recursion
     */
    public int getRecursion() {
        return recursion;
    }

    /**
     * @param recursion the recursion in number to set 
     */
    public void setRecursion(int recursion) {
        this.recursion = recursion;
    }
    
        
    /**
     * @return the end date
     */
      
    public Date getEndDate(){
        return newEndDate;
    }

    /**
     * @param date the end date to set
     */
    public void setEndDate(String  date) {
        this.endDate = date;
    }
    
    /**
     * @return the Recursion
     */
    public int getEvery() {
        return every;
    }

    /**
     * @param every the recursion in number to set 
     */
    public void setEvery(int every) {
        this.every = every;
    }
    
    /**
     * @return the Status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the Status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * @return the Snoozed
     */
    public int getSnoozed() {
        return snoozed;
    }

    /**
     * @param snoozed
     */
    public void setSnoozed(int snoozed) {
        this.snoozed = snoozed;
    }
    
    /**
     * @return the Aqid
     */
    public int getAqId(){
        return aqId;
    }

    /**
     *
     * @param aqId
     */
    public void setAqId(int aqId){
        this.aqId=aqId;
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
        Schedule.filter=filter;
    }
    
    /**
     * Set if to hide or to show closed tasks
     *
     * @param hideit    (true=hide closed tasks || false=show closed tasks)
     */
    public static void setHideClosedTasksFilter (boolean hideit){
        hideClosedTasksFilter=hideit;
    }
    
    // </editor-fold>

    /**
     * 
     * get Schedule event from Db by id
     * 
     * @param recId
     * @return Schedule event
     */
    public static Schedule getById (int recId){
        Schedule event = new Schedule();
        String qry = "SELECT * FROM " + TABLE + " WHERE id=" + recId // NOI18N
               //+ " AND AqID=" + Global.AqID 
                + " ;"; // NOI18N
        ResultSet rs;
        try {
            DB.openConn();
            rs = DB.getQuery(qry);
            while (rs.next()) {
                event.id = recId;
                event.event = rs.getString("Event"); // NOI18N
                event.notes = rs.getString("Notes"); // NOI18N
                event.reminder = rs.getBoolean("Reminder"); // NOI18N
                event.date = LocUtil.localizeDate(rs.getString("Date")); // NOI18N
                event.newDate = LocUtil.localizeAsDate(rs.getString("Date")); // NOI18N
                event.recursionType=rs.getString("RecursionType"); // NOI18N
                event.recursion=rs.getInt("Recursion"); // NOI18N
                event.endDate = LocUtil.localizeDate(rs.getString("EndDate")); // NOI18N
                event.newEndDate = LocUtil.localizeAsDate(rs.getString("EndDate")); // NOI18N
                event.every=rs.getInt("Every"); // NOI18N
                event.status=rs.getInt("Status"); // NOI18N
                event.snoozed=rs.getInt("Snoozed"); // NOI18N
                event.aqId=rs.getInt("AqID"); // NOI18N
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        return event;
    }



    /**
     * Save reading to db
     *
     * @param event Reading to save
     */
    public void save(Schedule event){
        int currID=event.id;
        DB.openConn();
        try {
            //Changes due to single quote problem
            PreparedStatement prep=DB.getConn().prepareStatement(""// NOI18N
                    + "INSERT OR REPLACE INTO " + TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?,?,?);");// NOI18N
            if (currID == 0) {
                prep.setString(1, null);
            } else {                //the record is in update
                prep.setString(1,  String.valueOf(currID));
            }    
            prep.setString(2,event.event);
            prep.setString(3,event.notes);
            prep.setBoolean(4,event.reminder);
            prep.setString(5,event.date);
            prep.setString(6,event.recursionType);
            prep.setInt(7,event.recursion);
            prep.setString(8,event.endDate);
            prep.setInt(9,event.every);
            prep.setInt(10,event.status);
            prep.setInt(11,event.snoozed);            
            prep.setInt(12,event.aqId);
            prep.executeUpdate();            
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }        
        DB.closeConn();
    }

    
    
    /**
     *      
     * @param diplayData
     * @param selDay 
     * @return  
     */
    public static int populateList (JList diplayData, Date selDay){
        ScheduledDate schedDate=new ScheduledDate(selDay);         
        ListDEntry[] lm2;
        //clean all lists
        DefaultListModel dlm;
        dlm = new DefaultListModel();
        diplayData.setModel(dlm);     
        String qry;
        int recCount=0;
        DB.openConn();
        ResultSet rs;        
        
        String selCount="Select count(*) as tot FROM " +TABLE ;//NOI18N
        String select="SELECT Event,id, Recursion FROM "+ TABLE ;//NOI18N
        String aqFilter= " WHERE (AqID ='"+ Global.AqID + "' OR AqID='0')";//NOI18N
        if (Global.AqID==0){
            aqFilter= " WHERE 1=1 ";//NOI18N
        }
        //filter for closed tasks 
        String tasksFilter="";
        if (hideClosedTasksFilter){
            tasksFilter=" AND Status <>'"+STATUS_CLOSED +"' "+
                    " AND Status <>'"+ STATUS_CANCELED+"' ";
        }
        
        //populate lists with dayly elements
        qry = selCount + aqFilter + tasksFilter//NOI18N
             + " AND RecursionType='"+ RECURSION_DAILY + "';"; // NOI18N
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                recCount=(rs.getInt("tot"));//NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (recCount >0){
            lm2=new ListDEntry[recCount];
            qry= select + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_DAILY + "';"; // NOI18N       
            try {
                rs = DB.getQuery(qry);
                int count=0;
                while (rs.next()) {
                    lm2[count]=new ListDEntry(rs.getInt("id"),rs.getString("Event"));//NOI18N
                    count++;
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            for (ListDEntry lm21 : lm2) {
                dlm.addElement(lm21);                
            }
        }
        
        //populate lists with selday elements  
        qry = selCount + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_SINGLE +   // NOI18N 
            "' AND Date ='"+ LocUtil.delocalizeDate(selDay) + "';"; // NOI18N
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                recCount=(rs.getInt("tot"));//NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (recCount >0){
            lm2=new ListDEntry[recCount];
            qry= select + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_SINGLE +   // NOI18N 
            "' AND Date ='"+ LocUtil.delocalizeDate(selDay) + "';"; // NOI18N        
            try {
                rs = DB.getQuery(qry);
                int count=0;
                while (rs.next()) {
                    lm2[count]=new ListDEntry(rs.getInt("id"),rs.getString("Event"));//NOI18N
                    count++;
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            for (ListDEntry lm21 : lm2) {
                dlm.addElement(lm21);
            }
        }
              
        
        //populate lists with day of year elements
        qry = selCount + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_YEARLY +   // NOI18N 
            "' AND Recursion ='"+ schedDate.getYearDay() + "';"; // NOI18N
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                recCount=(rs.getInt("tot"));//NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (recCount >0){            
            lm2=new ListDEntry[recCount];
            qry= select + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_YEARLY +   // NOI18N 
            "' AND Recursion ='"+ schedDate.getYearDay() + "';"; // NOI18N        
            try {
                rs = DB.getQuery(qry);
                int count=0;
                while (rs.next()) {
                    lm2[count]=new ListDEntry(rs.getInt("id"),rs.getString("Event"));//NOI18N
                    count++;
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            for (ListDEntry lm21 : lm2) {
                dlm.addElement(lm21);
            }
        }
        
        //populate lists with day of month elements
        qry = selCount + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_MONTHLY+"' ";   // NOI18N         
        //Take care of last day in month
        if (schedDate.getMonthDay()==schedDate.getLastMonthDay()){
            qry = qry + " AND Recursion >='"+ schedDate.getMonthDay() + "';"; // NOI18N
        }else{
            qry = qry + " AND Recursion ='"+ schedDate.getMonthDay() + "';"; // NOI18N
        }       
        
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                recCount=(rs.getInt("tot"));//NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (recCount >0){            
            lm2=new ListDEntry[recCount];
            qry= select + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_MONTHLY +"' ";   // NOI18N         
            //Take care of last day in month
            if (schedDate.getMonthDay()==schedDate.getLastMonthDay()){
                qry = qry + " AND Recursion >='"+ schedDate.getMonthDay() + "';"; // NOI18N
            }else{
                qry = qry + " AND Recursion ='"+ schedDate.getMonthDay() + "';"; // NOI18N
            }          
            try {
                rs = DB.getQuery(qry);
                int count=0;
                while (rs.next()) {
                    lm2[count]=new ListDEntry(rs.getInt("id"),rs.getString("Event"));//NOI18N
                    count++;
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            for (ListDEntry lm21 : lm2) {
                dlm.addElement(lm21);
            }
        }
        
            //populate lists with weekly elements
        qry = selCount + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_WEEKLY + "';"; // NOI18N
        try {
            rs = DB.getQuery(qry);
            while (rs.next()) {
                recCount=(rs.getInt("tot"));//NOI18N
            }
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        if (recCount >0){
            lm2=new ListDEntry[recCount];
            qry= select + aqFilter + tasksFilter//NOI18N
              + " AND RecursionType='"+ RECURSION_WEEKLY + "';"; // NOI18N       
            try {
                rs = DB.getQuery(qry);
                int count=0;
                while (rs.next()) {
                    int weekdays=rs.getInt("Recursion");// NOI18N
                    String binaryWeek=Integer.toBinaryString(weekdays);
                    if (binaryWeek.length()<7){    
                        String filler;
                        filler = "0000000";
                        binaryWeek=filler.substring(0, 7-binaryWeek.length())+binaryWeek;
                    }
                     char[] days=binaryWeek.toCharArray();
                     String one="1";
                     if (days[schedDate.getWeekDay()-1]==one.charAt(0)){  
                         lm2[count]=new ListDEntry(rs.getInt("id"),rs.getString("Event"));//NOI18N
                            count++;
                     } 
                }
            } catch (SQLException ex) {
                _log.log(Level.SEVERE, null, ex);
            }
            for (ListDEntry lm21 : lm2) {
                dlm.addElement(lm21);
            }
        }                    
        DB.closeConn();
        
        return dlm.getSize();
    }


public static void populateCombo (JComboBox DisplayCombo){
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        DisplayCombo.setModel(dcm); 
        int totElements=0;
        DB.openConn();
        String qry= "SELECT DISTINCT Event FROM " + TABLE + //NOI18N
            " WHERE Event IS NOT NULL ORDER BY Event;";//NOI18N
        ResultSet rs;
            try {
                rs = DB.getQuery(qry);
                while (rs.next()) {
                    dcm.addElement(rs.getString("Event"));//NOI18N
                    totElements ++;
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
    
    static final Logger _log = Logger.getLogger(Schedule.class.getName());

}

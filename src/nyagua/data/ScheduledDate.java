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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Rudi Giacomini Pilon
 * 
 * 
 * Contains all informations about a scheduled event date
 * to easily perform some operations on dates
 */
public class ScheduledDate {
    private final Date schedDate;
    private final Calendar c = Calendar.getInstance();

    public ScheduledDate(Date date) {
        this.schedDate=date;
        this.c.setTime(date);
    }
        
    /**
     * Returns the weekday of event date
     * 
     * @return integer representing day [1=Sun -> 7=Sat]
     */
    public int getWeekDay(){        
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns the day of the year of event date
     * 
     * @return integer representing day [1 -> 366]
     */
    public int getYearDay(){
        return c.get(GregorianCalendar.DAY_OF_YEAR);
    }
    
    /**
     * Returns the day of the moth of event date
     * 
     * @return integer representing day [1 -> 366]
     */
    public int getMonthDay(){
        return c.get(GregorianCalendar.DAY_OF_MONTH);
    }
    
    /**
     * Returns the last day in month for event date
     * 
     * @return int last day of month [28->31]
     */
    public int getLastMonthDay(){
        return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
    }
         
    
   /**
    * Get an array of date from day before date
    * 
    * @return the array of Dates
    */ 
   public Date[] getFourDays(){
       Date[] days=new Date[4];
       int yesterday=-1;
       int nextday=2;
       for (int i=yesterday;i<=nextday;i++){
           Calendar cal = Calendar.getInstance();
            cal.setTime(schedDate);
           cal.add(Calendar.DATE, i);
           Date day=new Date(cal.getTimeInMillis());
           days[i+1]=day;
       }       
       return days;
   } 
    
}

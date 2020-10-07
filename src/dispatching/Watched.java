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

package dispatching;

import java.util.Observable;

/**
 *  Watched.java 
 * Implements an internal message bus basing on Obsevable/Observer pattern
 * This is a single instance global object that act as main hub to 
 * broadcast all custom application messages.
 * 
 * Observers should:
 * 1) Get an instance of this object
 * 2) attach an observer to this object
 * 3) Filter messages to get only correct one
 * 
 * Notifiers should:
 * 1) Get an instance of this object
 * 2) Send a message as update
 * 
 * @author rudigiacomini
 */
public class Watched extends Observable{
    
    //public constant identifing kind of message in the bus
    public static int CHANGED_UNITS_SETTINGS=1;  //Units has been changed in settings
    public static int CHANGED_PRESETS_SETTINGS=11;  //Units has been changed in settings
    public static int ADDED_MAINTENANCE_EVENT=6;    //A maintenance event has been recorded in scheduler
    public static int ADDED_SOLUTION=7;    //A new recipe has been saved in Solutions panel
    public static int MOVE_FOCUS_TO_FISHBASE=2;     // Focus has to move to FishBasePanel
    public static int MOVE_FOCUS_TO_INVBASE=3;     // Focus has to move to InvBasePanel
    public static int MOVE_FOCUS_TO_PLANTSBASE=4;     // Focus has to move to PlantBasePanel
    public static int MOVE_FOCUS_TO_SOLUTIONS=8;     // Focus has to move to Solutions Panel
    public static int AQUARIUM_CLICKED=5;          //aquarium has changed (or non aquarium selected)
    public static int REQUEST_CLEAN_ALL_FIELDS=20;  //CleanAllFields Global message 
    public static int REQUEST_CLEAR_LIST=21;  //Empti combo list Global message
    public static int REQUEST_POPULATE_LIST=22;  //Fill combo list Global message
    public static int REQUEST_POPULATE_FBTABLE=23;  //Populate only FB table Global message
    public static int REQUEST_POPULATE_IBTABLE=24;  //Populate only IB table Global message
    public static int REQUEST_POPULATE_PBTABLE=25;  //Populate only PB table Global message
    public static int REQUEST_POPULATE_TREE=26;  //Populate Main tree Global message
    
    
    private final static Watched INSTANCE = new Watched();
    
    /**
     *  Singleton Pattern (thread safe implemet.)
     *
     * @return Watched instance
     */
    public static Watched getInstance() {
        return INSTANCE;
    }
    
    /**
     * Send notifications
     * 
     * @param kindOfMessage one of constants declared in this class 
     */
    public void Update (int kindOfMessage){
        setChanged();
        notifyObservers(kindOfMessage);
    }    
}

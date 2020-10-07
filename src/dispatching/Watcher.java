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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 *  Watcher.java 
 * Implements an internal message bus basing on Obsevable/Observer pattern
 * This is the observer part and is connected to Watched  * 
 * 
 * Observers should:
 * 1) Declare an instance of this object
 * 2) attach an observer to this object
 * 3) Filter messages to get only correct one
 * 
 * @author rudigiacomini
 */
public class Watcher implements Observer{
    private ActionListener watcherAL;
    //This connect a custom function to the object
    public Watcher (ActionListener al){
        watcherAL=al;
    }

    //This is called when an event occour
    @Override
    public void update(Observable o, Object arg) {
        int actionType=Integer.valueOf(arg.toString());
        this.watcherAL.actionPerformed(new ActionEvent(arg, actionType, null));        
    }
}

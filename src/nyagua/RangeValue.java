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
package nyagua;

import java.awt.Color;

/**
 *
 * @author rudi
 */
public class RangeValue {

    public RangeValue() {
    }

    public RangeValue(double minValue, double maxValue, Color color, String text) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.color = color;
        this.text = text;
    }
    
       
    // <editor-fold defaultstate="collapsed" desc="Getters and setters">
    public double getMin() {
        return minValue;
    }
    
    public void setMin(double min) {
        this.minValue = min;
    }
    
    public double getMax() {
        return maxValue;
    }
    
    public void setMax(double max) {
        this.maxValue = max;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
     public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
 // </editor-fold>
    
    private double minValue;    //min value of range
    private double maxValue;    //max value of range
    private Color color;        //color attribute for the range
    private String text;        //message for this range
            
}

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

package util_panels;

/**
 * Functions to converts some units to some others
 * @author rudigiacomini
 */
public class Converter {    
    
// <editor-fold defaultstate="collapsed" desc="lenght">
  
    /**
     * Converts cm to inch
     * 1 cm = 0.3937 inch
     * 
     * @param cm qty in cm
     * @return qty in inch
     */
    public static double cm2inch(double cm){
        return cm * 0.3937;
    }
    
    /**
     * Converts inch to cm
     * 1 inch = 2.54 cm
     * 
     * @param inch  qty in inch
     * @return qty in cm
     */
    public static double inch2cm(double inch){
        return inch * 2.54;
    }
    
    /**
     * Converts feet to cm
     * 1 feet = 30.48 cm
     * 
     * @param feet  qty in feet
     * @return qty in cm
     */
    public static double feet2cm(double feet){
        return feet * 30.48;
    }
    
    /**
     * Converts cm to feet
     * 1 cm = 0.0328 feet
     * 
     * @param cm    qty in cm
     * @return qty in feet
     */
    public static double cm2feet(double cm){
        return cm * 0.0328;
    }
     
    
// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="volume">
     
    /**
     * convert US gal to liters:
     * 1 US gallon = 3.78541178 liter
     * 
     * @param gal   qty in gals
     * @return      qty in liters
     */
    public static double gal2l(double gal){
        return gal* 3.78541178;
    }
    
    /**
     * convert liters to US gal:
     * 1 liter = 0.264172 US gallon
     * 
     * @param  l  qty in liters
     * @return      qty in gals
     */
    public static double l2gal(double l){
        return l* 0.264172;
    }
    
    /**
     * convert cc to liters:
     * 1  cc = 0.001 liter
     * 
     * @param  cc  qty in cc
     * @return      qty in liters
     */
    public static double cc2l(double cc){
        return cc * 0.001;
    }
    
    /**
     * convert liters to cc:
     * 1 liter = 1000 cc
     * 
     * @param  l  qty in liters
     * @return      qty in cc
     */
    public static double l2cc(double l){
        return l * 1000;
    }
    
    /**
     * convert cubic inch to liters:
     * 1  ci = 0.016387 liter
     * 
     * @param  ci  qty in ci
     * @return      qty in liters
     */
    public static double ci2l(double ci){
        return ci * 0.016387;
    }
    
    /**
     * convert liters to cubic inch:
     * 1 liter = 61.023744 ci
     * 
     * @param  l  qty in liters
     * @return      qty in ci
     */
    public static double l2ci(double l){
        return l * 61.023744;
    }
    
    /**
     * convert US gal to ci:
     * 1 US gallon = 231 ci
     * 
     * @param gal   qty in gals
     * @return      qty in ci
     */
    public static double gal2ci(double gal){
        return gal* 231;
    }
    
    /**
     * convert ci to US gal:
     * 1 ci = 0.004329 US gallon
     * 
     * @param ci   qty in ci
     * @return      qty in gals
     */
    public static double ci2gal(double ci){
        return ci* 0.004329;
    }
    
    /**
     * convert US gal to cc:
     * 1 US gallon = 3785.411784 cc
     * 
     * @param  gal  qty in gals
     * @return      qty in cc
     */
    public static double gal2cc(double gal){
        return gal * 3785.411784;
    }
    
    /**
     * convert cc to US gal:
     * 1 cc = 0.004329 US gallon
     * 
     * @param cc   qty in cc
     * @return      qty in gals
     */
    public static double cc2gal(double cc){
        return cc * 0.000264;
    }
    
    /**
     * convert table spoons (tsp)  to cc:
     * 1 tsp = 14.786765 cc
     * 
     * @param  tsp  qty in tsps
     * @return      qty in cc
     */
    public static double tsp2cc(double tsp){
        return tsp * 14.786765;
    }
    
    /**
     * convert cc to  tsp:
     * 1 cc = 0.067628  tsp
     * 
     * @param cc   qty in cc
     * @return      qty in tsps
     */
    public static double cc2tsp(double cc){
        return cc * 0.067628;
    }
    
  // </editor-fold> 
    

    
// <editor-fold defaultstate="collapsed" desc="mass">
    public static double mg2g(double mg){
        return mg/1000;
    }  
    
    public static double g2mg(double g){
        return g*1000;
    }
    
    /*
     * water only
     */
    public static double tsp2g(double tsp){
        return tsp*5;
    }
    
     /*
     * water only
     */
    public static double g2tsp(double g){
        return g*0.2;
    } 
    
    /**
     * Convert grams to ounces
     * 1oz = 0.035274 g
     * 
     * @param g qty in gram
     * @return qty in ounces
     */
    public static double g2oz(double g){
        return g*0.035274;
    } 
    
    /**
     * Convert ounces to grams
     * 1g=0.035274 oz
     * 
     * @param oz    qty in ounce
     * @return qty in gram
     */
    public static double oz2g(double oz){
        return oz*0.035274;
    }
    
   /**
     * Convert kilograms to pounds
     * 1 lb = 0.453592 kg
     * 
     * @param kg    qty in kilogram
     * @return qty in pounds
     */
    public static double kg2lb(double kg){
        return kg*0.453592;
    } 
    
    /**
     * Convert pounds to kilograms
     * 1 kg= 2.204623 lb
     * 
     * @param lb qty in ounce
     * @return qty in kilogram
     */
    public static double lb2kg(double lb){
        return lb*2.204623;
    }
    
// </editor-fold> 
    
    
// <editor-fold defaultstate="collapsed" desc="other">
  /**
     * convert Celsius to Fahrenheit:
     * 1 F = (1.8 × °C) + 32
     * 
     * @param  celsius  qty in celsius
     * @return      qty in Fahrenheit
     */
    public static double C2F(double celsius){
        return (celsius * 1.8)+32;
    }
    
    /**
     * convert Fahrenheit to Celsius:
     * 1  C = 0.555555555556 × (°F - 32)
     * 
     * @param far   qty in Fahrenheit
     * @return      qty in Celsius
     */
    public static double F2C(double far){
        return (far-32) * 0.555555555556;
    }
    
    /**
     * convert Kelvin to Fahrenheit:
     * °F = K × 1.8 − 459.67
     * 
     * @param kelvin   qty in Kelvin
     * @return      qty in Fahrenheit
     */
    public static double K2F(double kelvin){
        return (kelvin * 1.8)-459.67;
    }
    
    /**
     * convert Fahrenheit to Kelvin:
     * K = (°F + 459.67) x 0.555555555556
     * 
     * @param  far  qty in Fahrenheit
     * @return      qty in Kelvin
     */
    public static double F2K(double far){
        return (far+459.67) * 0.555555555556;
    }
    
    /**
     * convert Celsius to Kelvin:
     * K = °C + 273.15
     * 
     * @param  celsius  qty in celsius
     * @return      qty in Kelvin
     */
    public static double C2K(double celsius){
        return celsius +273.15;
    }
    
    /**
     * convert Kelvin to Celsius:
     * °C = K − 273.15
     * 
     * @param kelvin   qty in Kelvin
     * @return      qty in Celsius
     */
    public static double K2C(double kelvin){
        return kelvin-273.15;
    }

    
   
    // </editor-fold>
    
}

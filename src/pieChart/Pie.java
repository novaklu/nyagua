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

package pieChart;

import javax.swing.JComponent;

/**
 * Pie.java 
 * Implements Pie Chart basic class
 * 
 * @author Rudi Giacomini Pilon
 */
public class Pie extends JComponent {
  PieRenderer cp;
  protected double [] data;
  protected double[] percentages; // pie slices
  protected String [] labData;
  protected String[] labels; // labels for slices
  protected String footer;
  protected String header;
  
  
  protected java.text.NumberFormat formatter = java.text.NumberFormat.getPercentInstance();

  
  public Pie(double [] dataToDisplay, String[] labelsToDisplay,
          String footerToDisplay, String headerToDisplay){    
    data=dataToDisplay;
    labData=labelsToDisplay;
    footer=footerToDisplay;
    header=headerToDisplay;
    setUI(cp = new PieSlices());     
    updateLocalValues(true);
  }

  

  /**
   * 
   * Calculate values percentages against values sum
   *
   * @param mode [ true=percentages are calculated | false values are passed as is]
   */
  protected void calculatePercentages(boolean mode) {
    double runningTotal = 0.0;
    for (int i = data.length - 1; i >= 0; i--) {
      percentages[i] = data[i];      
      runningTotal += percentages[i];
    }
    if (mode){
        // Make each entry a percentage of the total.
        for (int i = data.length - 1; i >= 0; i--) {
          percentages[i] /= runningTotal;
        }
    }    
  }

  /*
   * Creates slice labels
   */
  protected void createLabelsAndTips() {
    for (int i = data.length - 1; i >= 0; i--) { 
      labels[i] = (String) labData[i] + "="+ (String)formatter.format(percentages[i]);
    }
  }

  /**
   *  Update chart (not used by now)
   * 
   * @param freshStart [true for first use | false otherwise]
   */
  private void updateLocalValues(boolean freshStart) {
    if (freshStart) {
      int count = data.length;
      percentages = new double[count];
      labels = new String[count];      
    }
    calculatePercentages(false);
    createLabelsAndTips();
    cp.setValues(percentages);   
    cp.setLabels(labels);
    cp.setFooter(footer);
    cp.setHeader(header);
    repaint();
  }
}
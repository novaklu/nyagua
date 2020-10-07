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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/*
 * PieRenderer.java
 * Implements the rendering of pie slices
 * @author Rudi Giacomini Pilon
 */
abstract class PieRenderer extends ComponentUI {
  protected Font textFont = new Font("Serif", Font.PLAIN, 11);
  protected Color textColor = Color.black;
  protected Color brown = new Color(139,69,19);
  protected Color lime = new Color(50,205,50);
  protected Color colors[] = new Color[] { Color.blue,Color.red,
      Color.green,  Color.magenta,Color.cyan,Color.gray, Color.yellow,Color.black,
      Color.darkGray, Color.white, brown,lime,Color.pink,Color.orange }; //max 14 colors/slices
  protected double values[] = new double[0];
  protected String labels[] = new String[0];
  protected String footer= new String();
  protected String header= new String();
  
  /**
   * Sets new labels
   * 
   * @param l the labels array
   */
  public void setLabels(String[] l) {
    labels = l;
  }
  
  /**
   * Sets new footers
   * 
   * @param l the labels array
   */
  public void setFooter(String l) {
    footer = l;
  }

   /**
   * Sets new header
   * 
   * @param l the labels array
   */
  public void setHeader(String l) {
    header = l;
  }
  
  /**
   * Set new values
   * 
   * @param v the values array
   */
  public void setValues(double[] v) {
    values = v;
  }
    @Override
  public abstract void paint(Graphics g, JComponent c);
}
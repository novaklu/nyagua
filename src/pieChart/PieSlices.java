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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

/**
 * PieSlices.java
 * 
 * implements the slices building of a pie chart
 * 
 @author Rudi Giacomini Pilon
 */
class PieSlices extends PieRenderer {
  protected static PieSlices chartUI = new PieSlices();
  protected int originX, originY;
  protected int radius;
  private static final double d2r = Math.PI / 180.0; // Degrees to radians.
  private static final int xGap = 5;  

    @Override  
  public void paint(Graphics g, JComponent c) {     
    Dimension size = c.getSize();
    originX = size.width / 2;
    originY = size.height / 2;
    int diameter = (int) (originX < originY ? size.width/1.5 : size.height/1.5);    
    radius = (diameter / 2) + 1;
    int cornerX = (originX - (diameter / 2));
    int cornerY = (originY - (diameter / 2));
    int startAngle = 0;
    int arcAngle;
    for (int i = 0; i < values.length; i++) {
      arcAngle = (int) (i < values.length - 1 ? Math
          .round(values[i] * 360) : 360 - startAngle);
      g.setColor(colors[i % colors.length]);
      g.fillArc(cornerX, cornerY, diameter, diameter, startAngle,
          arcAngle);
      drawLabel(g, labels[i], startAngle + (arcAngle / 2));
      startAngle += arcAngle;
    }
    g.setColor(Color.black);
    g.drawOval(cornerX, cornerY, diameter, diameter); // Cap the circle.
    addFooter(g, footer);
    addHeader(g, header);
  }

  /**
   * Draws a single label
   * 
   * @param g       graphis object dest
   * @param text    text to draw
   * @param angle   where to place label
   */  
  public void drawLabel(Graphics g, String text, double angle) {
    //g.setFont(textFont);
    //g.setColor(textColor);
    double radians = angle * d2r;
    int x = (int) ((radius + xGap) * Math.cos(radians));
    int y = (int) ((radius + xGap) * Math.sin(radians));
    if (x < 0) {
      x -= SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
    }
    if (y < 0) {
      y -= g.getFontMetrics().getHeight();
    }
    g.drawString(text, x + originX, originY - y);
  }
  
  public void addFooter(Graphics g, String text) {
    //g.setFont(textFont);
    g.setColor(Color.black);
    int x = 0;
    int y = 0;
    x -= SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
    y -= g.getFontMetrics().getHeight();    
    //g.drawString(text, originX+(x/2), (originY*2) + y); //center x, bottom of y minus full height
    g.drawString(text, xGap, (originY*2) + y/2); //center x, bottom of y minus full height
  }
  
  public void addHeader(Graphics g, String text) {
    //g.setFont(textFont);
    Font textFontTitle;
        textFontTitle = new Font("Serif", Font.BOLD, 12);
    g.setFont(textFontTitle);  
    g.setColor(Color.blue);
    int x = 0;
    int y = 0;
    x -= SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
    y -= g.getFontMetrics().getHeight();    
    //g.drawString(text, originX+(x/2), (originY*2) + y); //center x, bottom of y minus full height
    g.drawString(text, originX+(x/2), -y); //center x, bottom of y minus full height
  }

  public static ComponentUI createUI(JComponent c) {
    return chartUI;
  }

}


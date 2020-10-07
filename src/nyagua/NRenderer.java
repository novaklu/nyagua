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
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * NRenderer.java
 * Class to render in lightgrey the background of a column in a JTable
 *
 * @author rudigiacomini
 */
public class NRenderer extends DefaultTableCellRenderer {   
    JLabel lbl = new JLabel();
    @Override
    public Component getTableCellRendererComponent (JTable table, Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int row, int column) {
        if(column == 0 | column == 2) {
            lbl.setText((String) value);
            lbl.setBackground(Color.lightGray);
            lbl.setOpaque(true);
        }
        else {
        }
        return lbl;
  }
}


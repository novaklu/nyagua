/*
    Gui-bits - library of resuable gui components
    Copyright (C) 2010  Thomas C.A. Judge

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package nyagua;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Node renderer for JTrees containing IconNode's
 * 
 * @author tj
 */
public class IconNodeRenderer extends DefaultTreeCellRenderer {
    /**
     * Create new renderer.
     */
    public IconNodeRenderer() {
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel t = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof IconNode) {
            IconNode tmp = (IconNode)value;
            t.setIcon(tmp.getIcon());
        
            if (tmp.getToolTip() != null) {
                t.setToolTipText(tmp.getToolTip());
            } else {
                t.setToolTipText("");
            }
            t.setFont(new java.awt.Font("Dialog", 1, 12));
            
        } 
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(t);    
        return t;
    }
}

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
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A Tree Node with an Icon.
 * 
 * A comprehensive example is avaliable in com.tomjudge.gui.tree.Example
 * Example:
 * <pre>
 * {@code
 * IconNode rootNode = new IconNode("Root", new ImageIcon(getClass().getResource("/icons/root.png")), "Root") {
 *     
 *     public void showPopupMenu(int x, int y) {
 *     }
 *
 *     
 *     public void doubleClicked() {
 *     }
 * };
 * final JTree tree = new JTree();
 * tree.setCellRenderer(new IconNodeRenderer());
 * tree.setModel(new DefaultTreeModel(rootNode));
 *
 * // Add double click listener.
 * tree.addMouseListener(new DoubleClickListener(tree));
 *
 * // Add popup menu listener
 * tree.addMouseListener(new PopupMenuListener(tree));
 *
 * final JPopupMenu nodeMenu = new JPopupMenu();
 * JMenuItem menuItem = new JMenuItem();
 * menuItem.setMnemonic('1');
 * menuItem.setText("Menu item 1");
 * menuItem.addActionListener(new ActionListener() {
 *     public void actionPerformed(ActionEvent evt) {
 *         System.out.println("Node menu item 1 action");
 *     }
 * });
 * popupMenu.add(newContactMenuItem);
 *
 * rootNode.add(new IconNode("Node 1", null, "No Icon") {
 *     
 *     public void showPopupMenu(int x, int y) {
 *         popupMenu.show(tree, x,y);
 *     }
 *
 *     
 *     public void doubleClicked() {
 *         System.out.println("I was double clicked");
 *     }
 * });
 * }
 * </pre>
 *
 * 
 * @author Tom Judger
 */
public abstract class IconNode extends DefaultMutableTreeNode {
    /**
     * The current icon to be displayed
     */
    protected Icon icon;
    /**
     * The tool tip for the node.
     */
    protected String toolTip;

    /**
     * Create a new IconNode
     * @param userObject The object to be rendered as the items value (Usually a String)
     * @param i The icon for the node.
     * @param t The tool tip for the node.
     */
    public IconNode(Object userObject, Icon i, String t) {
        super(userObject, true);
        icon = i;
        toolTip = t;
        
    }
    
    /**
     * Set new tool tip.
     * You should call JTree.updateUI() for the tree that this node is in
     * after calling this function.
     * @param t New tool tip text
     */
    public void setToolTip(String t) {
        toolTip = t;
    }
    
    /**
     * Get the tool tip text.
     * @return tool tip text
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * Set the nodes icon.
     * You should call JTree.updateUI() for the tree that this node is in
     * after calling this function.
     * @param icon
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * The current icon.
     * @return the icon.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Show the PopUp menu for this node.
     * @param comp
     * @param x Co-ordinate
     * @param y Co-ordinate
     */
    abstract public void showPopupMenu(Component comp, int x, int y);

    /**
     * Action to perform when this node is double clicked.
     */
    abstract public void doubleClicked();
}


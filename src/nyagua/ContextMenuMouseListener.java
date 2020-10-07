/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2013 Rudi Giacomini Pilon
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

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 *
 * @author rudigiacomini
 */
public class ContextMenuMouseListener extends MouseAdapter{    
     private final JPopupMenu popup = new JPopupMenu();
     
    private final Action cutAction;
    private final Action copyAction;
    private final Action pasteAction;
    private final Action undoAction;
    private final Action selectAllAction;

    private JTextComponent textComponent;
    private String savedString = "";//NOI18N
    private Actions lastActionSelected;

    private enum Actions { UNDO, CUT, COPY, PASTE, SELECT_ALL };

    public ContextMenuMouseListener() {
        undoAction = new AbstractAction(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("undo")) {//NOI18N
            @Override
            public void actionPerformed(ActionEvent ae) {
                    textComponent.setText("");//NOI18N
                    textComponent.replaceSelection(savedString);
                    lastActionSelected = Actions.UNDO;
            }
        };
        popup.add(undoAction);
        popup.addSeparator();

        cutAction = new AbstractAction(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Cut")) {//NOI18N
            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.CUT;
                savedString = textComponent.getText();
                textComponent.cut();
            }
        };
        popup.add(cutAction);
        
        copyAction = new AbstractAction(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Copy")) {//NOI18N
            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.COPY;
                textComponent.copy();
            }
        };
        popup.add(copyAction);

       pasteAction = new AbstractAction(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Paste")) {//NOI18N
            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.PASTE;
                savedString = textComponent.getText();
                textComponent.paste();
            }
        };
        popup.add(pasteAction);
        popup.addSeparator();

        selectAllAction = new AbstractAction(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Select_All")) {//NOI18N
            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.SELECT_ALL;
                textComponent.selectAll();
            }
        };
        popup.add(selectAllAction);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
            if (!(e.getSource() instanceof JTextComponent)) {
                return;
            }
            textComponent = (JTextComponent) e.getSource();
            textComponent.requestFocus();
            boolean enabled = textComponent.isEnabled();
            boolean editable = textComponent.isEditable();
            boolean nonempty = !(textComponent.getText() == null || textComponent.getText().equals(""));//NOI18N
            boolean marked = textComponent.getSelectedText() != null;
            boolean pasteAvailable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).isDataFlavorSupported(DataFlavor.stringFlavor);
            undoAction.setEnabled(enabled && editable && (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE));
            cutAction.setEnabled(enabled && editable && marked);
            copyAction.setEnabled(enabled && marked);
            pasteAction.setEnabled(enabled && editable && pasteAvailable);
            selectAllAction.setEnabled(enabled && nonempty);
            int nx = e.getX();
            if (nx > 500) {
                nx = nx - popup.getSize().width;
            }
            popup.show(e.getComponent(), nx, e.getY() - popup.getSize().height);
        }
    }
}
     
     

     
    

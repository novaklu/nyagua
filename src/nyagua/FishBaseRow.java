/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon *
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
 */

package nyagua;

import java.awt.Image;
import java.awt.event.ActionListener;

/**
 *A row rappresenting fish info from fishbase
 * 
 * @author rgi
 */
public class FishBaseRow extends javax.swing.JPanel {

    /**
     * Creates new form FishBaseRow
     */
    public FishBaseRow() {
        initComponents();
    }    
    
    /**
     * Add Id returned on callback
     * 
     * @param id fish id
     */
    public void setID (String id) {
        choseButton.putClientProperty("id", id);
    }
    
    /**
     * Add image url returned on callback
     * 
     * @param imgURL the url of image
     */
    public void setImageUrl (String imgURL) {
        choseButton.putClientProperty("imgURL", imgURL);
    }
    
    /**
     * Set image of the row
     * 
     * @param img image
     */
    public void setImage (Image img) {
        if (img == null) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
            imageLabel.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        } else {
           imageLabel.setText("");
           imageLabel.setIcon(new javax.swing.ImageIcon(img));
        }
    }
    
    
    /**
     * Set name label of the row
     * 
     * @param name a string with common name / lang / scientific name
     */
    public void setNameLabel (String name) {
        nameLabel.setText(name);
    }
    
    /**
     * Add an action listener for the button of the row
     * 
     * @param al the action listener
     */
    public  void setChoseActionListener (ActionListener al) {
       this.choseButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        imageLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        choseButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setPreferredSize(new java.awt.Dimension(355, 90));
        setLayout(new java.awt.GridBagLayout());

        imageLabel.setForeground(new java.awt.Color(255, 102, 51));
        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        imageLabel.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        imageLabel.setAlignmentY(0.0F);
        imageLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        imageLabel.setMaximumSize(new java.awt.Dimension(85, 60));
        imageLabel.setMinimumSize(new java.awt.Dimension(85, 60));
        imageLabel.setPreferredSize(new java.awt.Dimension(85, 60));
        imageLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        add(imageLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 80;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(nameLabel, gridBagConstraints);

        choseButton.setText(bundle.getString("Choose")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(choseButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton choseButton;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel nameLabel;
    // End of variables declaration//GEN-END:variables

}

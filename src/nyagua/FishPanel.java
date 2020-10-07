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

/*
 * FishPanel.java
 *
 * Created on 13-giu-2012, 14.00.19
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Fish;
import nyagua.data.FishBase;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class FishPanel extends javax.swing.JPanel {
    
    //Connect listener to application bus
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getID()==Watched.AQUARIUM_CLICKED){
                 if (Global.AqID != 0) {
                     populateTable();
                 }
                 else {
                     emptyTable();
                 }
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            }  else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                emptyCombo();
            } else if(e.getID()==Watched.REQUEST_POPULATE_LIST){
                populateCombo();
            } else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                refreshUnits();
            }         
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    
    /** Creates new form FishPanel */
    public FishPanel() {
        initComponents();
        initCutAndPaste();  
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }

    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList6 = {fishIdTextField,  fishMaleQtyTextField,
                     fishFemaleQtyTextField, fishNotesTextField};
        Util.CleanTextFields(jtfList6);
        fishDateTextField.setDate(null);
        if (fishNameComboBox.getItemCount()>0){
            fishNameComboBox.setSelectedIndex(0);
        }                
        fbImageLabelImg.setIcon(null);
        fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));   
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Fish.populateTable(fishTable);
    }
    
    /**
     * Empties a jTable assigning null model
     * 
     */
    private static void emptyTable (){
        DefaultTableModel dm = new DefaultTableModel();
        String tableData[][] = {{null}};
        String[] nameHeader = {java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_SELECTION")};
        dm.setDataVector(tableData, nameHeader);
        fishTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("fishtable", Fish.CAPTIONS.length);//NOI18N
        Fish.setColWidth(widths);
        Util.setColSizes(fishTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("fishtable", fishTable);//NOI18N
    }
    
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        fishDateTextField.setDateFormatString(Global.dateFormat);
    }
    
    /**
     * populates the combo box
     */
    private void populateCombo(){
        FishBase.populateCombo(fishNameComboBox);
    }
    
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty lists
        fishNameComboBox.setModel(dcm);
    }
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){
        int recId = TablesUtil.getIdFromTable(
                fishTable, fishTable.getSelectedRow());
        
        Fish spec=Fish.getById(recId);
        fishIdTextField.setText(Integer.toString(spec.getId()));// NOI18N
        fishDateTextField.setDate(spec.getDate());// NOI18N
        fishNameComboBox.setSelectedItem(spec.getName());
        fishMaleQtyTextField.setText(spec.getMales());// NOI18N
        fishFemaleQtyTextField.setText(spec.getFemales());// NOI18N
        fishNotesTextField.setText(spec.getNotes());// NOI18N
        
        loadImage(spec.getName());
    }
    
    private void loadImage(String specName) {
        
        boolean loadImage = false;
        if ((specName != null) && (!specName.isEmpty())) {
            loadImage = true;
        }
        
        fbImageLabelImg.setText(null);
        FishBase specData = null;
        if (loadImage) {
            specData = FishBase.getByName(specName);
        }
        if ((specData != null) &&  specData.hasImage()){
            Util.ImageDisplayResize(specData.getImage(), fbImageLabelImg, 300);
        } else {
            fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
            fbImageLabelImg.setIcon(null);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fishMaleQtyLabel = new javax.swing.JLabel();
        fishIdTextField = new javax.swing.JTextField();
        fishDateLabel = new javax.swing.JLabel();
        fishMaleQtyTextField = new javax.swing.JTextField();
        fishNameLabel = new javax.swing.JLabel();
        fishNotesTextField = new javax.swing.JTextField();
        fishNotesLabel = new javax.swing.JLabel();
        fishFemaleQtyTextField = new javax.swing.JTextField();
        fishFemaleQtyLabel = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        fishTable = new javax.swing.JTable();
        fishIdLabel = new javax.swing.JLabel();
        jToolBar7 = new javax.swing.JToolBar();
        fishClearButton = new javax.swing.JButton();
        fishSaveButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        fishDeleteButton = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        fishReportButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        fishSearchButton = new javax.swing.JButton();
        fishSearchState = new javax.swing.JButton();
        fishNoSearchButton = new javax.swing.JButton();
        fishDateTextField = new com.toedter.calendar.JDateChooser();
        fishNameComboBox = new javax.swing.JComboBox();
        addItemButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fbImageLabelImg = new javax.swing.JLabel();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        fishMaleQtyLabel.setText(bundle.getString("Ny.fishMaleQtyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishMaleQtyLabel, gridBagConstraints);

        fishIdTextField.setEditable(false);
        fishIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fishIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishIdTextField, gridBagConstraints);

        fishDateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishDateLabel, gridBagConstraints);

        fishMaleQtyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fishMaleQtyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fishMaleQtyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fishMaleQtyTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishMaleQtyTextField, gridBagConstraints);

        fishNameLabel.setText(bundle.getString("Ny.fishNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishNameLabel, gridBagConstraints);

        fishNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fishNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishNotesTextField, gridBagConstraints);

        fishNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishNotesLabel, gridBagConstraints);

        fishFemaleQtyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fishFemaleQtyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fishFemaleQtyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fishFemaleQtyTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishFemaleQtyTextField, gridBagConstraints);

        fishFemaleQtyLabel.setText(bundle.getString("Ny.fishFemaleQtyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishFemaleQtyLabel, gridBagConstraints);

        fishTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        fishTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        fishTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fishTableMouseClicked(evt);
            }
        });
        fishTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fishTableKeyReleased(evt);
            }
        });
        jScrollPane8.setViewportView(fishTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 540;
        gridBagConstraints.ipady = 240;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane8, gridBagConstraints);

        fishIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishIdLabel, gridBagConstraints);

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);

        fishClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        fishClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        fishClearButton.setFocusable(false);
        fishClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fishClearButtonMouseClicked(evt);
            }
        });
        jToolBar7.add(fishClearButton);

        fishSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        fishSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        fishSaveButton.setFocusable(false);
        fishSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fishSaveButtonMouseClicked(evt);
            }
        });
        jToolBar7.add(fishSaveButton);
        jToolBar7.add(jSeparator6);

        fishDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        fishDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        fishDeleteButton.setFocusable(false);
        fishDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fishDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar7.add(fishDeleteButton);
        jToolBar7.add(jSeparator13);

        fishReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        fishReportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        fishReportButton.setFocusable(false);
        fishReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishReportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fishReportButtonMouseClicked(evt);
            }
        });
        jToolBar7.add(fishReportButton);
        jToolBar7.add(jSeparator16);

        fishSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        fishSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        fishSearchButton.setFocusable(false);
        fishSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fishSearchButtonActionPerformed(evt);
            }
        });
        jToolBar7.add(fishSearchButton);

        fishSearchState.setFocusable(false);
        fishSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        fishSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        fishSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        fishSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar7.add(fishSearchState);

        fishNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        fishNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        fishNoSearchButton.setFocusable(false);
        fishNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fishNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        fishNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        fishNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        fishNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fishNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fishNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar7.add(fishNoSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishDateTextField, gridBagConstraints);

        fishNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fishNameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fishNameComboBox, gridBagConstraints);

        addItemButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/plus.png"))); // NOI18N
        addItemButton.setMaximumSize(new java.awt.Dimension(28, 28));
        addItemButton.setMinimumSize(new java.awt.Dimension(28, 28));
        addItemButton.setPreferredSize(new java.awt.Dimension(28, 28));
        addItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(addItemButton, gridBagConstraints);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(63, 15));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(63, 15));

        fbImageLabelImg.setForeground(new java.awt.Color(255, 102, 51));
        fbImageLabelImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fbImageLabelImg.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        jScrollPane1.setViewportView(fbImageLabelImg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void fishMaleQtyTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fishMaleQtyTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fishMaleQtyTextFieldKeyTyped

private void fishFemaleQtyTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fishFemaleQtyTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fishFemaleQtyTextFieldKeyTyped

private void fishTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fishTableMouseClicked
    /**Populate TextFields on tab6 (Fishes) */
    refreshFields();
}//GEN-LAST:event_fishTableMouseClicked

private void fishClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fishClearButtonMouseClicked
    /** Cleans all textFields on tab6 (Fishes)*/
    CleanAllFields();
}//GEN-LAST:event_fishClearButtonMouseClicked

private void fishSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fishSaveButtonMouseClicked
    /**Insert record on db for table Fishes or update it if existing*/
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    String currID = fishIdTextField.getText();

    Fish spec=new Fish();
    if (currID == null || currID.equals("")) {
        spec.setId(0);
        } else {
        spec.setId(Integer.valueOf(currID));
    }
    if (LocUtil.isValidDate(fishDateTextField.getDate())){
        if (fishDateTextField.getDate() == null){
            spec.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
        }else {
            spec.setDate(LocUtil.delocalizeDate(fishDateTextField.getDate()));
        }
    } else {
        Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));
        fishDateTextField.requestFocus();
        return;
    }
    
    spec.setName(fishNameComboBox.getSelectedItem().toString());
    spec.setMales(LocUtil.delocalizeDouble(fishMaleQtyTextField.getText()));
    spec.setFemales(LocUtil.delocalizeDouble(fishFemaleQtyTextField.getText()));
    spec.setNotes(fishNotesTextField.getText());
    spec.save(spec);
    Fish.populateTable(fishTable);
    CleanAllFields();
}//GEN-LAST:event_fishSaveButtonMouseClicked

private void fishDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fishDeleteButtonMouseClicked
    /** Delete selected record on tab6 (Fishes)*/
    Fish.deleteById(fishIdTextField.getText());
    Fish.populateTable(fishTable);
    CleanAllFields();
}//GEN-LAST:event_fishDeleteButtonMouseClicked

private void fishReportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fishReportButtonMouseClicked
    //Show fishes data report in a browser
    String id = fishIdTextField.getText();
    try {
        Report.FishBaseReport(id,true);
    } catch (SQLException ex) {
        Logger.getLogger(FishPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_fishReportButtonMouseClicked

private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
// Open Fbase panel   via internal bus 
    Watched nyMessages=Watched.getInstance();
    nyMessages.Update(Watched.MOVE_FOCUS_TO_FISHBASE);
}//GEN-LAST:event_addItemButtonActionPerformed

private void fishSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fishSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { fishNotesTextField};
    String [] dbFields = {"Fishes.Notes"}; // NOI18N  
    JComboBox [] jTFC = {fishNameComboBox };
    String [] dbFieldsC = {"Fishes.Name"}; // NOI18N   
    JTextField [] jTFn = { fishMaleQtyTextField, fishFemaleQtyTextField};
    String [] dbFieldsn = {"Fishes.Males_qty", "Fishes.Females_Qty"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFC, dbFieldsC);
    Fish.setFilter(filter);
    Fish.populateTable(fishTable);    
    if (Fish.getFilter().isEmpty()){
        fishSearchState.setBackground(Global.BUTTON_GREY);
        fishSearchState.setToolTipText("");
    } else {
        fishSearchState.setBackground(Global.BUTTON_RED);
        fishSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_fishSearchButtonActionPerformed

private void fishNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fishNoSearchButtonActionPerformed
    // Reset  search
    Fish.setFilter("");//NOI18N
    Fish.populateTable(fishTable);    
    fishSearchState.setBackground(Global.BUTTON_GREY);
    fishSearchState.setToolTipText("");
}//GEN-LAST:event_fishNoSearchButtonActionPerformed

    private void fishTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fishTableKeyReleased
        // Populate TextFields on tab6 (Fishes)
        refreshFields();
    }//GEN-LAST:event_fishTableKeyReleased

    private void fishNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fishNameComboBoxActionPerformed
        // refresh related image
        String specName = fishNameComboBox.getSelectedItem().toString();
        loadImage(specName); 
    }//GEN-LAST:event_fishNameComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JLabel fbImageLabelImg;
    private javax.swing.JButton fishClearButton;
    private javax.swing.JLabel fishDateLabel;
    private com.toedter.calendar.JDateChooser fishDateTextField;
    private javax.swing.JButton fishDeleteButton;
    private javax.swing.JLabel fishFemaleQtyLabel;
    private javax.swing.JTextField fishFemaleQtyTextField;
    private javax.swing.JLabel fishIdLabel;
    private javax.swing.JTextField fishIdTextField;
    private javax.swing.JLabel fishMaleQtyLabel;
    private javax.swing.JTextField fishMaleQtyTextField;
    protected static javax.swing.JComboBox fishNameComboBox;
    private javax.swing.JLabel fishNameLabel;
    private javax.swing.JButton fishNoSearchButton;
    private javax.swing.JLabel fishNotesLabel;
    private javax.swing.JTextField fishNotesTextField;
    private javax.swing.JButton fishReportButton;
    private javax.swing.JButton fishSaveButton;
    private javax.swing.JButton fishSearchButton;
    private javax.swing.JButton fishSearchState;
    private static javax.swing.JTable fishTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar7;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        fishMaleQtyTextField.addMouseListener(new ContextMenuMouseListener());
        fishFemaleQtyTextField.addMouseListener(new ContextMenuMouseListener());
        fishNotesTextField.addMouseListener(new ContextMenuMouseListener());
    }
}

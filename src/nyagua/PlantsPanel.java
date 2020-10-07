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
 * PlantsPanel.java
 *
 * Created on 8-giu-2012, 16.03.07
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Plant;
import nyagua.data.PlantBase;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class PlantsPanel extends javax.swing.JPanel {
    
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
            } else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                emptyCombo();
            } else if(e.getID()==Watched.REQUEST_POPULATE_LIST){
                populateCombo();
            } else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                refreshUnits();
            }             
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    /** Creates new form PlantsPanel */
    public PlantsPanel() {
        initComponents();
        initCutAndPaste(); 
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }
    
    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList7 = {plantsIdTextField, plantsQtyTextField,  
                    plantsIStatusTextField, plantsNotesTextField};
        Util.CleanTextFields(jtfList7);
        plantsDateTextField.setDate(null);
        if (plantsNameComboBox.getItemCount()>0){
            plantsNameComboBox.setSelectedIndex(0);
        }       
        fbImageLabelImg.setIcon(null);
        fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));   
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Plant.populateTable(plantsTable);
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
        plantsTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("plantstable", Plant.CAPTIONS.length);//NOI18N
        Plant.setColWidth(widths);
        Util.setColSizes(plantsTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("plantstable", plantsTable);//NOI18N
    }
    
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        plantsDateTextField.setDateFormatString(Global.dateFormat);
    }
    
    /**
     * populates the combo box
     */
    private void populateCombo(){
        PlantBase.populateCombo(plantsNameComboBox);
    }
    
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty lists
        plantsNameComboBox.setModel(dcm);
    }
    
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){
        int recId = TablesUtil.getIdFromTable(
                plantsTable, plantsTable.getSelectedRow());
        
        Plant spec=Plant.getById(recId);
        plantsIdTextField.setText(Integer.toString(spec.getId()));// NOI18N
        plantsDateTextField.setDate(spec.getDate());// NOI18N
        //plantsNameTextField.setText(spec.getName());// NOI18N
        plantsNameComboBox.setSelectedItem(spec.getName());
        plantsQtyTextField.setText(spec.getQuantity());// NOI18N
        plantsIStatusTextField.setText(spec.getInitialStatus());// NOI18N
        plantsNotesTextField.setText(spec.getNotes());// NOI18N
        
        loadImage(spec.getName());
    }
    
    private void loadImage(String specName) {
        
        boolean loadImage = false;
        if ((specName != null) && (!specName.isEmpty())) {
            loadImage = true;
        }
        
        fbImageLabelImg.setText(null);
        PlantBase specData = null;
        if (loadImage) {
            specData = PlantBase.getByName(specName);
        }
        if ((specData != null) &&  specData.hasImage()){
            Util.ImageDisplayResize(specData.getImage(), fbImageLabelImg, 300);
        } else {
            fbImageLabelImg.setText(java.util.ResourceBundle.getBundle(
                "nyagua/Bundle").getString("NO_IMAGE!"));
            
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

        plantsNotesLabel = new javax.swing.JLabel();
        plantsIdTextField = new javax.swing.JTextField();
        plantsDateLabel = new javax.swing.JLabel();
        plantsNotesTextField = new javax.swing.JTextField();
        plantsNameLabel = new javax.swing.JLabel();
        plantsIStatusTextField = new javax.swing.JTextField();
        plantsIStatusLabel = new javax.swing.JLabel();
        plantsQtyTextField = new javax.swing.JTextField();
        plantsQtyLabel = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        plantsTable = new javax.swing.JTable();
        plantsIdLabel = new javax.swing.JLabel();
        jToolBar8 = new javax.swing.JToolBar();
        plantsClearButton = new javax.swing.JButton();
        plantsSaveButton = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        plantsDeleteButton = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        plantsReportButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        plantsSearchButton = new javax.swing.JButton();
        plantsSearchState = new javax.swing.JButton();
        plantsNoSearchButton = new javax.swing.JButton();
        plantsDateTextField = new com.toedter.calendar.JDateChooser();
        plantsNameComboBox = new javax.swing.JComboBox();
        addItemButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fbImageLabelImg = new javax.swing.JLabel();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setPreferredSize(new java.awt.Dimension(1096, 871));
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        plantsNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsNotesLabel, gridBagConstraints);

        plantsIdTextField.setEditable(false);
        plantsIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        plantsIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsIdTextField, gridBagConstraints);

        plantsDateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsDateLabel, gridBagConstraints);

        plantsNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        plantsNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(plantsNotesTextField, gridBagConstraints);

        plantsNameLabel.setText(bundle.getString("Ny.plantsNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsNameLabel, gridBagConstraints);

        plantsIStatusTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        plantsIStatusTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsIStatusTextField, gridBagConstraints);

        plantsIStatusLabel.setText(bundle.getString("Ny.plantsIStatusLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsIStatusLabel, gridBagConstraints);

        plantsQtyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        plantsQtyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        plantsQtyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                plantsQtyTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsQtyTextField, gridBagConstraints);

        plantsQtyLabel.setText(bundle.getString("Ny.plantsQtyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsQtyLabel, gridBagConstraints);

        plantsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        plantsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        plantsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plantsTableMouseClicked(evt);
            }
        });
        plantsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                plantsTableKeyReleased(evt);
            }
        });
        jScrollPane10.setViewportView(plantsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 540;
        gridBagConstraints.ipady = 240;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane10, gridBagConstraints);

        plantsIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsIdLabel, gridBagConstraints);

        jToolBar8.setFloatable(false);
        jToolBar8.setRollover(true);

        plantsClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        plantsClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        plantsClearButton.setFocusable(false);
        plantsClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plantsClearButtonMouseClicked(evt);
            }
        });
        jToolBar8.add(plantsClearButton);

        plantsSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        plantsSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        plantsSaveButton.setFocusable(false);
        plantsSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plantsSaveButtonMouseClicked(evt);
            }
        });
        jToolBar8.add(plantsSaveButton);
        jToolBar8.add(jSeparator7);

        plantsDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        plantsDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        plantsDeleteButton.setFocusable(false);
        plantsDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plantsDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar8.add(plantsDeleteButton);
        jToolBar8.add(jSeparator12);

        plantsReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        plantsReportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        plantsReportButton.setFocusable(false);
        plantsReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsReportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plantsReportButtonMouseClicked(evt);
            }
        });
        jToolBar8.add(plantsReportButton);
        jToolBar8.add(jSeparator16);

        plantsSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        plantsSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        plantsSearchButton.setFocusable(false);
        plantsSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantsSearchButtonActionPerformed(evt);
            }
        });
        jToolBar8.add(plantsSearchButton);

        plantsSearchState.setFocusable(false);
        plantsSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        plantsSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        plantsSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        plantsSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar8.add(plantsSearchState);

        plantsNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        plantsNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        plantsNoSearchButton.setFocusable(false);
        plantsNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        plantsNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        plantsNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        plantsNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        plantsNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        plantsNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantsNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar8.add(plantsNoSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsDateTextField, gridBagConstraints);

        plantsNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantsNameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(plantsNameComboBox, gridBagConstraints);

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

private void plantsQtyTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_plantsQtyTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_plantsQtyTextFieldKeyTyped

private void plantsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plantsTableMouseClicked
    /**Populate TextFields on tab7 (Plants) */
    refreshFields();
}//GEN-LAST:event_plantsTableMouseClicked

private void plantsClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plantsClearButtonMouseClicked
    /** Cleans all textFields on tab7 (Plants)*/
    CleanAllFields();
}//GEN-LAST:event_plantsClearButtonMouseClicked

private void plantsSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plantsSaveButtonMouseClicked
    /**Insert record on db for table Plants or update it if existing*/
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    String currID = plantsIdTextField.getText();
    Plant spec=new Plant();
    if (currID == null || currID.equals("")) {
        spec.setId(0);
    } else {
        spec.setId(Integer.valueOf(currID));
    }
    if (LocUtil.isValidDate(plantsDateTextField.getDate())){
        if (plantsDateTextField.getDate() == null){
            spec.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
        }else {
            spec.setDate(LocUtil.delocalizeDate(plantsDateTextField.getDate()));
        }
    } else {
        Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));
        plantsDateTextField.requestFocus();
        return;
    }
   
    spec.setName(plantsNameComboBox.getSelectedItem().toString());
    spec.setQuantity(LocUtil.delocalizeDouble(plantsQtyTextField.getText()));
    spec.setInitialStatus(plantsIStatusTextField.getText());
    spec.setNotes(plantsNotesTextField.getText());
    spec.save(spec);
    Plant.populateTable(plantsTable);
    CleanAllFields();
}//GEN-LAST:event_plantsSaveButtonMouseClicked

private void plantsDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plantsDeleteButtonMouseClicked
    /** Delete selected record on tab7 (Plants)*/
    Plant.deleteById(plantsIdTextField.getText());
    Plant.populateTable(plantsTable);
    CleanAllFields();
}//GEN-LAST:event_plantsDeleteButtonMouseClicked

private void plantsReportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plantsReportButtonMouseClicked
    //Show plants data report in a browser
    String id;
    id = plantsIdTextField.getText();
    try {
        Report.PlantBaseReport(id,true);
    } catch (SQLException ex) {
        Logger.getLogger(PlantsPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_plantsReportButtonMouseClicked

private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
// Open Pbase panel  via internal bus    
    Watched nyMessages=Watched.getInstance();
    nyMessages.Update(Watched.MOVE_FOCUS_TO_PLANTSBASE);   
}//GEN-LAST:event_addItemButtonActionPerformed

private void plantsSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantsSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { plantsNotesTextField, plantsIStatusTextField};
    String [] dbFields = {"Plants.Notes","Plants.Init_Status"}; // NOI18N  
    JComboBox [] jTFC = {plantsNameComboBox };
    String [] dbFieldsC = {"Plants.Name"}; // NOI18N   
    JTextField [] jTFn = { plantsQtyTextField};
    String [] dbFieldsn = {"Plants.Qty"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFC, dbFieldsC);
    Plant.setFilter(filter);
    Plant.populateTable(plantsTable);    
    if (Plant.getFilter().isEmpty()){
        plantsSearchState.setBackground(Global.BUTTON_GREY);
        plantsSearchState.setToolTipText("");
    } else {
        plantsSearchState.setBackground(Global.BUTTON_RED);
        plantsSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_plantsSearchButtonActionPerformed

private void plantsNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantsNoSearchButtonActionPerformed
    // Reset  search
    Plant.setFilter("");//NOI18N
    Plant.populateTable(plantsTable);    
    plantsSearchState.setBackground(Global.BUTTON_GREY);
    plantsSearchState.setToolTipText("");
}//GEN-LAST:event_plantsNoSearchButtonActionPerformed

    private void plantsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_plantsTableKeyReleased
        /**Populate TextFields on tab7 (Plants) */
        refreshFields();
    }//GEN-LAST:event_plantsTableKeyReleased

    private void plantsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantsNameComboBoxActionPerformed
        // refresh related image
       String specName = plantsNameComboBox.getSelectedItem().toString();
       loadImage(specName);        
        
    }//GEN-LAST:event_plantsNameComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JLabel fbImageLabelImg;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JButton plantsClearButton;
    private javax.swing.JLabel plantsDateLabel;
    private com.toedter.calendar.JDateChooser plantsDateTextField;
    private javax.swing.JButton plantsDeleteButton;
    private javax.swing.JLabel plantsIStatusLabel;
    private javax.swing.JTextField plantsIStatusTextField;
    private javax.swing.JLabel plantsIdLabel;
    private javax.swing.JTextField plantsIdTextField;
    protected static javax.swing.JComboBox plantsNameComboBox;
    private javax.swing.JLabel plantsNameLabel;
    private javax.swing.JButton plantsNoSearchButton;
    private javax.swing.JLabel plantsNotesLabel;
    private javax.swing.JTextField plantsNotesTextField;
    private javax.swing.JLabel plantsQtyLabel;
    private javax.swing.JTextField plantsQtyTextField;
    private javax.swing.JButton plantsReportButton;
    private javax.swing.JButton plantsSaveButton;
    private javax.swing.JButton plantsSearchButton;
    private javax.swing.JButton plantsSearchState;
    private static javax.swing.JTable plantsTable;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        plantsQtyTextField.addMouseListener(new ContextMenuMouseListener());
        plantsIStatusTextField.addMouseListener(new ContextMenuMouseListener());
        plantsNotesTextField.addMouseListener(new ContextMenuMouseListener());
    }
}

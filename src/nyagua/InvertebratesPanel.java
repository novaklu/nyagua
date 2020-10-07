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
 * InvertebratesPanel.java
 *
 * Created on 13-giu-2012, 14.00.19
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
import nyagua.data.InvBase;
import nyagua.data.Invertebrates;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class InvertebratesPanel extends javax.swing.JPanel {
    
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
    
    /** Creates new form InvertsPanel */
    public InvertebratesPanel() {
        initComponents();
        initCutAndPaste();  
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }

    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList6 = {invertsIdTextField,  invertsMaleQtyTextField,
                     invertsFemaleQtyTextField, invertsNotesTextField};
        Util.CleanTextFields(jtfList6);
        invertsDateTextField.setDate(null);
        if (invertsNameComboBox.getItemCount()>0){
            invertsNameComboBox.setSelectedIndex(0);
        }       
        fbImageLabelImg.setIcon(null);
        fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));   
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Invertebrates.populateTable(invertsTable);
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
        invertsTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("invertstable", Invertebrates.CAPTIONS.length);//NOI18N
        Invertebrates.setColWidth(widths);
        Util.setColSizes(invertsTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("invertstable", invertsTable);//NOI18N
    }
   
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
//        Setting s=Setting.getInstance();
        invertsDateTextField.setDateFormatString(Global.dateFormat);
    }
    
    /**
     * populates the combo box
     */
    private void populateCombo(){
        InvBase.populateCombo(invertsNameComboBox);
    }
    
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty lists
        invertsNameComboBox.setModel(dcm);
    }
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){  
        int recId = TablesUtil.getIdFromTable(
                invertsTable, invertsTable.getSelectedRow());
        
        Invertebrates spec=Invertebrates.getById(recId);
        invertsIdTextField.setText(Integer.toString(spec.getId()));// NOI18N
        invertsDateTextField.setDate(spec.getDate());// NOI18N
        invertsNameComboBox.setSelectedItem(spec.getName());
        invertsMaleQtyTextField.setText(spec.getMales());// NOI18N
        invertsFemaleQtyTextField.setText(spec.getFemales());// NOI18N
        invertsNotesTextField.setText(spec.getNotes());// NOI18N
        
        loadImage(spec.getName());
    }
    
    private void loadImage(String specName) {
        
        boolean loadImage = false;
        if ((specName != null) && (!specName.isEmpty())) {
            loadImage = true;
        }
        
        fbImageLabelImg.setText(null);
        InvBase specData = null;
        if (loadImage) {
            specData = InvBase.getByName(specName);
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

        invertsMaleQtyLabel = new javax.swing.JLabel();
        invertsIdTextField = new javax.swing.JTextField();
        invertsDateLabel = new javax.swing.JLabel();
        invertsMaleQtyTextField = new javax.swing.JTextField();
        invertsNameLabel = new javax.swing.JLabel();
        invertsNotesTextField = new javax.swing.JTextField();
        invertsNotesLabel = new javax.swing.JLabel();
        invertsFemaleQtyTextField = new javax.swing.JTextField();
        invertsFemaleQtyLabel = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        invertsTable = new javax.swing.JTable();
        invertsIdLabel = new javax.swing.JLabel();
        invertsjToolBar = new javax.swing.JToolBar();
        invertsClearButton = new javax.swing.JButton();
        invertsSaveButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        invertsDeleteButton = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        invertsReportButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        invertsSearchButton = new javax.swing.JButton();
        invertsSearchState = new javax.swing.JButton();
        invertsNoSearchButton = new javax.swing.JButton();
        invertsDateTextField = new com.toedter.calendar.JDateChooser();
        invertsNameComboBox = new javax.swing.JComboBox();
        addItemButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fbImageLabelImg = new javax.swing.JLabel();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        invertsMaleQtyLabel.setText(bundle.getString("Ny.fishMaleQtyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsMaleQtyLabel, gridBagConstraints);

        invertsIdTextField.setEditable(false);
        invertsIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        invertsIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsIdTextField, gridBagConstraints);

        invertsDateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsDateLabel, gridBagConstraints);

        invertsMaleQtyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        invertsMaleQtyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        invertsMaleQtyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                invertsMaleQtyTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsMaleQtyTextField, gridBagConstraints);

        invertsNameLabel.setText(bundle.getString("Ny.fishNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsNameLabel, gridBagConstraints);

        invertsNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        invertsNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsNotesTextField, gridBagConstraints);

        invertsNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsNotesLabel, gridBagConstraints);

        invertsFemaleQtyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        invertsFemaleQtyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        invertsFemaleQtyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                invertsFemaleQtyTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsFemaleQtyTextField, gridBagConstraints);

        invertsFemaleQtyLabel.setText(bundle.getString("Ny.fishFemaleQtyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsFemaleQtyLabel, gridBagConstraints);

        invertsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        invertsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        invertsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invertsTableMouseClicked(evt);
            }
        });
        invertsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                invertsTableKeyReleased(evt);
            }
        });
        jScrollPane8.setViewportView(invertsTable);

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

        invertsIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsIdLabel, gridBagConstraints);

        invertsjToolBar.setFloatable(false);
        invertsjToolBar.setRollover(true);

        invertsClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        invertsClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        invertsClearButton.setFocusable(false);
        invertsClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invertsClearButtonMouseClicked(evt);
            }
        });
        invertsjToolBar.add(invertsClearButton);

        invertsSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        invertsSaveButton.setToolTipText(bundle.getString("Ny.fishSaveButton.toolTipText")); // NOI18N
        invertsSaveButton.setFocusable(false);
        invertsSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invertsSaveButtonMouseClicked(evt);
            }
        });
        invertsjToolBar.add(invertsSaveButton);
        invertsjToolBar.add(jSeparator6);

        invertsDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        invertsDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        invertsDeleteButton.setFocusable(false);
        invertsDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invertsDeleteButtonMouseClicked(evt);
            }
        });
        invertsjToolBar.add(invertsDeleteButton);
        invertsjToolBar.add(jSeparator13);

        invertsReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        invertsReportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        invertsReportButton.setFocusable(false);
        invertsReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsReportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invertsReportButtonMouseClicked(evt);
            }
        });
        invertsjToolBar.add(invertsReportButton);
        invertsjToolBar.add(jSeparator16);

        invertsSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        invertsSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        invertsSearchButton.setFocusable(false);
        invertsSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertsSearchButtonActionPerformed(evt);
            }
        });
        invertsjToolBar.add(invertsSearchButton);

        invertsSearchState.setFocusable(false);
        invertsSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        invertsSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        invertsSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        invertsSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsjToolBar.add(invertsSearchState);

        invertsNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        invertsNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        invertsNoSearchButton.setFocusable(false);
        invertsNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invertsNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        invertsNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        invertsNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        invertsNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invertsNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertsNoSearchButtonActionPerformed(evt);
            }
        });
        invertsjToolBar.add(invertsNoSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 250;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(invertsjToolBar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(invertsDateTextField, gridBagConstraints);

        invertsNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertsNameComboBoxActionPerformed(evt);
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
        add(invertsNameComboBox, gridBagConstraints);

        addItemButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/plus.png"))); // NOI18N
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

private void invertsMaleQtyTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invertsMaleQtyTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_invertsMaleQtyTextFieldKeyTyped

private void invertsFemaleQtyTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invertsFemaleQtyTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_invertsFemaleQtyTextFieldKeyTyped

private void invertsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invertsTableMouseClicked
    /**Populate TextFields on tab (Invertebrates) */
    refreshFields();
}//GEN-LAST:event_invertsTableMouseClicked

private void invertsClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invertsClearButtonMouseClicked
    /** Cleans all textFields on tab (Invertebrates)*/
    CleanAllFields();
}//GEN-LAST:event_invertsClearButtonMouseClicked

private void invertsSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invertsSaveButtonMouseClicked
    /**Insert record on db for table Invertebrates or update it if existing*/
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    String currID = invertsIdTextField.getText();

    Invertebrates spec=new Invertebrates();
    if (currID == null || currID.equals("")) {
        spec.setId(0);
        } else {
        spec.setId(Integer.valueOf(currID));
    }
    if (LocUtil.isValidDate(invertsDateTextField.getDate())){
        if (invertsDateTextField.getDate() == null){
            spec.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
        }else {
            spec.setDate(LocUtil.delocalizeDate(invertsDateTextField.getDate()));
        }
    } else {
        Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));
        invertsDateTextField.requestFocus();
        return;
    }
       
    spec.setName(invertsNameComboBox.getSelectedItem().toString());
    spec.setMales(LocUtil.delocalizeDouble(invertsMaleQtyTextField.getText()));
    spec.setFemales(LocUtil.delocalizeDouble(invertsFemaleQtyTextField.getText()));
    spec.setNotes(invertsNotesTextField.getText());
    spec.save(spec);
    Invertebrates.populateTable(invertsTable);
    CleanAllFields();
}//GEN-LAST:event_invertsSaveButtonMouseClicked

private void invertsDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invertsDeleteButtonMouseClicked
    /** Delete selected record on tab (Invertebrates)*/
    Invertebrates.deleteById(invertsIdTextField.getText());
    Invertebrates.populateTable(invertsTable);
    CleanAllFields();
}//GEN-LAST:event_invertsDeleteButtonMouseClicked

private void invertsReportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invertsReportButtonMouseClicked
    //Show Invertebrates data report in a browser
    String id= invertsIdTextField.getText();
    try {
        Report.InvertsBaseReport(id,true);
    } catch (SQLException ex) {
        Logger.getLogger(InvertebratesPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_invertsReportButtonMouseClicked

private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
// Open Ibase panel via internal bus   
    Watched nyMessages=Watched.getInstance();
    nyMessages.Update(Watched.MOVE_FOCUS_TO_INVBASE);    
}//GEN-LAST:event_addItemButtonActionPerformed

private void invertsSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertsSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { invertsNotesTextField};
    String [] dbFields = {"Inverts.Notes"}; // NOI18N  
    JComboBox [] jTFC = {invertsNameComboBox };
    String [] dbFieldsC = {"Inverts.Name"}; // NOI18N   
    JTextField [] jTFn = { invertsMaleQtyTextField, invertsFemaleQtyTextField};
    String [] dbFieldsn = {"Inverts.Males_qty", "Inverts.Females_Qty"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFC, dbFieldsC);
    Invertebrates.setFilter(filter);
    Invertebrates.populateTable(invertsTable);    
    if (Invertebrates.getFilter().isEmpty()){
        invertsSearchState.setBackground(Global.BUTTON_GREY);
        invertsSearchState.setToolTipText("");
    } else {
        invertsSearchState.setBackground(Global.BUTTON_RED);
        invertsSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_invertsSearchButtonActionPerformed

private void invertsNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertsNoSearchButtonActionPerformed
    // Reset  search
    Invertebrates.setFilter("");//NOI18N
    Invertebrates.populateTable(invertsTable);    
    invertsSearchState.setBackground(Global.BUTTON_GREY);
    invertsSearchState.setToolTipText("");
}//GEN-LAST:event_invertsNoSearchButtonActionPerformed

    private void invertsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invertsTableKeyReleased
        /**Populate TextFields on tab (inverts) */
        refreshFields();
    }//GEN-LAST:event_invertsTableKeyReleased

    private void invertsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertsNameComboBoxActionPerformed
         // refresh related image
        String specName = invertsNameComboBox.getSelectedItem().toString();
        loadImage(specName); 
    }//GEN-LAST:event_invertsNameComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JLabel fbImageLabelImg;
    private javax.swing.JButton invertsClearButton;
    private javax.swing.JLabel invertsDateLabel;
    private com.toedter.calendar.JDateChooser invertsDateTextField;
    private javax.swing.JButton invertsDeleteButton;
    private javax.swing.JLabel invertsFemaleQtyLabel;
    private javax.swing.JTextField invertsFemaleQtyTextField;
    private javax.swing.JLabel invertsIdLabel;
    private javax.swing.JTextField invertsIdTextField;
    private javax.swing.JLabel invertsMaleQtyLabel;
    private javax.swing.JTextField invertsMaleQtyTextField;
    protected static javax.swing.JComboBox invertsNameComboBox;
    private javax.swing.JLabel invertsNameLabel;
    private javax.swing.JButton invertsNoSearchButton;
    private javax.swing.JLabel invertsNotesLabel;
    private javax.swing.JTextField invertsNotesTextField;
    private javax.swing.JButton invertsReportButton;
    private javax.swing.JButton invertsSaveButton;
    private javax.swing.JButton invertsSearchButton;
    private javax.swing.JButton invertsSearchState;
    private static javax.swing.JTable invertsTable;
    private javax.swing.JToolBar invertsjToolBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator6;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        invertsMaleQtyTextField.addMouseListener(new ContextMenuMouseListener());
        invertsFemaleQtyTextField.addMouseListener(new ContextMenuMouseListener());
        invertsNotesTextField.addMouseListener(new ContextMenuMouseListener());
    }
}

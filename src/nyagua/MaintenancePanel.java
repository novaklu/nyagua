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
 * MaintenancePanel.java
 *
 * Created on 15-giu-2012, 13.54.52
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Maintenance;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class MaintenancePanel extends javax.swing.JPanel {
    
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
            } else if(e.getID()==Watched.ADDED_MAINTENANCE_EVENT){
                populateTable();
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            } else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                emptyCombo();
            } else if(e.getID()==Watched.REQUEST_POPULATE_LIST){
                populateList();
            } else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                refreshUnits();
            } 
            else if(e.getID()==Watched.CHANGED_PRESETS_SETTINGS) {
                refreshPresetsOption();
                populateList();
            }         
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    
    /** Creates new form MaintenancePanel */
    public MaintenancePanel() {  
        refreshPresetsOption();
        initComponents();
        initCutAndPaste(); 
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }

    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList3 = {maintIdTextField,  maintTimeTextField,
                    maintUnitsTextField, maintNotesTextField, maintWarningTextField};
        Util.CleanTextFields(jtfList3);
        maintComboBox.setSelectedItem("");
        maintDateTextField.setDate(null);
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Maintenance.populateTable(maintTable);
    }
    
    /**
     * Empties a jTable assigning null model
     * 
     */
    private static void emptyTable (){
        DefaultTableModel dm = new DefaultTableModel();
        String tableData[][] = {{null}};
        String[] nameHeader = {bundle.getString("NO_SELECTION")};
        dm.setDataVector(tableData, nameHeader);
        maintTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("mainttable", Maintenance.CAPTIONS.length);//NOI18N
        Maintenance.setColWidth(widths);
        Util.setColSizes(maintTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("mainttable", maintTable);//NOI18N
    }
    
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        maintDateTextField.setDateFormatString(Global.dateFormat);
    }
    
    /*
    * Enable Presets Refresh 
    */
    static void refreshPresetsOption() {
         isPresetsEnabled =  Setting.getInstance().isMaintenancePresetsEnabled();
    }
    
    /*
    * Refresh or add preset options on combo
    */
    private List<String> getPresets() {
        
        if (!isPresetsEnabled) {
            return null;
        }
        
        String currents = DEFAULT_MAINTENANCE_PRESETS;
        List<String> result = Arrays.asList(currents.split(","));
        Collections.sort(result);
        return result;
    }
    
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty lists
        maintComboBox.setModel(dcm);
    }
    
    /**
     * Populate selected list with a field from a table
     *
     */
    private void populateList() {          
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        Maintenance.populateCombo(maintComboBox,getPresets());        
    }
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){  
        int recId = TablesUtil.getIdFromTable(
                maintTable, maintTable.getSelectedRow());
        
        Maintenance event=Maintenance.getById(recId);
        maintIdTextField.setText(Integer.toString(event.getId()));// NOI18N
        maintDateTextField.setDate(event.getDate());// NOI18N
        maintTimeTextField.setText(event.getTime());// NOI18N
        maintUnitsTextField.setText(event.getUnits());// NOI18N
        maintNotesTextField.setText(event.getNotes());// NOI18N
        maintWarningTextField.setText(event.getWarnings());// NOI18N

        Maintenance.populateCombo(maintComboBox,getPresets());
        maintComboBox.setSelectedItem(event.getEvent());
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

        maintTimeLabel = new javax.swing.JLabel();
        maintIdTextField = new javax.swing.JTextField();
        maintDateLabel = new javax.swing.JLabel();
        maintTimeTextField = new javax.swing.JTextField();
        maintEventLabel = new javax.swing.JLabel();
        maintNotesTextField = new javax.swing.JTextField();
        maintNotesLabel = new javax.swing.JLabel();
        maintUnitsTextField = new javax.swing.JTextField();
        maintUnitsLabel = new javax.swing.JLabel();
        maintWarningLabel = new javax.swing.JLabel();
        maintWarningTextField = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        maintTable = new javax.swing.JTable();
        maintIdLabel = new javax.swing.JLabel();
        jToolBar4 = new javax.swing.JToolBar();
        maintClearButton = new javax.swing.JButton();
        maintSaveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        maintDeleteButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        maintSearchButton = new javax.swing.JButton();
        maintSearchState = new javax.swing.JButton();
        maintNoSearchButton = new javax.swing.JButton();
        maintDateTextField = new com.toedter.calendar.JDateChooser();
        maintComboBox = new javax.swing.JComboBox();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        maintTimeLabel.setText(bundle.getString("TIME_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintTimeLabel, gridBagConstraints);

        maintIdTextField.setEditable(false);
        maintIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        maintIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintIdTextField, gridBagConstraints);

        maintDateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintDateLabel, gridBagConstraints);

        maintTimeTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        maintTimeTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintTimeTextField, gridBagConstraints);

        maintEventLabel.setText(bundle.getString("Ny.maintEventLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintEventLabel, gridBagConstraints);

        maintNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        maintNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(maintNotesTextField, gridBagConstraints);

        maintNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintNotesLabel, gridBagConstraints);

        maintUnitsTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        maintUnitsTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintUnitsTextField, gridBagConstraints);

        maintUnitsLabel.setText(bundle.getString("Ny.maintUnitsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintUnitsLabel, gridBagConstraints);

        maintWarningLabel.setText(bundle.getString("Ny.maintWarningLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintWarningLabel, gridBagConstraints);

        maintWarningTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        maintWarningTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 295;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(maintWarningTextField, gridBagConstraints);

        maintTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        maintTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        maintTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintTableMouseClicked(evt);
            }
        });
        maintTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                maintTableKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(maintTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 613;
        gridBagConstraints.ipady = 277;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane4, gridBagConstraints);

        maintIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintIdLabel, gridBagConstraints);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        maintClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        maintClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        maintClearButton.setFocusable(false);
        maintClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        maintClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintClearButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(maintClearButton);

        maintSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        maintSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        maintSaveButton.setFocusable(false);
        maintSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        maintSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintSaveButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(maintSaveButton);
        jToolBar4.add(jSeparator1);

        maintDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        maintDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        maintDeleteButton.setFocusable(false);
        maintDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        maintDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(maintDeleteButton);
        jToolBar4.add(jSeparator16);

        maintSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        maintSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        maintSearchButton.setFocusable(false);
        maintSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        maintSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maintSearchButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(maintSearchButton);

        maintSearchState.setFocusable(false);
        maintSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        maintSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        maintSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        maintSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(maintSearchState);

        maintNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        maintNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        maintNoSearchButton.setFocusable(false);
        maintNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maintNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        maintNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        maintNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        maintNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        maintNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maintNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(maintNoSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 513;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintDateTextField, gridBagConstraints);

        maintComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(maintComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void maintTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintTableMouseClicked
    /**Populate TextFields on tab (maintenance) */
    refreshFields();
}//GEN-LAST:event_maintTableMouseClicked

private void maintClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintClearButtonMouseClicked
    /** Cleans all textFields on tab (Maintenance)*/
    CleanAllFields();
}//GEN-LAST:event_maintClearButtonMouseClicked

private void maintSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintSaveButtonMouseClicked
    /**Insert record on db for table maintenance or update it if existing*/
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    String currID = maintIdTextField.getText();
    Maintenance event=new Maintenance();
    if (currID == null || currID.equals("")) {
        event.setId(0);
    } else {
        event.setId(Integer.valueOf(currID));
    }
    if (LocUtil.isValidDate(maintDateTextField.getDate())){
        if (maintDateTextField.getDate() == null){
            event.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
        }else {
            event.setDate(LocUtil.delocalizeDate(maintDateTextField.getDate()));
        }
        
    } else {
        Util.showErrorMsg(bundle.getString("INVALID_DATE."));
        maintDateTextField.requestFocus();
        return;
    }
    event.setTime(maintTimeTextField.getText());
    event.setUnits(maintUnitsTextField.getText());
    event.setNotes(maintNotesTextField.getText());
    event.setWarnings(maintWarningTextField.getText());
    event.setEvent(maintComboBox.getSelectedItem().toString());    
    event.save(event,Global.AqID);    
    
    Maintenance.populateCombo(maintComboBox,getPresets());
    Maintenance.populateTable(maintTable);
    CleanAllFields();
}//GEN-LAST:event_maintSaveButtonMouseClicked

private void maintDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintDeleteButtonMouseClicked
    /** Delete seete record on tab (maintenance)*/
    Maintenance.deleteById(maintIdTextField.getText());
    Maintenance.populateTable(maintTable);
    populateList();
    CleanAllFields();
}//GEN-LAST:event_maintDeleteButtonMouseClicked

private void maintSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maintSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { maintTimeTextField,maintNotesTextField,
    maintWarningTextField,maintUnitsTextField};
    String [] dbFields = {"Time","Notes",  "Warnings", "Units"}; // NOI18N  
    JComboBox [] jTFC = {maintComboBox };
    String [] dbFieldsC = {"Event"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createFilter(jTFC, dbFieldsC);
    Maintenance.setFilter(filter);
    Maintenance.populateTable(maintTable);    
    if (Maintenance.getFilter().isEmpty()){
        maintSearchState.setBackground(Global.BUTTON_GREY);
        maintSearchState.setToolTipText("");
    } else {
        maintSearchState.setBackground(Global.BUTTON_RED);
        maintSearchState.setToolTipText(bundle.getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_maintSearchButtonActionPerformed

private void maintNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maintNoSearchButtonActionPerformed
    // Reset  search
    Maintenance.setFilter("");//NOI18N
    Maintenance.populateTable(maintTable);    
    maintSearchState.setBackground(Global.BUTTON_GREY);
    maintSearchState.setToolTipText("");
}//GEN-LAST:event_maintNoSearchButtonActionPerformed

    private void maintTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maintTableKeyReleased
        /**Populate TextFields on tab (maintenance) */
        refreshFields();
    }//GEN-LAST:event_maintTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JButton maintClearButton;
    private javax.swing.JComboBox maintComboBox;
    private javax.swing.JLabel maintDateLabel;
    private com.toedter.calendar.JDateChooser maintDateTextField;
    private javax.swing.JButton maintDeleteButton;
    private javax.swing.JLabel maintEventLabel;
    private javax.swing.JLabel maintIdLabel;
    private javax.swing.JTextField maintIdTextField;
    private javax.swing.JButton maintNoSearchButton;
    private javax.swing.JLabel maintNotesLabel;
    private javax.swing.JTextField maintNotesTextField;
    private javax.swing.JButton maintSaveButton;
    private javax.swing.JButton maintSearchButton;
    private javax.swing.JButton maintSearchState;
    private static javax.swing.JTable maintTable;
    private javax.swing.JLabel maintTimeLabel;
    private javax.swing.JTextField maintTimeTextField;
    private javax.swing.JLabel maintUnitsLabel;
    private javax.swing.JTextField maintUnitsTextField;
    private javax.swing.JLabel maintWarningLabel;
    private javax.swing.JTextField maintWarningTextField;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        maintTimeTextField.addMouseListener(new ContextMenuMouseListener());
        maintUnitsTextField.addMouseListener(new ContextMenuMouseListener());
        maintNotesTextField.addMouseListener(new ContextMenuMouseListener());
        maintWarningTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
     static ResourceBundle bundle = ResourceBundle.getBundle("nyagua/Bundle");
    
    private static boolean isPresetsEnabled;
    
    //TODO DEFAULTS
    //Translated defaults for expenses:
    public static final String WHATER_CHANGE =  bundle.getString("Water_change");// NOI18N    
    public static final String MAINTENANCE =  bundle.getString("MAINTENANCE");// NOI18N  
    public static final String ADDED_SPECIE =  bundle.getString("Added_specie");// NOI18N        
    public static final String REMOVED_ITEM =  bundle.getString("Removed_item");// NOI18N     
    public static final String FERTILIZATION =  bundle.getString("Fertilization");// NOI18N 
    
    public static final String DEFAULT_MAINTENANCE_PRESETS = 
        WHATER_CHANGE  + "," +  MAINTENANCE + "," +  ADDED_SPECIE + "," +  REMOVED_ITEM  + // NOI18N
        "," + FERTILIZATION ; // NOI18N
}

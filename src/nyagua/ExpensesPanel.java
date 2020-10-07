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
 * ExpensesPanel.java
 *
 * Created on 15-giu-2012, 13.29.38
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Expense;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class ExpensesPanel extends javax.swing.JPanel {
    
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
            } 
            else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS) {
                cleanAllFields();
            } 
            else if(e.getID()==Watched.REQUEST_CLEAR_LIST) {
                emptyCombo();
            } 
            else if(e.getID()==Watched.REQUEST_POPULATE_LIST) {
                populateList();
                 displayTotal();
            } 
            else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS) {
                refreshUnits();
            } 
            else if(e.getID()==Watched.CHANGED_PRESETS_SETTINGS) {
                refreshPresetsOption();
                populateList();
            }            
        }
    };            
    Watcher settingWatch=new Watcher(al);
     
    /** Creates new form ExpensesPanel */
    public ExpensesPanel() {   
        refreshPresetsOption();
        initComponents(); 
        initCutAndPaste();  
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);      
        
    }
        
    /**
     * Cleans all fields
     */
    private void cleanAllFields () {        
        JTextField[] jtfList4 = {expensesIdTextField,  expensesItemTextField,
            expensesPriceTextField, expensesNotesTextField};
        Util.CleanTextFields(jtfList4);
        expensesShopComboBox.setSelectedItem("");
        typeComboBox.setSelectedItem("");
        expensesDateTextField.setDate(null);
         displayTotal();
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Expense.populateTable(expensesTable);
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
        expensesTable.setModel(dm);        
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("expensestable", Expense.CAPTIONS.length);//NOI18N
        Expense.setColWidth(widths);
        Util.setColSizes(expensesTable,widths );        
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("expensestable", expensesTable);//NOI18N
    }
    
    /*
    * enable Presets Refresh
    */
    static void refreshPresetsOption() {
         isPresetsEnabled =  Setting.getInstance().isExpensesPresetsEnabled();
    }
    
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        expensesDateTextField.setDateFormatString(Global.dateFormat);
    }
    
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty lists
        typeComboBox.setModel(dcm);
    }
    
    /**
     * Populate selected list with a field from a table
     *
     */
    private void populateList() {          
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        Expense.populateCombo(typeComboBox,Expense.COMBO_TYPE, getPresets()); 
        Expense.populateCombo(expensesShopComboBox,Expense.COMBO_SHOP, null);
    }

    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){        
        int recId = TablesUtil.getIdFromTable(
                expensesTable, expensesTable.getSelectedRow());
        Expense exp=Expense.getById(recId);
        expensesIdTextField.setText(Integer.toString(exp.getId()));// NOI18N
        expensesDateTextField.setDate(exp.getDate());// NOI18N
        expensesItemTextField.setText(exp.getItem());// NOI18N
        expensesPriceTextField.setText(exp.getPrice());// NOI18N
        expensesNotesTextField.setText(exp.getNotes());// NOI18N
        Expense.populateCombo(typeComboBox,Expense.COMBO_SHOP, null);
        expensesShopComboBox.setSelectedItem(exp.getShop());
        Expense.populateCombo(typeComboBox,Expense.COMBO_TYPE, getPresets());    
        typeComboBox.setSelectedItem(exp.getType());

        displayTotal();
    }
    
    /*
    * Refresh or add preset options on combo
    */
    private List<String> getPresets() {
        
        if (!isPresetsEnabled) {
            return null;
        }
        
        String currents = DEFAULT_EXPENSES_PRESETS;
        List<String> result = Arrays.asList(currents.split(","));
        Collections.sort(result);
        return result;
    }
    
    private void displayTotal() {
        expensesTotalTextField.setText(Expense.getTotalExpense());
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

        expensesIdTextField = new javax.swing.JTextField();
        expensesDateLabel = new javax.swing.JLabel();
        expensesItemTextField = new javax.swing.JTextField();
        expensesItemLabel = new javax.swing.JLabel();
        expensesNotesTextField = new javax.swing.JTextField();
        expensesNotesLabel = new javax.swing.JLabel();
        expensesPriceTextField = new javax.swing.JTextField();
        expensesPriceLabel = new javax.swing.JLabel();
        expensesShopLabel = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        expensesTable = new javax.swing.JTable();
        expensesIdLabel = new javax.swing.JLabel();
        jToolBar5 = new javax.swing.JToolBar();
        expensesClearButton = new javax.swing.JButton();
        expensesSaveButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        expensesDeleteButton = new javax.swing.JButton();
        jSeparator15 = new javax.swing.JToolBar.Separator();
        expensesReportButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        expensesSearchButton = new javax.swing.JButton();
        expensesSearchState = new javax.swing.JButton();
        expensesNoSearchButton = new javax.swing.JButton();
        expensesTotalLabel = new javax.swing.JLabel();
        expensesTotalTextField = new javax.swing.JTextField();
        expensesDateTextField = new com.toedter.calendar.JDateChooser();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        expensesShopComboBox = new javax.swing.JComboBox();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        expensesIdTextField.setEditable(false);
        expensesIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        expensesIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 29;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesIdTextField, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        expensesDateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesDateLabel, gridBagConstraints);

        expensesItemTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        expensesItemTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.ipadx = 319;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesItemTextField, gridBagConstraints);

        expensesItemLabel.setText(bundle.getString("Ny.expensesItemLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesItemLabel, gridBagConstraints);

        expensesNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        expensesNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 464;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(expensesNotesTextField, gridBagConstraints);

        expensesNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesNotesLabel, gridBagConstraints);

        expensesPriceTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        expensesPriceTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        expensesPriceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                expensesPriceTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesPriceTextField, gridBagConstraints);

        expensesPriceLabel.setText(bundle.getString("Ny.expensesPriceLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesPriceLabel, gridBagConstraints);

        expensesShopLabel.setText(bundle.getString("Ny.expensesShopLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesShopLabel, gridBagConstraints);

        expensesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        expensesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        expensesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesTableMouseClicked(evt);
            }
        });
        expensesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                expensesTableKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(expensesTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 613;
        gridBagConstraints.ipady = 240;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane6, gridBagConstraints);

        expensesIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesIdLabel, gridBagConstraints);

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);

        expensesClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        expensesClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        expensesClearButton.setFocusable(false);
        expensesClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesClearButtonMouseClicked(evt);
            }
        });
        jToolBar5.add(expensesClearButton);

        expensesSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        expensesSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        expensesSaveButton.setFocusable(false);
        expensesSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesSaveButtonMouseClicked(evt);
            }
        });
        jToolBar5.add(expensesSaveButton);
        jToolBar5.add(jSeparator4);

        expensesDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        expensesDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        expensesDeleteButton.setFocusable(false);
        expensesDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar5.add(expensesDeleteButton);
        jToolBar5.add(jSeparator15);

        expensesReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        expensesReportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        expensesReportButton.setFocusable(false);
        expensesReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesReportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                expensesReportButtonMouseClicked(evt);
            }
        });
        jToolBar5.add(expensesReportButton);
        jToolBar5.add(jSeparator16);

        expensesSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        expensesSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        expensesSearchButton.setFocusable(false);
        expensesSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensesSearchButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(expensesSearchButton);

        expensesSearchState.setFocusable(false);
        expensesSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        expensesSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        expensesSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        expensesSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(expensesSearchState);

        expensesNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        expensesNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        expensesNoSearchButton.setFocusable(false);
        expensesNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expensesNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        expensesNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        expensesNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        expensesNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expensesNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensesNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(expensesNoSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 459;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar5, gridBagConstraints);

        expensesTotalLabel.setText(bundle.getString("Ny.expensesTotalLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesTotalLabel, gridBagConstraints);

        expensesTotalTextField.setEditable(false);
        expensesTotalTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        expensesTotalTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(expensesTotalTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 95;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesDateTextField, gridBagConstraints);

        typeLabel.setText(bundle.getString("TYPE")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(typeLabel, gridBagConstraints);

        typeComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(typeComboBox, gridBagConstraints);

        expensesShopComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expensesShopComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void expensesPriceTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_expensesPriceTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_expensesPriceTextFieldKeyTyped

private void expensesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesTableMouseClicked
    /**Populate TextFields on tab4 (expenses) */
    refreshFields();
}//GEN-LAST:event_expensesTableMouseClicked

private void expensesClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesClearButtonMouseClicked
    /** Cleans all textFields on tab4 (Expenses)*/
    cleanAllFields();
}//GEN-LAST:event_expensesClearButtonMouseClicked

private void expensesSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesSaveButtonMouseClicked
    /**Insert record on db for table expenses or update it if existing*/
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    String currID = expensesIdTextField.getText();

    Expense exp=new Expense();
    if (currID == null || currID.equals("")) {
        exp.setId(0);
        } else {
        exp.setId(Integer.valueOf(currID));
    }
    if (LocUtil.isValidDate(expensesDateTextField.getDate())){
        exp.setDate(LocUtil.delocalizeDate(expensesDateTextField.getDate()));
    } else {
        Util.showErrorMsg(bundle.getString("INVALID_DATE."));
        expensesDateTextField.requestFocus();
        return;
    }  
    
    exp.setItem(expensesItemTextField.getText());
    exp.setPrice(LocUtil.delocalizeCurrency(expensesPriceTextField.getText()));
    exp.setNotes(expensesNotesTextField.getText());
    //exp.setShop(expensesShopTextField.getText());
    exp.setShop(expensesShopComboBox.getSelectedItem().toString());
    exp.setType(typeComboBox.getSelectedItem().toString());
    exp.save(exp);
    Expense.populateCombo(expensesShopComboBox,Expense.COMBO_SHOP,null);
    Expense.populateCombo(typeComboBox,Expense.COMBO_TYPE, getPresets());
    Expense.populateTable(expensesTable);
    cleanAllFields();
}//GEN-LAST:event_expensesSaveButtonMouseClicked

private void expensesDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesDeleteButtonMouseClicked
    /** Delete selected record on tab4 (expenses)*/
    Expense.deleteById(expensesIdTextField.getText());
    Expense.populateTable(expensesTable);
    populateList();
    cleanAllFields();
}//GEN-LAST:event_expensesDeleteButtonMouseClicked

private void expensesReportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_expensesReportButtonMouseClicked
//Show expenses  report in a browser
    try {
        Report.ExpensesReport(true);

    } catch (ClassNotFoundException | SQLException ex) {
        Logger.getLogger(ExpensesPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_expensesReportButtonMouseClicked

private void expensesNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensesNoSearchButtonActionPerformed
    // Reset  search
    Expense.setFilter("");//NOI18N
    Expense.populateTable(expensesTable);    
    expensesSearchState.setBackground(Global.BUTTON_GREY);
    expensesSearchState.setToolTipText("");
}//GEN-LAST:event_expensesNoSearchButtonActionPerformed

private void expensesSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensesSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { expensesItemTextField,expensesNotesTextField};
    String [] dbFields = {"Expenses.Item", "Expenses.Notes"}; // NOI18N      
    JComboBox [] jTFC = {typeComboBox,expensesShopComboBox };
    String [] dbFieldsC = {"Expenses.Type", "Expenses.Shop"}; // NOI18N   
    JTextField [] jTFn = { expensesPriceTextField};
    String [] dbFieldsn = {"Expenses.Price"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFC, dbFieldsC);
    Expense.setFilter(filter);
    Expense.populateTable(expensesTable);    
    if (Expense.getFilter().isEmpty()){
        expensesSearchState.setBackground(Global.BUTTON_GREY);
        expensesSearchState.setToolTipText("");
    } else {
        expensesSearchState.setBackground(Global.BUTTON_RED);
        expensesSearchState.setToolTipText(bundle.getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_expensesSearchButtonActionPerformed

    private void expensesTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_expensesTableKeyReleased
        /**Populate TextFields on tab4 (expenses) */
        refreshFields();
    }//GEN-LAST:event_expensesTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expensesClearButton;
    private javax.swing.JLabel expensesDateLabel;
    private com.toedter.calendar.JDateChooser expensesDateTextField;
    private javax.swing.JButton expensesDeleteButton;
    private javax.swing.JLabel expensesIdLabel;
    private javax.swing.JTextField expensesIdTextField;
    private javax.swing.JLabel expensesItemLabel;
    private javax.swing.JTextField expensesItemTextField;
    private javax.swing.JButton expensesNoSearchButton;
    private javax.swing.JLabel expensesNotesLabel;
    private javax.swing.JTextField expensesNotesTextField;
    private javax.swing.JLabel expensesPriceLabel;
    private javax.swing.JTextField expensesPriceTextField;
    private javax.swing.JButton expensesReportButton;
    private javax.swing.JButton expensesSaveButton;
    private javax.swing.JButton expensesSearchButton;
    private javax.swing.JButton expensesSearchState;
    private javax.swing.JComboBox expensesShopComboBox;
    private javax.swing.JLabel expensesShopLabel;
    private static javax.swing.JTable expensesTable;
    private javax.swing.JLabel expensesTotalLabel;
    private javax.swing.JTextField expensesTotalTextField;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        expensesPriceTextField.addMouseListener(new ContextMenuMouseListener());
        expensesItemTextField.addMouseListener(new ContextMenuMouseListener());
        expensesNotesTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
    static ResourceBundle bundle = ResourceBundle.getBundle("nyagua/Bundle");
    
    private static boolean isPresetsEnabled;
    
    //Translated defaults for expenses:
    public static final String EQUIPMENT =  bundle.getString("Equipment");// NOI18N    
    public static final String FOOD =  bundle.getString("Food");// NOI18N     
    public static final String MAINTENANCE =  bundle.getString("MAINTENANCE");// NOI18N     
    public static final String SPARE_PARTS =  bundle.getString("Spare_Parts");// NOI18N     
    public static final String CHEMISTRY =  bundle.getString("Chemistry");// NOI18N     
    public static final String FISH =  bundle.getString("FISH");// NOI18N     
    public static final String INVERTEBRATES =  bundle.getString("INVERTS_");// NOI18N     
    public static final String PLANTS = bundle.getString("PLANTS");// NOI18N 
    
    public static final String DEFAULT_EXPENSES_PRESETS = 
        EQUIPMENT + "," +  FOOD  + "," +  MAINTENANCE + "," +  SPARE_PARTS  + // NOI18N
        "," + CHEMISTRY + "," +  FISH  + "," +  INVERTEBRATES + "," +  PLANTS; // NOI18N
}
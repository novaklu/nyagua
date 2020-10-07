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

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import nyagua.data.Reading;
import nyagua.data.Setting;

/**
 *
 * @author rudi
 */
public class ReadingsPanel extends javax.swing.JPanel {
    
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
            }else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                refreshOptions();
                refreshUnits();
            } 
            
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    /**
     * Creates new form ReadingsPanel
     */
    public ReadingsPanel() {
        initComponents();
        initCallbacks();
        initCutAndPaste(); 
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }

    /**
     * Cleans all fields
     */
    private void CleanAllFields () {        
        JTextField[] jtfList2 = {idTextField, timeTextField,
            NO2TextField, NO3TextField, KHTextField, 
            GHTextField, condTextField, FETextField, 
            tempTextField, CO2TextField, pHTextField, 
            CATextField, CUTextField, MGTextField, 
            NHTextField, PO4TextField, O2TextField, 
            densityTextField,NH3TextField,iodineTextField,salinityTextField};
        Util.CleanTextFields(jtfList2);
        dateTextField.setDate(null);    
        AppUtil.evaluateTextField(AppUtil.getNH3Ranges(), 
                NH3TextField, NH3AlertLabel);
        AppUtil.evaluateTextField(AppUtil.getCO2Ranges(), 
                CO2TextField, CO2AlertLabel); 
        
        setCustomLabels();
    }
    
    /**
     * populate the table
     */
    static private void populateTable(){
        Reading.populateTable(readingsTable);
    }
    
    /**
     * Empties a jTable assigning null model
     * 
     */
    private static void emptyTable (){
        DefaultTableModel dm = new DefaultTableModel();
        String tableData[][] = {{null}};
        String[] nameHeader = {
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                    "NO_SELECTION")};
        dm.setDataVector(tableData, nameHeader);
        readingsTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths(
                "readingstable", Reading.CAPTIONS.length);//NOI18N
        Reading.setColWidth(widths);
        Util.setColSizes(readingsTable,widths ); 
        
    }
    
    private void setCustomLabels() {       
        
        Setting s=Setting.getInstance();
       
        //custom labels start here
        String cl1=s.getDensCustomLabel();
        if (!cl1.isEmpty()) {
            if (!cl1.endsWith(":")) {
                cl1 = cl1 + ":";
            }
            densityLabel.setText(cl1);
        }
        if (!Global.densCustomUnit.isEmpty()) {
            densityUnitLabel.setText(Global.densCustomUnit);
        }
        
        String cl2=s.getCondCustomLabel();
        if (!cl2.isEmpty()) {
            if (!cl2.endsWith(":")) {
                cl2 = cl2 + ":";
            }
            condLabel.setText(cl2);
        }
        if (!Global.condCustomUnit.isEmpty()) {
            conductivityUnitLabel.setText(Global.condCustomUnit);
        }
        
        String cl3=s.getKHCustomLabel();
        if (!cl3.isEmpty()) {
            if (!cl3.endsWith(":")) {
                cl3 = cl3 + ":";
            }
            KHLabel.setText(cl3);
        }
        
        String cl4=s.getTempCustomLabel();        
        if (!cl4.isEmpty()) {
            if (!cl4.endsWith(":")) {
                cl4 = cl4 + ":";
            }
            tempLabel.setText(cl4);
        }
        String cl5=s.getSalinityCustomLabel();        
        if (!cl5.isEmpty()) {
            if (!cl5.endsWith(":")) {
                cl5 = cl5 + ":";
            }
            salinityLabel.setText(cl5);
        }
        if (!Global.salinityCustomUnit.isEmpty()) {
            salinityUnitLabel.setText(Global.salinityCustomUnit);
        }
        
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("readingstable", readingsTable);//NOI18N
    }
    
    /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        dateTextField.setDateFormatString(Global.dateFormat);         
    }
    
     /** This is called when global settings change */
    private void refreshOptions(){
        if (Global.khunit.matches("degree")){
            KHUnitLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("degree"));            
        } else {
           KHUnitLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ppm"));          
        }
        if (Global.temperatureunit.matches("C")){
          tempUnitLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("_C"));  
        } else {
          tempUnitLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("_F"));  
        }
    }        
    
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){
        int recId = TablesUtil.getIdFromTable(
                readingsTable, readingsTable.getSelectedRow());
        
        Reading measure = Reading.getById(recId);
        idTextField.setText(Integer.toString(measure.getId()));// NOI18N
        dateTextField.setDate(measure.getDate());// NOI18N
        timeTextField.setText(measure.getTime());// NOI18N
        NO2TextField.setText(measure.getNo2());// NOI18N
        NO3TextField.setText(measure.getNo3());// NOI18N
        KHTextField.setText(measure.getKh());// NOI18N
        GHTextField.setText(measure.getGh());// NOI18N
        condTextField.setText(measure.getCond());// NOI18N
        FETextField.setText(measure.getFe());// NOI18N
        tempTextField.setText(measure.getTemp());// NOI18N
        CO2TextField.setText(measure.getCo2());// NOI18N
        pHTextField.setText(measure.getPh());// NOI18N
        CATextField.setText(measure.getCa());// NOI18N
        CUTextField.setText(measure.getCu());// NOI18N
        MGTextField.setText(measure.getMg());// NOI18N
        NHTextField.setText(measure.getNh());// NOI18N
        PO4TextField.setText(measure.getPo4());// NOI18N
        O2TextField.setText(measure.getO2());// NOI18N
        densityTextField.setText(measure.getDensity());// NOI18N  
        NH3TextField.setText(measure.getNh3());// NOI18N
        iodineTextField.setText(measure.getIodine());// NOI18N
        salinityTextField.setText(measure.getSalinity());// NOI18N
        AppUtil.evaluateTextField(AppUtil.getNH3Ranges(), 
                NH3TextField, NH3AlertLabel);
        AppUtil.evaluateTextField(AppUtil.getCO2Ranges(), 
                CO2TextField, CO2AlertLabel);      
         
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

        jScrollPane2 = new javax.swing.JScrollPane();
        readingsTable = new javax.swing.JTable();
        idLabel = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        dateLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        timeTextField = new javax.swing.JTextField();
        NO2Label = new javax.swing.JLabel();
        NO2TextField = new javax.swing.JTextField();
        NO3Label = new javax.swing.JLabel();
        NO3TextField = new javax.swing.JTextField();
        KHLabel = new javax.swing.JLabel();
        KHTextField = new javax.swing.JTextField();
        GHLabel = new javax.swing.JLabel();
        GHTextField = new javax.swing.JTextField();
        pHLabel = new javax.swing.JLabel();
        condTextField = new javax.swing.JTextField();
        CALabel = new javax.swing.JLabel();
        FETextField = new javax.swing.JTextField();
        tempLabel = new javax.swing.JLabel();
        tempTextField = new javax.swing.JTextField();
        CO2Label = new javax.swing.JLabel();
        CO2TextField = new javax.swing.JTextField();
        condLabel = new javax.swing.JLabel();
        pHTextField = new javax.swing.JTextField();
        FELabel = new javax.swing.JLabel();
        CATextField = new javax.swing.JTextField();
        MGLabel = new javax.swing.JLabel();
        CULabel = new javax.swing.JLabel();
        CUTextField = new javax.swing.JTextField();
        MGTextField = new javax.swing.JTextField();
        NHLabel = new javax.swing.JLabel();
        NHTextField = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        readingsClearButton = new javax.swing.JButton();
        readingsSaveButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        readingsDeleteButton = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        readingsAverageButton = new javax.swing.JButton();
        readingsReportButton = new javax.swing.JButton();
        readingsPlotButton = new javax.swing.JButton();
        readingsCompatibilityButton = new javax.swing.JButton();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        readingsCO2Button = new javax.swing.JButton();
        readingsNH3Button = new javax.swing.JButton();
        dateTextField = new com.toedter.calendar.JDateChooser();
        PO4Label = new javax.swing.JLabel();
        PO4TextField = new javax.swing.JTextField();
        O2Label = new javax.swing.JLabel();
        O2TextField = new javax.swing.JTextField();
        densityLabel = new javax.swing.JLabel();
        densityTextField = new javax.swing.JTextField();
        NH3Label = new javax.swing.JLabel();
        NH3TextField = new javax.swing.JTextField();
        NH3AlertLabel = new javax.swing.JLabel();
        CO2AlertLabel = new javax.swing.JLabel();
        NO2UnitLabel = new javax.swing.JLabel();
        NO3UnitLabel = new javax.swing.JLabel();
        NHUnitLabel = new javax.swing.JLabel();
        NH3UnitLabel = new javax.swing.JLabel();
        KHUnitLabel = new javax.swing.JLabel();
        GHUnitLabel = new javax.swing.JLabel();
        tempUnitLabel = new javax.swing.JLabel();
        CO2UnitLabel = new javax.swing.JLabel();
        FEUnitLabel = new javax.swing.JLabel();
        CAUnitLabel = new javax.swing.JLabel();
        CUUnitLabel = new javax.swing.JLabel();
        MGUnitLabel = new javax.swing.JLabel();
        conductivityUnitLabel = new javax.swing.JLabel();
        PO4UnitLabel = new javax.swing.JLabel();
        densityUnitLabel = new javax.swing.JLabel();
        O2UnitLabel = new javax.swing.JLabel();
        iodineTextField = new javax.swing.JTextField();
        iodineLabel = new javax.swing.JLabel();
        iodineUnitLabel = new javax.swing.JLabel();
        salinityTextField = new javax.swing.JTextField();
        salinityLabel = new javax.swing.JLabel();
        salinityUnitLabel = new javax.swing.JLabel();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        readingsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        readingsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        readingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsTableMouseClicked(evt);
            }
        });
        readingsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                readingsTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(readingsTable);
        readingsTable.getAccessibleContext().setAccessibleName("readingsTable");
        readingsTable.getAccessibleContext().setAccessibleDescription("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 600;
        gridBagConstraints.ipady = 271;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane2, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        idLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(idLabel, gridBagConstraints);

        idTextField.setEditable(false);
        idTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        idTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(idTextField, gridBagConstraints);

        dateLabel.setText(bundle.getString("Date_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(dateLabel, gridBagConstraints);

        timeLabel.setText(bundle.getString("TIME_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(timeLabel, gridBagConstraints);

        timeTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        timeTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(timeTextField, gridBagConstraints);

        NO2Label.setText(bundle.getString("Ny.readingsNO2Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(NO2Label, gridBagConstraints);

        NO2TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        NO2TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        NO2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NO2TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(NO2TextField, gridBagConstraints);

        NO3Label.setText(bundle.getString("Ny.readingsNO3Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(NO3Label, gridBagConstraints);

        NO3TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        NO3TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        NO3TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NO3TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(NO3TextField, gridBagConstraints);

        KHLabel.setText(bundle.getString("Ny.readingsKHLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(KHLabel, gridBagConstraints);

        KHTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        KHTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        KHTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                KHTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(KHTextField, gridBagConstraints);

        GHLabel.setText(bundle.getString("Ny.readingsGHLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(GHLabel, gridBagConstraints);

        GHTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        GHTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        GHTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                GHTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(GHTextField, gridBagConstraints);

        pHLabel.setText(bundle.getString("Ny.readingsPHLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(pHLabel, gridBagConstraints);

        condTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        condTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        condTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                condTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(condTextField, gridBagConstraints);

        CALabel.setText(bundle.getString("Ny.readingsCALabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(CALabel, gridBagConstraints);

        FETextField.setMinimumSize(new java.awt.Dimension(30, 19));
        FETextField.setPreferredSize(new java.awt.Dimension(80, 19));
        FETextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                FETextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(FETextField, gridBagConstraints);

        tempLabel.setText(bundle.getString("Ny.readingsTempLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(tempLabel, gridBagConstraints);

        tempTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        tempTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        tempTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tempTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(tempTextField, gridBagConstraints);

        CO2Label.setText(bundle.getString("Ny.readingsCO2Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(CO2Label, gridBagConstraints);

        CO2TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        CO2TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        CO2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                CO2TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(CO2TextField, gridBagConstraints);

        condLabel.setText(bundle.getString("Ny.readingsCondLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(condLabel, gridBagConstraints);

        pHTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pHTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pHTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pHTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(pHTextField, gridBagConstraints);

        FELabel.setText(bundle.getString("Ny.readingsFELabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(FELabel, gridBagConstraints);

        CATextField.setMinimumSize(new java.awt.Dimension(30, 19));
        CATextField.setPreferredSize(new java.awt.Dimension(80, 19));
        CATextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                CATextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(CATextField, gridBagConstraints);

        MGLabel.setText(bundle.getString("Ny.readingsMGLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(MGLabel, gridBagConstraints);

        CULabel.setText(bundle.getString("Ny.readingsCULabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(CULabel, gridBagConstraints);

        CUTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        CUTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        CUTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                CUTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(CUTextField, gridBagConstraints);

        MGTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        MGTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        MGTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                MGTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(MGTextField, gridBagConstraints);

        NHLabel.setText(bundle.getString("Ny.readingsNHLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(NHLabel, gridBagConstraints);

        NHTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        NHTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        NHTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NHTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(NHTextField, gridBagConstraints);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        readingsClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        readingsClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        readingsClearButton.setFocusable(false);
        readingsClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsClearButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsClearButton);

        readingsSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        readingsSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        readingsSaveButton.setFocusable(false);
        readingsSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsSaveButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsSaveButton);
        jToolBar2.add(jSeparator2);

        readingsDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        readingsDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        readingsDeleteButton.setFocusable(false);
        readingsDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsDeleteButton);
        jToolBar2.add(jSeparator10);

        readingsAverageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/meter.png"))); // NOI18N
        readingsAverageButton.setToolTipText(bundle.getString("Ny.readingsAverageButton.toolTipText")); // NOI18N
        readingsAverageButton.setFocusable(false);
        readingsAverageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsAverageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsAverageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsAverageButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsAverageButton);

        readingsReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        readingsReportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        readingsReportButton.setFocusable(false);
        readingsReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsReportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsReportButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsReportButton);

        readingsPlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_plot.png"))); // NOI18N
        readingsPlotButton.setToolTipText(bundle.getString("Ny.readingsPlotButton.toolTipText")); // NOI18N
        readingsPlotButton.setFocusable(false);
        readingsPlotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsPlotButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsPlotButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsPlotButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsPlotButton);

        readingsCompatibilityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/compat.png"))); // NOI18N
        readingsCompatibilityButton.setToolTipText(bundle.getString("Ny.readingsCompatibilityButton.toolTipText")); // NOI18N
        readingsCompatibilityButton.setFocusable(false);
        readingsCompatibilityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsCompatibilityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsCompatibilityButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsCompatibilityButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsCompatibilityButton);
        jToolBar2.add(jSeparator19);

        readingsCO2Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/co2.png"))); // NOI18N
        readingsCO2Button.setToolTipText(bundle.getString("Ny.readingsCO2Button.toolTipText")); // NOI18N
        readingsCO2Button.setFocusable(false);
        readingsCO2Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsCO2Button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsCO2Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsCO2ButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsCO2Button);

        readingsNH3Button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nh3.png"))); // NOI18N
        readingsNH3Button.setToolTipText(bundle.getString("Ny.readingsNH3Button.toolTipText")); // NOI18N
        readingsNH3Button.setFocusable(false);
        readingsNH3Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        readingsNH3Button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        readingsNH3Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readingsNH3ButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(readingsNH3Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 327;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(dateTextField, gridBagConstraints);

        PO4Label.setText(bundle.getString("Ny.readingsPO4Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(PO4Label, gridBagConstraints);

        PO4TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        PO4TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        PO4TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                PO4TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(PO4TextField, gridBagConstraints);

        O2Label.setText(bundle.getString("Ny.readingsO2Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(O2Label, gridBagConstraints);

        O2TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        O2TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        O2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                O2TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(O2TextField, gridBagConstraints);

        densityLabel.setText(bundle.getString("Ny.readingsDensLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(densityLabel, gridBagConstraints);

        densityTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        densityTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        densityTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                densityTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(densityTextField, gridBagConstraints);

        NH3Label.setText(bundle.getString("NH3_lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(NH3Label, gridBagConstraints);

        NH3TextField.setMinimumSize(new java.awt.Dimension(30, 19));
        NH3TextField.setPreferredSize(new java.awt.Dimension(80, 19));
        NH3TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NH3TextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(NH3TextField, gridBagConstraints);

        NH3AlertLabel.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(NH3AlertLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(CO2AlertLabel, gridBagConstraints);

        NO2UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        NO2UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(NO2UnitLabel, gridBagConstraints);

        NO3UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        NO3UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(NO3UnitLabel, gridBagConstraints);

        NHUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        NHUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(NHUnitLabel, gridBagConstraints);

        NH3UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        NH3UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(NH3UnitLabel, gridBagConstraints);

        KHUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        KHUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(KHUnitLabel, gridBagConstraints);

        GHUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        GHUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(GHUnitLabel, gridBagConstraints);

        tempUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tempUnitLabel.setText(bundle.getString("_C")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(tempUnitLabel, gridBagConstraints);

        CO2UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        CO2UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(CO2UnitLabel, gridBagConstraints);

        FEUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        FEUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(FEUnitLabel, gridBagConstraints);

        CAUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        CAUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(CAUnitLabel, gridBagConstraints);

        CUUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        CUUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(CUUnitLabel, gridBagConstraints);

        MGUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        MGUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(MGUnitLabel, gridBagConstraints);

        conductivityUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        conductivityUnitLabel.setText(bundle.getString("microS_cm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(conductivityUnitLabel, gridBagConstraints);

        PO4UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        PO4UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(PO4UnitLabel, gridBagConstraints);

        densityUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        densityUnitLabel.setText(bundle.getString("_rho")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(densityUnitLabel, gridBagConstraints);

        O2UnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        O2UnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(O2UnitLabel, gridBagConstraints);

        iodineTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        iodineTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        iodineTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                iodineTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(iodineTextField, gridBagConstraints);

        iodineLabel.setText(bundle.getString("Ny.readingsIodineLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(iodineLabel, gridBagConstraints);

        iodineUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        iodineUnitLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(iodineUnitLabel, gridBagConstraints);

        salinityTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        salinityTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        salinityTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                salinityTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
        add(salinityTextField, gridBagConstraints);

        salinityLabel.setText(bundle.getString("Ny.readingsSalinityLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        add(salinityLabel, gridBagConstraints);

        salinityUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        salinityUnitLabel.setText(bundle.getString("ppt")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(salinityUnitLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void readingsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsTableMouseClicked
        /**
         * Populate TextFields on tab2 (measures)
         */
        refreshFields();
    }//GEN-LAST:event_readingsTableMouseClicked

    private void NO2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NO2TextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_NO2TextFieldKeyTyped

    private void NO3TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NO3TextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_NO3TextFieldKeyTyped

    private void KHTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KHTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_KHTextFieldKeyTyped

    private void GHTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GHTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_GHTextFieldKeyTyped

    private void condTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_condTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_condTextFieldKeyTyped

    private void FETextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_FETextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_FETextFieldKeyTyped

    private void tempTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tempTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_tempTextFieldKeyTyped

    private void CO2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CO2TextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_CO2TextFieldKeyTyped

    private void pHTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pHTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_pHTextFieldKeyTyped

    private void CATextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CATextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_CATextFieldKeyTyped

    private void CUTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CUTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_CUTextFieldKeyTyped

    private void MGTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MGTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_MGTextFieldKeyTyped

    private void NHTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NHTextFieldKeyTyped
        //allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_NHTextFieldKeyTyped

    private void readingsClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsClearButtonMouseClicked
        /**
         * Cleans all textFields on tab2 (measures)
         */
        CleanAllFields();
    }//GEN-LAST:event_readingsClearButtonMouseClicked

    private void readingsSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsSaveButtonMouseClicked
        /**
         * Insert record on db for table measures or update it if existing
         */
        if (Global.AqID == 0) {
            AppUtil.msgSelectAquarium();
            return;
        }
        String currID = idTextField.getText();

        Reading measure = new Reading();
        if (currID == null || currID.equals("")) {
            measure.setId(0);
        } else {
            measure.setId(Integer.valueOf(currID));
        }

        if (LocUtil.isValidDate(dateTextField.getDate())) {
            if (dateTextField.getDate() == null) {
                measure.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
            } else {
                measure.setDate(LocUtil.delocalizeDate(dateTextField.getDate()));
            }
        } else {
            Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));
            dateTextField.requestFocus();
            //readingsDateTextField.selectAll();
            return;
        }
        measure.setTime(timeTextField.getText());
        measure.setNo2(LocUtil.delocalizeDouble(NO2TextField.getText()));
        measure.setNo3(LocUtil.delocalizeDouble(NO3TextField.getText()));
        measure.setGh(LocUtil.delocalizeDouble(GHTextField.getText()));
        measure.setKh(LocUtil.delocalizeDouble(KHTextField.getText()));
        measure.setPh(LocUtil.delocalizeDouble(pHTextField.getText()));
        measure.setTemp(LocUtil.delocalizeDouble(tempTextField.getText()));
        measure.setFe(LocUtil.delocalizeDouble(FETextField.getText()));
        measure.setNh(LocUtil.delocalizeDouble(NHTextField.getText()));
        measure.setCo2(LocUtil.delocalizeDouble(CO2TextField.getText()));
        measure.setCond(LocUtil.delocalizeDouble(condTextField.getText()));
        measure.setCa(LocUtil.delocalizeDouble(CATextField.getText()));
        measure.setMg(LocUtil.delocalizeDouble(MGTextField.getText()));
        measure.setCu(LocUtil.delocalizeDouble(CUTextField.getText()));
        measure.setPo4(LocUtil.delocalizeDouble(PO4TextField.getText()));
        measure.setO2(LocUtil.delocalizeDouble(O2TextField.getText()));
        measure.setDensity(LocUtil.delocalizeDouble(densityTextField.getText()));
        measure.setNh3(LocUtil.delocalizeDouble(NH3TextField.getText()));
        measure.setIodine(LocUtil.delocalizeDouble(iodineTextField.getText()));
        measure.setSalinity(LocUtil.delocalizeDouble(
                salinityTextField.getText()));
        
        measure.save(measure);
        Reading.populateTable(readingsTable);
        CleanAllFields();
    }//GEN-LAST:event_readingsSaveButtonMouseClicked

    private void readingsDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsDeleteButtonMouseClicked
        /**
         * Delete seete record on tab2 (measures)
         */
        Reading.deleteById(idTextField.getText());
        Reading.populateTable(readingsTable);
        CleanAllFields();
    }//GEN-LAST:event_readingsDeleteButtonMouseClicked

    private void readingsAverageButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsAverageButtonMouseClicked
        // Show average measures
        if (Global.AqID == 0) {
            AppUtil.msgSelectAquarium();
            return;
        }
        InfoDisplay A = new InfoDisplay();
        A.setTitle(java.util.ResourceBundle.getBundle(
                "nyagua/Bundle").getString("AVERAGE_MEASURES"));
        
        String infos = "";
        try {
            infos = Report.getAvgHtm();
        } catch (ClassNotFoundException | SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        A.displayInformations(infos);
        A.setVisible(true);
    }//GEN-LAST:event_readingsAverageButtonMouseClicked

    private void readingsReportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsReportButtonMouseClicked
        // Show reading analisis report
        if (Global.AqID == 0) {
            AppUtil.msgSelectAquarium();
            return;
        }
        String id = idTextField.getText();
        try {
            Report.ReadingsReport(id,true);
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_readingsReportButtonMouseClicked

    private void readingsPlotButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsPlotButtonMouseClicked
        //imports xml file saved from fishbase.com
        if (Global.AqID == 0) {
            AppUtil.msgSelectAquarium();
            return;
        }
        if (readingsTable.getRowCount()==0){
            Util.showInfoMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.readings.norecords"));
            return;
        }    
        Plotting s = new Plotting(null, true);
        s.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLOTTING_OPTIONS"));
        s.setVisible(true);
    }//GEN-LAST:event_readingsPlotButtonMouseClicked

    private void readingsCompatibilityButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsCompatibilityButtonMouseClicked
        // plot compatibility chart
        if (Global.AqID == 0) {
            AppUtil.msgSelectAquarium();
            return;
        }
        if (readingsTable.getRowCount()==0){
            Util.showInfoMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.readings.norecords"));
            return;
        }
        nyagua.Compatibility s = new nyagua.Compatibility(null, true);
        String currID = idTextField.getText();
        if (currID == null || currID.equals("")) {
            Compatibility.selId = 0;
        } else {
            Compatibility.selId = Integer.parseInt(currID);
        }

        s.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLOTTING_OPTIONS"));
        s.setVisible(true);
    }//GEN-LAST:event_readingsCompatibilityButtonMouseClicked

    private void readingsCO2ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsCO2ButtonMouseClicked
        // Calc co2 on measures page
        AppUtil.calcCO2(KHTextField, pHTextField, CO2TextField);
    }//GEN-LAST:event_readingsCO2ButtonMouseClicked

    private void PO4TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PO4TextFieldKeyTyped
// allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_PO4TextFieldKeyTyped

    private void O2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_O2TextFieldKeyTyped
// allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_O2TextFieldKeyTyped

    private void densityTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_densityTextFieldKeyTyped
// allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_densityTextFieldKeyTyped

    private void readingsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_readingsTableKeyReleased
        /**
         * Populate TextFields on tab2 (measures)
         */
        refreshFields();
    }//GEN-LAST:event_readingsTableKeyReleased

    private void NH3TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NH3TextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_NH3TextFieldKeyTyped

    private void readingsNH3ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_readingsNH3ButtonMouseClicked
        // Calculate NH3 from other values
        AppUtil.calcNh3(pHTextField, tempTextField, 
                NHTextField, NH3TextField);       
        
    }//GEN-LAST:event_readingsNH3ButtonMouseClicked

    private void iodineTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iodineTextFieldKeyTyped
       // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_iodineTextFieldKeyTyped

    private void salinityTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salinityTextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_salinityTextFieldKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CALabel;
    private javax.swing.JTextField CATextField;
    private javax.swing.JLabel CAUnitLabel;
    private javax.swing.JLabel CO2AlertLabel;
    private javax.swing.JLabel CO2Label;
    private javax.swing.JTextField CO2TextField;
    private javax.swing.JLabel CO2UnitLabel;
    private javax.swing.JLabel CULabel;
    private javax.swing.JTextField CUTextField;
    private javax.swing.JLabel CUUnitLabel;
    private javax.swing.JLabel FELabel;
    private javax.swing.JTextField FETextField;
    private javax.swing.JLabel FEUnitLabel;
    private javax.swing.JLabel GHLabel;
    private javax.swing.JTextField GHTextField;
    private javax.swing.JLabel GHUnitLabel;
    private javax.swing.JLabel KHLabel;
    private javax.swing.JTextField KHTextField;
    private javax.swing.JLabel KHUnitLabel;
    private javax.swing.JLabel MGLabel;
    private javax.swing.JTextField MGTextField;
    private javax.swing.JLabel MGUnitLabel;
    private javax.swing.JLabel NH3AlertLabel;
    private javax.swing.JLabel NH3Label;
    private javax.swing.JTextField NH3TextField;
    private javax.swing.JLabel NH3UnitLabel;
    private javax.swing.JLabel NHLabel;
    private javax.swing.JTextField NHTextField;
    private javax.swing.JLabel NHUnitLabel;
    private javax.swing.JLabel NO2Label;
    private javax.swing.JTextField NO2TextField;
    private javax.swing.JLabel NO2UnitLabel;
    private javax.swing.JLabel NO3Label;
    private javax.swing.JTextField NO3TextField;
    private javax.swing.JLabel NO3UnitLabel;
    private javax.swing.JLabel O2Label;
    private javax.swing.JTextField O2TextField;
    private javax.swing.JLabel O2UnitLabel;
    private javax.swing.JLabel PO4Label;
    private javax.swing.JTextField PO4TextField;
    private javax.swing.JLabel PO4UnitLabel;
    private javax.swing.JLabel condLabel;
    private javax.swing.JTextField condTextField;
    private javax.swing.JLabel conductivityUnitLabel;
    private javax.swing.JLabel dateLabel;
    private com.toedter.calendar.JDateChooser dateTextField;
    private javax.swing.JLabel densityLabel;
    private javax.swing.JTextField densityTextField;
    private javax.swing.JLabel densityUnitLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JLabel iodineLabel;
    private javax.swing.JTextField iodineTextField;
    private javax.swing.JLabel iodineUnitLabel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator19;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel pHLabel;
    private javax.swing.JTextField pHTextField;
    private javax.swing.JButton readingsAverageButton;
    private javax.swing.JButton readingsCO2Button;
    private javax.swing.JButton readingsClearButton;
    private javax.swing.JButton readingsCompatibilityButton;
    private javax.swing.JButton readingsDeleteButton;
    private javax.swing.JButton readingsNH3Button;
    private javax.swing.JButton readingsPlotButton;
    private javax.swing.JButton readingsReportButton;
    private javax.swing.JButton readingsSaveButton;
    private static javax.swing.JTable readingsTable;
    private javax.swing.JLabel salinityLabel;
    private javax.swing.JTextField salinityTextField;
    private javax.swing.JLabel salinityUnitLabel;
    private javax.swing.JLabel tempLabel;
    private javax.swing.JTextField tempTextField;
    private javax.swing.JLabel tempUnitLabel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JTextField timeTextField;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        timeTextField.addMouseListener(new ContextMenuMouseListener());
        NHTextField.addMouseListener(new ContextMenuMouseListener());
        NO2TextField.addMouseListener(new ContextMenuMouseListener());
        NO3TextField.addMouseListener(new ContextMenuMouseListener());
        KHTextField.addMouseListener(new ContextMenuMouseListener());
        GHTextField.addMouseListener(new ContextMenuMouseListener());
        pHTextField.addMouseListener(new ContextMenuMouseListener());
        FETextField.addMouseListener(new ContextMenuMouseListener());
        tempTextField.addMouseListener(new ContextMenuMouseListener());
        CO2TextField.addMouseListener(new ContextMenuMouseListener());
        condTextField.addMouseListener(new ContextMenuMouseListener());
        CATextField.addMouseListener(new ContextMenuMouseListener());
        MGTextField.addMouseListener(new ContextMenuMouseListener());
        CUTextField.addMouseListener(new ContextMenuMouseListener());
        PO4TextField.addMouseListener(new ContextMenuMouseListener());
        O2TextField.addMouseListener(new ContextMenuMouseListener());
        densityTextField.addMouseListener(new ContextMenuMouseListener());
        NH3TextField.addMouseListener(new ContextMenuMouseListener());
        iodineTextField.addMouseListener(new ContextMenuMouseListener());
        salinityTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
    private void initCallbacks() {
        NH3TextField.getDocument().addDocumentListener(new DocumentListener() {
                    
                @Override
                public void changedUpdate(DocumentEvent e) {
                    AppUtil.evaluateTextField(AppUtil.getNH3Ranges(), NH3TextField, 
                            NH3AlertLabel);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                   //not used
                }
                @Override
                public void insertUpdate(DocumentEvent e) {
                  AppUtil.evaluateTextField(AppUtil.getNH3Ranges(), NH3TextField, 
                          NH3AlertLabel);
                }
          }
        );
        
        CO2TextField.getDocument().addDocumentListener(new DocumentListener() {
                    
                @Override
                public void changedUpdate(DocumentEvent e) {
                    AppUtil.evaluateTextField(AppUtil.getCO2Ranges(), 
                        CO2TextField, CO2AlertLabel);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                   //not used
                }
                @Override
                public void insertUpdate(DocumentEvent e) {
                  AppUtil.evaluateTextField(AppUtil.getCO2Ranges(), CO2TextField, 
                          CO2AlertLabel);
                }
          }
        );
    }
    
    static final Logger _log = Logger.getLogger(ReadingsPanel.class.getName());
    
}

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
 * Invertebrates Base Panel.java
 *
 * Created on 11-giu-2012, 13.36.53
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import nyagua.data.InvBase;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class IBPanel extends javax.swing.JPanel {
    private static JComboBox invertsNameComboBox;
    
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
            } else if (e.getID()==Watched.REQUEST_POPULATE_IBTABLE){
                populateTable();
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            }
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
     
    /** Creates new form FBPanel */
    public IBPanel() {
        initComponents();
        initCutAndPaste();  
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }
    
    public static void setAssociatedCombo (JComboBox fnC){
        invertsNameComboBox=fnC;
    }
    
    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextComponent[] jtfList = {ibIdTextField, ibCommonNameTextField, 
                ibClassTextField,ibNameTextField, ibDistributionTextField, 
                ibDiagnosisTextField, ibBiologyTextField, ibEnviromentTextField,
                ibMaxSizeTextField, ibClimateTextField, ibDangerousTextField, 
                ibPHMinTextField, ibPHMaxTextField,ibDHMinTextField, 
                ibDHMaxTextField, ibTMinTextField, ibTMaxTextField, 
                ibSwimLevelTextField, ibAkaTextField,ibTDSMinTextField,
                ibTDSMaxTextField};
        Util.CleanTextFields(jtfList);        
        ibImageLabelImg.setIcon(null);
        ibImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));            
    }
    
    /** populate the table*/
    static private void populateTable(){
        InvBase.populateTable(ibTable);
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
        ibTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("ibtable", InvBase.CAPTIONS.length);//NOI18N
        InvBase.setColWidth(widths);
        Util.setColSizes(ibTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("ibtable", ibTable);//NOI18N
    }

    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){  
        int recId = TablesUtil.getIdFromTable(
                ibTable, ibTable.getSelectedRow());
        
        InvBase specData=InvBase.getById(recId);
        ibIdTextField.setText(Integer.toString(specData.getId()));// NOI18N
        ibCommonNameTextField.setText(specData.getCommonName());// NOI18N
        ibClassTextField.setText(specData.getType());// NOI18N
        ibNameTextField.setText(specData.getName());// NOI18N
        ibDistributionTextField.setText(specData.getDistribution());// NOI18N
        ibDiagnosisTextField.setText(specData.getDiagnosis());// NOI18N    
        ibBiologyTextField.setText(specData.getBiology());// NOI18N
        ibEnviromentTextField.setText(specData.getEnvironment());// NOI18N
        ibMaxSizeTextField.setText(specData.getMaxSize());// NOI18N
        ibClimateTextField.setText(specData.getClimate());// NOI18N
        ibDangerousTextField.setText(specData.getDangerous());// NOI18N
        ibPHMinTextField.setText(specData.getPhMin());// NOI18N
        ibPHMaxTextField.setText(specData.getPhMax());// NOI18N
        ibDHMinTextField.setText(specData.getDhMin());// NOI18N
        ibDHMaxTextField.setText(specData.getDhMax());// NOI18N
        ibTMinTextField.setText(specData.getTempMin());// NOI18N
        ibTMaxTextField.setText(specData.getTempMax());// NOI18N    
        ibSwimLevelTextField.setText(specData.getSwimLevel());// NOI18N
        ibLifeSpanTextField.setText(specData.getLifeSpam());// NOI18N
        ibAkaTextField.setText(specData.getAKA());// NOI18N
        ibTDSMinTextField.setText(specData.getTdsMin());// NOI18N
        ibTDSMaxTextField.setText(specData.getTdsMax());// NOI18N
        BufferedImage img = null;
        ibImageLabelImg.setText(null);
        if (specData.hasImage()){
            //ibImageLabelImg.setIcon((new javax.swing.ImageIcon(specData.getImage())));
            Util.ImageDisplayResize(specData.getImage(), ibImageLabelImg, 300);
        } else {
            ibImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
            ibImageLabelImg.setIcon(null);
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

        ibIdLabel = new javax.swing.JLabel();
        ibIdTextField = new javax.swing.JTextField();
        ibCommonNameLabel = new javax.swing.JLabel();
        ibCommonNameTextField = new javax.swing.JTextField();
        IBTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        ibClassLabel = new javax.swing.JLabel();
        ibClassTextField = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        ibTable = new javax.swing.JTable();
        ibNameLabel = new javax.swing.JLabel();
        ibNameTextField = new javax.swing.JTextField();
        ibImageLoadButton = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        ibImageLabelImg = new javax.swing.JLabel();
        ibImageDeleteButton = new javax.swing.JButton();
        ibMaxSizeTextField = new javax.swing.JTextField();
        ibMaxSizeLabel = new javax.swing.JLabel();
        ibPHMinLabel = new javax.swing.JLabel();
        ibPHMinTextField = new javax.swing.JTextField();
        ibPHMaxLabel = new javax.swing.JLabel();
        ibPHMaxTextField = new javax.swing.JTextField();
        ibDHMinLabel = new javax.swing.JLabel();
        ibDHMinTextField = new javax.swing.JTextField();
        ibDHMaxLabel = new javax.swing.JLabel();
        ibDHMaxTextField = new javax.swing.JTextField();
        ibTMinLabel = new javax.swing.JLabel();
        ibTMinTextField = new javax.swing.JTextField();
        ibTMaxLabel = new javax.swing.JLabel();
        ibTMaxTextField = new javax.swing.JTextField();
        ibSwimLevelLabel = new javax.swing.JLabel();
        ibSwimLevelTextField = new javax.swing.JTextField();
        ibLifeSpanLabel = new javax.swing.JLabel();
        ibAkaTextField = new javax.swing.JTextField();
        ibLifeSpanTextField = new javax.swing.JTextField();
        ibAkaLabel = new javax.swing.JLabel();
        ibTDSMinLabel = new javax.swing.JLabel();
        ibTDSMinTextField = new javax.swing.JTextField();
        ibTDSMaxLabel = new javax.swing.JLabel();
        ibTDSMaxTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        ibDangerousLabel = new javax.swing.JLabel();
        ibClimateLabel = new javax.swing.JLabel();
        ibEnviromentLabel = new javax.swing.JLabel();
        ibBiologyLabel = new javax.swing.JLabel();
        ibDistributionLabel = new javax.swing.JLabel();
        ibDiagnosisLabel = new javax.swing.JLabel();
        ibdiagScrollPane = new javax.swing.JScrollPane();
        ibDiagnosisTextField = new javax.swing.JTextArea();
        ibdangScrollPane = new javax.swing.JScrollPane();
        ibDangerousTextField = new javax.swing.JTextArea();
        ibenvScrollPane = new javax.swing.JScrollPane();
        ibEnviromentTextField = new javax.swing.JTextArea();
        ibclimScrollPane = new javax.swing.JScrollPane();
        ibClimateTextField = new javax.swing.JTextArea();
        ibbioScrollPane = new javax.swing.JScrollPane();
        ibBiologyTextField = new javax.swing.JTextArea();
        ibdistrScrollPane = new javax.swing.JScrollPane();
        ibDistributionTextField = new javax.swing.JTextArea();
        ibjToolBar = new javax.swing.JToolBar();
        ibClearButton = new javax.swing.JButton();
        ibSaveButton = new javax.swing.JButton();
        ibLoadXMLButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        ibDeleteButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        ibSearchButton = new javax.swing.JButton();
        ibSearchState = new javax.swing.JButton();
        ibNoSearchButton = new javax.swing.JButton();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMinimumSize(new java.awt.Dimension(0, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        ibIdLabel.setText(bundle.getString("ID_")); // NOI18N

        ibIdTextField.setEditable(false);
        ibIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        ibCommonNameLabel.setText(bundle.getString("Ny.fbCommonNameLabel.text")); // NOI18N

        ibCommonNameTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibCommonNameTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        IBTabbedPane1.setAlignmentX(0.0F);
        IBTabbedPane1.setAlignmentY(0.0F);
        IBTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        IBTabbedPane1.setPreferredSize(new java.awt.Dimension(1096, 951));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        ibClassLabel.setText(bundle.getString("Ny.fbClassLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibClassLabel, gridBagConstraints);

        ibClassTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibClassTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibClassTextField, gridBagConstraints);

        ibTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        ibTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        ibTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ibTableMouseClicked(evt);
            }
        });
        ibTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ibTableKeyReleased(evt);
            }
        });
        jScrollPane12.setViewportView(ibTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 500;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 15, 15);
        jPanel1.add(jScrollPane12, gridBagConstraints);

        ibNameLabel.setText(bundle.getString("Ny.fbNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibNameLabel, gridBagConstraints);

        ibNameTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibNameTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibNameTextField, gridBagConstraints);

        ibImageLoadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/insert-image.png"))); // NOI18N
        ibImageLoadButton.setToolTipText(bundle.getString("Load_an_image")); // NOI18N
        ibImageLoadButton.setMaximumSize(new java.awt.Dimension(44, 44));
        ibImageLoadButton.setMinimumSize(new java.awt.Dimension(44, 44));
        ibImageLoadButton.setPreferredSize(new java.awt.Dimension(44, 44));
        ibImageLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ibImageLoadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        jPanel1.add(ibImageLoadButton, gridBagConstraints);

        jScrollPane13.setMaximumSize(new java.awt.Dimension(63, 15));
        jScrollPane13.setPreferredSize(new java.awt.Dimension(63, 15));

        ibImageLabelImg.setForeground(new java.awt.Color(255, 102, 51));
        ibImageLabelImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ibImageLabelImg.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        jScrollPane13.setViewportView(ibImageLabelImg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 15);
        jPanel1.add(jScrollPane13, gridBagConstraints);

        ibImageDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/archive-remove.png"))); // NOI18N
        ibImageDeleteButton.setToolTipText(bundle.getString("Clear_image")); // NOI18N
        ibImageDeleteButton.setMaximumSize(new java.awt.Dimension(44, 44));
        ibImageDeleteButton.setMinimumSize(new java.awt.Dimension(44, 44));
        ibImageDeleteButton.setPreferredSize(new java.awt.Dimension(44, 44));
        ibImageDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ibImageDeleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 15);
        jPanel1.add(ibImageDeleteButton, gridBagConstraints);

        ibMaxSizeTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibMaxSizeTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibMaxSizeTextField, gridBagConstraints);

        ibMaxSizeLabel.setText(bundle.getString("Ny.fbMaxSizeLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibMaxSizeLabel, gridBagConstraints);

        ibPHMinLabel.setText(bundle.getString("Ny.fbPHMinLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibPHMinLabel, gridBagConstraints);

        ibPHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibPHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibPHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibPHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibPHMinTextField, gridBagConstraints);

        ibPHMaxLabel.setText(bundle.getString("Ny.fbPHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibPHMaxLabel, gridBagConstraints);

        ibPHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibPHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibPHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibPHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibPHMaxTextField, gridBagConstraints);

        ibDHMinLabel.setText(bundle.getString("Ny.fbDHMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibDHMinLabel, gridBagConstraints);

        ibDHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibDHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibDHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibDHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibDHMinTextField, gridBagConstraints);

        ibDHMaxLabel.setText(bundle.getString("Ny.fbDHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibDHMaxLabel, gridBagConstraints);

        ibDHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibDHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibDHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibDHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibDHMaxTextField, gridBagConstraints);

        ibTMinLabel.setText(bundle.getString("Ny.fbTMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTMinLabel, gridBagConstraints);

        ibTMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibTMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibTMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibTMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTMinTextField, gridBagConstraints);

        ibTMaxLabel.setText(bundle.getString("Ny.fbTMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTMaxLabel, gridBagConstraints);

        ibTMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibTMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibTMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibTMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTMaxTextField, gridBagConstraints);

        ibSwimLevelLabel.setText(bundle.getString("Ny.fbSwimLevelLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibSwimLevelLabel, gridBagConstraints);

        ibSwimLevelTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibSwimLevelTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibSwimLevelTextField, gridBagConstraints);

        ibLifeSpanLabel.setText(bundle.getString("Ny.fbLifeSpanLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 3, 2);
        jPanel1.add(ibLifeSpanLabel, gridBagConstraints);

        ibAkaTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibAkaTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibAkaTextField, gridBagConstraints);

        ibLifeSpanTextField.setMinimumSize(new java.awt.Dimension(50, 19));
        ibLifeSpanTextField.setPreferredSize(new java.awt.Dimension(50, 19));
        ibLifeSpanTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibLifeSpanTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(ibLifeSpanTextField, gridBagConstraints);

        ibAkaLabel.setText(bundle.getString("AKA")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibAkaLabel, gridBagConstraints);

        ibTDSMinLabel.setText(bundle.getString("TDS_MIN")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTDSMinLabel, gridBagConstraints);

        ibTDSMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibTDSMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibTDSMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibTDSMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTDSMinTextField, gridBagConstraints);

        ibTDSMaxLabel.setText(bundle.getString("TDS_MAX")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTDSMaxLabel, gridBagConstraints);

        ibTDSMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        ibTDSMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        ibTDSMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ibTDSMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(ibTDSMaxTextField, gridBagConstraints);

        IBTabbedPane1.addTab(" ", new javax.swing.ImageIcon(getClass().getResource("/icons/shrimp.png")), jPanel1); // NOI18N

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setAutoscrolls(true);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        ibDangerousLabel.setText(bundle.getString("Ny.fbDangerousLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(ibDangerousLabel, gridBagConstraints);

        ibClimateLabel.setText(bundle.getString("Ny.fbClimateLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(ibClimateLabel, gridBagConstraints);

        ibEnviromentLabel.setText(bundle.getString("Ny.fbEnviromentLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(ibEnviromentLabel, gridBagConstraints);

        ibBiologyLabel.setText(bundle.getString("Ny.fbBiologyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(ibBiologyLabel, gridBagConstraints);

        ibDistributionLabel.setText(bundle.getString("Ny.fbDistributionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(ibDistributionLabel, gridBagConstraints);

        ibDiagnosisLabel.setText(bundle.getString("Ny.fbDiagnosisLabel.text")); // NOI18N
        ibDiagnosisLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel2.add(ibDiagnosisLabel, gridBagConstraints);

        ibdiagScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibdiagScrollPane.setAlignmentX(0.0F);
        ibdiagScrollPane.setAlignmentY(0.0F);
        ibdiagScrollPane.setAutoscrolls(true);
        ibdiagScrollPane.setHorizontalScrollBar(null);
        ibdiagScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibDiagnosisTextField.setColumns(20);
        ibDiagnosisTextField.setLineWrap(true);
        ibDiagnosisTextField.setRows(3);
        ibDiagnosisTextField.setWrapStyleWord(true);
        ibDiagnosisTextField.setAlignmentX(0.0F);
        ibDiagnosisTextField.setAlignmentY(0.0F);
        ibDiagnosisTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibDiagnosisTextField.setName(""); // NOI18N
        ibdiagScrollPane.setViewportView(ibDiagnosisTextField);
        ibDiagnosisTextField.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibdiagScrollPane, gridBagConstraints);

        ibdangScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibdangScrollPane.setAlignmentX(0.0F);
        ibdangScrollPane.setAlignmentY(0.0F);
        ibdangScrollPane.setAutoscrolls(true);
        ibdangScrollPane.setHorizontalScrollBar(null);
        ibdangScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibDangerousTextField.setColumns(20);
        ibDangerousTextField.setLineWrap(true);
        ibDangerousTextField.setRows(3);
        ibDangerousTextField.setWrapStyleWord(true);
        ibDangerousTextField.setAlignmentX(0.0F);
        ibDangerousTextField.setAlignmentY(0.0F);
        ibDangerousTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibDangerousTextField.setName(""); // NOI18N
        ibdangScrollPane.setViewportView(ibDangerousTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibdangScrollPane, gridBagConstraints);

        ibenvScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibenvScrollPane.setAlignmentX(0.0F);
        ibenvScrollPane.setAlignmentY(0.0F);
        ibenvScrollPane.setAutoscrolls(true);
        ibenvScrollPane.setHorizontalScrollBar(null);
        ibenvScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibEnviromentTextField.setColumns(20);
        ibEnviromentTextField.setLineWrap(true);
        ibEnviromentTextField.setRows(3);
        ibEnviromentTextField.setWrapStyleWord(true);
        ibEnviromentTextField.setAlignmentX(0.0F);
        ibEnviromentTextField.setAlignmentY(0.0F);
        ibEnviromentTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibEnviromentTextField.setName(""); // NOI18N
        ibenvScrollPane.setViewportView(ibEnviromentTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibenvScrollPane, gridBagConstraints);

        ibclimScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibclimScrollPane.setAlignmentX(0.0F);
        ibclimScrollPane.setAlignmentY(0.0F);
        ibclimScrollPane.setAutoscrolls(true);
        ibclimScrollPane.setHorizontalScrollBar(null);
        ibclimScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibClimateTextField.setColumns(20);
        ibClimateTextField.setLineWrap(true);
        ibClimateTextField.setRows(3);
        ibClimateTextField.setWrapStyleWord(true);
        ibClimateTextField.setAlignmentX(0.0F);
        ibClimateTextField.setAlignmentY(0.0F);
        ibClimateTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibClimateTextField.setName(""); // NOI18N
        ibclimScrollPane.setViewportView(ibClimateTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibclimScrollPane, gridBagConstraints);

        ibbioScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibbioScrollPane.setAlignmentX(0.0F);
        ibbioScrollPane.setAlignmentY(0.0F);
        ibbioScrollPane.setAutoscrolls(true);
        ibbioScrollPane.setHorizontalScrollBar(null);
        ibbioScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibBiologyTextField.setColumns(20);
        ibBiologyTextField.setLineWrap(true);
        ibBiologyTextField.setRows(3);
        ibBiologyTextField.setWrapStyleWord(true);
        ibBiologyTextField.setAlignmentX(0.0F);
        ibBiologyTextField.setAlignmentY(0.0F);
        ibBiologyTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibBiologyTextField.setName(""); // NOI18N
        ibbioScrollPane.setViewportView(ibBiologyTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibbioScrollPane, gridBagConstraints);

        ibdistrScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ibdistrScrollPane.setAlignmentX(0.0F);
        ibdistrScrollPane.setAlignmentY(0.0F);
        ibdistrScrollPane.setAutoscrolls(true);
        ibdistrScrollPane.setHorizontalScrollBar(null);
        ibdistrScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        ibDistributionTextField.setColumns(20);
        ibDistributionTextField.setLineWrap(true);
        ibDistributionTextField.setRows(3);
        ibDistributionTextField.setWrapStyleWord(true);
        ibDistributionTextField.setAlignmentX(0.0F);
        ibDistributionTextField.setAlignmentY(0.0F);
        ibDistributionTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ibDistributionTextField.setName(""); // NOI18N
        ibdistrScrollPane.setViewportView(ibDistributionTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 4, 10);
        jPanel2.add(ibdistrScrollPane, gridBagConstraints);

        IBTabbedPane1.addTab(" ", new javax.swing.ImageIcon(getClass().getResource("/icons/sheets.png")), jPanel2); // NOI18N

        ibjToolBar.setFloatable(false);
        ibjToolBar.setRollover(true);
        ibjToolBar.setAlignmentX(0.0F);

        ibClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        ibClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        ibClearButton.setFocusable(false);
        ibClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ibClearButtonMouseClicked(evt);
            }
        });
        ibjToolBar.add(ibClearButton);

        ibSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        ibSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        ibSaveButton.setFocusable(false);
        ibSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ibSaveButtonMouseClicked(evt);
            }
        });
        ibjToolBar.add(ibSaveButton);

        ibLoadXMLButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/folder-new.png"))); // NOI18N
        ibLoadXMLButton.setToolTipText(bundle.getString("Ny.fbLoadXMLButton.toolTipText")); // NOI18N
        ibLoadXMLButton.setFocusable(false);
        ibLoadXMLButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibLoadXMLButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibLoadXMLButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ibLoadXMLButtonMouseClicked(evt);
            }
        });
        ibjToolBar.add(ibLoadXMLButton);
        ibjToolBar.add(jSeparator8);

        ibDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        ibDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        ibDeleteButton.setFocusable(false);
        ibDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ibDeleteButtonMouseClicked(evt);
            }
        });
        ibjToolBar.add(ibDeleteButton);
        ibjToolBar.add(jSeparator16);

        ibSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        ibSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        ibSearchButton.setFocusable(false);
        ibSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ibSearchButtonActionPerformed(evt);
            }
        });
        ibjToolBar.add(ibSearchButton);

        ibSearchState.setFocusable(false);
        ibSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        ibSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        ibSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        ibSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibjToolBar.add(ibSearchState);

        ibNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        ibNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        ibNoSearchButton.setFocusable(false);
        ibNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ibNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        ibNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        ibNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        ibNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ibNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ibNoSearchButtonActionPerformed(evt);
            }
        });
        ibjToolBar.add(ibNoSearchButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ibjToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ibIdLabel)
                .addGap(10, 10, 10)
                .addComponent(ibIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addComponent(ibCommonNameLabel)
                .addGap(10, 10, 10)
                .addComponent(ibCommonNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(IBTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ibjToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ibIdLabel)
                    .addComponent(ibIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ibCommonNameLabel)
                    .addComponent(ibCommonNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(IBTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void ibTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ibTableMouseClicked
    /**Populate TextFields on tab8 (InvBase) */
    refreshFields();
}//GEN-LAST:event_ibTableMouseClicked

private void ibClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ibClearButtonMouseClicked
    /** Cleans all textFields on tab8 (InvBase)*/
    CleanAllFields();
}//GEN-LAST:event_ibClearButtonMouseClicked

private void ibSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ibSaveButtonMouseClicked
    /**Insert record on db for table InvBase or update it if existing*/
    /*if (Global.AqID == 0) {
        Util.msgSelectAquarium();
        return;
    }*/
    String currID = ibIdTextField.getText();
    InvBase specData=new InvBase();
    if (currID == null || currID.equals("")) {
        specData.setId(0);
        } else {
        specData.setId(Integer.valueOf(currID));
    }
    specData.setCommonName(ibCommonNameTextField.getText());
    specData.setType(ibClassTextField.getText());
    specData.setName(ibNameTextField.getText());
    specData.setDistribution(ibDistributionTextField.getText());
    specData.setDiagnosis(ibDiagnosisTextField.getText());
    specData.setBiology(ibBiologyTextField.getText());
    specData.setMaxSize(ibMaxSizeTextField.getText());
    specData.setEnvironment(ibEnviromentTextField.getText());
    specData.setClimate(ibClimateTextField.getText());
    specData.setDangerous(ibDangerousTextField.getText());
    specData.setPhMin(LocUtil.delocalizeDouble(ibPHMinTextField.getText()));
    specData.setPhMax(LocUtil.delocalizeDouble(ibPHMaxTextField.getText()));
    specData.setDhMin(LocUtil.delocalizeDouble(ibDHMinTextField.getText()));
    specData.setDhMax(LocUtil.delocalizeDouble(ibDHMaxTextField.getText()));
    specData.setTempMin(LocUtil.delocalizeDouble(ibTMinTextField.getText()));
    specData.setTempMax(LocUtil.delocalizeDouble(ibTMaxTextField.getText()));
    specData.setSwimLevel(ibSwimLevelTextField.getText());    
    specData.setLifeSpam(ibLifeSpanTextField.getText());    
    specData.setAka(ibAkaTextField.getText());   
    specData.setTdsMin(LocUtil.delocalizeDouble(ibTDSMinTextField.getText()));
    specData.setTdsMax(LocUtil.delocalizeDouble(ibTDSMaxTextField.getText()));
    //save image
    BufferedImage image;
    ImageIcon icn;
    icn = (ImageIcon) ibImageLabelImg.getIcon();
    if (icn != null) {
        image = (BufferedImage) icn.getImage();
        specData.setImage(image);
    }
    InvBase.save(specData);
    InvBase.populateTable(ibTable);
    InvBase.populateCombo(invertsNameComboBox);
    CleanAllFields();
}//GEN-LAST:event_ibSaveButtonMouseClicked

private void ibLoadXMLButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ibLoadXMLButtonMouseClicked
    //imports xml file saved from InvBase.com
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    CleanAllFields();
    File file = XMLFilter.getXMLFile();
        
    try {
        //JTextField[] 
        JTextComponent [] jtfList = {ibIdTextField, ibCommonNameTextField, ibClassTextField,
            ibNameTextField, ibDistributionTextField, ibDiagnosisTextField, ibBiologyTextField, ibEnviromentTextField,
            ibMaxSizeTextField, ibClimateTextField, ibDangerousTextField, ibPHMinTextField, ibPHMaxTextField,
            ibDHMinTextField, ibDHMaxTextField, ibTMinTextField, ibTMaxTextField,ibSwimLevelTextField,
        ibLifeSpanTextField};
        

        AppUtil.importXML(file, jtfList);
        
    } catch (ParserConfigurationException ex) {
        Logger.getLogger(IBPanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_ibLoadXMLButtonMouseClicked

private void ibDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ibDeleteButtonMouseClicked
    /** Delete selected record on tab8 (InvBase)*/
    InvBase.deleteById(ibIdTextField.getText(),ibNameTextField.getText());
    InvBase.populateTable(ibTable);
    InvBase.populateCombo(invertsNameComboBox);
    CleanAllFields();
}//GEN-LAST:event_ibDeleteButtonMouseClicked

private void ibImageLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ibImageLoadButtonActionPerformed
    /**Load image from file to image field*/
    ibImageLabelImg.setText(null);
    Util.ImageLoadResize(ibImageLabelImg,300);
}//GEN-LAST:event_ibImageLoadButtonActionPerformed

private void ibImageDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ibImageDeleteButtonActionPerformed
    /** Clean image field*/
    ibImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
    ibImageLabelImg.setIcon(null);
}//GEN-LAST:event_ibImageDeleteButtonActionPerformed

private void ibPHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibPHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibPHMinTextFieldKeyTyped

private void ibPHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibPHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibPHMaxTextFieldKeyTyped

private void ibDHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibDHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibDHMinTextFieldKeyTyped

private void ibDHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibDHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibDHMaxTextFieldKeyTyped

private void ibTMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibTMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibTMinTextFieldKeyTyped

private void ibTMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibTMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibTMaxTextFieldKeyTyped

private void ibLifeSpanTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibLifeSpanTextFieldKeyTyped
// allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_ibLifeSpanTextFieldKeyTyped

private void ibSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ibSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { ibCommonNameTextField,ibNameTextField,ibClassTextField,
    ibLifeSpanTextField,ibSwimLevelTextField,ibMaxSizeTextField,ibAkaTextField};
    String [] dbFields = {"CommonName","Name","Class","lifeSpan","swimLevel",
        "Maxsize","Aka"}; // NOI18N 
    JTextArea [] jTFTA = {ibDiagnosisTextField,ibDistributionTextField, 
        ibBiologyTextField,ibEnviromentTextField,ibClimateTextField,
        ibDangerousTextField};
    String [] dbFieldsTA = {"Diagnosis",  "Distribution","Biology","Environment", 
        "Climate", "Dangerous"}; // NOI18N   
    JTextField [] jTFn = { ibPHMinTextField,ibPHMaxTextField,
        ibDHMinTextField,ibDHMaxTextField,ibTMinTextField,ibTMaxTextField,
        ibTDSMinTextField,ibTDSMaxTextField};
    String [] dbFieldsn = {"PHMin", "PHMax", "DHMin", "DHMax", "t_Min", "t_Max",
        "TDSmin","TDSmax"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFTA, dbFieldsTA);
    InvBase.setFilter(filter);
    InvBase.populateTable(ibTable);    
    if (InvBase.getFilter().isEmpty()){
        ibSearchState.setBackground(Global.BUTTON_GREY);
        ibSearchState.setToolTipText("");
    } else {
        ibSearchState.setBackground(Global.BUTTON_RED);
        ibSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_ibSearchButtonActionPerformed

private void ibNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ibNoSearchButtonActionPerformed
    // Reset  search
    InvBase.setFilter("");//NOI18N
    InvBase.populateTable(ibTable);    
    ibSearchState.setBackground(Global.BUTTON_GREY);
    ibSearchState.setToolTipText("");
}//GEN-LAST:event_ibNoSearchButtonActionPerformed

    private void ibTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibTableKeyReleased
        /**Populate TextFields on tab8 (InvBase) */
      refreshFields();
    }//GEN-LAST:event_ibTableKeyReleased

    private void ibTDSMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibTDSMinTextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_ibTDSMinTextFieldKeyTyped

    private void ibTDSMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ibTDSMaxTextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_ibTDSMaxTextFieldKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane IBTabbedPane1;
    private javax.swing.JLabel ibAkaLabel;
    private javax.swing.JTextField ibAkaTextField;
    private javax.swing.JLabel ibBiologyLabel;
    private javax.swing.JTextArea ibBiologyTextField;
    private javax.swing.JLabel ibClassLabel;
    private javax.swing.JTextField ibClassTextField;
    private javax.swing.JButton ibClearButton;
    private javax.swing.JLabel ibClimateLabel;
    private javax.swing.JTextArea ibClimateTextField;
    private javax.swing.JLabel ibCommonNameLabel;
    private javax.swing.JTextField ibCommonNameTextField;
    private javax.swing.JLabel ibDHMaxLabel;
    private javax.swing.JTextField ibDHMaxTextField;
    private javax.swing.JLabel ibDHMinLabel;
    private javax.swing.JTextField ibDHMinTextField;
    private javax.swing.JLabel ibDangerousLabel;
    private javax.swing.JTextArea ibDangerousTextField;
    private javax.swing.JButton ibDeleteButton;
    private javax.swing.JLabel ibDiagnosisLabel;
    private javax.swing.JTextArea ibDiagnosisTextField;
    private javax.swing.JLabel ibDistributionLabel;
    private javax.swing.JTextArea ibDistributionTextField;
    private javax.swing.JLabel ibEnviromentLabel;
    private javax.swing.JTextArea ibEnviromentTextField;
    private javax.swing.JLabel ibIdLabel;
    private javax.swing.JTextField ibIdTextField;
    private javax.swing.JButton ibImageDeleteButton;
    private javax.swing.JLabel ibImageLabelImg;
    private javax.swing.JButton ibImageLoadButton;
    private javax.swing.JLabel ibLifeSpanLabel;
    private javax.swing.JTextField ibLifeSpanTextField;
    private javax.swing.JButton ibLoadXMLButton;
    private javax.swing.JLabel ibMaxSizeLabel;
    private javax.swing.JTextField ibMaxSizeTextField;
    private javax.swing.JLabel ibNameLabel;
    private javax.swing.JTextField ibNameTextField;
    private javax.swing.JButton ibNoSearchButton;
    private javax.swing.JLabel ibPHMaxLabel;
    private javax.swing.JTextField ibPHMaxTextField;
    private javax.swing.JLabel ibPHMinLabel;
    private javax.swing.JTextField ibPHMinTextField;
    private javax.swing.JButton ibSaveButton;
    private javax.swing.JButton ibSearchButton;
    private javax.swing.JButton ibSearchState;
    private javax.swing.JLabel ibSwimLevelLabel;
    private javax.swing.JTextField ibSwimLevelTextField;
    private javax.swing.JLabel ibTDSMaxLabel;
    private javax.swing.JTextField ibTDSMaxTextField;
    private javax.swing.JLabel ibTDSMinLabel;
    private javax.swing.JTextField ibTDSMinTextField;
    private javax.swing.JLabel ibTMaxLabel;
    private javax.swing.JTextField ibTMaxTextField;
    private javax.swing.JLabel ibTMinLabel;
    private javax.swing.JTextField ibTMinTextField;
    private static javax.swing.JTable ibTable;
    private javax.swing.JScrollPane ibbioScrollPane;
    private javax.swing.JScrollPane ibclimScrollPane;
    private javax.swing.JScrollPane ibdangScrollPane;
    private javax.swing.JScrollPane ibdiagScrollPane;
    private javax.swing.JScrollPane ibdistrScrollPane;
    private javax.swing.JScrollPane ibenvScrollPane;
    private javax.swing.JToolBar ibjToolBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator8;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        ibCommonNameTextField.addMouseListener(new ContextMenuMouseListener());
        ibNameTextField.addMouseListener(new ContextMenuMouseListener());
        ibAkaTextField.addMouseListener(new ContextMenuMouseListener());
        ibClassTextField.addMouseListener(new ContextMenuMouseListener());
        ibLifeSpanTextField.addMouseListener(new ContextMenuMouseListener());
        ibMaxSizeTextField.addMouseListener(new ContextMenuMouseListener());
        ibSwimLevelTextField.addMouseListener(new ContextMenuMouseListener());
        ibPHMinTextField.addMouseListener(new ContextMenuMouseListener());
        ibPHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        ibDHMinTextField.addMouseListener(new ContextMenuMouseListener());
        ibDHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        ibTMinTextField.addMouseListener(new ContextMenuMouseListener());
        ibTMaxTextField.addMouseListener(new ContextMenuMouseListener());
        ibDiagnosisTextField.addMouseListener(new ContextMenuMouseListener());
        ibDistributionTextField.addMouseListener(new ContextMenuMouseListener());
        ibBiologyTextField.addMouseListener(new ContextMenuMouseListener());
        ibClimateTextField.addMouseListener(new ContextMenuMouseListener());
        ibEnviromentTextField.addMouseListener(new ContextMenuMouseListener());
        ibDangerousTextField.addMouseListener(new ContextMenuMouseListener());
        ibAkaTextField.addMouseListener(new ContextMenuMouseListener());
        ibTDSMinTextField.addMouseListener(new ContextMenuMouseListener());
        ibTDSMaxTextField.addMouseListener(new ContextMenuMouseListener());
    }
}

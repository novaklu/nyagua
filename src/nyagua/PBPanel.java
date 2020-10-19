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
 * Plant Base Panel.java
 *
 * Created on 6-giu-2012, 13.36.45
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import nyagua.data.PlantBase;
import nyagua.data.Setting;

/**
 *
 * @author rudigiacomini
 */
public class PBPanel extends javax.swing.JPanel {
    private static JComboBox plantsNameComboBox;
 
      
    //Foreground, Mid-ground, Background, 'Blank'
    public static final String FOREGROUND = 
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Foreground");
    public static final String MIDGROUND = 
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Midground");
    public static final String BACKGROUND = 
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Background");
    public static final String FLOATING = 
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Floating");
    
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
            } else if (e.getID()==Watched.REQUEST_POPULATE_PBTABLE){
                populateTable();
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            }
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    /** Creates new form PBPanel */
    public PBPanel() {
        initComponents();
        initCutAndPaste();  
        populateList();
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }
    
    public static void populateList(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        pbPlacementComboBox.setModel(dcm);
        List <String> defaults=new ArrayList<String>();
        defaults.add(FOREGROUND);
        defaults.add(MIDGROUND);
        defaults.add(BACKGROUND);
        defaults.add(FLOATING);
         PlantBase.populateCombo(pbPlacementComboBox,"Placement", defaults);
    }
    
    public static void setAssociatedCombo (JComboBox pnC){
        plantsNameComboBox=pnC;
    }
    
    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList9 = {pbIdTextField, pbNameTextField, pbFamilyTextField,
                    pbDistributionTextField, pbHightTextField, pbWidthTextField, pbLigthTextField, pbGrowthTextField,
                    pbDemandsTextField, pbPHMinTextField, pbPHMaxTextField, pbDHMinTextField, pbDHMaxTextField,
                    pbTMinTextField, pbTMaxTextField,pbAkaTextField};
                Util.CleanTextFields(jtfList9);
                pbNoteTextArea.setText("");//NOI18N
                pbAquaticCheckBox.setSelected(false);
                pbCo2CheckBox.setSelected(false);
                pbPlacementComboBox.setSelectedItem("");
                pbImageLabelImg.setIcon(null);
                pbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));                
    }
    
    /** populate the table*/
    static private void populateTable(){
        PlantBase.populateTable(pbTable);
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
        pbTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("pbtable", PlantBase.CAPTIONS.length);//NOI18N
        PlantBase.setColWidth(widths);
        Util.setColSizes(pbTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("pbtable", pbTable);//NOI18N
    }

    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){
        int recId = TablesUtil.getIdFromTable(pbTable, pbTable.getSelectedRow());        
        PlantBase specData=PlantBase.getById(recId);
        pbIdTextField.setText(Integer.toString(specData.getId()));// NOI18N
        pbNameTextField.setText(specData.getName());// NOI18N
        pbFamilyTextField.setText(specData.getFamily());// NOI18N
        pbDistributionTextField.setText(specData.getDistribution());// NOI18N
        pbHightTextField.setText(specData.getHeight());// NOI18N
        pbWidthTextField.setText(specData.getWidth());// NOI18N
        pbLigthTextField.setText(specData.getLight());// NOI18N
        pbGrowthTextField.setText(specData.getGrowth());// NOI18N
        pbDemandsTextField.setText(specData.getDemands());// NOI18N
        pbPHMinTextField.setText(specData.getPhMin());// NOI18N
        pbPHMaxTextField.setText(specData.getPhMax());// NOI18N
        pbDHMinTextField.setText(specData.getDhMin());// NOI18N
        pbDHMaxTextField.setText(specData.getDhMax());// NOI18N
        pbTMinTextField.setText(specData.getTempMin());// NOI18N
        pbTMaxTextField.setText(specData.getTempMax());// NOI18N
        pbAquaticCheckBox.setSelected(specData.isAquatic());
        pbNoteTextArea.setText(specData.getNote());
        pbCo2CheckBox.setSelected(specData.isCO2Required());
        pbAkaTextField.setText(specData.getAKA());// NOI18N
        BufferedImage img = null;
        pbImageLabelImg.setText(null);
        if (specData.hasImage()){
            //pbImageLabelImg.setIcon((new javax.swing.ImageIcon(specData.getImage())));
            Util.ImageDisplayResize(specData.getImage(), pbImageLabelImg, 300);
        } else {
            pbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
            pbImageLabelImg.setIcon(null);
        } 
        populateList();
        pbPlacementComboBox.setSelectedItem(specData.getPlacement());
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

        jToolBar10 = new javax.swing.JToolBar();
        pbClearButton = new javax.swing.JButton();
        pbSaveButton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        pbDeleteButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        pbSearchButton = new javax.swing.JButton();
        pbSearchState = new javax.swing.JButton();
        pbNoSearchButton = new javax.swing.JButton();
        pbNameLabel = new javax.swing.JLabel();
        pbNameTextField = new javax.swing.JTextField();
        pbIdTextField = new javax.swing.JTextField();
        pbIdLabel = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pbPlacementLabel = new javax.swing.JLabel();
        pbLigthTextField = new javax.swing.JTextField();
        pbDHMinTextField = new javax.swing.JTextField();
        pbFamilyLabel = new javax.swing.JLabel();
        pbHightTextField = new javax.swing.JTextField();
        pbPlacementComboBox = new javax.swing.JComboBox();
        jScrollPane14 = new javax.swing.JScrollPane();
        pbTable = new javax.swing.JTable();
        pbGrowthTextField = new javax.swing.JTextField();
        pbDistributionTextField = new javax.swing.JTextField();
        pbTMinLabel = new javax.swing.JLabel();
        pbDHMaxTextField = new javax.swing.JTextField();
        pbGrowthLabel = new javax.swing.JLabel();
        pbLigthLabel = new javax.swing.JLabel();
        pbImageDeleteButton = new javax.swing.JButton();
        pbFamilyTextField = new javax.swing.JTextField();
        pbWidthLabel = new javax.swing.JLabel();
        pbDHMinLabel = new javax.swing.JLabel();
        pbDemandsLabel = new javax.swing.JLabel();
        pbPHMinLabel = new javax.swing.JLabel();
        pbDHMaxLabel = new javax.swing.JLabel();
        pbPHMinTextField = new javax.swing.JTextField();
        pbAquaticCheckBox = new javax.swing.JCheckBox();
        pbTMinTextField = new javax.swing.JTextField();
        pbImageLoadButton = new javax.swing.JButton();
        pbTMaxTextField = new javax.swing.JTextField();
        pbHightLabel = new javax.swing.JLabel();
        pbDistributionLabel = new javax.swing.JLabel();
        pbPHMaxLabel = new javax.swing.JLabel();
        pbDemandsTextField = new javax.swing.JTextField();
        pbPHMaxTextField = new javax.swing.JTextField();
        jScrollPane15 = new javax.swing.JScrollPane();
        pbImageLabelImg = new javax.swing.JLabel();
        pbWidthTextField = new javax.swing.JTextField();
        pbTMaxLabel = new javax.swing.JLabel();
        pbAkaLabel = new javax.swing.JLabel();
        pbAkaTextField = new javax.swing.JTextField();
        pbCo2CheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        pbNoteLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pbNoteTextArea = new javax.swing.JTextArea();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setPreferredSize(new java.awt.Dimension(672, 617));

        jToolBar10.setFloatable(false);
        jToolBar10.setRollover(true);
        jToolBar10.setMaximumSize(new java.awt.Dimension(346, 48));
        jToolBar10.setMinimumSize(new java.awt.Dimension(346, 48));
        jToolBar10.setPreferredSize(new java.awt.Dimension(302, 48));

        pbClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        pbClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        pbClearButton.setFocusable(false);
        pbClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pbClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pbClearButtonMouseClicked(evt);
            }
        });
        jToolBar10.add(pbClearButton);

        pbSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        pbSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        pbSaveButton.setFocusable(false);
        pbSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pbSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pbSaveButtonMouseClicked(evt);
            }
        });
        jToolBar10.add(pbSaveButton);
        jToolBar10.add(jSeparator9);

        pbDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        pbDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        pbDeleteButton.setFocusable(false);
        pbDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pbDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pbDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar10.add(pbDeleteButton);
        jToolBar10.add(jSeparator16);

        pbSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        pbSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        pbSearchButton.setFocusable(false);
        pbSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pbSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbSearchButtonActionPerformed(evt);
            }
        });
        jToolBar10.add(pbSearchButton);

        pbSearchState.setFocusable(false);
        pbSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        pbSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        pbSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        pbSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar10.add(pbSearchState);

        pbNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        pbNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        pbNoSearchButton.setFocusable(false);
        pbNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pbNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar10.add(pbNoSearchButton);

        pbNameLabel.setText(bundle.getString("Ny.pbNameLabel.text")); // NOI18N

        pbNameTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbNameTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        pbIdTextField.setEditable(false);
        pbIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        pbIdLabel.setText(bundle.getString("ID_")); // NOI18N

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(973, 695));

        jPanel1.setMinimumSize(new java.awt.Dimension(658, 321));
        jPanel1.setPreferredSize(new java.awt.Dimension(973, 695));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        pbPlacementLabel.setText(bundle.getString("placement")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 2);
        jPanel1.add(pbPlacementLabel, gridBagConstraints);

        pbLigthTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbLigthTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbLigthTextField, gridBagConstraints);

        pbDHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbDHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbDHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbDHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDHMinTextField, gridBagConstraints);

        pbFamilyLabel.setText(bundle.getString("Ny.pbFamilyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbFamilyLabel, gridBagConstraints);

        pbHightTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbHightTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 28;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbHightTextField, gridBagConstraints);

        pbPlacementComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 15);
        jPanel1.add(pbPlacementComboBox, gridBagConstraints);

        pbTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        pbTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        pbTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pbTableMouseClicked(evt);
            }
        });
        pbTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pbTableKeyReleased(evt);
            }
        });
        jScrollPane14.setViewportView(pbTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 480;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 15, 15);
        jPanel1.add(jScrollPane14, gridBagConstraints);

        pbGrowthTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbGrowthTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbGrowthTextField, gridBagConstraints);

        pbDistributionTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbDistributionTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbDistributionTextField, gridBagConstraints);

        pbTMinLabel.setText(bundle.getString("Ny.pbTMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        jPanel1.add(pbTMinLabel, gridBagConstraints);

        pbDHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbDHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbDHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbDHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDHMaxTextField, gridBagConstraints);

        pbGrowthLabel.setText(bundle.getString("Ny.pbGrowthLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbGrowthLabel, gridBagConstraints);

        pbLigthLabel.setText(bundle.getString("Ny.pbLigthLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbLigthLabel, gridBagConstraints);

        pbImageDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/archive-remove.png"))); // NOI18N
        pbImageDeleteButton.setToolTipText(bundle.getString("Clear_image")); // NOI18N
        pbImageDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbImageDeleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 45);
        jPanel1.add(pbImageDeleteButton, gridBagConstraints);

        pbFamilyTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbFamilyTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbFamilyTextField, gridBagConstraints);

        pbWidthLabel.setText(bundle.getString("WIDTH_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbWidthLabel, gridBagConstraints);

        pbDHMinLabel.setText(bundle.getString("Ny.pbDHMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDHMinLabel, gridBagConstraints);

        pbDemandsLabel.setText(bundle.getString("Ny.pbDemandsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDemandsLabel, gridBagConstraints);

        pbPHMinLabel.setText(bundle.getString("Ny.pbPHMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbPHMinLabel, gridBagConstraints);

        pbDHMaxLabel.setText(bundle.getString("Ny.pbDHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDHMaxLabel, gridBagConstraints);

        pbPHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbPHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbPHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbPHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbPHMinTextField, gridBagConstraints);

        pbAquaticCheckBox.setText(bundle.getString("True_Aquatic")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbAquaticCheckBox, gridBagConstraints);

        pbTMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbTMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbTMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbTMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        jPanel1.add(pbTMinTextField, gridBagConstraints);

        pbImageLoadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/insert-image.png"))); // NOI18N
        pbImageLoadButton.setToolTipText(bundle.getString("Load_an_image")); // NOI18N
        pbImageLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbImageLoadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 0);
        jPanel1.add(pbImageLoadButton, gridBagConstraints);

        pbTMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbTMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbTMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbTMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        jPanel1.add(pbTMaxTextField, gridBagConstraints);

        pbHightLabel.setText(bundle.getString("Ny.pbHightLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbHightLabel, gridBagConstraints);

        pbDistributionLabel.setText(bundle.getString("Ny.pbDistributionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbDistributionLabel, gridBagConstraints);

        pbPHMaxLabel.setText(bundle.getString("Ny.pbPHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbPHMaxLabel, gridBagConstraints);

        pbDemandsTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbDemandsTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbDemandsTextField, gridBagConstraints);

        pbPHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbPHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        pbPHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pbPHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbPHMaxTextField, gridBagConstraints);

        jScrollPane15.setMaximumSize(new java.awt.Dimension(63, 15));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(63, 15));

        pbImageLabelImg.setForeground(new java.awt.Color(255, 102, 51));
        pbImageLabelImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pbImageLabelImg.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        jScrollPane15.setViewportView(pbImageLabelImg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 15);
        jPanel1.add(jScrollPane15, gridBagConstraints);

        pbWidthTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbWidthTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 28;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbWidthTextField, gridBagConstraints);

        pbTMaxLabel.setText(bundle.getString("Ny.pbTMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        jPanel1.add(pbTMaxLabel, gridBagConstraints);

        pbAkaLabel.setText(bundle.getString("AKA")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbAkaLabel, gridBagConstraints);

        pbAkaTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        pbAkaTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        jPanel1.add(pbAkaTextField, gridBagConstraints);

        pbCo2CheckBox.setText(bundle.getString("CO2.Required")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(pbCo2CheckBox, gridBagConstraints);

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/icons/plants.png")), jPanel1); // NOI18N

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        pbNoteLabel.setText(bundle.getString("NOTES")); // NOI18N
        pbNoteLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(pbNoteLabel, gridBagConstraints);

        jScrollPane1.setAlignmentX(0.0F);
        jScrollPane1.setAlignmentY(0.0F);

        pbNoteTextArea.setColumns(20);
        pbNoteTextArea.setLineWrap(true);
        pbNoteTextArea.setRows(5);
        pbNoteTextArea.setWrapStyleWord(true);
        pbNoteTextArea.setAlignmentX(0.0F);
        pbNoteTextArea.setAlignmentY(0.0F);
        jScrollPane1.setViewportView(pbNoteTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 15);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jTabbedPane1.addTab("", new javax.swing.ImageIcon(getClass().getResource("/icons/sheets.png")), jPanel2); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pbIdLabel)
                .addGap(10, 10, 10)
                .addComponent(pbIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addComponent(pbNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pbNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar10, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbIdLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pbIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pbNameLabel)
                        .addComponent(pbNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void pbTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pbTableMouseClicked
    /**Populate TextFields on tab9 (PlantsBase) */
    refreshFields();
}//GEN-LAST:event_pbTableMouseClicked

private void pbClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pbClearButtonMouseClicked
    /** Cleans all textFields on tab9 (PlantsBase)*/
    CleanAllFields();
}//GEN-LAST:event_pbClearButtonMouseClicked

private void pbSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pbSaveButtonMouseClicked
    /**Insert record on db for table plantsbase or update it if existing*/
    /*if (Global.AqID == 0) {
        Util.msgSelectAquarium();
        return;
    }*/
    String currID = pbIdTextField.getText();
    PlantBase specData=new PlantBase();
    if (currID == null || currID.equals("")) {
        specData.setId(0);
        } else {
        specData.setId(Integer.valueOf(currID));
    }
    specData.setName(pbNameTextField.getText());
    specData.setFamily(pbFamilyTextField.getText());
    specData.setDistribution(pbDistributionTextField.getText());
    specData.setHeight(pbHightTextField.getText());
    specData.setWidth(pbWidthTextField.getText());
    specData.setLight(pbLigthTextField.getText());
    specData.setGrowth(pbGrowthTextField.getText());
    specData.setDemands(pbDemandsTextField.getText());
    specData.setPhMin(LocUtil.delocalizeDouble(pbPHMinTextField.getText()));
    specData.setPhMax(LocUtil.delocalizeDouble(pbPHMaxTextField.getText()));
    specData.setDhMin(LocUtil.delocalizeDouble(pbDHMinTextField.getText()));
    specData.setDhMax(LocUtil.delocalizeDouble(pbDHMaxTextField.getText()));
    specData.setTempMin(LocUtil.delocalizeDouble(pbTMinTextField.getText()));
    specData.setTempMax(LocUtil.delocalizeDouble(pbTMaxTextField.getText()));
    specData.setAquatic(pbAquaticCheckBox.isSelected());
    specData.setNote(pbNoteTextArea.getText());
    specData.setAka(pbAkaTextField.getText());
    specData.setCO2Required(pbCo2CheckBox.isSelected());
    if (pbPlacementComboBox.getSelectedItem() !=null) {
         specData.setPlacement(pbPlacementComboBox.getSelectedItem().toString());
    }
   
    //save image

    BufferedImage image = null;
    ImageIcon icn = null;
    icn = (ImageIcon) pbImageLabelImg.getIcon();
    if (icn != null) {
        image = (BufferedImage) icn.getImage();
        specData.setImage(image);
    }
    PlantBase.save(specData);
    PlantBase.populateTable(pbTable);
    PlantBase.populateCombo(plantsNameComboBox);    
    populateList();
    CleanAllFields();
}//GEN-LAST:event_pbSaveButtonMouseClicked

private void pbDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pbDeleteButtonMouseClicked
    /** Delete selected record on tab9 (PlantsBase)*/
    PlantBase.deleteById(pbIdTextField.getText(),pbNameTextField.getText());
    PlantBase.populateTable(pbTable);
    PlantBase.populateCombo(plantsNameComboBox);
    populateList();
    CleanAllFields();
}//GEN-LAST:event_pbDeleteButtonMouseClicked

private void pbImageLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbImageLoadButtonActionPerformed
    /**Load image from file to image field*/
    pbImageLabelImg.setText(null);
    Util.ImageLoadResize(pbImageLabelImg,300);
}//GEN-LAST:event_pbImageLoadButtonActionPerformed

private void pbImageDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbImageDeleteButtonActionPerformed
    /** Clean image field*/
    pbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
    pbImageLabelImg.setIcon(null);
}//GEN-LAST:event_pbImageDeleteButtonActionPerformed

private void pbPHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbPHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbPHMinTextFieldKeyTyped

private void pbPHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbPHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbPHMaxTextFieldKeyTyped

private void pbDHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbDHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbDHMinTextFieldKeyTyped

private void pbDHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbDHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbDHMaxTextFieldKeyTyped

private void pbTMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbTMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbTMinTextFieldKeyTyped

private void pbTMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbTMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_pbTMaxTextFieldKeyTyped

private void pbSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { pbNameTextField,pbFamilyTextField,pbDistributionTextField,
    pbLigthTextField,pbGrowthTextField,pbDemandsTextField,pbAkaTextField};
    String [] dbFields = {"Name","Family", "Distribution", "Light", "Growth", "Demands","Aka"}; // NOI18N 
    JTextField [] jTFn = { pbHightTextField,pbWidthTextField, pbPHMinTextField,pbPHMaxTextField,
    pbDHMinTextField,pbDHMaxTextField,pbTMinTextField,pbTMaxTextField};
    String [] dbFieldsn = { "Height", "Width","PHMin", "PHMax", "DHMin", "DHMax", "t_Min", "t_Max"}; // NOI18N  
    JComboBox [] jTFC = {pbPlacementComboBox };
    JTextArea [] jTAF = { pbNoteTextArea};
    String [] dbFieldsTA = {"Note"}; // NOI18N     
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createFilter(jTAF, dbFieldsTA);
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    PlantBase.setFilter(filter);
    PlantBase.populateTable(pbTable);    
    if (PlantBase.getFilter().isEmpty()){
        pbSearchState.setBackground(Global.BUTTON_GREY);
        pbSearchState.setToolTipText("");
    } else {
        pbSearchState.setBackground(Global.BUTTON_RED);
        pbSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_pbSearchButtonActionPerformed

private void pbNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbNoSearchButtonActionPerformed
    // Reset  search
    PlantBase.setFilter("");//NOI18N
    PlantBase.populateTable(pbTable);    
    pbSearchState.setBackground(Global.BUTTON_GREY);
    pbSearchState.setToolTipText("");
}//GEN-LAST:event_pbNoSearchButtonActionPerformed

    private void pbTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pbTableKeyReleased
        /**Populate TextFields on tab9 (PlantsBase) */
        refreshFields();
    }//GEN-LAST:event_pbTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JLabel pbAkaLabel;
    private javax.swing.JTextField pbAkaTextField;
    private javax.swing.JCheckBox pbAquaticCheckBox;
    private javax.swing.JButton pbClearButton;
    private javax.swing.JCheckBox pbCo2CheckBox;
    private javax.swing.JLabel pbDHMaxLabel;
    private javax.swing.JTextField pbDHMaxTextField;
    private javax.swing.JLabel pbDHMinLabel;
    private javax.swing.JTextField pbDHMinTextField;
    private javax.swing.JButton pbDeleteButton;
    private javax.swing.JLabel pbDemandsLabel;
    private javax.swing.JTextField pbDemandsTextField;
    private javax.swing.JLabel pbDistributionLabel;
    private javax.swing.JTextField pbDistributionTextField;
    private javax.swing.JLabel pbFamilyLabel;
    private javax.swing.JTextField pbFamilyTextField;
    private javax.swing.JLabel pbGrowthLabel;
    private javax.swing.JTextField pbGrowthTextField;
    private javax.swing.JLabel pbHightLabel;
    private javax.swing.JTextField pbHightTextField;
    private javax.swing.JLabel pbIdLabel;
    private javax.swing.JTextField pbIdTextField;
    private javax.swing.JButton pbImageDeleteButton;
    private javax.swing.JLabel pbImageLabelImg;
    private javax.swing.JButton pbImageLoadButton;
    private javax.swing.JLabel pbLigthLabel;
    private javax.swing.JTextField pbLigthTextField;
    private javax.swing.JLabel pbNameLabel;
    private javax.swing.JTextField pbNameTextField;
    private javax.swing.JButton pbNoSearchButton;
    private javax.swing.JLabel pbNoteLabel;
    private javax.swing.JTextArea pbNoteTextArea;
    private javax.swing.JLabel pbPHMaxLabel;
    private javax.swing.JTextField pbPHMaxTextField;
    private javax.swing.JLabel pbPHMinLabel;
    private javax.swing.JTextField pbPHMinTextField;
    private static javax.swing.JComboBox pbPlacementComboBox;
    private javax.swing.JLabel pbPlacementLabel;
    private javax.swing.JButton pbSaveButton;
    private javax.swing.JButton pbSearchButton;
    private javax.swing.JButton pbSearchState;
    private javax.swing.JLabel pbTMaxLabel;
    private javax.swing.JTextField pbTMaxTextField;
    private javax.swing.JLabel pbTMinLabel;
    private javax.swing.JTextField pbTMinTextField;
    private static javax.swing.JTable pbTable;
    private javax.swing.JLabel pbWidthLabel;
    private javax.swing.JTextField pbWidthTextField;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        
        pbNameTextField.addMouseListener(new ContextMenuMouseListener());
        pbAkaTextField.addMouseListener(new ContextMenuMouseListener());
        pbFamilyTextField.addMouseListener(new ContextMenuMouseListener());
        pbDistributionTextField.addMouseListener(new ContextMenuMouseListener());
        pbHightTextField.addMouseListener(new ContextMenuMouseListener());
        pbWidthTextField.addMouseListener(new ContextMenuMouseListener());
        pbLigthTextField.addMouseListener(new ContextMenuMouseListener());
        pbGrowthTextField.addMouseListener(new ContextMenuMouseListener());
        pbDemandsTextField.addMouseListener(new ContextMenuMouseListener());
        pbPHMinTextField.addMouseListener(new ContextMenuMouseListener());
        pbPHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        pbDHMinTextField.addMouseListener(new ContextMenuMouseListener());
        pbDHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        pbTMinTextField.addMouseListener(new ContextMenuMouseListener());
        pbTMaxTextField.addMouseListener(new ContextMenuMouseListener());
        pbNoteTextArea.addMouseListener(new ContextMenuMouseListener());
    }
}

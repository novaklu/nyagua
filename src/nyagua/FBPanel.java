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
 * Fish Base Panel.java
 *
 * Created on 11-giu-2012, 13.36.53
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.Image;
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
import nyagua.data.FishBase;
import nyagua.data.Setting;
import org.jsoup.nodes.Document;

/**
 *
 * @author rudigiacomini
 */
public class FBPanel extends javax.swing.JPanel {
    
    private static JComboBox fishNameComboBox;
    
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
            } else if (e.getID()==Watched.REQUEST_POPULATE_FBTABLE){
                populateTable();
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            }
        }
    };            
    Watcher settingWatch=new Watcher(al);

    /** Creates new form FBPanel */
    public FBPanel() {
        initComponents();
        initCutAndPaste(); 
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }
    
    public static void setAssociatedCombo (JComboBox fnC){
        fishNameComboBox=fnC;
    }
    
    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextComponent[] jtfList = {fbIdTextField, fbCommonNameTextField, fbClassTextField,
                    fbNameTextField, fbDistributionTextField, fbDiagnosisTextField, fbBiologyTextField, fbEnviromentTextField,
                    fbMaxSizeTextField, fbClimateTextField, fbDangerousTextField, fbPHMinTextField, fbPHMaxTextField,
                    fbDHMinTextField, fbDHMaxTextField, fbTMinTextField, fbTMaxTextField, fbSwimLevelTextField, fbLifeSpanTextField,
                    fbAkaTextField};
        Util.CleanTextFields(jtfList);        
        fbImageLabelImg.setIcon(null);
        fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));            
    }
    
    /** populate the table*/
    private static void populateTable(){
        FishBase.populateTable(fbTable);
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
        fbTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("pbtable", FishBase.CAPTIONS.length);//NOI18N
        FishBase.setColWidth(widths);
        Util.setColSizes(fbTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("fbtable", fbTable);//NOI18N
    }

    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){        
        int recId = TablesUtil.getIdFromTable(fbTable, fbTable.getSelectedRow());
        FishBase specData=FishBase.getById(recId);
        fbIdTextField.setText(Integer.toString(specData.getId()));// NOI18N
        fbCommonNameTextField.setText(specData.getCommonName());// NOI18N
        fbClassTextField.setText(specData.getType());// NOI18N
        fbNameTextField.setText(specData.getName());// NOI18N
        fbDistributionTextField.setText(specData.getDistribution());// NOI18N
        fbDiagnosisTextField.setText(specData.getDiagnosis());// NOI18N    
        fbBiologyTextField.setText(specData.getBiology());// NOI18N
        fbEnviromentTextField.setText(specData.getEnvironment());// NOI18N
        fbMaxSizeTextField.setText(specData.getMaxSize());// NOI18N
        fbClimateTextField.setText(specData.getClimate());// NOI18N
        fbDangerousTextField.setText(specData.getDangerous());// NOI18N
        fbPHMinTextField.setText(specData.getPhMin());// NOI18N
        fbPHMaxTextField.setText(specData.getPhMax());// NOI18N
        fbDHMinTextField.setText(specData.getDhMin());// NOI18N
        fbDHMaxTextField.setText(specData.getDhMax());// NOI18N
        fbTMinTextField.setText(specData.getTempMin());// NOI18N
        fbTMaxTextField.setText(specData.getTempMax());// NOI18N    
        fbSwimLevelTextField.setText(specData.getSwimLevel());// NOI18N
        fbLifeSpanTextField.setText(specData.getLifeSpam());// NOI18N
        fbAkaTextField.setText(specData.getAKA());// NOI18N
        BufferedImage img = null;
        fbImageLabelImg.setText(null);
        if (specData.hasImage()){
            //fbImageLabelImg.setIcon((new javax.swing.ImageIcon(specData.getImage())));
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

        fbIdLabel = new javax.swing.JLabel();
        fbIdTextField = new javax.swing.JTextField();
        fbCommonNameLabel = new javax.swing.JLabel();
        fbCommonNameTextField = new javax.swing.JTextField();
        FBTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        fbClassLabel = new javax.swing.JLabel();
        fbClassTextField = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        fbTable = new javax.swing.JTable();
        fbNameLabel = new javax.swing.JLabel();
        fbNameTextField = new javax.swing.JTextField();
        fbImageLoadButton = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        fbImageLabelImg = new javax.swing.JLabel();
        fbImageDeleteButton = new javax.swing.JButton();
        fbMaxSizeTextField = new javax.swing.JTextField();
        fbMaxSizeLabel = new javax.swing.JLabel();
        fbPHMinLabel = new javax.swing.JLabel();
        fbPHMinTextField = new javax.swing.JTextField();
        fbPHMaxLabel = new javax.swing.JLabel();
        fbPHMaxTextField = new javax.swing.JTextField();
        fbDHMinLabel = new javax.swing.JLabel();
        fbDHMinTextField = new javax.swing.JTextField();
        fbDHMaxLabel = new javax.swing.JLabel();
        fbDHMaxTextField = new javax.swing.JTextField();
        fbTMinLabel = new javax.swing.JLabel();
        fbTMinTextField = new javax.swing.JTextField();
        fbTMaxLabel = new javax.swing.JLabel();
        fbTMaxTextField = new javax.swing.JTextField();
        fbSwimLevelLabel = new javax.swing.JLabel();
        fbSwimLevelTextField = new javax.swing.JTextField();
        fbLifeSpanLabel = new javax.swing.JLabel();
        fbLifeSpanTextField = new javax.swing.JTextField();
        fbAkaLabel = new javax.swing.JLabel();
        fbAkaTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        fbDangerousLabel = new javax.swing.JLabel();
        fbClimateLabel = new javax.swing.JLabel();
        fbEnviromentLabel = new javax.swing.JLabel();
        fbBiologyLabel = new javax.swing.JLabel();
        fbDistributionLabel = new javax.swing.JLabel();
        fbDiagnosisLabel = new javax.swing.JLabel();
        fbdiagScrollPane = new javax.swing.JScrollPane();
        fbDiagnosisTextField = new javax.swing.JTextArea();
        fbdangScrollPane = new javax.swing.JScrollPane();
        fbDangerousTextField = new javax.swing.JTextArea();
        fbenvScrollPane = new javax.swing.JScrollPane();
        fbEnviromentTextField = new javax.swing.JTextArea();
        fbclimScrollPane = new javax.swing.JScrollPane();
        fbClimateTextField = new javax.swing.JTextArea();
        fbbioScrollPane = new javax.swing.JScrollPane();
        fbBiologyTextField = new javax.swing.JTextArea();
        fbdistrScrollPane = new javax.swing.JScrollPane();
        fbDistributionTextField = new javax.swing.JTextArea();
        jToolBar9 = new javax.swing.JToolBar();
        fbClearButton = new javax.swing.JButton();
        fbSaveButton = new javax.swing.JButton();
        fbLoadXMLButton = new javax.swing.JButton();
        fbWebLoadXMLButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        fbDeleteButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        fbSearchButton = new javax.swing.JButton();
        fbSearchState = new javax.swing.JButton();
        fbNoSearchButton = new javax.swing.JButton();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMinimumSize(new java.awt.Dimension(0, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        fbIdLabel.setText(bundle.getString("ID_")); // NOI18N

        fbIdTextField.setEditable(false);
        fbIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        fbCommonNameLabel.setText(bundle.getString("Ny.fbCommonNameLabel.text")); // NOI18N

        fbCommonNameTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbCommonNameTextField.setPreferredSize(new java.awt.Dimension(80, 19));

        FBTabbedPane1.setAlignmentX(0.0F);
        FBTabbedPane1.setAlignmentY(0.0F);
        FBTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        FBTabbedPane1.setPreferredSize(new java.awt.Dimension(1096, 951));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        fbClassLabel.setText(bundle.getString("Ny.fbClassLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbClassLabel, gridBagConstraints);

        fbClassTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbClassTextField.setName(""); // NOI18N
        fbClassTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbClassTextField, gridBagConstraints);

        fbTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        fbTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        fbTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbTableMouseClicked(evt);
            }
        });
        fbTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fbTableKeyReleased(evt);
            }
        });
        jScrollPane12.setViewportView(fbTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 500;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 15, 15);
        jPanel1.add(jScrollPane12, gridBagConstraints);

        fbNameLabel.setText(bundle.getString("Ny.fbNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbNameLabel, gridBagConstraints);

        fbNameTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbNameTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbNameTextField, gridBagConstraints);

        fbImageLoadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/insert-image.png"))); // NOI18N
        fbImageLoadButton.setToolTipText(bundle.getString("Load_an_image")); // NOI18N
        fbImageLoadButton.setMaximumSize(new java.awt.Dimension(44, 44));
        fbImageLoadButton.setMinimumSize(new java.awt.Dimension(44, 44));
        fbImageLoadButton.setPreferredSize(new java.awt.Dimension(44, 44));
        fbImageLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbImageLoadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        jPanel1.add(fbImageLoadButton, gridBagConstraints);

        jScrollPane13.setMaximumSize(new java.awt.Dimension(63, 15));
        jScrollPane13.setPreferredSize(new java.awt.Dimension(63, 15));

        fbImageLabelImg.setForeground(new java.awt.Color(255, 102, 51));
        fbImageLabelImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fbImageLabelImg.setText(bundle.getString("NO_IMAGE!")); // NOI18N
        jScrollPane13.setViewportView(fbImageLabelImg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 15);
        jPanel1.add(jScrollPane13, gridBagConstraints);

        fbImageDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/archive-remove.png"))); // NOI18N
        fbImageDeleteButton.setToolTipText(bundle.getString("Clear_image")); // NOI18N
        fbImageDeleteButton.setMaximumSize(new java.awt.Dimension(44, 44));
        fbImageDeleteButton.setMinimumSize(new java.awt.Dimension(44, 44));
        fbImageDeleteButton.setPreferredSize(new java.awt.Dimension(44, 44));
        fbImageDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbImageDeleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 15);
        jPanel1.add(fbImageDeleteButton, gridBagConstraints);

        fbMaxSizeTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbMaxSizeTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbMaxSizeTextField, gridBagConstraints);

        fbMaxSizeLabel.setText(bundle.getString("Ny.fbMaxSizeLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbMaxSizeLabel, gridBagConstraints);

        fbPHMinLabel.setText(bundle.getString("Ny.fbPHMinLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbPHMinLabel, gridBagConstraints);

        fbPHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbPHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbPHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbPHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbPHMinTextField, gridBagConstraints);

        fbPHMaxLabel.setText(bundle.getString("Ny.fbPHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbPHMaxLabel, gridBagConstraints);

        fbPHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbPHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbPHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbPHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbPHMaxTextField, gridBagConstraints);

        fbDHMinLabel.setText(bundle.getString("Ny.fbDHMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbDHMinLabel, gridBagConstraints);

        fbDHMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbDHMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbDHMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbDHMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbDHMinTextField, gridBagConstraints);

        fbDHMaxLabel.setText(bundle.getString("Ny.fbDHMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbDHMaxLabel, gridBagConstraints);

        fbDHMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbDHMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbDHMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbDHMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbDHMaxTextField, gridBagConstraints);

        fbTMinLabel.setText(bundle.getString("Ny.fbTMinLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbTMinLabel, gridBagConstraints);

        fbTMinTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbTMinTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbTMinTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbTMinTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbTMinTextField, gridBagConstraints);

        fbTMaxLabel.setText(bundle.getString("Ny.fbTMaxLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbTMaxLabel, gridBagConstraints);

        fbTMaxTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbTMaxTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fbTMaxTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbTMaxTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbTMaxTextField, gridBagConstraints);

        fbSwimLevelLabel.setText(bundle.getString("Ny.fbSwimLevelLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbSwimLevelLabel, gridBagConstraints);

        fbSwimLevelTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbSwimLevelTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbSwimLevelTextField, gridBagConstraints);

        fbLifeSpanLabel.setText(bundle.getString("Ny.fbLifeSpanLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 1, 3, 2);
        jPanel1.add(fbLifeSpanLabel, gridBagConstraints);

        fbLifeSpanTextField.setMinimumSize(new java.awt.Dimension(50, 19));
        fbLifeSpanTextField.setPreferredSize(new java.awt.Dimension(50, 19));
        fbLifeSpanTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fbLifeSpanTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbLifeSpanTextField, gridBagConstraints);

        fbAkaLabel.setText(bundle.getString("AKA")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel1.add(fbAkaLabel, gridBagConstraints);

        fbAkaTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fbAkaTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 15);
        jPanel1.add(fbAkaTextField, gridBagConstraints);

        FBTabbedPane1.addTab(" ", new javax.swing.ImageIcon(getClass().getResource("/icons/fish.png")), jPanel1); // NOI18N

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setAutoscrolls(true);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        fbDangerousLabel.setText(bundle.getString("Ny.fbDangerousLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(fbDangerousLabel, gridBagConstraints);

        fbClimateLabel.setText(bundle.getString("Ny.fbClimateLabel.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(fbClimateLabel, gridBagConstraints);

        fbEnviromentLabel.setText(bundle.getString("Ny.fbEnviromentLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(fbEnviromentLabel, gridBagConstraints);

        fbBiologyLabel.setText(bundle.getString("Ny.fbBiologyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(fbBiologyLabel, gridBagConstraints);

        fbDistributionLabel.setText(bundle.getString("Ny.fbDistributionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 3, 5);
        jPanel2.add(fbDistributionLabel, gridBagConstraints);

        fbDiagnosisLabel.setText(bundle.getString("Ny.fbDiagnosisLabel.text")); // NOI18N
        fbDiagnosisLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel2.add(fbDiagnosisLabel, gridBagConstraints);

        fbdiagScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbdiagScrollPane.setAlignmentX(0.0F);
        fbdiagScrollPane.setAlignmentY(0.0F);
        fbdiagScrollPane.setAutoscrolls(true);
        fbdiagScrollPane.setHorizontalScrollBar(null);
        fbdiagScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbDiagnosisTextField.setColumns(20);
        fbDiagnosisTextField.setLineWrap(true);
        fbDiagnosisTextField.setRows(3);
        fbDiagnosisTextField.setWrapStyleWord(true);
        fbDiagnosisTextField.setAlignmentX(0.0F);
        fbDiagnosisTextField.setAlignmentY(0.0F);
        fbDiagnosisTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbDiagnosisTextField.setName(""); // NOI18N
        fbdiagScrollPane.setViewportView(fbDiagnosisTextField);
        fbDiagnosisTextField.getAccessibleContext().setAccessibleName("");

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
        jPanel2.add(fbdiagScrollPane, gridBagConstraints);

        fbdangScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbdangScrollPane.setAlignmentX(0.0F);
        fbdangScrollPane.setAlignmentY(0.0F);
        fbdangScrollPane.setAutoscrolls(true);
        fbdangScrollPane.setHorizontalScrollBar(null);
        fbdangScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbDangerousTextField.setColumns(20);
        fbDangerousTextField.setLineWrap(true);
        fbDangerousTextField.setRows(3);
        fbDangerousTextField.setWrapStyleWord(true);
        fbDangerousTextField.setAlignmentX(0.0F);
        fbDangerousTextField.setAlignmentY(0.0F);
        fbDangerousTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbDangerousTextField.setName(""); // NOI18N
        fbdangScrollPane.setViewportView(fbDangerousTextField);

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
        jPanel2.add(fbdangScrollPane, gridBagConstraints);

        fbenvScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbenvScrollPane.setAlignmentX(0.0F);
        fbenvScrollPane.setAlignmentY(0.0F);
        fbenvScrollPane.setAutoscrolls(true);
        fbenvScrollPane.setHorizontalScrollBar(null);
        fbenvScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbEnviromentTextField.setColumns(20);
        fbEnviromentTextField.setLineWrap(true);
        fbEnviromentTextField.setRows(3);
        fbEnviromentTextField.setWrapStyleWord(true);
        fbEnviromentTextField.setAlignmentX(0.0F);
        fbEnviromentTextField.setAlignmentY(0.0F);
        fbEnviromentTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbEnviromentTextField.setName(""); // NOI18N
        fbenvScrollPane.setViewportView(fbEnviromentTextField);

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
        jPanel2.add(fbenvScrollPane, gridBagConstraints);

        fbclimScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbclimScrollPane.setAlignmentX(0.0F);
        fbclimScrollPane.setAlignmentY(0.0F);
        fbclimScrollPane.setAutoscrolls(true);
        fbclimScrollPane.setHorizontalScrollBar(null);
        fbclimScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbClimateTextField.setColumns(20);
        fbClimateTextField.setLineWrap(true);
        fbClimateTextField.setRows(3);
        fbClimateTextField.setWrapStyleWord(true);
        fbClimateTextField.setAlignmentX(0.0F);
        fbClimateTextField.setAlignmentY(0.0F);
        fbClimateTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbClimateTextField.setName(""); // NOI18N
        fbclimScrollPane.setViewportView(fbClimateTextField);

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
        jPanel2.add(fbclimScrollPane, gridBagConstraints);

        fbbioScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbbioScrollPane.setAlignmentX(0.0F);
        fbbioScrollPane.setAlignmentY(0.0F);
        fbbioScrollPane.setAutoscrolls(true);
        fbbioScrollPane.setHorizontalScrollBar(null);
        fbbioScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbBiologyTextField.setColumns(20);
        fbBiologyTextField.setLineWrap(true);
        fbBiologyTextField.setRows(3);
        fbBiologyTextField.setWrapStyleWord(true);
        fbBiologyTextField.setAlignmentX(0.0F);
        fbBiologyTextField.setAlignmentY(0.0F);
        fbBiologyTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbBiologyTextField.setName(""); // NOI18N
        fbbioScrollPane.setViewportView(fbBiologyTextField);

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
        jPanel2.add(fbbioScrollPane, gridBagConstraints);

        fbdistrScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fbdistrScrollPane.setAlignmentX(0.0F);
        fbdistrScrollPane.setAlignmentY(0.0F);
        fbdistrScrollPane.setAutoscrolls(true);
        fbdistrScrollPane.setHorizontalScrollBar(null);
        fbdistrScrollPane.setMinimumSize(new java.awt.Dimension(100, 10));

        fbDistributionTextField.setColumns(20);
        fbDistributionTextField.setLineWrap(true);
        fbDistributionTextField.setRows(3);
        fbDistributionTextField.setWrapStyleWord(true);
        fbDistributionTextField.setAlignmentX(0.0F);
        fbDistributionTextField.setAlignmentY(0.0F);
        fbDistributionTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fbDistributionTextField.setName(""); // NOI18N
        fbdistrScrollPane.setViewportView(fbDistributionTextField);

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
        jPanel2.add(fbdistrScrollPane, gridBagConstraints);

        FBTabbedPane1.addTab(" ", new javax.swing.ImageIcon(getClass().getResource("/icons/sheets.png")), jPanel2); // NOI18N

        jToolBar9.setFloatable(false);
        jToolBar9.setRollover(true);
        jToolBar9.setAlignmentX(0.0F);

        fbClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        fbClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        fbClearButton.setFocusable(false);
        fbClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbClearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbClearButtonMouseClicked(evt);
            }
        });
        jToolBar9.add(fbClearButton);

        fbSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        fbSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        fbSaveButton.setFocusable(false);
        fbSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbSaveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbSaveButtonMouseClicked(evt);
            }
        });
        jToolBar9.add(fbSaveButton);

        fbLoadXMLButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/folder-new.png"))); // NOI18N
        fbLoadXMLButton.setToolTipText(bundle.getString("Ny.fbLoadXMLButton.toolTipText")); // NOI18N
        fbLoadXMLButton.setFocusable(false);
        fbLoadXMLButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbLoadXMLButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbLoadXMLButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbLoadXMLButtonMouseClicked(evt);
            }
        });
        jToolBar9.add(fbLoadXMLButton);

        fbWebLoadXMLButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Web_folder.png"))); // NOI18N
        fbWebLoadXMLButton.setToolTipText(bundle.getString("fbWebLoadXMLButton.toolTipText")); // NOI18N
        fbWebLoadXMLButton.setFocusable(false);
        fbWebLoadXMLButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbWebLoadXMLButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbWebLoadXMLButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbWebLoadXMLButtonMouseClicked(evt);
            }
        });
        jToolBar9.add(fbWebLoadXMLButton);
        jToolBar9.add(jSeparator8);

        fbDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        fbDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        fbDeleteButton.setFocusable(false);
        fbDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fbDeleteButtonMouseClicked(evt);
            }
        });
        jToolBar9.add(fbDeleteButton);
        jToolBar9.add(jSeparator16);

        fbSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_statistic.png"))); // NOI18N
        fbSearchButton.setToolTipText(bundle.getString("Ny.expensesSearchButton.toolTipText")); // NOI18N
        fbSearchButton.setFocusable(false);
        fbSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbSearchButtonActionPerformed(evt);
            }
        });
        jToolBar9.add(fbSearchButton);

        fbSearchState.setFocusable(false);
        fbSearchState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbSearchState.setMaximumSize(new java.awt.Dimension(14, 44));
        fbSearchState.setMinimumSize(new java.awt.Dimension(14, 44));
        fbSearchState.setPreferredSize(new java.awt.Dimension(14, 44));
        fbSearchState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar9.add(fbSearchState);

        fbNoSearchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_no_search.png"))); // NOI18N
        fbNoSearchButton.setToolTipText(bundle.getString("Ny.expensesNoSearchButton.toolTipText")); // NOI18N
        fbNoSearchButton.setFocusable(false);
        fbNoSearchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fbNoSearchButton.setMaximumSize(new java.awt.Dimension(44, 44));
        fbNoSearchButton.setMinimumSize(new java.awt.Dimension(44, 44));
        fbNoSearchButton.setPreferredSize(new java.awt.Dimension(44, 44));
        fbNoSearchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fbNoSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbNoSearchButtonActionPerformed(evt);
            }
        });
        jToolBar9.add(fbNoSearchButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar9, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fbIdLabel)
                .addGap(10, 10, 10)
                .addComponent(fbIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addComponent(fbCommonNameLabel)
                .addGap(10, 10, 10)
                .addComponent(fbCommonNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(FBTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fbIdLabel)
                    .addComponent(fbIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fbCommonNameLabel)
                    .addComponent(fbCommonNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FBTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void fbTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbTableMouseClicked
    /**Populate TextFields on tab8 (FishBase) */
    refreshFields();
}//GEN-LAST:event_fbTableMouseClicked

private void fbClearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbClearButtonMouseClicked
    /** Cleans all textFields on tab8 (FishBase)*/
    CleanAllFields();
}//GEN-LAST:event_fbClearButtonMouseClicked

private void fbSaveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbSaveButtonMouseClicked
    /**Insert record on db for table fishbase or update it if existing*/
    /*if (Global.AqID == 0) {
        Util.msgSelectAquarium();
        return;
    }*/
    String currID = fbIdTextField.getText();
    FishBase specData=new FishBase();
    if (currID == null || currID.equals("")) {
        specData.setId(0);
        } else {
        specData.setId(Integer.valueOf(currID));
    }
    specData.setCommonName(fbCommonNameTextField.getText());
    specData.setType(fbClassTextField.getText());
    specData.setName(fbNameTextField.getText());
    specData.setDistribution(fbDistributionTextField.getText());
    specData.setDiagnosis(fbDiagnosisTextField.getText());
    specData.setBiology(fbBiologyTextField.getText());
    specData.setMaxSize(fbMaxSizeTextField.getText());
    specData.setEnvironment(fbEnviromentTextField.getText());
    specData.setClimate(fbClimateTextField.getText());
    specData.setDangerous(fbDangerousTextField.getText());
    specData.setPhMin(LocUtil.delocalizeDouble(fbPHMinTextField.getText()));
    specData.setPhMax(LocUtil.delocalizeDouble(fbPHMaxTextField.getText()));
    specData.setDhMin(LocUtil.delocalizeDouble(fbDHMinTextField.getText()));
    specData.setDhMax(LocUtil.delocalizeDouble(fbDHMaxTextField.getText()));
    specData.setTempMin(LocUtil.delocalizeDouble(fbTMinTextField.getText()));
    specData.setTempMax(LocUtil.delocalizeDouble(fbTMaxTextField.getText()));
    specData.setSwimLevel(fbSwimLevelTextField.getText());    
    specData.setLifeSpam(fbLifeSpanTextField.getText());    
    specData.setAka(fbAkaTextField.getText()); 
    //save image
    BufferedImage image;
    ImageIcon icn;
    icn = (ImageIcon) fbImageLabelImg.getIcon();
    if (icn != null) {
        image = (BufferedImage) icn.getImage();
        specData.setImage(image);
    }
    FishBase.save(specData);
    FishBase.populateTable(fbTable);
    FishBase.populateCombo(fishNameComboBox);
    CleanAllFields();
}//GEN-LAST:event_fbSaveButtonMouseClicked

private void fbLoadXMLButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbLoadXMLButtonMouseClicked
    //imports xml file saved from fishbase.com
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    CleanAllFields();
    File file = XMLFilter.getXMLFile();
        
    try {
        //JTextField[] 
        JTextComponent [] jtfList = {fbIdTextField, fbCommonNameTextField, fbClassTextField,
            fbNameTextField, fbDistributionTextField, fbDiagnosisTextField, fbBiologyTextField, fbEnviromentTextField,
            fbMaxSizeTextField, fbClimateTextField, fbDangerousTextField, fbPHMinTextField, fbPHMaxTextField,
            fbDHMinTextField, fbDHMaxTextField, fbTMinTextField, fbTMaxTextField,fbSwimLevelTextField,
        fbLifeSpanTextField};
        

        AppUtil.importXML(file, jtfList);
        
    } catch (ParserConfigurationException ex) {
        _log.log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_fbLoadXMLButtonMouseClicked

private void fbDeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbDeleteButtonMouseClicked
    /** Delete selected record on tab8 (FishBase)*/
    FishBase.deleteById(fbIdTextField.getText(),fbNameTextField.getText());
    FishBase.populateTable(fbTable);
    FishBase.populateCombo(fishNameComboBox);
    CleanAllFields();
}//GEN-LAST:event_fbDeleteButtonMouseClicked

private void fbImageLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbImageLoadButtonActionPerformed
    /**Load image from file to image field*/
    fbImageLabelImg.setText(null);
    Util.ImageLoadResize(fbImageLabelImg,300);
}//GEN-LAST:event_fbImageLoadButtonActionPerformed

private void fbImageDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbImageDeleteButtonActionPerformed
    /** Clean image field*/
    fbImageLabelImg.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_IMAGE!"));
    fbImageLabelImg.setIcon(null);
}//GEN-LAST:event_fbImageDeleteButtonActionPerformed

private void fbPHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbPHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbPHMinTextFieldKeyTyped

private void fbPHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbPHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbPHMaxTextFieldKeyTyped

private void fbDHMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbDHMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbDHMinTextFieldKeyTyped

private void fbDHMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbDHMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbDHMaxTextFieldKeyTyped

private void fbTMinTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbTMinTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbTMinTextFieldKeyTyped

private void fbTMaxTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbTMaxTextFieldKeyTyped
    // allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbTMaxTextFieldKeyTyped

private void fbLifeSpanTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbLifeSpanTextFieldKeyTyped
// allow only numbers a related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_fbLifeSpanTextFieldKeyTyped

private void fbSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbSearchButtonActionPerformed
// Search 
    JTextField [] jTF = { fbCommonNameTextField,fbNameTextField,fbClassTextField,
    fbLifeSpanTextField,fbSwimLevelTextField,fbMaxSizeTextField,fbAkaTextField};
    String [] dbFields = {"CommonName","Name","Class","lifeSpan","swimLevel","Maxsize","Aka"}; // NOI18N 
    JTextArea [] jTFTA = {fbDiagnosisTextField,fbDistributionTextField, fbBiologyTextField,
    fbEnviromentTextField,fbClimateTextField,fbDangerousTextField};
    String [] dbFieldsTA = {"Diagnosis",  "Distribution","Biology","Environment", "Climate", "Dangerous"}; // NOI18N   
    JTextField [] jTFn = { fbPHMinTextField,fbPHMaxTextField,
    fbDHMinTextField,fbDHMaxTextField,fbTMinTextField,fbTMaxTextField};
    String [] dbFieldsn = {"PHMin", "PHMax", "DHMin", "DHMax", "t_Min", "t_Max"}; // NOI18N    
    String filter= DB.createFilter(jTF, dbFields);     
    filter=filter+DB.createNumericFilter(jTFn, dbFieldsn);
    filter=filter+DB.createFilter(jTFTA, dbFieldsTA);
    FishBase.setFilter(filter);
    FishBase.populateTable(fbTable);    
    if (FishBase.getFilter().isEmpty()){
        fbSearchState.setBackground(Global.BUTTON_GREY);
        fbSearchState.setToolTipText("");
    } else {
        fbSearchState.setBackground(Global.BUTTON_RED);
        fbSearchState.setToolTipText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on")
                +": " + filter);
    }
}//GEN-LAST:event_fbSearchButtonActionPerformed

private void fbNoSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbNoSearchButtonActionPerformed
    // Reset  search
    FishBase.setFilter("");//NOI18N
    FishBase.populateTable(fbTable);    
    fbSearchState.setBackground(Global.BUTTON_GREY);
    fbSearchState.setToolTipText("");
}//GEN-LAST:event_fbNoSearchButtonActionPerformed

    private void fbTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fbTableKeyReleased
        /**Populate TextFields on tab8 (FishBase) */
        refreshFields();
    }//GEN-LAST:event_fbTableKeyReleased

    private void fbWebLoadXMLButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fbWebLoadXMLButtonMouseClicked
        // Open FB Connector and import XML
        JTextComponent [] jtfList = {fbIdTextField, fbCommonNameTextField, fbClassTextField,
            fbNameTextField, fbDistributionTextField, fbDiagnosisTextField, fbBiologyTextField, fbEnviromentTextField,
            fbMaxSizeTextField, fbClimateTextField, fbDangerousTextField, fbPHMinTextField, fbPHMaxTextField,
            fbDHMinTextField, fbDHMaxTextField, fbTMinTextField, fbTMaxTextField,fbSwimLevelTextField,
        fbLifeSpanTextField};
        FBConnector fbc = new FBConnector(null, true);
        fbc.setVisible(true);
//        String docToParse = fbc.getReturnedDoc();
        
        Document docToParse = fbc.getReturnedDoc();
        if (docToParse == null) return;
        try {            
            AppUtil.webImportHTML(docToParse, jtfList);
        } catch (Exception ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        finally {
            Image img = fbc.getImage();
            if (img != null) {
                fbImageLabelImg.setText(null);
                Util.ImageDisplayResize(
                        Util.toBufferedImage(img), fbImageLabelImg, 300);                             
            }            
        }        
    }//GEN-LAST:event_fbWebLoadXMLButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane FBTabbedPane1;
    private javax.swing.JLabel fbAkaLabel;
    private javax.swing.JTextField fbAkaTextField;
    private javax.swing.JLabel fbBiologyLabel;
    private javax.swing.JTextArea fbBiologyTextField;
    private javax.swing.JLabel fbClassLabel;
    private javax.swing.JTextField fbClassTextField;
    private javax.swing.JButton fbClearButton;
    private javax.swing.JLabel fbClimateLabel;
    private javax.swing.JTextArea fbClimateTextField;
    private javax.swing.JLabel fbCommonNameLabel;
    private javax.swing.JTextField fbCommonNameTextField;
    private javax.swing.JLabel fbDHMaxLabel;
    private javax.swing.JTextField fbDHMaxTextField;
    private javax.swing.JLabel fbDHMinLabel;
    private javax.swing.JTextField fbDHMinTextField;
    private javax.swing.JLabel fbDangerousLabel;
    private javax.swing.JTextArea fbDangerousTextField;
    private javax.swing.JButton fbDeleteButton;
    private javax.swing.JLabel fbDiagnosisLabel;
    private javax.swing.JTextArea fbDiagnosisTextField;
    private javax.swing.JLabel fbDistributionLabel;
    private javax.swing.JTextArea fbDistributionTextField;
    private javax.swing.JLabel fbEnviromentLabel;
    private javax.swing.JTextArea fbEnviromentTextField;
    private javax.swing.JLabel fbIdLabel;
    private javax.swing.JTextField fbIdTextField;
    private javax.swing.JButton fbImageDeleteButton;
    private javax.swing.JLabel fbImageLabelImg;
    private javax.swing.JButton fbImageLoadButton;
    private javax.swing.JLabel fbLifeSpanLabel;
    private javax.swing.JTextField fbLifeSpanTextField;
    private javax.swing.JButton fbLoadXMLButton;
    private javax.swing.JLabel fbMaxSizeLabel;
    private javax.swing.JTextField fbMaxSizeTextField;
    private javax.swing.JLabel fbNameLabel;
    private javax.swing.JTextField fbNameTextField;
    private javax.swing.JButton fbNoSearchButton;
    private javax.swing.JLabel fbPHMaxLabel;
    private javax.swing.JTextField fbPHMaxTextField;
    private javax.swing.JLabel fbPHMinLabel;
    private javax.swing.JTextField fbPHMinTextField;
    private javax.swing.JButton fbSaveButton;
    private javax.swing.JButton fbSearchButton;
    private javax.swing.JButton fbSearchState;
    private javax.swing.JLabel fbSwimLevelLabel;
    private javax.swing.JTextField fbSwimLevelTextField;
    private javax.swing.JLabel fbTMaxLabel;
    private javax.swing.JTextField fbTMaxTextField;
    private javax.swing.JLabel fbTMinLabel;
    private javax.swing.JTextField fbTMinTextField;
    private static javax.swing.JTable fbTable;
    private javax.swing.JButton fbWebLoadXMLButton;
    private javax.swing.JScrollPane fbbioScrollPane;
    private javax.swing.JScrollPane fbclimScrollPane;
    private javax.swing.JScrollPane fbdangScrollPane;
    private javax.swing.JScrollPane fbdiagScrollPane;
    private javax.swing.JScrollPane fbdistrScrollPane;
    private javax.swing.JScrollPane fbenvScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar jToolBar9;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        fbCommonNameTextField.addMouseListener(new ContextMenuMouseListener());
        fbNameTextField.addMouseListener(new ContextMenuMouseListener());
        fbAkaTextField.addMouseListener(new ContextMenuMouseListener());
        fbClassTextField.addMouseListener(new ContextMenuMouseListener());
        fbLifeSpanTextField.addMouseListener(new ContextMenuMouseListener());
        fbMaxSizeTextField.addMouseListener(new ContextMenuMouseListener());
        fbSwimLevelTextField.addMouseListener(new ContextMenuMouseListener());
        fbPHMinTextField.addMouseListener(new ContextMenuMouseListener());
        fbPHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        fbDHMinTextField.addMouseListener(new ContextMenuMouseListener());
        fbDHMaxTextField.addMouseListener(new ContextMenuMouseListener());
        fbTMinTextField.addMouseListener(new ContextMenuMouseListener());
        fbTMaxTextField.addMouseListener(new ContextMenuMouseListener());
        fbDiagnosisTextField.addMouseListener(new ContextMenuMouseListener());
        fbDistributionTextField.addMouseListener(new ContextMenuMouseListener());
        fbBiologyTextField.addMouseListener(new ContextMenuMouseListener());
        fbClimateTextField.addMouseListener(new ContextMenuMouseListener());
        fbEnviromentTextField.addMouseListener(new ContextMenuMouseListener());
        fbDangerousTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
    static final Logger _log = Logger.getLogger(FBPanel.class.getName());
    
}

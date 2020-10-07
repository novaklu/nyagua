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
 * This calculator is heavily based on flores' yanc (Yet Another Nutrient Calculator)
 * at http://calc.petalphile.com/ shared as Open Source.
 * Thanks to mr. Flores for this concept piece.
 * 
 */

/*
 * Solutions.javat
 * Fertilization Solutions calculator 
 * -----------------------------------
 * This form is used to do some calculations on fertilisation argument
 * Some therms and situations need to be defined to understand the code.
 * 
 * The calculations may operate over commercial products or over diy (do-it-yourself) products.
 * Last ones can be powder or solutions of basic elements or compounds.
 * You can use them as pure powder or solution (as-is) or add water or mix other elements to obtain
 * your own diy solution.
 * So what is involved in operation is:
 * 1) The tank water volume 
 * 2) The commercial or diy initial compound
 * 3) The target element - that is the primary element you add to the water of 
 *      aquarium when you add compound (secondary elements qty are also showed)
 * 4) The target - that is the amount of element you want in aquarium 
 * 5) The solution water - If you are creating a solution then the water you use is needed
 * 6) Dose volume - The dose of solution you will add to tank. It should be a confortable dose 
 *  not to little that you cant measure it, not so big that you can't use
 * 7) The results depend on calculation you request.
 * 
 * Calculations:
 * a) Solution to reach target
 * b) The result of a dose
 * c) Estimative Index
 * d) Estimative Index Daily
 * e) Estimative Index Weekly
 * f) Perpetual Preservation System
 * g) Poor Man Dosing Drops
 *
 * Created on 25-set-2012, 13.44.57
 *
 @author Rudi Giacomini Pilon
 */
package util_panels;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataPlot;
import nyagua.*;
import nyagua.data.Aquarium;
import nyagua.data.Plans;
import nyagua.data.Recipe;
import nyagua.data.Setting;
import nyagua.data.Solutions;
import pieChart.Pie;


public class SolutionsPanel extends javax.swing.JPanel {
    //Connect listener to application bus
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                refreshOptions();
            }
            else if (e.getID()==Watched.AQUARIUM_CLICKED){
                setCurrentAquarium();
            } 
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    //get Solutions 
    Solutions solutions = new Solutions();

    //elements constants
    public final int NO3=Solutions.NO3;
    public final int PO4=Solutions.PO4;
    public final int K=Solutions.K;
    public final int Ca=Solutions.Ca;
    public final int Mg=Solutions.Mg;
    public final int Fe=Solutions.Fe;
    public final int Mn=Solutions.Mn;
    public final int B=Solutions.B;
    public final int Cu=Solutions.Cu;
    public final int Mo=Solutions.Mo;
    public final int Cl=Solutions.Cl;
    public final int S=Solutions.S;
    public final int Zn=Solutions.Zn;
    public final int gH=Solutions.gH;
    public final int Solubility=Solutions.Solubility;  
    public final int Target=Solutions.Target;
    
    //Elements names constants array   
    String[] elements = solutions.getElements();
    String [] shortMethodsCaptions = solutions.getShortMethodsCaptions();   
    
    public final int METHOD_TARGET=Solutions.METHOD_TARGET;
    public final int METHOD_RESULT=Solutions.METHOD_RESULT;
    public final int METHOD_EI=Solutions.METHOD_EI;
    public final int METHOD_EID=Solutions.METHOD_EID;
    public final int METHOD_EIW=Solutions.METHOD_EIW;
    public final int METHOD_PPS=Solutions.METHOD_PPS;
    public final int METHOD_ADA=Solutions.METHOD_ADA;
    public final int METHOD_PMDD=Solutions.METHOD_PMDD;
    public final int METHOD_WALSTAD=Solutions.METHOD_WALSTAD;//used in graphs
    
    /*
    # nutrient 
    # method (EI = Tom Barr's Estimative Index, PPS = Edward's Perpetual Preservation System, Walstad = Diana Walstad's Natural Aquariums, PMDD = Poor Man's Dupla Drops)
    # method: recommended ppm by author
    # low and high ppm (or +/- 20% if author uses exact value) for given nutrient
    # margin: high - low*/
    
    //For each method and for each element a table of values is needed
    //Constant for values column:
    public final int PAR_METHOD= Solutions.PAR_METHOD;  //Method Target
    public final int PAR_LOW=Solutions.PAR_LOW;     //Lowest value
    public final int PAR_HIGH=Solutions.PAR_HIGH;     //Highest value
    public final int PAR_MARGIN=Solutions.PAR_MARGIN;   //margin=High-Low
    
    /* method parameter array is a doble with following dimensions:
     * [Element: NO3=0 to Mn=6]    - Elements over Mn are not in graphs by now
     *   [Method: EI=2 to  PMDD=6] - Wet and Walstad not used by now
     *      [value: Target=0 to Margin=3]   
       */
    public double [][][] methods_parameters= solutions.getMethodParameters();
    
    HashMap solute = solutions.getSolute();//new HashMap();
    HashMap soluteElementsValues= solutions.getSoluteElementsValues(); //new HashMap();
    HashMap soluteElementsLabels= solutions.getSoluteElementsLabels(); //new HashMap();
    HashMap soluteElementsName= solutions.getSoluteElementsName(); //new HashMap();
           
    /** Creates new form Solutions */
    public SolutionsPanel() {
        initComponents(); 
        initCutAndPaste();  
        refreshOptions();      
//        createSolutionsMap();
        loadDiy(); 
        buildElementsTable(null);   
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
        
        initRecipes();    
        targetToggleButtonMouseClicked(null);
    }
    
    /** This is called when global settings change */
    private void refreshOptions(){
        if (Global.volunit.matches("l")){
            unitLiterRadioButton.setSelected(true);
        }else{
            unitGalRadioButton.setSelected(true);
        }  
    }
    
    public final void setCurrentAquarium () {
        if (Global.AqID == 0) {//deselected
            //clean fields
            JTextField [] jtfList = { waterVolumeTextField };
            Util.CleanTextFields(jtfList);         
        }
        else { //selected
            Aquarium aquarium = Aquarium.getById(Global.AqID);
            waterVolumeTextField.setText(aquarium.getVolume());
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

        solutionTypeButtonGroup = new javax.swing.ButtonGroup();
        productTypebuttonGroup = new javax.swing.ButtonGroup();
        unitsButtonGroup = new javax.swing.ButtonGroup();
        doseUnitsbuttonGroup = new javax.swing.ButtonGroup();
        toolbarButtonGroup = new javax.swing.ButtonGroup();
        jToolBar = new javax.swing.JToolBar();
        cleanFieldsButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        targetToggleButton = new javax.swing.JToggleButton();
        doseToggleButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        eiToggleButton = new javax.swing.JToggleButton();
        eiDayToggleButton = new javax.swing.JToggleButton();
        eiWeekToggleButton = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        ppsToggleButton = new javax.swing.JToggleButton();
        pmddToggleButton = new javax.swing.JToggleButton();
        adasToggleButton = new javax.swing.JToggleButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        reportButton = new javax.swing.JButton();
        waterVolumeTextField = new javax.swing.JTextField();
        waterVolumeLabel = new javax.swing.JLabel();
        solNameLabel = new javax.swing.JLabel();
        solNameComboBox = new javax.swing.JComboBox();
        solutionWaterLabel = new javax.swing.JLabel();
        solutionWaterTextField = new javax.swing.JTextField();
        doseVolumeLabel = new javax.swing.JLabel();
        doseVolumeTextField = new javax.swing.JTextField();
        methodLabel = new javax.swing.JLabel();
        targetLabel = new javax.swing.JLabel();
        targetTextField = new javax.swing.JTextField();
        calcButton = new javax.swing.JButton();
        targetNameLabel = new javax.swing.JLabel();
        resultLabel = new javax.swing.JLabel();
        resultLblLabel = new javax.swing.JLabel();
        solubleLabel = new javax.swing.JLabel();
        solYesNoLabel = new javax.swing.JLabel();
        unitsPanel = new javax.swing.JPanel();
        unitLiterRadioButton = new javax.swing.JRadioButton();
        unitGalRadioButton = new javax.swing.JRadioButton();
        ml1Label = new javax.swing.JLabel();
        ml2Label = new javax.swing.JLabel();
        ppmLabel = new javax.swing.JLabel();
        doseLabel = new javax.swing.JLabel();
        doseTextField = new javax.swing.JTextField();
        doseUnitsPanel = new javax.swing.JPanel();
        doseUnit_mgRadioButton = new javax.swing.JRadioButton();
        doseUnits_gRadioButton = new javax.swing.JRadioButton();
        rightPanel = new javax.swing.JPanel();
        addedLabel = new javax.swing.JLabel();
        elementsScrollPane = new javax.swing.JScrollPane();
        elementsTable = new javax.swing.JTable();
        hintLabel = new javax.swing.JLabel();
        graphPanel = new javax.swing.JPanel();
        typejPanel = new javax.swing.JPanel();
        powderRadioButton = new javax.swing.JRadioButton();
        solutionRadioButton = new javax.swing.JRadioButton();
        piePanel = new javax.swing.JPanel();
        currMethodLabel = new javax.swing.JLabel();
        recipesComboBox = new javax.swing.JComboBox();
        saveRecipeButton = new javax.swing.JButton();
        deleteRecipeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        cleanFieldsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        cleanFieldsButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        cleanFieldsButton.setFocusable(false);
        cleanFieldsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cleanFieldsButton.setMaximumSize(new java.awt.Dimension(42, 42));
        cleanFieldsButton.setMinimumSize(new java.awt.Dimension(42, 42));
        cleanFieldsButton.setPreferredSize(new java.awt.Dimension(42, 42));
        cleanFieldsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cleanFieldsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanFieldsButtonActionPerformed(evt);
            }
        });
        jToolBar.add(cleanFieldsButton);
        jToolBar.add(jSeparator3);

        toolbarButtonGroup.add(targetToggleButton);
        targetToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_dose.png"))); // NOI18N
        targetToggleButton.setSelected(true);
        targetToggleButton.setToolTipText(bundle.getString("solutions.method.1.target")); // NOI18N
        targetToggleButton.setFocusable(false);
        targetToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        targetToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        targetToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        targetToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        targetToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        targetToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                targetToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(targetToggleButton);

        toolbarButtonGroup.add(doseToggleButton);
        doseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_mix.png"))); // NOI18N
        doseToggleButton.setToolTipText(bundle.getString("solutions.method.2.result")); // NOI18N
        doseToggleButton.setFocusable(false);
        doseToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        doseToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        doseToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        doseToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        doseToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        doseToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doseToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(doseToggleButton);
        jToolBar.add(jSeparator1);

        toolbarButtonGroup.add(eiToggleButton);
        eiToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_ei.png"))); // NOI18N
        eiToggleButton.setToolTipText(bundle.getString("solutions.method.3.ei")); // NOI18N
        eiToggleButton.setFocusable(false);
        eiToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eiToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        eiToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        eiToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        eiToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eiToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eiToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(eiToggleButton);

        toolbarButtonGroup.add(eiDayToggleButton);
        eiDayToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_ei_daily.png"))); // NOI18N
        eiDayToggleButton.setToolTipText(bundle.getString("solutions.method.4.eid")); // NOI18N
        eiDayToggleButton.setFocusable(false);
        eiDayToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eiDayToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        eiDayToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        eiDayToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        eiDayToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eiDayToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eiDayToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(eiDayToggleButton);

        toolbarButtonGroup.add(eiWeekToggleButton);
        eiWeekToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_ei_weekly.png"))); // NOI18N
        eiWeekToggleButton.setToolTipText(bundle.getString("solutions.method.5.eiw")); // NOI18N
        eiWeekToggleButton.setFocusable(false);
        eiWeekToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eiWeekToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        eiWeekToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        eiWeekToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        eiWeekToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eiWeekToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eiWeekToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(eiWeekToggleButton);
        jToolBar.add(jSeparator2);

        toolbarButtonGroup.add(ppsToggleButton);
        ppsToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_pps.png"))); // NOI18N
        ppsToggleButton.setToolTipText(bundle.getString("solutions.method.6.pps")); // NOI18N
        ppsToggleButton.setFocusable(false);
        ppsToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ppsToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        ppsToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        ppsToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        ppsToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ppsToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ppsToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(ppsToggleButton);

        toolbarButtonGroup.add(pmddToggleButton);
        pmddToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_pmdd.png"))); // NOI18N
        pmddToggleButton.setToolTipText(bundle.getString("solutions.method.7.pmdd")); // NOI18N
        pmddToggleButton.setFocusable(false);
        pmddToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pmddToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        pmddToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        pmddToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        pmddToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pmddToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pmddToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(pmddToggleButton);

        toolbarButtonGroup.add(adasToggleButton);
        adasToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ic_ada.png"))); // NOI18N
        adasToggleButton.setToolTipText(bundle.getString("solutions.method.8.ada")); // NOI18N
        adasToggleButton.setFocusable(false);
        adasToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        adasToggleButton.setMaximumSize(new java.awt.Dimension(44, 44));
        adasToggleButton.setMinimumSize(new java.awt.Dimension(44, 44));
        adasToggleButton.setPreferredSize(new java.awt.Dimension(44, 44));
        adasToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        adasToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adasToggleButtonMouseClicked(evt);
            }
        });
        jToolBar.add(adasToggleButton);
        jToolBar.add(jSeparator4);

        reportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-properties.png"))); // NOI18N
        reportButton.setToolTipText(bundle.getString("Create_report")); // NOI18N
        reportButton.setEnabled(false);
        reportButton.setFocusable(false);
        reportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportButtonMouseClicked(evt);
            }
        });
        jToolBar.add(reportButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 327;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jToolBar, gridBagConstraints);

        waterVolumeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        waterVolumeTextField.setToolTipText(bundle.getString("solutions.tankwatervolume.tooltip")); // NOI18N
        waterVolumeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                waterVolumeTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                waterVolumeTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 67;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 3, 3, 3);
        add(waterVolumeTextField, gridBagConstraints);

        waterVolumeLabel.setText(bundle.getString("solutions.tankwatervolume")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 3, 3);
        add(waterVolumeLabel, gridBagConstraints);

        solNameLabel.setText(bundle.getString("solutions.solutionName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        add(solNameLabel, gridBagConstraints);

        solNameComboBox.setName(""); // NOI18N
        solNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solNameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 10);
        add(solNameComboBox, gridBagConstraints);

        solutionWaterLabel.setText(bundle.getString("solutions.solutionwatervolume")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        add(solutionWaterLabel, gridBagConstraints);

        solutionWaterTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        solutionWaterTextField.setToolTipText(bundle.getString("solutions.solutionwatervolume.tooltip")); // NOI18N
        solutionWaterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                solutionWaterTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                solutionWaterTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(solutionWaterTextField, gridBagConstraints);

        doseVolumeLabel.setText(bundle.getString("solutions.dosevolume")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        add(doseVolumeLabel, gridBagConstraints);

        doseVolumeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        doseVolumeTextField.setToolTipText(bundle.getString("solutions.dosevolume.tooltip")); // NOI18N
        doseVolumeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                doseVolumeTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                doseVolumeTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 69;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(doseVolumeTextField, gridBagConstraints);

        methodLabel.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        methodLabel.setText(bundle.getString("solutions.selected.method")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 3, 3);
        add(methodLabel, gridBagConstraints);

        targetLabel.setText(bundle.getString("solutions.target")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        add(targetLabel, gridBagConstraints);

        targetTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        targetTextField.setToolTipText(bundle.getString("solutions.target.tooltip")); // NOI18N
        targetTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                targetTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                targetTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(targetTextField, gridBagConstraints);

        calcButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/calculator2.png"))); // NOI18N
        calcButton.setToolTipText(bundle.getString("Calc")); // NOI18N
        calcButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        calcButton.setEnabled(false);
        calcButton.setMaximumSize(new java.awt.Dimension(42, 42));
        calcButton.setMinimumSize(new java.awt.Dimension(42, 42));
        calcButton.setPreferredSize(new java.awt.Dimension(42, 42));
        calcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(calcButton, gridBagConstraints);

        targetNameLabel.setText("----");
        targetNameLabel.setMinimumSize(new java.awt.Dimension(10, 10));
        targetNameLabel.setPreferredSize(new java.awt.Dimension(10, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(targetNameLabel, gridBagConstraints);

        resultLabel.setBackground(new java.awt.Color(152, 255, 165));
        resultLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        resultLabel.setText("0");
        resultLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        resultLabel.setMaximumSize(new java.awt.Dimension(90, 17));
        resultLabel.setMinimumSize(new java.awt.Dimension(70, 17));
        resultLabel.setOpaque(true);
        resultLabel.setPreferredSize(new java.awt.Dimension(70, 17));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        add(resultLabel, gridBagConstraints);

        resultLblLabel.setText(bundle.getString("solutions.result")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        add(resultLblLabel, gridBagConstraints);

        solubleLabel.setText(bundle.getString("solutions.soluble")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        add(solubleLabel, gridBagConstraints);

        solYesNoLabel.setText("---");
        solYesNoLabel.setMaximumSize(new java.awt.Dimension(25, 25));
        solYesNoLabel.setMinimumSize(new java.awt.Dimension(25, 25));
        solYesNoLabel.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(solYesNoLabel, gridBagConstraints);

        unitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Ny.utilityHCUnitsPanel.border.title"))); // NOI18N
        unitsPanel.setLayout(new java.awt.GridBagLayout());

        unitsButtonGroup.add(unitLiterRadioButton);
        unitLiterRadioButton.setSelected(true);
        unitLiterRadioButton.setText(bundle.getString("solutions.units.liters")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        unitsPanel.add(unitLiterRadioButton, gridBagConstraints);

        unitsButtonGroup.add(unitGalRadioButton);
        unitGalRadioButton.setText(bundle.getString("solutions.units.gals")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 0, 0);
        unitsPanel.add(unitGalRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(unitsPanel, gridBagConstraints);

        ml1Label.setText(bundle.getString("ml")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(ml1Label, gridBagConstraints);

        ml2Label.setText(bundle.getString("ml")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(ml2Label, gridBagConstraints);

        ppmLabel.setText(bundle.getString("ppm")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(ppmLabel, gridBagConstraints);

        doseLabel.setText(bundle.getString("solutions.addingDose")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(doseLabel, gridBagConstraints);

        doseTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        doseTextField.setText("0");
        doseTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                doseTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                doseTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(doseTextField, gridBagConstraints);

        doseUnitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("UNITS"))); // NOI18N

        doseUnitsbuttonGroup.add(doseUnit_mgRadioButton);
        doseUnit_mgRadioButton.setSelected(true);
        doseUnit_mgRadioButton.setText("mg/ml");
        doseUnitsPanel.add(doseUnit_mgRadioButton);

        doseUnitsbuttonGroup.add(doseUnits_gRadioButton);
        doseUnits_gRadioButton.setText("g");
        doseUnitsPanel.add(doseUnits_gRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(doseUnitsPanel, gridBagConstraints);

        rightPanel.setLayout(new java.awt.GridBagLayout());

        addedLabel.setText(bundle.getString("solutions.elements.added")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        rightPanel.add(addedLabel, gridBagConstraints);

        elementsScrollPane.setBorder(null);
        elementsScrollPane.setPreferredSize(new java.awt.Dimension(450, 180));

        elementsTable.setBackground(new java.awt.Color(204, 255, 204));
        elementsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "NO3", "PO4", "K", "Ca", "Mg", "Fe", "Mn", "B", "Cu", "Mo", "Cl", "S", "Zn", "Gh"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        elementsTable.setToolTipText(bundle.getString("solutions.elements.added.tooltip")); // NOI18N
        elementsTable.setAlignmentX(0.0F);
        elementsTable.setAlignmentY(0.0F);
        elementsTable.setFillsViewportHeight(true);
        elementsTable.setMinimumSize(new java.awt.Dimension(30, 16));
        elementsTable.setPreferredSize(new java.awt.Dimension(600, 20));
        elementsTable.setRowHeight(20);
        elementsScrollPane.setViewportView(elementsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 800;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 15);
        rightPanel.add(elementsScrollPane, gridBagConstraints);

        hintLabel.setText(bundle.getString("solutions.hints.default")); // NOI18N
        hintLabel.setToolTipText(bundle.getString("solutions.hints.tip")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 20);
        rightPanel.add(hintLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 15);
        add(rightPanel, gridBagConstraints);

        graphPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 80;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 10, 15);
        add(graphPanel, gridBagConstraints);

        typejPanel.setLayout(new java.awt.GridBagLayout());

        solutionTypeButtonGroup.add(powderRadioButton);
        powderRadioButton.setText(bundle.getString("solutions.type.powder")); // NOI18N
        powderRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                powderRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        typejPanel.add(powderRadioButton, gridBagConstraints);

        solutionTypeButtonGroup.add(solutionRadioButton);
        solutionRadioButton.setSelected(true);
        solutionRadioButton.setText(bundle.getString("solutions.type.solution")); // NOI18N
        solutionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solutionRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 15, 3, 3);
        typejPanel.add(solutionRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        add(typejPanel, gridBagConstraints);

        piePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        piePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 15);
        add(piePanel, gridBagConstraints);

        currMethodLabel.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        currMethodLabel.setText(bundle.getString("solutions.method.1.target")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        add(currMethodLabel, gridBagConstraints);

        recipesComboBox.setEditable(true);
        recipesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recipesComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 5, 0);
        add(recipesComboBox, gridBagConstraints);

        saveRecipeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        saveRecipeButton.setToolTipText(bundle.getString("SAVE")); // NOI18N
        saveRecipeButton.setEnabled(false);
        saveRecipeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        saveRecipeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        saveRecipeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        saveRecipeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveRecipeButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 5, 5);
        add(saveRecipeButton, gridBagConstraints);

        deleteRecipeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete_16x.png"))); // NOI18N
        deleteRecipeButton.setToolTipText(bundle.getString("Delete_recipe")); // NOI18N
        deleteRecipeButton.setEnabled(false);
        deleteRecipeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        deleteRecipeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        deleteRecipeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        deleteRecipeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteRecipeButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        add(deleteRecipeButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    
private void waterVolumeTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_waterVolumeTextFieldKeyTyped
//allow only numbers and related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_waterVolumeTextFieldKeyTyped

private void solutionWaterTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_solutionWaterTextFieldKeyTyped
//allow only numbers and related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_solutionWaterTextFieldKeyTyped

private void doseVolumeTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseVolumeTextFieldKeyTyped
//allow only numbers and related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_doseVolumeTextFieldKeyTyped

private void targetTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_targetTextFieldKeyTyped
//allow only numbers and related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_targetTextFieldKeyTyped

private void calcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcButtonActionPerformed
    //do all calculations
    hintLabel.setText("");//NOI18N
    doAllCalc();      
}//GEN-LAST:event_calcButtonActionPerformed

private void solNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solNameComboBoxActionPerformed
// When changed Refresh target element label
    if (solNameComboBox.getSelectedItem() != null){
        RefreshTargetLabel (solNameComboBox.getSelectedItem().toString());
        setTarget();
        refreshPie(); 
    }
}//GEN-LAST:event_solNameComboBoxActionPerformed

/**
 * get the selected method
 * 
 * @return method
 */
private int getSelectedMethod() {    
    if (pmddToggleButton.isSelected()) {
        return METHOD_PMDD;
    }    
    if (ppsToggleButton.isSelected()) {
        return METHOD_PPS;
    }   
    if (eiWeekToggleButton.isSelected()) {
        return METHOD_EIW;
    } 
    if (eiDayToggleButton.isSelected()) {
        return METHOD_EID;
    } 
    if (adasToggleButton.isSelected()) {
        return METHOD_ADA;
    }
    if (eiToggleButton.isSelected()) {
        return METHOD_EI;
    } 
    if (doseToggleButton.isSelected()) {
        return METHOD_RESULT;
    } 
    else {
        return METHOD_TARGET;
    }    
}

private void solutionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solutionRadioButtonActionPerformed
// Activate related fields:
    switchSolutionFields(true);
    checkFields();
}//GEN-LAST:event_solutionRadioButtonActionPerformed

private void powderRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powderRadioButtonActionPerformed
// (De-)Activate related fields:
    switchSolutionFields(false);
    checkFields();
}//GEN-LAST:event_powderRadioButtonActionPerformed

private void doseTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseTextFieldKeyTyped
// allow only numbers and related chars
    Util.checkNumericKey(evt);
}//GEN-LAST:event_doseTextFieldKeyTyped

    private void waterVolumeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_waterVolumeTextFieldKeyReleased
        // Check fields
        checkFields();
    }//GEN-LAST:event_waterVolumeTextFieldKeyReleased

    private void solutionWaterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_solutionWaterTextFieldKeyReleased
        //  Check fields
        checkFields();
    }//GEN-LAST:event_solutionWaterTextFieldKeyReleased

    private void doseVolumeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseVolumeTextFieldKeyReleased
        // Check fields
        checkFields();
    }//GEN-LAST:event_doseVolumeTextFieldKeyReleased

    private void doseTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseTextFieldKeyReleased
        // Check fields
        checkFields();
    }//GEN-LAST:event_doseTextFieldKeyReleased

    private void targetTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_targetTextFieldKeyReleased
        // Check fields
        checkFields();
    }//GEN-LAST:event_targetTextFieldKeyReleased

    private void cleanFieldsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanFieldsButtonActionPerformed
        // Clear Fields
        JTextField [] jtfList={waterVolumeTextField,targetTextField,
            solutionWaterTextField,doseVolumeTextField,doseTextField};
        Util.CleanTextFields(jtfList);
        resultLabel.setText("");//NOI18N
        hintLabel.setText("");//NOI18N

        switchMethodFields(true);
        switchSolutionFields(true);
        
        solutionRadioButton.setSelected(true);
        targetToggleButton.setSelected(true);
        currMethodLabel.setText(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "solutions.method.1.target"));
        
        solNameComboBox.setSelectedIndex(0);
        buildElementsTable(null);
        //clean also grafic
        int previousCanvas=graphPanel.getComponentCount();
        while (previousCanvas > 0 ){
            graphPanel.remove(0);
            previousCanvas=graphPanel.getComponentCount();
        }
        solYesNoLabel.setText("---");//NOI18N
        solYesNoLabel.setIcon(null);
        saveRecipeButton.setEnabled(false);
        calcButton.setEnabled(false);
    }//GEN-LAST:event_cleanFieldsButtonActionPerformed

    private void targetToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_targetToggleButtonMouseClicked
        // METHOD_TARGET
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "solutions.method.1.target"));
        
        checkFields();    
    }//GEN-LAST:event_targetToggleButtonMouseClicked

    private void doseToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doseToggleButtonMouseClicked
        // METHOD_RESULT
        setTarget();
        switchMethodFields(false);
        currMethodLabel.setText(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "solutions.method.2.result"));
        
        checkFields();
    }//GEN-LAST:event_doseToggleButtonMouseClicked

    private void eiToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eiToggleButtonMouseClicked
        //  METHOD_EI
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.3.ei"));
        checkFields();
    }//GEN-LAST:event_eiToggleButtonMouseClicked

    private void eiDayToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eiDayToggleButtonMouseClicked
        //  METHOD_EID
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.4.eid"));
        checkFields();
    }//GEN-LAST:event_eiDayToggleButtonMouseClicked

    private void eiWeekToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eiWeekToggleButtonMouseClicked
        //   METHOD_EIW
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.5.eiw"));
        checkFields();
    }//GEN-LAST:event_eiWeekToggleButtonMouseClicked

    private void ppsToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ppsToggleButtonMouseClicked
        //   METHOD_PPS
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.6.pps"));
        checkFields();
    }//GEN-LAST:event_ppsToggleButtonMouseClicked

    private void pmddToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pmddToggleButtonMouseClicked
        //   METHOD_PMDD
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.7.pmdd"));
        checkFields();
    }//GEN-LAST:event_pmddToggleButtonMouseClicked

    private void saveRecipeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveRecipeButtonMouseClicked
        // save recipe
        saveRecipe();        
    }//GEN-LAST:event_saveRecipeButtonMouseClicked

    private void deleteRecipeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteRecipeButtonMouseClicked
        // Delete selected recipe
        deleteRecipe();
    }//GEN-LAST:event_deleteRecipeButtonMouseClicked

    private void recipesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recipesComboBoxActionPerformed
        // Change recipe
        loadRecipe();
    }//GEN-LAST:event_recipesComboBoxActionPerformed

    private void reportButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportButtonMouseClicked
        String recipeName = recipesComboBox.getSelectedItem().toString();
        
        double targ=Double.valueOf(LocUtil.delocalizeDouble(targetTextField.getText()));
        double av=Double.valueOf(LocUtil.delocalizeDouble(waterVolumeTextField.getText()));        
        double sv=Double.valueOf(LocUtil.delocalizeDouble(solutionWaterTextField.getText()));
        double dv=Double.valueOf(LocUtil.delocalizeDouble(doseVolumeTextField.getText()));
        //if us gal is selected--> convert to liters
        if (unitGalRadioButton.isSelected()){
            av=Converter.gal2l(av);
        }
        double calcresult=calcSolute(solNameComboBox.getSelectedItem().toString(),
            targ,av,sv,dv);    
        String solname=solNameComboBox.getSelectedItem().toString();
        double [] values = calcElements(solname,calcresult, av, sv, dv);
        String element = targetNameLabel.getText();
        
        Report.recipeReport(recipeName,element, elements,values, formatMilligrams(calcresult));
    }//GEN-LAST:event_reportButtonMouseClicked

    private void adasToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adasToggleButtonMouseClicked
        //   METHOD_ADA
        setTarget();
        switchMethodFields(true);
        currMethodLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.8.ada"));
        checkFields();
    }//GEN-LAST:event_adasToggleButtonMouseClicked



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton adasToggleButton;
    private javax.swing.JLabel addedLabel;
    private javax.swing.JButton calcButton;
    private javax.swing.JButton cleanFieldsButton;
    private javax.swing.JLabel currMethodLabel;
    private javax.swing.JButton deleteRecipeButton;
    private javax.swing.JLabel doseLabel;
    private javax.swing.JTextField doseTextField;
    private javax.swing.JToggleButton doseToggleButton;
    private javax.swing.JRadioButton doseUnit_mgRadioButton;
    private javax.swing.JPanel doseUnitsPanel;
    private javax.swing.JRadioButton doseUnits_gRadioButton;
    private javax.swing.ButtonGroup doseUnitsbuttonGroup;
    private javax.swing.JLabel doseVolumeLabel;
    private javax.swing.JTextField doseVolumeTextField;
    private javax.swing.JToggleButton eiDayToggleButton;
    private javax.swing.JToggleButton eiToggleButton;
    private javax.swing.JToggleButton eiWeekToggleButton;
    private javax.swing.JScrollPane elementsScrollPane;
    private javax.swing.JTable elementsTable;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JLabel ml1Label;
    private javax.swing.JLabel ml2Label;
    private javax.swing.JPanel piePanel;
    private javax.swing.JToggleButton pmddToggleButton;
    private javax.swing.JRadioButton powderRadioButton;
    private javax.swing.JLabel ppmLabel;
    private javax.swing.JToggleButton ppsToggleButton;
    private javax.swing.ButtonGroup productTypebuttonGroup;
    private javax.swing.JComboBox recipesComboBox;
    private javax.swing.JButton reportButton;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JLabel resultLblLabel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton saveRecipeButton;
    private javax.swing.JComboBox solNameComboBox;
    private javax.swing.JLabel solNameLabel;
    private javax.swing.JLabel solYesNoLabel;
    private javax.swing.JLabel solubleLabel;
    private javax.swing.JRadioButton solutionRadioButton;
    private javax.swing.ButtonGroup solutionTypeButtonGroup;
    private javax.swing.JLabel solutionWaterLabel;
    private javax.swing.JTextField solutionWaterTextField;
    private javax.swing.JLabel targetLabel;
    private javax.swing.JLabel targetNameLabel;
    private javax.swing.JTextField targetTextField;
    private javax.swing.JToggleButton targetToggleButton;
    private javax.swing.ButtonGroup toolbarButtonGroup;
    private javax.swing.JPanel typejPanel;
    private javax.swing.JRadioButton unitGalRadioButton;
    private javax.swing.JRadioButton unitLiterRadioButton;
    private javax.swing.ButtonGroup unitsButtonGroup;
    private javax.swing.JPanel unitsPanel;
    private javax.swing.JLabel waterVolumeLabel;
    private javax.swing.JTextField waterVolumeTextField;
    // End of variables declaration//GEN-END:variables

        
    /**
     * Init recipes combo
     */
    private void initRecipes() {
        Recipe.populateCombo(recipesComboBox, getSelectedMethod());
    }
    
    /**
     * loads diy products list on solName combo box
     */
    private void loadDiy() {
        solNameComboBox.removeAllItems(); 
        List sortedKeys=new ArrayList(solute.keySet());
        Collections.sort(sortedKeys);
        Iterator iterator=sortedKeys.iterator();
        while(iterator.hasNext()){
            Object key = iterator.next();
            solNameComboBox.addItem(key);
        }
    }
    
    /**
     * Refresh the target label extracting the target element from the compound
     * 
     * @param compound selected compound
     */
    private void RefreshTargetLabel (String compound){
        double [] values=(double[]) solute.get(compound); //default on diy
        int targetElement=(int) values[Target];
        targetNameLabel.setText(elements[targetElement]);
    }
    
    /**
     * If milligrams is used then the output is converted in grams wheen needed
     * If milliliters is the right one the used ml or liters when needed
     * 
     * @param mg
     * @return String mg or g
     */
    private String formatMilligrams (double mg){
        boolean use_mg=true;    //default on mg
        String fm="mg";//NOI18N        
        if (mg < 1000){ 
            fm=LocUtil.localizeDouble(mg)+" "+fm;//NOI18N
        }else{ 
            if (use_mg){//in grams
               fm=LocUtil.localizeDouble(mg/1000)+" g";//NOI18N 
            }else{
                fm=LocUtil.localizeDouble(mg/1000)+" l";//NOI18N 
            }            
        }        
        return fm;
    }
    
    /**
     * Render and refresh the elements table
     */
    private void buildElementsTable(double [] values) {
        DefaultTableModel dm = new DefaultTableModel();        
        elementsTable.setRowHeight(30);
        elementsTable.setGridColor(Color.darkGray);
        Object [][] dv=new Object [1][14];
        String[] ci=new String [14];
        
        if (values==null) {
            values=new double [14];
        }
        for (int x=0; x<14; x++){
            TableColumn col = elementsTable.getColumnModel().getColumn(x);
            col.setPreferredWidth(100);     
        }    
            //col.set
        for (int y=0; y<14; y++){
            ci[y]=elements[y];
            dv[0][y]=LocUtil.localizeDouble(values[y]);             
        }     
        dm.setDataVector(dv, ci);
     elementsTable.setModel(dm);
        
    }
    
    /**
     * Calc the amount of compound to be dissolved in water to obtain a solution 
     * that will bring the target element to the desired value
     * 
     * All required fields need to be non zero values checked before calling function
     * 
     * @param element   (the target element)
     * 
     * @return  amount of compound to put in water 
     */private double calcSolute(String compound, double targ, double AV, double SV,
             double DV){
        /*used formula is:
          * x=  solute in mg/l = amount of powder (or liquid) to put in water to get the solution
          * targ = target quantity in ppm or mg/l for the element to reach in tank
          * K=  value of target lement contained in a unit of the initial compount
          * AV= Aquarium Water total volume in liters
          * SV= Solution Water volume in ml
          * DV= Dose Volume (the minimal dose used to reach targ in ml
          * 
          * x=targ * (1 / k) * AV * (SV/DV)
          */
         double [] values=(double[]) solute.get(compound);         
         //calc solute
         int targetElement=(int) values[Target];
         double k=values[targetElement];
         double x=targ * (1 / k) * AV * (SV/DV); //x=
         
         //refresh the solubility label
         double solub;
        solub = values[Solubility];
         //System.out.println("g. " + (x/SV) + " - solub:" + solub  );
         if ((x/SV) < solub){
            solYesNoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok_sig.png"))); // NOI18N
            saveRecipeButton.setEnabled(true);
         }else{
            solYesNoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ko_sig.png"))); // NOI18N
            saveRecipeButton.setEnabled(false);
         }        
         return x; 
    } 
     
     /**
      * Calculate the amount of each element that will be dosed at selected conditions
      * 
      * @param compound the introduced compound
      * @param x    solute in mg/l = amount of powder (or liquid) to put in water to get the solution
      * @param AV   Aquarium Water total volume in liters
      * @param SV   Solution Water volume in ml
      * @param DV   Dose Volume (the minimal dose used to reach targ in ml
      * @return     An array with the amount for each element
      */
     public double [] calcElements(String compound, double x, double AV, double SV,
             double DV){
         /*for each element the formula is:
         * x=  solute in mg/l = amount of powder (or liquid) to put in water to get the solution
          * targ = target quantity in ppm or mg/l for the element to reach in tank
          * K=  value of target lement contained in a unit of the initial compount
          * AV= Aquarium Water total volume in liters
          * SV= Solution Water volume in ml
          * DV= Dose Volume (the minimal dose used to reach targ in ml
          * 
          * x * K * {1/ [AV * (SV/DV)]} =targ 
          */ 
         double [] elementsDosed=new double [14];
         double [] values = (double[]) solute.get(compound);
         String status = ""; //NOI18N
         for (int y=0; y<14;y++){
             double k=values[y];
             elementsDosed[y]= x*k*(1 / (AV * (SV/DV)));
             if (y==Cu){
                status = status + solutions.checkCu(elementsDosed[y]);
                hintLabel.setText(status); 
             }
         }
         if (compound.contains("EDDHA")){
             checkEDDHA();
         } else if (compound.contains("K3PO4")){
             checkK3PO4();
         }
         
         return elementsDosed;
     }
     
          
     /**
      * Add a warning for EDDHA FE
      */
     private void checkK3PO4(){
         String hint= solutions.checkK3PO4();
        hintLabel.setText(hint);
     }
     
     /**
      * Add a warning for EDDHA FE
      */
     private void checkEDDHA(){
        String hint= solutions.checkEDDHA();
        hintLabel.setText(hint);
     }
     
     /**
      * Add a warning for toxic Cu concentration
      * 
      * @param cuDose   Amount of Cu in compound
      */
     private void checkCu(double cuDose){
         if (cuDose > 0.072){
             int toxic=(int) ((cuDose/0.072)*100);
             String hint="<html>";//NOI18N
             hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.cu")+"<br>";//NOI18N
             hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.cu2");//NOI18N
             hint=hint + " " + String.valueOf(toxic) + "% </html>";//NOI18N
             hintLabel.setText(hint);
         }
     }
     
     /**
      * Check input text fields for empty or zero values
      * Check for valid number format 
      * (values are surely numbers because there is an input filter on field)
      * 
      * @param jtf      text field to be checked
      * @param errMsg   the error message to display 
      * @return         the error message or anempty string
      */
     private String checkNumericInput(JTextField jtf, String errMsg){
         String thisErrMsg="";//NOI18N
         if (jtf.getText().isEmpty()){
             thisErrMsg=thisErrMsg+errMsg;  
             return thisErrMsg; 
         }
         try {
             if (Double.valueOf(LocUtil.delocalizeDouble(jtf.getText()))==0){
                 thisErrMsg=thisErrMsg+errMsg;            
             }
         }catch (NumberFormatException err){
             thisErrMsg=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("number_format");//NOI18N
         }         
         return thisErrMsg;  
     }
     
     /**
      * Get target value for the component in choosed method
      * 
      * @param methodChoosed    the selected method
      * @return     target component value
      */
     private String getTarget(int methodChoosed){
         String target="1"; //NOI18N //default unitary target
         String compound =solNameComboBox.getSelectedItem().toString();
         double [] values=(double[]) solute.get(compound);         
         //calc solute
         int targetElement=(int) values[Target];         
         String targ=LocUtil.localizeDouble(methods_parameters[targetElement][methodChoosed][PAR_METHOD]);
         if (! targ.isEmpty()){
             target=targ;
         }
         return target;
     }
     
     
     
     /**
      * Sets visible fields according to method
      * 
      * @param standardFields [true for standard (most used) fields | false for result method]
      */
     private void switchMethodFields (boolean standardFields){         
         boolean nonStandardField=!standardFields;
         resultLabel.setVisible(standardFields);
         resultLblLabel.setVisible(standardFields);
         solubleLabel.setVisible(standardFields);
         solYesNoLabel.setVisible(standardFields);
         
         recipesComboBox.setVisible(standardFields);
         deleteRecipeButton.setVisible(standardFields);
         saveRecipeButton.setVisible(standardFields);
         
         targetTextField.setEditable(standardFields);

         doseLabel.setVisible(nonStandardField);
         doseTextField.setVisible(nonStandardField);
         doseUnitsPanel.setVisible(nonStandardField); 
         
         initRecipes();
     }
     
     /**
      * Sets visible fields according to solution dosing
      * 
      * @param standardFields [true for solution | false for dry]
      */
     private void switchSolutionFields (boolean standardFields){
         if (!standardFields){
             solutionWaterTextField.setText("1");//NOI18N
             doseVolumeTextField.setText("1");//NOI18N
         }
         solutionWaterLabel.setVisible(standardFields);
         solutionWaterTextField.setVisible(standardFields);
         doseVolumeLabel.setVisible(standardFields);
         doseVolumeTextField.setVisible(standardFields);
         ml1Label.setVisible(standardFields);
         ml2Label.setVisible(standardFields);
     }
     
     /**
      * enable or disable target field depending on method
      */
     private void setTarget () {
        int methodChoosed = getSelectedMethod();
        if (methodChoosed < 0) {
             return;
         }
        if (methodChoosed==METHOD_TARGET || methodChoosed==METHOD_RESULT){
            targetTextField.setEnabled(true);
            targetTextField.setText("");
        } else {        
            targetTextField.setEnabled(false); 
            targetTextField.setText(getTarget(methodChoosed));
        }
     }
    
     private void checkFields(){
         //do all checks for input values        
        //check for empty fields        
        int methodChoosed = getSelectedMethod();
        JTextField [] jtfList;

        switch (methodChoosed) {
            case METHOD_TARGET:
                if (powderRadioButton.isSelected()){  //these fiels are needed only for Diy
                    jtfList =new JTextField [] {waterVolumeTextField,targetTextField};
                }
                else {
                    jtfList =new JTextField []{waterVolumeTextField,targetTextField,
                        solutionWaterTextField,doseVolumeTextField};
                }   
                break;
                
            case METHOD_RESULT:
                if (powderRadioButton.isSelected()){
                    jtfList =new JTextField [] {waterVolumeTextField,doseTextField };
                }
                else {
                    jtfList =new JTextField []{waterVolumeTextField,doseTextField,
                        solutionWaterTextField,doseVolumeTextField};
                }   
                break;
                
            default:
                if (powderRadioButton.isSelected()){
                    jtfList =new JTextField [] {waterVolumeTextField };
                }
                else {
                    jtfList =new JTextField []{waterVolumeTextField,
                        solutionWaterTextField,doseVolumeTextField};
                }   
                break;
        }      
        calcButton.setEnabled(Util.CheckTestFields(jtfList));
     }
     

     
     /**
      * do calc operations
      */
     private void doAllCalc(){         
//         int methodChoosed=methodComboBox.getSelectedIndex();
         int methodChoosed = getSelectedMethod();
         if (methodChoosed!=METHOD_TARGET && methodChoosed!=METHOD_RESULT ){
             targetTextField.setText(getTarget(methodChoosed));
         }else if (methodChoosed==METHOD_RESULT){
             targetTextField.setText("1");//NOI18N
         }

        //do all checks for input values        
        String msg="";//NOI18N
        String errMsg=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.parCheck.field") + "\n"; //NOI18N;
        //check for empty fields:
        //This fields need ever to be checked
        msg=msg+checkNumericInput(targetTextField, targetLabel.getText() + errMsg); 
        msg=msg+checkNumericInput(waterVolumeTextField,waterVolumeLabel.getText()+errMsg); 
        //these fiels are needed only for Diy
        msg=msg+checkNumericInput(solutionWaterTextField,solutionWaterLabel.getText()+errMsg);  
        msg=msg+checkNumericInput(doseVolumeTextField,doseVolumeLabel.getText()+errMsg);
        //this field is only for result method
        if (methodChoosed==METHOD_RESULT){
            msg=msg+checkNumericInput(doseTextField,doseLabel.getText()+errMsg);
        }        
        //there is an input error
        if (! msg.contentEquals("")) {//NOI18N
            Util.showErrorMsg(msg);
            return;
        }

        //all values are OK
        double targ=Double.valueOf(LocUtil.delocalizeDouble(targetTextField.getText()));
        double av=Double.valueOf(LocUtil.delocalizeDouble(waterVolumeTextField.getText()));        
        double sv=Double.valueOf(LocUtil.delocalizeDouble(solutionWaterTextField.getText()));
        double dv=Double.valueOf(LocUtil.delocalizeDouble(doseVolumeTextField.getText()));
        //if us gal is selected--> convert to liters
        if (unitGalRadioButton.isSelected()){
            av=Converter.gal2l(av);
        }
        
        //evaluate selected operation        
        if (methodChoosed==METHOD_RESULT){            
            resultLabel.setText("0");//NOI18N
            double calcresult=Double.valueOf(LocUtil.delocalizeDouble(doseTextField.getText()));            
            //evaluate units
            if (doseUnits_gRadioButton.isSelected()){
                calcresult=Converter.g2mg(calcresult);
            }            
            String solname=solNameComboBox.getSelectedItem().toString();
            double [] values = calcElements(solname,calcresult, av, sv, dv);
            buildElementsTable(values);
        }else {
            double calcresult=calcSolute(solNameComboBox.getSelectedItem().toString(),
            targ,av,sv,dv);    
            resultLabel.setText(formatMilligrams(calcresult));
            String solname=solNameComboBox.getSelectedItem().toString();
            double [] values = calcElements(solname,calcresult, av, sv, dv);
            buildElementsTable(values);
        }
        
        DoPlot();
     } 
     
     private void DoPlot() {
        //Get values for lines in error bar
        
        int count=0;
        String compound =solNameComboBox.getSelectedItem().toString();
//        int currMethod=methodComboBox.getSelectedIndex();
        int currMethod = getSelectedMethod();
        double [] values=(double[]) solute.get(compound);
        
         //calc solute
         int targetElement=(int) values[Target]; 
         double targetValue=1;
         if(targetElement < B ){
            switch (currMethod) {
                case 0:
                    targetValue=Double.valueOf(LocUtil.delocalizeDouble(
                            targetTextField.getText()));
                    break;
                    
                case 1:
                    targetValue=Double.valueOf(LocUtil.delocalizeDouble(
                        elementsTable.getValueAt(0,targetElement).toString()));
                    break;
                    
                default:
                    targetValue=methods_parameters[targetElement][currMethod][PAR_METHOD];
                    break;
            }
         }                
                 
        //dinamize series
        //captions should be dinamic     
        int tot_series=5;    //total number of series      
        String capt[]=new String[tot_series+1];    
        System.arraycopy(shortMethodsCaptions, 0, capt, 0, tot_series);
        capt[tot_series]="";
        
       //in graphs only some of methos from methods_parameters are used.
        //the order and the methos are stored in an array to simplify 
        //some operations                
        int graphMethod []=new int[]{METHOD_WALSTAD,METHOD_EI,
            METHOD_PPS,METHOD_PMDD,METHOD_ADA}; 
        
        //Extracting data--> dimensions are: 
        //[serieid] [media or bar] [x|y axis] [point value]
        //serie id is 1 to 3 (PH-KH-temp)
        //media is line value - bar is single specimen data
        //y value is ever the counter value (specimen id)
        // in x axis are the data
        //dimensions are: 
        //[serieid] [media or bar] [x|y axis] [point value]   
         
        double[][][][] data = new double [tot_series+1][][][];        
        int x=0;    //x axis data are at 0
        int y=1;    //y axis data are at 1
        int med=0;
        int bar=1;
        int line=2;
        double bigNum=0;
        String [] yMap= new String[tot_series+1];
        yMap[0]=""; //caption value that stay in x axis
       
        //Some existing routines has been used so this is a little bit tricky
        //Using 1 more than tot_series creates an empty bar loaded
        // with medium value
        for (int serie=0; serie<tot_series+1; serie++){
            data [serie]=new double [3][][];    //med,bar,line
                int elementcount=1;             //only one measure   
                data [serie][med] =new double [2][]; //x,y
                data [serie][med][x]=new double[elementcount];
                data [serie][med][y]=new double [elementcount];
                data [serie][bar] =new double [2][]; //x,y
                data [serie][bar][x]=new double[elementcount];
                data [serie][bar][y]=new double [elementcount];
                
                double xMax=-10;    //Default value are out of axis
                double xMin=-10; 
                if (serie < tot_series){
                    xMax=methods_parameters[targetElement][graphMethod[serie]][PAR_HIGH];
                    xMin=methods_parameters[targetElement][graphMethod[serie]][PAR_LOW];
                    yMap[serie+1]=capt[serie];// NOI18N
                }              
                if (xMax>bigNum){bigNum=xMax;}
                data [serie][bar][x][count]=(xMax-xMin)/2;                    
                data [serie][bar][y][count]=0;  
                data [serie][med][x][count]=(xMax+xMin)/2;                    
                double yVal=1;
                data [serie][med][y][count]=serie+yVal; 
                data [serie][line] =new double [2][]; //x,y
                data [serie][line][x]=new double[tot_series];
                data [serie][line][y]=new double [tot_series];             
                double lineValue=targetValue;
                
               // Here is the trick: medium value is rewrited all times and
                //only last stay
                for (int i=0;i<tot_series;i++){
                    data [serie][line][x][i]=lineValue;
                    data [serie][line][y][i]=0;
                } 
                data [serie][line][y][tot_series-1]=tot_series+1;//vertical full bar                
                
        }    //end for
        
                
        //Setting jccKit interface--->
        jccKitIface jki=new jccKitIface();
        jki.setGrid(true);  //set grid      
        String title=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method");
        boolean setLegend=true;
        jki.setLegend(setLegend,title);
        //change axis labels
        String xlabel=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ppm");
        String x2label="";
        String ylabel=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method");        
        jki.setXLabel(xlabel + " "+x2label);
        jki.setYLabel(ylabel);
        
        //set Axis Max Values
        bigNum++;
        jki.setXMax(bigNum);
        jki.setYMax(tot_series+1);        
        jki.setYmap(yMap);
        
        //Grid step 
        double mytics = 1;
        jki.setGridStep(mytics);      
        
        //add series
        GraphicsPlotCanvas pc=jki.createSolBarsPlotCanvas(tot_series);
        DataPlot dataPlot = new DataPlot();
        jki.doBarsGraph(dataPlot, data, capt) ;
               
        pc.connect(dataPlot);        
        Canvas plotter = pc.getGraphicsCanvas();
        int previousCanvas=graphPanel.getComponentCount();
        while (previousCanvas > 0 ){
            graphPanel.remove(0);
            previousCanvas=graphPanel.getComponentCount();
        }
        graphPanel.add(plotter);
        plotter.setSize(graphPanel.getWidth()-10, graphPanel.getHeight()-10);
        plotter.repaint();
    }
     
     /*
     * Load some global settings
     */
    static void loadGlobalSettings(){
        Setting s= Setting.getInstance();
        if (s.getUnitWHardness().matches("degree")){
            Global.khunit="degree";
        }else{
            Global.khunit="ppm";
        }
        if (s.getUnitTemp().matches("C")){
            Global.temperatureunit="C";
        }else{
            Global.temperatureunit="F";
        }
        if (s.getUnitLenght().matches("cm")){
            Global.lenghtunit="cm";
        }else{
           Global.lenghtunit="inch";
        }
        if (s.getUnitVolume().matches("l")){
            Global.volunit="l";
        }else{
            Global.volunit="usGal";
        }  
            
    }
    
    
    
    private void refreshPie(){
        String [] labels;
        double [] values ; 
        double solub;   //Solubility               
        
        //get sol name
        String solname=solNameComboBox.getSelectedItem().toString();
        String footer=java.util.ResourceBundle.getBundle("nyagua.Bundle").getString("solutions.solubility");
        String header=(String) soluteElementsName.get(solname);
        double [] solValues=(double[]) solute.get(solname); 
         solub = solValues[Solubility]/10;
         
        footer=footer + ": " + String.valueOf(solub) + " g/100ml (20°C)" ;
        
        double [] elementsValues=(double[]) soluteElementsValues.get(solname);        
        //counting non zero elements and summing them
        int count=elementsValues.length;        
        values=new double [count];
        labels=(String[])soluteElementsLabels.get(solname);
        //build elements to display
        for (int x=0;x<count;x++){
            values[x]=elementsValues[x]/100;//total; // divide to get %            
        }
        
        //buld pie
        Pie pie;
        pie = new Pie(values,labels, footer,header);
        int previousCanvas=piePanel.getComponentCount();
        while (previousCanvas > 0 ){
            piePanel.remove(0);
            previousCanvas=piePanel.getComponentCount();
        }          
        pie.setSize(piePanel.getWidth(),piePanel.getHeight());
        piePanel.add(pie, BorderLayout.CENTER);
        piePanel.repaint();
    }

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        waterVolumeTextField.addMouseListener(new ContextMenuMouseListener());
        solutionWaterTextField.addMouseListener(new ContextMenuMouseListener());
        doseVolumeTextField.addMouseListener(new ContextMenuMouseListener());
        targetTextField.addMouseListener(new ContextMenuMouseListener());
        doseTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
    private void loadRecipe() {
        if (recipesComboBox.getSelectedIndex()== 0) {
            deleteRecipeButton.setEnabled(false);
            reportButton.setEnabled(false);
            return;
        }
        deleteRecipeButton.setEnabled(true);
        reportButton.setEnabled(true);
        String recipeName = recipesComboBox.getSelectedItem().toString();
        Recipe recipe = Recipe.getByRecipeName(recipeName); 
        
        if (recipe.getRecipeName() == null) { //new recipe 
            return;
        }
            
        if (! recipe.getMethod().isEmpty()) {            
            int method = Integer.parseInt(recipe.getMethod());
            switch (method) {
                case METHOD_TARGET:
                    targetToggleButtonMouseClicked(null);
                    break;
                   
                case METHOD_EI:
                    eiToggleButtonMouseClicked(null);
                    break;
                
                case METHOD_EID:
                    eiDayToggleButtonMouseClicked(null);
                    break;
                
                case METHOD_EIW:
                    eiWeekToggleButtonMouseClicked(null);
                    break;
                
                case METHOD_PMDD:
                    pmddToggleButtonMouseClicked(null);
                    break;
                
                case METHOD_PPS:
                    ppsToggleButtonMouseClicked(null);
                    break;
            }
        }
        
        if (recipe.getUnits().equalsIgnoreCase("mg")) {
            doseUnit_mgRadioButton.setSelected(true);
        } 
        else {
            doseUnits_gRadioButton.setSelected(true);
        }
        
        if (recipe.getWaterUnits().equalsIgnoreCase("l")) {
            unitLiterRadioButton.setSelected(true);
        }
        else {
            unitGalRadioButton.setSelected(true);
        }
        
        if (recipe.getForm().equalsIgnoreCase("solution")) {
            solutionRadioButton.setSelected(true);
        }
        else {
            powderRadioButton.setSelected(true);
        }
        
        solNameComboBox.setSelectedItem(recipe.getProduct());
        
        waterVolumeTextField.setText(recipe.getWaterVolume());
        solutionWaterTextField.setText(recipe.getSolutionVolume());
        doseVolumeTextField.setText(recipe.getDoseVolume());
        targetTextField.setText(recipe.getTarget());
        doseTextField.setText(recipe.getAdded());
        
        checkFields();
        if (calcButton.isEnabled()) {
            calcButtonActionPerformed(null);
        }
        
        recipesComboBox.setSelectedItem(recipeName);
    }
    
    private void saveRecipe() {
       boolean newRecipe = true;
        //check Name
        String recipeName = recipesComboBox.getSelectedItem().toString();
        if (recipeName.isEmpty() || recipeName.equalsIgnoreCase("---")) {
            String msg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Insert.recipe.name.tosave");
            Util.showErrorMsg(msg);
            return;
        }
        Recipe recipe = new Recipe();
        Recipe existingRecipe = Recipe.getByRecipeName(recipeName);
        if (existingRecipe.getRecipeName() != null) { //name exist update ask for update
             int a = JOptionPane.showConfirmDialog(null,
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DUPLICATE_RECIPE_NAME"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("WARNING"), JOptionPane.YES_NO_OPTION);
            if ( a != JOptionPane.YES_OPTION){
                return;
            }
            newRecipe = false;
            recipe = existingRecipe;
        }
        
        recipe.setRecipeName(recipeName);
        recipe.setMethod(String.valueOf(getSelectedMethod()));
        recipe.setWaterVolume(LocUtil.delocalizeDouble(waterVolumeTextField.getText()));
        if (unitLiterRadioButton.isSelected()) {
            recipe.setWaterUnits("l");
        } else {
            recipe.setWaterUnits("usGal");
        }
        recipe.setProduct(solNameComboBox.getSelectedItem().toString());
        if (solutionRadioButton.isSelected()) {
            recipe.setForm("solution");
        } else {
            recipe.setForm("powder");
        }
        recipe.setSolutionVolume(LocUtil.delocalizeDouble(solutionWaterTextField.getText()));
        recipe.setDoseVolume(LocUtil.delocalizeDouble(doseVolumeTextField.getText()));
        recipe.setTarget(LocUtil.delocalizeDouble(targetTextField.getText()));
        recipe.setAdded(LocUtil.delocalizeDouble(doseTextField.getText()));
        if (doseUnit_mgRadioButton.isSelected()) {
            recipe.setUnits("mg");
        } else {
            recipe.setUnits("g");
        }
        recipe.save(recipe);
        
        if (newRecipe) {
            recipesComboBox.addItem(recipeName);
            
            Watched nyMessages=Watched.getInstance();
            nyMessages.Update(Watched.ADDED_SOLUTION);
        } 
    }
    
    private void deleteRecipe() {
        String recipeName = recipesComboBox.getSelectedItem().toString();
        if (recipeName.isEmpty() || recipeName.equalsIgnoreCase("---")) {
            String msg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Cant.delete.default.recipe");
            Util.showErrorMsg(msg);
            return;
        }
        //check if in use
        if (Plans.isRecipeInUse(recipeName)) {
            String msg = java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Cant.delete.recipe.in.use");
            Util.showErrorMsg(msg);
            return;
        }
        Recipe.deleteById(recipeName);
        recipesComboBox.removeItem(recipesComboBox.getSelectedItem());
        recipesComboBox.setSelectedIndex(0);
    }

}

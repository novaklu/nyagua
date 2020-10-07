/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon
 *    Copyright (C) 2010 Tom Judge <tom(at)tomjudge.com>
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
 * Ny.java - Nyagua main form
 *
 * Created on 24 novembre 2009, 11.05
 */
package nyagua;

import dispatching.Watched;
import dispatching.Watcher;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nyagua.data.Aquarium;
import nyagua.data.Setting;
import util_panels.Calculators;
import util_panels.ConvertersPanel;
import util_panels.SolutionsPanel;


/**
 *
 * @author  giacomini
 * @version 1.0
 * 
 */
public class Ny extends javax.swing.JFrame {
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getID()==Watched.MOVE_FOCUS_TO_FISHBASE) {
                baseDisplay(BaseTabbedPane,FBPANEL_IDX);
            }
            else if(e.getID()==Watched.MOVE_FOCUS_TO_SOLUTIONS) {
                utilDisplay(UtilTabbedPane, UTIL_SOLPANEL_IDX);
            }
            else if(e.getID()==Watched.MOVE_FOCUS_TO_INVBASE) {
                baseDisplay(BaseTabbedPane,INVBPANEL_IDX);
            }
            else if(e.getID()==Watched.MOVE_FOCUS_TO_PLANTSBASE){
                baseDisplay(BaseTabbedPane,PBPANEL_IDX);
            }
            else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                mainTree.setSelectionRow(0);
            }
            else if(e.getID()==Watched.REQUEST_POPULATE_TREE){
                PopulateTree();
            }
            
            
        }
    };            
    Watcher settingWatch=new Watcher(al);
    
    //some constants values
    /*final int FBPANEL_IDX=5;
    final int INVBPANEL_IDX=7;
    final int PBPANEL_IDX=9;
    final int UTILPANEL_IDX=11;*/
    final int MAIN_PANEL_IDX=0;
    final int BASE_PANEL_IDX=1;
    final int FBPANEL_IDX=0;
    final int INVBPANEL_IDX=1;
    final int PBPANEL_IDX=2;    
    final int SCHEDPANEL_IDX=3;
    final int UTILPANEL_IDX=2;
    final int UTIL_CALCPANEL_IDX=0;
    final int UTIL_SOLPANEL_IDX=1;
    final int UTIL_CONVPANEL_IDX=2;
    
    AquariumPanel aquariumPanel=new AquariumPanel();
    ReadingsPanel readingPanel=new ReadingsPanel();
    MaintenancePanel maintPanel=new MaintenancePanel();
    ExpensesPanel expensePanel=new ExpensesPanel();
    DevicesPanel devPanel=new DevicesPanel();
    HistoryPanel histPanel= new HistoryPanel();
    FBPanel FB=new FBPanel();
    FishPanel fishPanel=new FishPanel();
    IBPanel IB=new IBPanel();
    InvertebratesPanel invertsPanel=new InvertebratesPanel();
    PBPanel PB=new PBPanel();
    PlantsPanel plantPanel=new PlantsPanel();
    PlansPanel plansPanel = new PlansPanel(); 
    SchedulePanel schedulePanel=new SchedulePanel();
    
    /** Creates new form Ny */
    @SuppressWarnings("empty-statement")
    public Ny() {
        loadGlobalSettings(); 
        initComponents();
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);        
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/fish_ico.png")).getImage());//NOI18N        
        loadTablesSettings();  
        initUnitsAndDateFormats();      
        buildPanels();        
        buildMenus();       
        
        //load *base data
        nyMessages.Update(Watched.REQUEST_POPULATE_FBTABLE);
        nyMessages.Update(Watched.REQUEST_POPULATE_IBTABLE);
        nyMessages.Update(Watched.REQUEST_POPULATE_PBTABLE);
        
    }
    
    /**
     * Add panels to main panel
     */
    private void buildPanels(){
        topTabbedPane.setIconAt(0, new ImageIcon(getClass().getResource("/icons/acqua32x.png")));
        topTabbedPane.setIconAt(1, new ImageIcon(getClass().getResource("/icons/book32x.png")));
        topTabbedPane.setIconAt(2, new ImageIcon(getClass().getResource("/icons/tools.png")));
                
         //aquarium panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQUARIUM"), aquariumPanel);      
        
        //readings panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.readingsPanel.TabConstraints.tabTitle_1"), readingPanel);      
        //maintenances panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.maintenancePanel.TabConstraints.tabTitle"), maintPanel);      
        //maintenances panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.historyPanel.TabConstraints.tabTitle"), histPanel);      
        //expenses panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.expensesPanel.TabConstraints.tabTitle"), expensePanel);      
        //devices panel
         MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.devicesPanel.TabConstraints.tabTitle"), devPanel);      
        //FishBasePanel
        BaseTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISHBASE"), FB);
        FBPanel.setAssociatedCombo( FishPanel.fishNameComboBox);
        //fish panel
        MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISH"), fishPanel);
         //InvBasePanel
        BaseTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IB_title"), IB);
        IBPanel.setAssociatedCombo( InvertebratesPanel.invertsNameComboBox);
        //invertebrates panel
        MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVERTS_"),invertsPanel );
         //PlantsBasePanel
        BaseTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTSBASE"), PB);
        PBPanel.setAssociatedCombo( PlantsPanel.plantsNameComboBox);     
        //plants panel
        MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTS_"), plantPanel);   
        //plans panel
        MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Fertilization"), plansPanel);      
        //Scheduler panel
        BaseTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Scheduler"), schedulePanel);
        if (!Global.isSchedulerEnabled) {
            BaseTabbedPane.remove(BaseTabbedPane.getComponentAt(BaseTabbedPane.getComponentCount()-1));
        }
        
        //Utility Panel
        //---- creates util tabbed container
        
        //final JTabbedPane utilTabs=new JTabbedPane();        
        //MainTabbedPane.add(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.utilityPanel.TabConstraints.tabTitle"), utilTabs);
        //calculator
        JPanel utilityPanel=new Calculators();        
        UtilTabbedPane.addTab(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.utility.calculators.title"), utilityPanel);
        //solutions
        JPanel solutionsPanel =new SolutionsPanel();
        UtilTabbedPane.addTab(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.panel.title"), solutionsPanel);
        //converters
        JPanel converterPanel= new ConvertersPanel();
        UtilTabbedPane.addTab(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Converters"),converterPanel);
    }
    
    
    /**
     * Build non static menu
     * 
     */
    private void buildMenus(){
       //--- creates utility menù
        //calculator
        JMenuItem utilityCalculatorMenu= new javax.swing.JMenuItem();
        UtilityMenu.add(utilityCalculatorMenu);
        utilityCalculatorMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                utilityDisplay(UtilTabbedPane,UTIL_CALCPANEL_IDX);
            }
            
        });
        utilityCalculatorMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.utility.calculators.title"));
        //solutions
        JMenuItem utilitySolutionsMenu= new javax.swing.JMenuItem();
        UtilityMenu.add(utilitySolutionsMenu);
        utilitySolutionsMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utilityDisplay(UtilTabbedPane,UTIL_SOLPANEL_IDX);
            }
        });
        utilitySolutionsMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.panel.title"));
        //converters
        JMenuItem utilityConvertersMenu= new javax.swing.JMenuItem();
        UtilityMenu.add(utilityConvertersMenu);
        utilityConvertersMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utilityDisplay(UtilTabbedPane,UTIL_CONVPANEL_IDX);
            }
        });
        utilityConvertersMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Converters"));
               
        //--end
        
        //Creates *Base Menu.
        //FishBase
        JMenuItem baseFishMenu= new javax.swing.JMenuItem();
        baseMenu.add(baseFishMenu);
        baseFishMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                baseDisplay(BaseTabbedPane,FBPANEL_IDX);
            }
            
        });
        baseFishMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISHBASE"));
        //InvertBase
        JMenuItem baseInvertsMenu= new javax.swing.JMenuItem();
        baseMenu.add(baseInvertsMenu);
        baseInvertsMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                baseDisplay(BaseTabbedPane,INVBPANEL_IDX);
            }
            
        });
        baseInvertsMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IB_title"));
        //PlantBase
        JMenuItem basePlantsMenu= new javax.swing.JMenuItem();
        baseMenu.add(basePlantsMenu);
        basePlantsMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                baseDisplay(BaseTabbedPane,PBPANEL_IDX);
            }
            
        });
        basePlantsMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTSBASE"));
        
        //Export
        JSeparator jSeparator25=new JSeparator();
        baseMenu.add(jSeparator25);
        
        JMenuItem baseExportMenu= new javax.swing.JMenuItem();
        baseMenu.add(baseExportMenu);
        baseExportMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                baseExport();
            }            
        });
        baseExportMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.SettingsExportMenu.text"));
        
        //Import
        JMenuItem baseImportMenu= new javax.swing.JMenuItem();
        baseMenu.add(baseImportMenu);
        baseImportMenu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt ) {
                baseImport();
            }            
        });
        baseImportMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.SettingsImportMenu.text"));
        
        if (Global.isSchedulerEnabled) {            
            //separator
            JSeparator jSeparator2=new JSeparator();
            baseMenu.add(jSeparator2);
            
            //Scheduler
            JMenuItem baseScheduleMenu= new javax.swing.JMenuItem();
            baseMenu.add(baseScheduleMenu);
            baseScheduleMenu.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt ) {
                    baseDisplay(BaseTabbedPane,SCHEDPANEL_IDX);
                }

            });
            baseScheduleMenu.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Scheduler")); 
        }
        
        
    }
    
    //Display the right tab on utility menu
    private void utilityDisplay(JTabbedPane jtp, int i) {
        jtp.setSelectedIndex(i);
        jtp.requestFocus();
        this.topTabbedPane.setSelectedIndex(UTILPANEL_IDX); //utility

    }
    
    //Display the right tab on base menu
    private void baseDisplay(JTabbedPane jtp, int i) {
        jtp.setSelectedIndex(i);
        jtp.requestFocus(); 
        Watched nyMessages=Watched.getInstance();
        this.topTabbedPane.setSelectedIndex(BASE_PANEL_IDX); //base
        switch (i){
            case FBPANEL_IDX: nyMessages.Update(Watched.REQUEST_POPULATE_FBTABLE);
                break;
            case INVBPANEL_IDX: nyMessages.Update(Watched.REQUEST_POPULATE_IBTABLE);
                break;
            case PBPANEL_IDX: nyMessages.Update(Watched.REQUEST_POPULATE_PBTABLE);
                break;                
            case SCHEDPANEL_IDX: schedulePanel.refreshDaysList(null);
                break;    
        }
    }   
    
     //Display the right tab on util menu
    private void utilDisplay(JTabbedPane jtp, int i) {
        jtp.setSelectedIndex(i);
        jtp.requestFocus(); 
        Watched nyMessages=Watched.getInstance();
        this.topTabbedPane.setSelectedIndex(UTILPANEL_IDX); //base
       
    }  

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        treePanel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        filterDateFromLabel = new javax.swing.JLabel();
        filterDateSelLabel = new javax.swing.JLabel();
        filterDateToLabel = new javax.swing.JLabel();
        filterDateFromTextField = new com.toedter.calendar.JDateChooser();
        filterDateToTextField = new com.toedter.calendar.JDateChooser();
        filterApplyBtn = new javax.swing.JLabel();
        filterStateLabel = new javax.swing.JLabel();
        filterStateBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainTree = new javax.swing.JTree();
        jSeparator17 = new javax.swing.JSeparator();
        topTabbedPane = new javax.swing.JTabbedPane();
        MainTabbedPane = new javax.swing.JTabbedPane();
        BaseTabbedPane = new javax.swing.JTabbedPane();
        UtilTabbedPane = new javax.swing.JTabbedPane();
        menuBar1 = new javax.swing.JMenuBar();
        fileBackupMenu = new javax.swing.JMenu();
        FileNewMenuItem = new javax.swing.JMenuItem();
        FileOpenMenuItem = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JSeparator();
        FileBackupMenu = new javax.swing.JMenuItem();
        FileBackMaintDBMenu = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        exitMenuItem1 = new javax.swing.JMenuItem();
        editMenu1 = new javax.swing.JMenu();
        SettingsMenu = new javax.swing.JMenuItem();
        SettingsExportMenu = new javax.swing.JMenuItem();
        SettingsImportMenu = new javax.swing.JMenuItem();
        baseMenu = new javax.swing.JMenu();
        UtilityMenu = new javax.swing.JMenu();
        helpMenu1 = new javax.swing.JMenu();
        creditsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        setTitle(bundle.getString("Ny.title_1")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jSplitPane1.setDividerLocation(160);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setContinuousLayout(true);

        treePanel.setLayout(new java.awt.BorderLayout());

        filterDateFromLabel.setText(bundle.getString("Ny.filterDateFromLabel.text")); // NOI18N

        filterDateSelLabel.setText(bundle.getString("Ny.filterDateSelLabel.text")); // NOI18N
        filterDateSelLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterDateSelLabelMouseClicked(evt);
            }
        });

        filterDateToLabel.setText(bundle.getString("Ny.filterDateToLabel.text")); // NOI18N

        filterApplyBtn.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        filterApplyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/recycle.png"))); // NOI18N
        filterApplyBtn.setToolTipText(bundle.getString("FILTERSTATE")); // NOI18N
        filterApplyBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        filterApplyBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterApplyBtnMouseClicked(evt);
            }
        });

        filterStateLabel.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        filterStateLabel.setText(bundle.getString("Ny.filterStateLabel.text")); // NOI18N

        filterStateBtn.setToolTipText("");
        filterStateBtn.setFocusable(false);
        filterStateBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filterStateBtn.setMaximumSize(new java.awt.Dimension(8, 20));
        filterStateBtn.setMinimumSize(new java.awt.Dimension(8, 20));
        filterStateBtn.setPreferredSize(new java.awt.Dimension(8, 20));
        filterStateBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterStateBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterStateBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterDateFromLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(filterDateToLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(filterDateFromTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(filterApplyBtn)
                        .addGap(10, 10, 10)
                        .addComponent(filterStateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filterStateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(filterDateToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterDateSelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(filterDateSelLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterDateFromLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterDateFromTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterDateToLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterDateToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(filterStateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterApplyBtn)
                    .addComponent(filterStateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        treePanel.add(jPanel12, java.awt.BorderLayout.PAGE_END);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(97, 403));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Aquariums");
        mainTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        mainTree.setToolTipText(bundle.getString("Ny.mainTree.toolTipText")); // NOI18N
        mainTree.setAlignmentX(1.0F);
        mainTree.setAlignmentY(1.0F);
        mainTree.setAutoscrolls(true);
        mainTree.setPreferredSize(new java.awt.Dimension(80, 20));
        PopulateTree();
        mainTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainTreeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mainTree);

        treePanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        treePanel.add(jSeparator17, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setLeftComponent(treePanel);

        topTabbedPane.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        topTabbedPane.setAlignmentX(0.0F);
        topTabbedPane.setAlignmentY(0.0F);
        topTabbedPane.setName(""); // NOI18N

        MainTabbedPane.setName("tab"); // NOI18N
        topTabbedPane.addTab("", null, MainTabbedPane, "");
        topTabbedPane.addTab("", BaseTabbedPane);
        topTabbedPane.addTab("", UtilTabbedPane);

        jSplitPane1.setRightComponent(topTabbedPane);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        fileBackupMenu.setText(bundle.getString("File")); // NOI18N

        FileNewMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/filenew.png"))); // NOI18N
        FileNewMenuItem.setText(bundle.getString("Ny.FileNewMenuItem.text")); // NOI18N
        FileNewMenuItem.setPreferredSize(new java.awt.Dimension(240, 37));
        FileNewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileNewMenuItemActionPerformed(evt);
            }
        });
        fileBackupMenu.add(FileNewMenuItem);

        FileOpenMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/fileopen.png"))); // NOI18N
        FileOpenMenuItem.setText(bundle.getString("Ny.FileOpenMenuItem.text")); // NOI18N
        FileOpenMenuItem.setPreferredSize(new java.awt.Dimension(240, 37));
        FileOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileOpenMenuItemActionPerformed(evt);
            }
        });
        fileBackupMenu.add(FileOpenMenuItem);
        fileBackupMenu.add(jSeparator18);

        FileBackupMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/media-tape.png"))); // NOI18N
        FileBackupMenu.setText(bundle.getString("Ny.FileBackupMenu.text")); // NOI18N
        FileBackupMenu.setPreferredSize(new java.awt.Dimension(240, 37));
        FileBackupMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileBackupMenuActionPerformed(evt);
            }
        });
        fileBackupMenu.add(FileBackupMenu);

        FileBackMaintDBMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run-build-configure.png"))); // NOI18N
        FileBackMaintDBMenu.setText(bundle.getString("Ny.FileBackMaintDBMenu.text")); // NOI18N
        FileBackMaintDBMenu.setPreferredSize(new java.awt.Dimension(240, 37));
        FileBackMaintDBMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileBackMaintDBMenuActionPerformed(evt);
            }
        });
        fileBackupMenu.add(FileBackMaintDBMenu);
        fileBackupMenu.add(jSeparator14);

        exitMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/application-exit.png"))); // NOI18N
        exitMenuItem1.setText(bundle.getString("Exit")); // NOI18N
        exitMenuItem1.setPreferredSize(new java.awt.Dimension(240, 37));
        exitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileBackupMenu.add(exitMenuItem1);

        menuBar1.add(fileBackupMenu);

        editMenu1.setText(bundle.getString("Options")); // NOI18N

        SettingsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SettingsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/package_settings.png"))); // NOI18N
        SettingsMenu.setText(bundle.getString("Ny.SettingsMenu.text")); // NOI18N
        SettingsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsMenuActionPerformed(evt);
            }
        });
        editMenu1.add(SettingsMenu);

        SettingsExportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/filesave.png"))); // NOI18N
        SettingsExportMenu.setText(bundle.getString("Ny.SettingsExportMenu.text")); // NOI18N
        SettingsExportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsExportMenuActionPerformed(evt);
            }
        });
        editMenu1.add(SettingsExportMenu);

        SettingsImportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/folder-new.png"))); // NOI18N
        SettingsImportMenu.setText(bundle.getString("Ny.SettingsImportMenu.text")); // NOI18N
        SettingsImportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingsImportMenuActionPerformed(evt);
            }
        });
        editMenu1.add(SettingsImportMenu);

        menuBar1.add(editMenu1);

        baseMenu.setText(bundle.getString("Ny.baseMenu.text")); // NOI18N
        menuBar1.add(baseMenu);

        UtilityMenu.setText(bundle.getString("Ny.utilityPanel.TabConstraints.tabTitle")); // NOI18N
        menuBar1.add(UtilityMenu);

        helpMenu1.setText(bundle.getString("Ny.helpMenu1.text_1")); // NOI18N

        creditsMenuItem.setText(bundle.getString("Ny.creditsMenuItem.text")); // NOI18N
        creditsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditsMenuItemActionPerformed(evt);
            }
        });
        helpMenu1.add(creditsMenuItem);

        aboutMenuItem1.setText(bundle.getString("About")); // NOI18N
        aboutMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu1.add(aboutMenuItem1);

        menuBar1.add(helpMenu1);

        setJMenuBar(menuBar1);

        setSize(new java.awt.Dimension(977, 650));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        Setting s= Setting.getInstance();
        if (s.getSaveWinPosition()){
            Util.savePosition(this);
        }
        saveTableSettings();
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    @SuppressWarnings("static-access")
private void mainTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainTreeMouseClicked
    /**Load selected aquarium data from db */
    int node=mainTree.getLeadSelectionRow();     
    if (node==1) {//fishbase icon selected
        baseDisplay(BaseTabbedPane,FBPANEL_IDX);  
    } else if (node==2){//invertssbase icon selected            
        baseDisplay(BaseTabbedPane,INVBPANEL_IDX);
    } else if (node==3){//plantsbase icon selected            
        baseDisplay(BaseTabbedPane,PBPANEL_IDX);
    } else if (node==4){//scheduler icon selected    
        if (Global.isSchedulerEnabled) {
            baseDisplay(BaseTabbedPane,SCHEDPANEL_IDX);
        }    
    } else if (node==5){//util icon selected            
        this.topTabbedPane.setSelectedIndex(UTILPANEL_IDX);        
    } else if (node==6){//calculator icon selected
        JTabbedPane jtp = (JTabbedPane) this.topTabbedPane.getComponentAt(UTILPANEL_IDX);
        utilityDisplay(jtp, UTIL_CALCPANEL_IDX);
    } else if (node==7){//solutions icon selected
        JTabbedPane jtp = (JTabbedPane) this.topTabbedPane.getComponentAt(UTILPANEL_IDX);
        utilityDisplay(jtp, UTIL_SOLPANEL_IDX);
    }else if (node==8){//converter icon selected
        JTabbedPane jtp = (JTabbedPane) this.topTabbedPane.getComponentAt(UTILPANEL_IDX);
        utilityDisplay(jtp, UTIL_CONVPANEL_IDX);
    }else  {
        this.topTabbedPane.setSelectedIndex(MAIN_PANEL_IDX);
        this.MainTabbedPane.setSelectedIndex(0);
        if (mainTree.getLastSelectedPathComponent().toString()==null) {//click on empty part of tree
            return;
        }       
        
        try {
            //get all Aquarium data:
            String qry = "SELECT * FROM Aquarium;";// NOI18N
            DB.openConn();
            ResultSet rs = DB.getQuery(qry);
            while (rs.next()) {
                String aquariumName = rs.getString("Name");// NOI18N
                //compare with selected element
                if (aquariumName.matches(
                        mainTree.getLastSelectedPathComponent().toString())) {
                    cleanAllFields();
                    Global.AqID = rs.getInt("id");// NOI18N
                    Ny.populateTable();
                    Aquarium tank=Aquarium.getById(Global.AqID);                   
                   
                    aquariumPanel.LoadData();
                    Watched nyMessages=Watched.getInstance();
                    nyMessages.Update(Watched.REQUEST_POPULATE_LIST);                    
//                    schedulePanel.setCurrentAquarium();                  
                    
                }
            }
            DB.closeConn();
        } catch (SQLException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }    
    if (mainTree.isRowSelected(0)) {
        Global.AqID = 0;
        populateTable();
        cleanAllFields();
        Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.REQUEST_CLEAR_LIST);
    }
}//GEN-LAST:event_mainTreeMouseClicked

    
private void aboutMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItem1ActionPerformed
    NAboutDialog na=new NAboutDialog(null,true);
    na.setVisible(true);
}//GEN-LAST:event_aboutMenuItem1ActionPerformed

private void SettingsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsMenuActionPerformed
    // Show setting window
    Settings s = new Settings(null, true);
    s.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("SETTINGS"));
    s.setVisible(true);    
}//GEN-LAST:event_SettingsMenuActionPerformed

private void filterDateSelLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterDateSelLabelMouseClicked
    // 
}//GEN-LAST:event_filterDateSelLabelMouseClicked

private void creditsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creditsMenuItemActionPerformed
    // Display credits
    Application.ShowCredits();
}//GEN-LAST:event_creditsMenuItemActionPerformed

private void SettingsExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsExportMenuActionPerformed
    // export current settings to file
    Setting s = Setting.getInstance();
    s.exportSettings();
}//GEN-LAST:event_SettingsExportMenuActionPerformed

private void SettingsImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsImportMenuActionPerformed
    // import current settings from file
    Setting s = Setting.getInstance();
        try {
            s.importSettings();
        } catch (IOException | InvalidPreferencesFormatException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_SettingsImportMenuActionPerformed

private void FileBackupMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileBackupMenuActionPerformed
    //Backup db file
    Util.backupFile(treePanel);
}//GEN-LAST:event_FileBackupMenuActionPerformed

private void FileOpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileOpenMenuItemActionPerformed
    // Open existing File
    if (DB.fileDBOperations(DBSelector.MODE_DB_OPEN)){
        PopulateTree();
        Global.AqID=0;
        cleanAllFields();
    }
}//GEN-LAST:event_FileOpenMenuItemActionPerformed

private void FileNewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileNewMenuItemActionPerformed
    // Creates new DB    
    if (DB.fileDBOperations(DBSelector.MODE_DB_CREATE)){
        PopulateTree();
        Global.AqID=0;
        cleanAllFields();
    }
}//GEN-LAST:event_FileNewMenuItemActionPerformed

private void filterApplyBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterApplyBtnMouseClicked
    Global.dFrom=LocUtil.delocalizeDate(filterDateFromTextField.getDate());
    Global.dTo=LocUtil.delocalizeDate(filterDateToTextField.getDate());   
    if (Global.dFrom.isEmpty() && Global.dTo.isEmpty()){
        Global.filterState=false; 
        filterStateBtn.setBackground(new Color(238,238,238));
        filterStateLabel.setText("");//NOI18N
    }else{
        Global.filterState=true;
        filterStateBtn.setBackground(new Color(255,136,0));        
        filterStateLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("filter_on"));
    }
    populateTable();
}//GEN-LAST:event_filterApplyBtnMouseClicked

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    Setting s= Setting.getInstance();
    if (s.getSaveWinPosition()){
            Util.loadPosition(this);
            //resize tree
            int treeWidth=s.getTreeWidth();
            if (treeWidth>100 && treeWidth<400){
                jSplitPane1.setDividerLocation(treeWidth);
            }            
        }    
    saveTableSettings();
}//GEN-LAST:event_formWindowOpened

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Setting s= Setting.getInstance();
    if (s.getSaveWinPosition()){
            Util.savePosition(this);
            //save tree size
            s.setTreeWidth(jSplitPane1.getDividerLocation());
        }
    saveTableSettings();
}//GEN-LAST:event_formWindowClosing

private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    Setting s= Setting.getInstance();
    if (s.getSaveWinPosition()){
            Util.savePosition(this);
        }
    saveTableSettings();
}//GEN-LAST:event_formWindowClosed

private void FileBackMaintDBMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileBackMaintDBMenuActionPerformed
// Show backup and maintenance box
    DBMaintenance dBM= new DBMaintenance(this, true);
    dBM.setVisible(true);
}//GEN-LAST:event_FileBackMaintDBMenuActionPerformed

    private void filterStateBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterStateBtnMouseClicked
        // TODO USED for test only:
//       JPanel testPanel=new test();        
//       MainTabbedPane.add("Test", testPanel);
//       testPanel.setVisible(true);
    }//GEN-LAST:event_filterStateBtnMouseClicked

    /**
     * Show scheduler if required
     * 
     * @param evt 
     */
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if (Global.isSchedulerEnabled) {            
            if (Application.isStarted() && Application.getDefaultForm()==Application.DEFAULT_FORM_SCHEDULER){            
                baseDisplay(BaseTabbedPane,SCHEDPANEL_IDX);
            }
        }
    }//GEN-LAST:event_formWindowActivated

    
/**
 * Set the default nationalized date format
 * for all date fields
 */    
private  void initUnitsAndDateFormats(){
    
    
    Setting s=Setting.getInstance();
    String df = s.getDateFormat();
    filterDateFromTextField.setDateFormatString(df);
    filterDateToTextField.setDateFormatString(df);
    LocUtil.setDefaultDateFormat(df);
    Global.dateFormat=df;
    
     Watched nyMessages=Watched.getInstance();
     nyMessages.Update(Watched.CHANGED_UNITS_SETTINGS);
}

    /**
     * Populates the main tree
     *
     */
    public void PopulateTree() {
        /**Populate tree from db */
        String treename=Application.FS;
        if (DB.getCurrent()!=null){            
            treename=DB.getCurrent().substring(DB.getCurrent().lastIndexOf(Application.FS)+1);
            treename=treename + ".db";
        }
        mainTree.setModel(null);                
        //root node
        IconNode treeNode1=new IconNodeImpl(treename, new javax.swing.ImageIcon(getClass().getResource("/icons/database.png")), "");//NOI18N                    
        //jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        mainTree.setCellRenderer(new IconNodeRenderer());
        mainTree.setModel(new DefaultTreeModel(treeNode1));//Apply custom gui-bits model structure
        //Fishbase leaf
        IconNode leaf1 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("FISHBASE"), new javax.swing.ImageIcon(getClass().getResource("/icons/fish.png")),"");// NOI18N
        treeNode1.add(leaf1);
        //Invbase leaf        
        IconNode leaf2 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("IB_title"), new javax.swing.ImageIcon(getClass().getResource("/icons/shrimp.png")),"");// NOI18N
        treeNode1.add(leaf2);
        //PlantsBase leaf
        IconNode leaf3 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PLANTSBASE"), new javax.swing.ImageIcon(getClass().getResource("/icons/plants.png")),"");// NOI18N
        treeNode1.add(leaf3); 
        //Scheduler leaf
        
        if (Global.isSchedulerEnabled) {
            IconNode leaf4 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Scheduler"), new javax.swing.ImageIcon(getClass().getResource("/icons/clock_16x.png")),"");// NOI18N
            treeNode1.add(leaf4);         
        }
        else {
            IconNode    leaf4 = new IconNodeImpl("", null,"");// NOI18N
            treeNode1.add(leaf4);   
        }
        
        
        //Utils leaf        
        IconNode leaf5 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.utilityPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/icons/utils.png")),"");// NOI18N
        treeNode1.add(leaf5);
            IconNode leaf5_0 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Ny.utility.calculators.title"), new javax.swing.ImageIcon(getClass().getResource("/icons/calculator.png")),"");// NOI18N
            leaf5.add(leaf5_0);
            IconNode leaf5_1 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.panel.title"), new javax.swing.ImageIcon(getClass().getResource("/icons/chemistry.png")),"");// NOI18N
            leaf5.add(leaf5_1);
            IconNode leaf5_2 = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Converters"), new javax.swing.ImageIcon(getClass().getResource("/icons/convert.png")),"");// NOI18N
            leaf5.add(leaf5_2);
        int utilleaf=5;
        
        int aqleaf=6; //NOTE THIS IS the last leaf number must vary when tabs added
        IconNode aquariumleaf = new IconNodeImpl(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AQUARIUMS"), new javax.swing.ImageIcon(getClass().getResource("/icons/aquarium.png")),"");// NOI18N
        treeNode1.add(aquariumleaf);
        
        Aquarium [] aquariums = Aquarium.getAll();
        if (aquariums != null) {
            for (int i=0; i<aquariums.length; i++){
                DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(aquariums[i].getName());// NOI18N
                aquariumleaf.add(leaf);
            }
        }    

        mainTree.expandRow(0);
        mainTree.expandRow(aqleaf);   
        mainTree.expandRow(utilleaf);
        Global.AqID=0;
        Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.REQUEST_POPULATE_FBTABLE);
        nyMessages.Update(Watched.REQUEST_POPULATE_IBTABLE);
        nyMessages.Update(Watched.REQUEST_POPULATE_PBTABLE);
        populateTable();
        
    }
        
    /**
     * Populate tables from db
     *
     */
    public static void populateTable() {   
        Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.AQUARIUM_CLICKED);
    }

    /**
     * Clear Fields of all tables
     * @param tab a progressive table index
     */
    private void cleanAllFields() {
         Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.REQUEST_CLEAN_ALL_FIELDS);
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
        Global.useUnitsInForms=false;
        if (s.getUseUnits()){           
            Global.useUnitsInForms=true;
        }
        String cl1u=s.getDensCustomUnits();
        if (!cl1u.isEmpty()) {
            Global.densCustomUnit=cl1u;
        }
        String cl2u=s.getCondCustomUnits();
        if (!cl2u.isEmpty()) {
            Global.condCustomUnit = cl2u;
        }
        String cl3u=s.getSalinityCustomUnits();        
        if (!cl3u.isEmpty()) {
            Global.salinityCustomUnit = cl3u;
        }
        Global.generateAllReports=true;
        if (!s.getGenerateCompleteReport()) {
            Global.generateAllReports=false;
        }
        Global.includeExpensesReport=false;
        if (s.getIncludeExpensesReport()) {
            Global.includeExpensesReport=true;
        }
        Global.reportChartWidth=s.getChartWidth();
        Global.reportChartHeight=s.getChartHeight();  
        
        Global.isSchedulerEnabled=s.isSchedulerEnabled();
    }
      
    /**
     * export base
     */
    private static void baseExport() {
        ExpImp.Export();
    }
    
    /**
     * export base
     */
    private static void baseImport() {
        ExpImp.Import();
    }
    
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        ReadingsPanel.loadTablesSettings();        
        MaintenancePanel.loadTablesSettings();
        HistoryPanel.loadTablesSettings();
        PlansPanel.loadTablesSettings(); 
        ExpensesPanel.loadTablesSettings();
        DevicesPanel.loadTablesSettings();
        FishPanel.loadTablesSettings();
        FBPanel.loadTablesSettings();
        InvertebratesPanel.loadTablesSettings();
        IBPanel.loadTablesSettings();
        PlantsPanel.loadTablesSettings();
        PBPanel.loadTablesSettings();
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        //Save all setting related with tables (by now only column widths)
        Setting s=Setting.getInstance();        
        ReadingsPanel.saveTableSettings();
        MaintenancePanel.saveTableSettings();
        HistoryPanel.saveTableSettings();
        PlansPanel.saveTableSettings();
        ExpensesPanel.saveTableSettings();
        DevicesPanel.saveTableSettings();
        FishPanel.saveTableSettings();
        FBPanel.saveTableSettings();
        InvertebratesPanel.saveTableSettings();
        IBPanel.saveTableSettings();
        PlantsPanel.saveTableSettings();
        PBPanel.saveTableSettings();
    }
    
    /**
     * Start a pseudo wizard to initialize program
     */
    static void initialWiz() {
        boolean noAquarium = false;
        Aquarium [] aquariums = Aquarium.getAll();
        if ((aquariums == null) ||   (aquariums.length == 0)) noAquarium = true;
        Setting set = Setting.getInstance();
        double settingVersion = set.getSettingVersion();
        if (settingVersion == 0 && noAquarium) { // first time ever this setting version is used
            Global.firstStartup = true;
            Settings s = new Settings(null, true);
            
            Welcome welcome = new Welcome(null, true);
            welcome.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Welcome"));
            welcome.setSize(s.getWidth(), s.getHeight());   //make two dialogs same dimension
            welcome.setVisible(true);
            
            s.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Welcome"));
            s.setVisible(true); 
        }
        Global.firstStartup = false;
    }
        
    
    /**
     * Main form - main routine
     *
     * @param args the command line arguments
     * 
     * @throws ClassNotFoundException
     * @throws SQLException  
     */
    public static void main(String args[]) throws ClassNotFoundException, SQLException {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                //initialising the application globals                
                Application A = new Application();    
                A.setStartup(Application.STARTING);
                //creates or connect the database
                DB DataBase = new DB();
                
                initialWiz();
                
                //show main form
                new Ny().setVisible(true);                 
                A.setStartup(Application.STARTED);
               
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane BaseTabbedPane;
    private javax.swing.JMenuItem FileBackMaintDBMenu;
    private javax.swing.JMenuItem FileBackupMenu;
    private javax.swing.JMenuItem FileNewMenuItem;
    private javax.swing.JMenuItem FileOpenMenuItem;
    private javax.swing.JTabbedPane MainTabbedPane;
    private javax.swing.JMenuItem SettingsExportMenu;
    private javax.swing.JMenuItem SettingsImportMenu;
    private javax.swing.JMenuItem SettingsMenu;
    private javax.swing.JTabbedPane UtilTabbedPane;
    private javax.swing.JMenu UtilityMenu;
    private javax.swing.JMenuItem aboutMenuItem1;
    private javax.swing.JMenu baseMenu;
    private javax.swing.JMenuItem creditsMenuItem;
    private javax.swing.JMenu editMenu1;
    private javax.swing.JMenuItem exitMenuItem1;
    private javax.swing.JMenu fileBackupMenu;
    private javax.swing.JLabel filterApplyBtn;
    private javax.swing.JLabel filterDateFromLabel;
    private com.toedter.calendar.JDateChooser filterDateFromTextField;
    private javax.swing.JLabel filterDateSelLabel;
    private javax.swing.JLabel filterDateToLabel;
    private com.toedter.calendar.JDateChooser filterDateToTextField;
    private javax.swing.JButton filterStateBtn;
    private javax.swing.JLabel filterStateLabel;
    private javax.swing.JMenu helpMenu1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree mainTree;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JTabbedPane topTabbedPane;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables

    private static class IconNodeImpl extends IconNode {

        public IconNodeImpl(Object userObject, Icon i, String t) {
            super(userObject, i, t);
        }

        @Override
        public void showPopupMenu(Component comp, int x, int y) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void doubleClicked() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    static final Logger _log = Logger.getLogger(Ny.class.getName());
    
}
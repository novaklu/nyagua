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
 * PlansPanel.java
 *
*/
package nyagua;

import nyagua.components.ElementRenderer;
import dispatching.Watched;
import dispatching.Watcher;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import nyagua.data.Aquarium;
import nyagua.data.Plans;
import nyagua.data.Recipe;
import nyagua.data.Setting;
import nyagua.data.Solutions;

/**
 *
 * @author rudigiacomini
 */
public class PlansPanel extends javax.swing.JPanel {
    
    //get Solutions 
    Solutions solutions = new Solutions();
    
    public final int METHOD_TARGET=Solutions.METHOD_TARGET;
    public final int METHOD_EI=Solutions.METHOD_EI;
    public final int METHOD_EID=Solutions.METHOD_EID;
    public final int METHOD_EIW=Solutions.METHOD_EIW;
    public final int METHOD_PPS=Solutions.METHOD_PPS;
    public final int METHOD_ADA=Solutions.METHOD_ADA;
    public final int METHOD_PMDD=Solutions.METHOD_PMDD;
    
        
    //Types of operations we can do with this form
    public static String[] methods=new String [] {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("fertilization_method_combobox_default"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("fertilization_method_combobox_custom"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.3.ei"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.4.eid"),//NOI18N            
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.5.eiw"),//NOI18N 
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.6.pps"),//NOI18N 
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.7.pmdd"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.8.ada")//NOI18N
    };
    
    private HashMap solute = solutions.getSolute();
            
    private static double aquariumWaterVolume;
    
    //Connect listener to application bus
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getID()==Watched.AQUARIUM_CLICKED){
                 if (Global.AqID != 0) {                     
                     fillWCvalue();
                     populateTable();
                 }
                 else {
                     emptyTable();
                 }
            } 
            else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            }  
            else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                emptyCombo();
            } 
            else if(e.getID()==Watched.REQUEST_POPULATE_LIST){
                PopulateList();
            } 
            else if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                //No field use units here
            } 
            else if(e.getID()==Watched.ADDED_SOLUTION){
                loadRecipes();
            } 
        }
    };         
   
    Watcher settingWatch=new Watcher(al); 
    
    
    /** Creates new form HistoryPanel */
    public PlansPanel() {
        initComponents();
        initCutAndPaste(); 
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
    }

    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList3 = {idTextField,  fertIntervalTextField};
        Util.CleanTextFields(jtfList3);
        String selected = Plans.getSelectedMethod();
        if (selected != null &&  !selected.isEmpty()) {
            int selectedIndex = Integer.parseInt(selected);
            if (selectedIndex == 0) {
                selectedIndex = 1; //custom method is saved as 0
            }
            methodComboBox.setSelectedIndex(selectedIndex);
            methodComboBox.setEnabled(false);
            recipeComboBox.setEnabled(true);
            setFertInterval();
            loadRecipes();
            setDelayInterval();
        }
        else {
            recipeComboBox.setEnabled(false);
            methodComboBox.setEnabled(true);
            methodComboBox.setSelectedIndex(0);
        }
        recipeComboBox.setSelectedIndex(0);
    }
    
    
//TODO Check this
    private void fillElementsTable() {
        
        double [] allValues = new double [14];
        String status = ""; //NOI18N
        double wc = 0;
        
        if (!wcIntervalValueLabel.getText().isEmpty()) {
            wc = Double.parseDouble(LocUtil.delocalizeDouble(
                wcIntervalValueLabel.getText()));
        }
        
        List <Plans> plans = Plans.getAllPlans();
        
        if ((plans != null) && (!plans.isEmpty())) {
            for (Plans plan : plans) {
                
                double method = 0;
                double fi= 0;
                double delay = 0;
                
                if (!plan.getMethod().isEmpty()){
                    method = Double.parseDouble(LocUtil.delocalizeDouble(
                        plan.getMethod())); 
                }
                
                if (!plan.getFertInterval().isEmpty()){
                    fi = Double.parseDouble(LocUtil.delocalizeDouble(
                        plan.getFertInterval())); 
                }

                if (!plan.getFertDelay().isEmpty()) {
                    delay = Double.parseDouble(LocUtil.delocalizeDouble(
                        plan.getFertDelay()));
                }
                
                if (plan.getRecipe().isEmpty()) {
                    continue;
                }
                Recipe recipe = Recipe.getByRecipeName(plan.getRecipe());
                
                double targ=getDouble(recipe.getTarget());
                double av=getDouble(recipe.getWaterVolume());        
                double sv=getDouble(recipe.getSolutionVolume());
                double dv=getDouble(recipe.getDoseVolume());
                String compound=recipe.getProduct();
                
                double calcresult=solutions.calcSolute(compound,targ,av,sv,dv);    
                double [] values = 
                    solutions.calcElements(compound,calcresult, av, sv, dv);
                
                for (int i = 0; i < values.length; i++ ) {
                    double currentValue = values[i];
                    
                    //multiply for fertilization days
                    if ((wc > 0) && (fi > 0)) {
                        if (method == Solutions.METHOD_PMDD) {
                            //in PMDD dose is already calculated for daily dosing
                            currentValue = 
                                currentValue * Math.max(1, (wc-delay)/(fi*7));
                        }
                        else if (method != Solutions.METHOD_PPS) {
                            
                            currentValue = 
                                currentValue * Math.max(1, (wc-delay)/fi);
                        }
                    }
                    
                    //sum all values in new array
                    allValues [i] = allValues[i] + currentValue;
                }
                
                if (compound.contains("EDDHA")){
                    status = status + solutions.checkEDDHA();
                } else if (compound.contains("K3PO4")){
                    status = status + solutions.checkK3PO4();
                }
            }
            
        }
        buildElementsTable(allValues, solutions.getElements());
        
        status = status + solutions.checkCu(allValues[Solutions.Cu]);
        hintLabel.setText(status); 
    }
    
    private double getDouble (String value) {
        return Double.valueOf(LocUtil.delocalizeDouble(value));
    }
    
    
    private void buildElementsTable(double [] values, String[] elements) {
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
        
        for (int y=0; y<14; y++){
            ci[y] = elements[y];
            dv[0][y]=LocUtil.localizeDouble(values[y]);  
        }     
        
        dm.setDataVector(dv, ci);
        elementsTable.setModel(dm);          
        
         for (int x=0; x<14; x++){
            TableColumn col = elementsTable.getColumnModel().getColumn(x);
            col.setPreferredWidth(100);
                       
            DefaultTableCellRenderer renderer = new ElementRenderer();
            
            int method = methodComboBox.getSelectedIndex(); 
            ElementRenderer.setFertMethod(method);
            
            col.setCellRenderer(renderer);
        }  
        
    }
    
    private void  fillWCvalue() {
        //read value from aquarium
        if (Global.AqID <= 0) {
            wcIntervalValueLabel.setText("0");
        } 
        else {    
            Aquarium aquarium = Aquarium.getById(Global.AqID);
            String wci = aquarium.getWaterChange();
            if (wci != null && !wci.isEmpty() && Integer.parseInt(wci) > 0) {
                 
                 wcIntervalValueLabel.setText(wci);
            }
            else {
                 wcIntervalValueLabel.setText("0");
                //TODO alert if empty
            }
            
             String vol = aquarium.getVolume();
            if (vol != null && !vol.isEmpty()) {
                aquariumWaterVolume = Double.parseDouble(
                    LocUtil.delocalizeDouble(vol));
            }
            
        }        
    }
    
    /**
     * populate the table
     */
    private void populateTable(){
        Plans.populateTable(recipesTable);
        fillElementsTable();
    }
    
    

    private void emptyCombo() {
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        methodComboBox.setModel(dcm);
        recipeComboBox.setModel(dcm);
    }
    
    /**
     * Empties a jTable assigning null model
     * 
     */
    private static void emptyTable (){
        DefaultTableModel dm = new DefaultTableModel();
        String tableData[][] = {{null}};
        String[] nameHeader = {
            java.util.ResourceBundle.getBundle("nyagua/Bundle").
                    getString("NO_SELECTION")
        };
        dm.setDataVector(tableData, nameHeader);
        recipesTable.setModel(dm);
    }
    
    /**
     * Load tables widths
     */
    static void loadTablesSettings(){
        Setting s=Setting.getInstance();
        int [] widths=s.getTableWidths("histtable", Plans.CAPTIONS.length);//NOI18N
        Plans.setColWidth(widths);
        Util.setColSizes(recipesTable,widths );
    }
    
    /**
     * Save tables widths
     */
    static void saveTableSettings(){
        Setting s=Setting.getInstance();
        s.setTableWidths("histtable", recipesTable);//NOI18N
    }
    
       
    /**
     * Populate selected list with a field from a table
     *
     */
    private void PopulateList() {          
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        loadMethods();
    }
    
     /**
     * adds methos to related combo
     */
    private void loadMethods(){
        methodComboBox.removeAllItems();
        for (String method : methods) {
            methodComboBox.addItem(method.trim());
        }
       String selected = Plans.getSelectedMethod();
       if (selected != null &&  !selected.isEmpty()) {
           int selectedIndex = Integer.parseInt(selected);
           if (selectedIndex == 0) {
               selectedIndex = 1; //custom method is saved as 0
           }
           methodComboBox.setSelectedIndex(selectedIndex);
           methodComboBox.setEnabled(false);
           recipeComboBox.setEnabled(true);
           setFertInterval();
           loadRecipes();
           setDelayInterval();
       }
       else {
           recipeComboBox.setEnabled(false); 
           methodComboBox.setEnabled(true);
       }
    }
    
    /**
     * adds Recipes to related combo
     */
    private void loadRecipes(){
        if (methodComboBox.getSelectedIndex() > 0) {
            int method = methodComboBox.getSelectedIndex();
            if (method == 1) {
                method = METHOD_TARGET; 
            }
            Plans.populateRecipes(
                    recipeComboBox, method, aquariumWaterVolume);
            
            if (recipeComboBox.getItemCount() > 0 && !recipeComboBox.isEnabled()) {
                recipeComboBox.setEnabled(true);
            }
        }    
    }
    
    /**
     * refresh all fields when table selection change
     */
    private void refreshFields(){  
        String tmp = recipesTable.getValueAt(
                recipesTable.getSelectedRow(), 0).toString();
        
        int recId = TablesUtil.getIdFromTable(
                recipesTable, recipesTable.getSelectedRow());
        
        if (recId > 0) {
            idTextField.setText(tmp);
        }
            
        Plans plan = Plans.getById(recId);
        int method = Integer.parseInt(plan.getMethod());
        if (method == METHOD_TARGET) {
            method = 1;
        }
        methodComboBox.setSelectedIndex(method);        
        recipeComboBox.setSelectedItem(plan.getRecipe());
        fertIntervalTextField.setText(plan.getFertInterval());
        delayIntervalTextField.setText(plan.getFertDelay());
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

        idLabel = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        methodLabel = new javax.swing.JLabel();
        methodComboBox = new javax.swing.JComboBox<>();
        recipeLabel = new javax.swing.JLabel();
        recipeComboBox = new javax.swing.JComboBox<>();
        fertIntervalLabel = new javax.swing.JLabel();
        fertIntervalTextField = new javax.swing.JTextField();
        delayIntervalLabel = new javax.swing.JLabel();
        delayIntervalTextField = new javax.swing.JTextField();
        waterCIntervalLabel = new javax.swing.JLabel();
        wcIntervalValueLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        recipesTable = new javax.swing.JTable();
        jToolBar4 = new javax.swing.JToolBar();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        deleteButton = new javax.swing.JButton();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        rightPanel = new javax.swing.JPanel();
        addedLabel = new javax.swing.JLabel();
        elementsScrollPane = new javax.swing.JScrollPane();
        elementsTable = new javax.swing.JTable();
        hintLabel = new javax.swing.JLabel();
        addItemButton = new javax.swing.JButton();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        idLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(idLabel, gridBagConstraints);

        idTextField.setEditable(false);
        idTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        idTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 8, 5);
        add(idTextField, gridBagConstraints);

        methodLabel.setText(bundle.getString("METHOD")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 5);
        add(methodLabel, gridBagConstraints);

        methodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---" }));
        methodComboBox.setMinimumSize(new java.awt.Dimension(146, 24));
        methodComboBox.setPreferredSize(new java.awt.Dimension(146, 24));
        methodComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        add(methodComboBox, gridBagConstraints);

        recipeLabel.setText(bundle.getString("Recipe")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(recipeLabel, gridBagConstraints);

        recipeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---" }));
        recipeComboBox.setEnabled(false);
        recipeComboBox.setMinimumSize(new java.awt.Dimension(146, 24));
        recipeComboBox.setName(""); // NOI18N
        recipeComboBox.setPreferredSize(new java.awt.Dimension(146, 24));
        recipeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recipeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        add(recipeComboBox, gridBagConstraints);

        fertIntervalLabel.setText(bundle.getString("FERT_INT")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fertIntervalLabel, gridBagConstraints);

        fertIntervalTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        fertIntervalTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        fertIntervalTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fertIntervalTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fertIntervalTextField, gridBagConstraints);

        delayIntervalLabel.setText(bundle.getString("FERT_DELAY")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(delayIntervalLabel, gridBagConstraints);

        delayIntervalTextField.setMinimumSize(new java.awt.Dimension(40, 19));
        delayIntervalTextField.setPreferredSize(new java.awt.Dimension(40, 19));
        delayIntervalTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                delayIntervalTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(delayIntervalTextField, gridBagConstraints);

        waterCIntervalLabel.setText(bundle.getString("Water_change_interval")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 8, 5, 8);
        add(waterCIntervalLabel, gridBagConstraints);

        wcIntervalValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        wcIntervalValueLabel.setText("0");
        wcIntervalValueLabel.setToolTipText("");
        wcIntervalValueLabel.setMinimumSize(new java.awt.Dimension(26, 19));
        wcIntervalValueLabel.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 15);
        add(wcIntervalValueLabel, gridBagConstraints);

        recipesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "-- No selection --"
            }
        ));
        recipesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        recipesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recipesTableMouseClicked(evt);
            }
        });
        recipesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                recipesTableKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(recipesTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 613;
        gridBagConstraints.ipady = 277;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        add(jScrollPane4, gridBagConstraints);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        clearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(clearButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(saveButton);
        jToolBar4.add(jSeparator1);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        deleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteButtonMouseClicked(evt);
            }
        });
        jToolBar4.add(deleteButton);
        jToolBar4.add(jSeparator16);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 513;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jToolBar4, gridBagConstraints);

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
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 20);
        rightPanel.add(hintLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 15);
        add(rightPanel, gridBagConstraints);

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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 3);
        add(addItemButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void recipesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recipesTableMouseClicked
    /**Populate TextFields on tab3 */
    refreshFields();
}//GEN-LAST:event_recipesTableMouseClicked

private void clearButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearButtonMouseClicked
    /** Cleans all textFields on tab(Plans)*/
    CleanAllFields();
}//GEN-LAST:event_clearButtonMouseClicked

private void saveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseClicked
    /**Insert record on db for plans table or update it if existing*/
    
    if (Global.AqID == 0) {
        AppUtil.msgSelectAquarium();
        return;
    }
    if (methodComboBox.getSelectedIndex() <= 0) {
        Util.showErrorMsg(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "SELECT_A_METHOD_BEFORE"));
        return;
    }
    
    if (recipeComboBox.getSelectedIndex() <= 0) {
        Util.showErrorMsg(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "SELECT_A_RECIPE_BEFORE"));
        return;
    }
    
    int method = methodComboBox.getSelectedIndex();
    if (method == 1) {
        method = METHOD_TARGET; 
    }
    
    String fi =  fertIntervalTextField.getText();
    if (method == METHOD_TARGET) {
        if (fi.isEmpty()  || fi.equalsIgnoreCase("0")) {
            Util.showErrorMsg(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "FERT_INTERVAL_NOT_NULL"));
            return;
        }          
    }
    
    String fd =  delayIntervalTextField.getText();
    if (fd.isEmpty()) {
          fd = "0";
    }
    
    double fertInterval = Double.parseDouble(
            LocUtil.delocalizeDouble(fi));
    
    double fertDelay = Double.parseDouble(
            LocUtil.delocalizeDouble(fd));
    
    double wcInterval = Double.parseDouble(
            LocUtil.delocalizeDouble(wcIntervalValueLabel.getText()));
    
    if ((wcInterval > 0) && (wcInterval < fertInterval)) {
         Util.showErrorMsg(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "WC_INTERVAL_SHOULD_BE_GREATER"));
        return;
    }
    
    if ((wcInterval > 0) && ((wcInterval < fertDelay) || ((wcInterval-fertInterval) < fertDelay))) {
         Util.showErrorMsg(
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "FERT_DELAY_TO_BIG"));
        return;
    }
    
    String currID = idTextField.getText();
    Plans plan=new Plans();
    if (currID == null || currID.equals("")) {
        plan.setId(0);
    } else {
        plan.setId(Integer.valueOf(currID));
    }
    
    plan.setAqId(Global.AqID);
    plan.setFertInterval(LocUtil.delocalizeDouble(
            fertIntervalTextField.getText()));   
    
    plan.setFertDelay(LocUtil.delocalizeDouble(
            delayIntervalTextField.getText()));   
    
    
    if (recipeComboBox.getSelectedItem() !=null) {
         plan.setRecipe(recipeComboBox.getSelectedItem().toString());
    }
    //    methodComboBox.getSelectedIndex() > 0 already tested
            
    plan.setMethod(String.valueOf(method));
    
    plan.setName(String.valueOf(Global.AqID) + "_" + String.valueOf(method));
    
    plan.save(plan);        
    
    CleanAllFields();
    populateTable();
}//GEN-LAST:event_saveButtonMouseClicked

private void deleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteButtonMouseClicked
    Plans.deleteById(idTextField.getText());
    PopulateList();
    CleanAllFields();
    populateTable();
}//GEN-LAST:event_deleteButtonMouseClicked

    private void recipesTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recipesTableKeyReleased
        /**Populate TextFields on tab3 (maintenance) */
        refreshFields();
    }//GEN-LAST:event_recipesTableKeyReleased

    private void methodComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodComboBoxActionPerformed
        //Changing method load related recipes
        if (methodComboBox.getSelectedIndex() > 0) {
            setFertInterval();
            loadRecipes();
            setDelayInterval();
        }
    }//GEN-LAST:event_methodComboBoxActionPerformed

    private void fertIntervalTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fertIntervalTextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_fertIntervalTextFieldKeyTyped

    private void delayIntervalTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_delayIntervalTextFieldKeyTyped
        // allow only numbers a related chars
        Util.checkNumericKey(evt);
    }//GEN-LAST:event_delayIntervalTextFieldKeyTyped

    private void recipeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recipeComboBoxActionPerformed
        // refresh delay interval if needed:
        setDelayInterval();
    }//GEN-LAST:event_recipeComboBoxActionPerformed

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
        // Open Solutions panel   via internal bus
        Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.MOVE_FOCUS_TO_SOLUTIONS);
    }//GEN-LAST:event_addItemButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JLabel addedLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel delayIntervalLabel;
    private javax.swing.JTextField delayIntervalTextField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane elementsScrollPane;
    private javax.swing.JTable elementsTable;
    private javax.swing.JLabel fertIntervalLabel;
    private javax.swing.JTextField fertIntervalTextField;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JComboBox<String> methodComboBox;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JComboBox<String> recipeComboBox;
    private javax.swing.JLabel recipeLabel;
    private static javax.swing.JTable recipesTable;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel waterCIntervalLabel;
    private javax.swing.JLabel wcIntervalValueLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        fertIntervalTextField.addMouseListener(new ContextMenuMouseListener());
        delayIntervalTextField.addMouseListener(new ContextMenuMouseListener());
    }

    /*
    * Set fertilization interval according with following table (d=day of week):
    *
    *           KNO3	           KH2PO4	         micro		  WC
    *   ------------------------------------------------------------------------
    *    Ei     d0+d2+d4           d0+d2+d4             d1+d3+d6	  d7==d0
    *    EIW	d0	           d0                   d0		  d7==d0
    *    EID	d0+d1+d2+d3+d4+d5  d0+d1+d2+d3+d4+d5	d0+d1+d2+d3+d4+d5 d7==d0
    *    PPS	d0+d1+d2+d3+d4+d5  d0+d1+d2+d3+d4+d5	d0+d1+d2+d3+d4+d5 NO
    *    PMDD	d0+d1+d2+d3+d4+d5  d0+d1+d2+d3+d4+d5	d0+d1+d2+d3+d4+d5 d7==d0
    */
    private void setFertInterval() {
        
        int method = methodComboBox.getSelectedIndex();
        switch (method) {
            case METHOD_EI:
                fertIntervalTextField.setText("2");
                fertIntervalTextField.setEnabled(false);
                delayIntervalTextField.setText("0");
                delayIntervalTextField.setEnabled(false);
                break;
                
            case METHOD_EIW:
                fertIntervalTextField.setText("7");
                fertIntervalTextField.setEnabled(false);
                delayIntervalTextField.setText("0");
                delayIntervalTextField.setEnabled(false);  
                break;
                
            case METHOD_EID:
                fertIntervalTextField.setText("1");                
                fertIntervalTextField.setEnabled(false); 
                delayIntervalTextField.setText("0");
                delayIntervalTextField.setEnabled(false); 
                break;
                
            case METHOD_ADA:
                fertIntervalTextField.setText("1");                
                fertIntervalTextField.setEnabled(false);  
                delayIntervalTextField.setText("0");
                delayIntervalTextField.setEnabled(false);
                break;
                
            case METHOD_PMDD:
                if (fertIntervalTextField.getText().isEmpty()){
                    fertIntervalTextField.setText("1");
                }
                fertIntervalTextField.setEnabled(false);  
                 
                if (delayIntervalTextField.getText().isEmpty()) {
                    delayIntervalTextField.setText("0");
                }
                delayIntervalTextField.setEnabled(false);
                break;
                
            default:
                fertIntervalTextField.setEnabled(true);  
                delayIntervalTextField.setEnabled(true);
        }
        
    }
    
    private void setDelayInterval() {
        //elements are Ca/PO4/Fe/K/Mg/Mn/NO3
        int method = methodComboBox.getSelectedIndex();
        if (method == METHOD_EI) {
            int delay=0;
            int selectedRecipe = recipeComboBox.getSelectedIndex();
            if (selectedRecipe > 0) {
                String recipeName = (String) recipeComboBox.getSelectedItem();
                if (!recipeName.isEmpty()) {
                    Recipe recipe = Recipe.getByRecipeName(recipeName);
                    String compound=recipe.getProduct();
                    double [] values=(double[]) solute.get(compound);    
                    int targetElement=(int) values[Solutions.Target];
                    
                    if (targetElement >= 5 ) { //> 5 are micro elements
                        delay = 1;
                    }
                }
            }            
            delayIntervalTextField.setText(String.valueOf(delay));
        }
        else if (method != METHOD_TARGET) {
            delayIntervalTextField.setText("0");
        }
        
    }
}

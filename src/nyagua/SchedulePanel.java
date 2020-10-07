/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2014 Rudi Giacomini Pilon
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
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import nyagua.data.Aquarium;
import nyagua.data.ListDEntry;
import nyagua.data.Maintenance;
import nyagua.data.Schedule;
import nyagua.data.ScheduledDate;
import nyagua.data.Setting;

/**
 *
 * @author Rudi Giacomini Pilon
 */
public class SchedulePanel extends javax.swing.JPanel {
    //Connect listener to application bus
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getID()==Watched.CHANGED_UNITS_SETTINGS){
                populateDaysLists(eventCalendar.getDate());
                refreshUnits();                
            } else  if(e.getID()==Watched.AQUARIUM_CLICKED){
                populateAquariums();
            } else if(e.getID()==Watched.REQUEST_CLEAN_ALL_FIELDS){
                CleanAllFields();
            } else if(e.getID()==Watched.REQUEST_CLEAR_LIST){
                emptyCombo();
                PopulateList();
                refreshDaysList(null);
            } else if(e.getID()==Watched.REQUEST_POPULATE_LIST){
                PopulateList();
                refreshDaysList(null);
                setCurrentAquarium();
            } 
        }
    };            
    Watcher settingWatch=new Watcher(al);

    
     static Aquarium [] aquariumList;    
     static Date today=new Date();
       
    /**
     * Creates new form test
     */
    public SchedulePanel() {
        initComponents();                
        changeRecursionType();        
        initCutAndPaste(); 
        populateCombo();
        Watched nyMessages=Watched.getInstance();
        nyMessages.addObserver(settingWatch);
        statusHiddenLabel.setVisible(false); 
        schedDateChooser.setDate(today);
        populateDaysLists(today);
    }
    
    /**
     * Cleans all fields
     */
    private void CleanAllFields () {
        JTextField[] jtfList = {schedIdTextField,  schedNotesTextField};
        Util.CleanTextFields(jtfList);
        populateCombo();
        setCurrentAquarium();
        schedComboBox.setSelectedIndex(0);
        schedDateChooser.setDate(today);
        dailyRadioButton.setSelected(true);
        changeRecursionType();    
        statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clock_16x.png"))); // NOI18N
        statusHiddenLabel.setText("");// NOI18N
        yesterdayList.clearSelection();
        todayList.clearSelection();
        tomorrowList.clearSelection();
        nextdayList.clearSelection();
    }
      
     /*
     * Applies given format to date text fiel
     * 
     */
    private void refreshUnits(){
        schedDateChooser.setDateFormatString(Global.dateFormat);        
    }
    
    public void refreshDaysList(Date selectedDay){
        populateDaysLists(selectedDay);
    }
    
    /**
     * populate day lists from Schedule table
     * 
     * @param selectedDay 
     */
    private void populateDaysLists(Date selectedDay){
        if (selectedDay == null){
            selectedDay=today;
            eventCalendar.setDate(today);
            eventCalendar.updateUI();
        } 
        ScheduledDate schedDate=new ScheduledDate(selectedDay);
        Date[] fourDays= schedDate.getFourDays(); 
        JList[] dayLists={yesterdayList,todayList,tomorrowList,nextdayList};
        for (int i=0; i<dayLists.length;i++){
            int records=Schedule.populateList(dayLists[i],fourDays[i] ); 
            if (records>0 && !Application.isStarted() && i==1){ //There are events for today and application is starting
                //Ask to set scheduler as startup
                  setStartupForm();
            }
        }
    }
    
    /**
     * Ask and set startup form
     */
    private void setStartupForm(){
         Setting s=Setting.getInstance(); 
        boolean ask=s.getAskForDefaultForm();
        if (ask){
            ImageIcon imageLabel; // NOI18N
            imageLabel = new  javax.swing.ImageIcon(getClass().getResource("/icons/dbm3.png"));// NOI18N 
            Icon ic=imageLabel;
            String message=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("there_are_events")+"\n"// NOI18N 
                    +java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Do_you_want_to_go")+"\n"+"\n";//NOI18N                
            JCheckBox checkbox = new JCheckBox(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("dont_ask_again"));//NOI18N
            Object[] params = {message, checkbox};
            int reply;
                    reply = JOptionPane.showConfirmDialog(null, params, Application.NAME, JOptionPane.YES_NO_OPTION,1,ic);
            if (reply == JOptionPane.YES_OPTION) {                  
              Application.setDefaultForm(Application.DEFAULT_FORM_SCHEDULER);  
            }
            boolean dontShow = checkbox.isSelected(); 
            if (dontShow){  //save this preference
                s.setAskForDefaultForm(false);
                s.setStartupForm(Application.getDefaultForm());
            }
        }else{
            //get from setting
            Application.setDefaultForm(s.getStartupForm());
        } 
    }
    
    /**
     * populates the combo box
     */
    private  void populateCombo(){
        Schedule.populateCombo(schedComboBox);
    }
        
    /**
     * empties the combo box
     */
    private void emptyCombo(){
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        //empty list
        schedComboBox.setModel(dcm);
    }
    
    /**
     * Populate selected list with a field from a table
     *
     */
    private void PopulateList() {          
        DefaultComboBoxModel dcm =new DefaultComboBoxModel();
        Schedule.populateCombo(schedComboBox);  
    }
    
    
    
    /**
     * pupulate aquarium combo
     */
    private static void populateAquariums() {
        aquariumList=Aquarium.getAll();
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        schedAquariumComboBox.setModel(dcm); 
        if (aquariumList!=null){
            int totElements=aquariumList.length;
            for (int i=0; i<totElements; i++){
                dcm.addElement(aquariumList[i].getName());//NOI18N
            }
        }            
        dcm.insertElementAt("---", 0);//NOI18N
        dcm.setSelectedItem(dcm.getElementAt(0));
    }
    
    /**
     * Save all data
     */
    private void saveSchedule(){
        String currID = schedIdTextField.getText();
        Schedule schedData=new Schedule();
        if (currID == null || currID.equals("")) {
            schedData.setId(0);
            } else {
            schedData.setId(Integer.valueOf(currID));
        }
        if(schedComboBox.getItemCount()>0){
            schedData.setEvent(schedComboBox.getSelectedItem().toString());
        }else{
            schedData.setEvent("");
        }
        
        schedData.setNotes(schedNotesTextField.getText());
        if (LocUtil.isValidDate(schedDateChooser.getDate())){
            if (schedDateChooser.getDate() == null){
                schedData.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
            }else {
                schedData.setDate(LocUtil.delocalizeDate(schedDateChooser.getDate()));
            }
        } else {
            Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));// NOI18N 
            schedDateChooser.requestFocus();   
            return;
        }        
        schedData.setRecursionType(getRecursionType());
        schedData.setRecursion(getRecursion());
        schedData.setEndDate(null); //NOT implemented by now-> default to null
        if (statusHiddenLabel.getText().isEmpty() || statusHiddenLabel.getText().matches("")){
            schedData.setStatus(Schedule.STATUS_TODO);
        }else{
            int currStatus=Integer.parseInt(statusHiddenLabel.getText());
            if (currStatus<=Schedule.STATUS_CLOSED){
                schedData.setStatus(currStatus);
            }else{//undefined status
                schedData.setStatus(Schedule.STATUS_TODO);
            }
        }
        
        schedData.setSnoozed(0); //NOT implemented by now-> default to 0
        if (schedAquariumComboBox.getItemCount()>0){
            if (schedAquariumComboBox.getSelectedIndex()>0){
                schedData.setAqId(aquariumList[schedAquariumComboBox.getSelectedIndex()-1].getId());
            }else{
                schedData.setAqId(0);
            }            
        }else{
            schedData.setAqId(0);
        }
        schedData.save(schedData);
                
        CleanAllFields();
        populateDaysLists(eventCalendar.getDate());
    }
    
    /**
     * Save and close a single event from a recurrent serie
     */
    private void saveClone(){
        String currID = null;
        Schedule schedData=new Schedule();
        if (currID == null || currID.equals("")) {
            schedData.setId(0);
            } else {
            schedData.setId(Integer.valueOf(currID));
        }
        if(schedComboBox.getItemCount()>0){
            schedData.setEvent(schedComboBox.getSelectedItem().toString());
        }else{
            schedData.setEvent("");
        }
        
        schedData.setNotes(schedNotesTextField.getText());
        schedData.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate())); 
        schedData.setRecursionType(Schedule.RECURSION_SINGLE);
        schedData.setRecursion(0);
        schedData.setEndDate(LocUtil.delocalizeDate(today)); //NOT implemented by now
        if (statusHiddenLabel.getText().isEmpty() || statusHiddenLabel.getText().matches("")){
            schedData.setStatus(Schedule.STATUS_TODO);
        }else{
            int currStatus=Integer.parseInt(statusHiddenLabel.getText());
            if (currStatus<=Schedule.STATUS_CLOSED){
                schedData.setStatus(currStatus);
            }else{//undefined status
                schedData.setStatus(Schedule.STATUS_TODO);
            }
        }
        schedData.setSnoozed(0); //NOT implemented by now-> default to 0
        if (schedAquariumComboBox.getItemCount()>0){
            if (schedAquariumComboBox.getSelectedIndex()>0){
                schedData.setAqId(aquariumList[schedAquariumComboBox.getSelectedIndex()-1].getId());
            }else{
                schedData.setAqId(0);
            }            
        }else{
            schedData.setAqId(0);
        }
        schedData.save(schedData);
                
        CleanAllFields();
        populateDaysLists(eventCalendar.getDate());
    
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

        schedTypeButtonGroup = new javax.swing.ButtonGroup();
        eventCalendar = new com.toedter.calendar.JCalendar();
        jScrollPane1 = new javax.swing.JScrollPane();
        yesterdayList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        todayList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        tomorrowList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        nextdayList = new javax.swing.JList();
        editPanel = new javax.swing.JPanel();
        calPanel = new javax.swing.JPanel();
        dailyRadioButton = new javax.swing.JRadioButton();
        singleRadioButton = new javax.swing.JRadioButton();
        weeklyRadioButton = new javax.swing.JRadioButton();
        monthlyRadioButton = new javax.swing.JRadioButton();
        yearlyRadioButton = new javax.swing.JRadioButton();
        schedDateChooser = new com.toedter.calendar.JDateChooser();
        sunCheckBox = new javax.swing.JCheckBox();
        monCheckBox = new javax.swing.JCheckBox();
        tueCheckBox = new javax.swing.JCheckBox();
        wedCheckBox = new javax.swing.JCheckBox();
        thuCheckBox = new javax.swing.JCheckBox();
        satCheckBox = new javax.swing.JCheckBox();
        friCheckBox = new javax.swing.JCheckBox();
        daySpinField = new com.toedter.components.JSpinField();
        fieldsPanel = new javax.swing.JPanel();
        schedIdLabel = new javax.swing.JLabel();
        schedIdTextField = new javax.swing.JTextField();
        schedAquariumLabel = new javax.swing.JLabel();
        schedAquariumComboBox = new javax.swing.JComboBox();
        schedEventLabel = new javax.swing.JLabel();
        schedComboBox = new javax.swing.JComboBox();
        schedNotesLabel = new javax.swing.JLabel();
        schedNotesTextField = new javax.swing.JTextField();
        schedStatusLabel = new javax.swing.JLabel();
        statusIconLabel = new javax.swing.JLabel();
        statusHiddenLabel = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        schedClearButton = new javax.swing.JButton();
        schedSaveButton = new javax.swing.JButton();
        schedDeleteButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        actionsPanel = new javax.swing.JPanel();
        schedDoneTaskButton = new javax.swing.JButton();
        schedMarkTaskButton = new javax.swing.JButton();
        cancelTaskButton = new javax.swing.JButton();
        hideClosedTasksCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        eventCalendar.setName(""); // NOI18N
        eventCalendar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                eventCalendarPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(eventCalendar, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        yesterdayList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("yesterday"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        yesterdayList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        yesterdayList.setName("yesterday"); // NOI18N
        yesterdayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                yesterdayListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(yesterdayList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 83;
        gridBagConstraints.ipady = 178;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 18, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        todayList.setBackground(new java.awt.Color(253, 253, 218));
        todayList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("today"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        todayList.setName("today"); // NOI18N
        todayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                todayListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(todayList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 84;
        gridBagConstraints.ipady = 178;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(jScrollPane2, gridBagConstraints);

        tomorrowList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("tomorrow"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        tomorrowList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tomorrowList.setName("tomorrow"); // NOI18N
        tomorrowList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tomorrowListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tomorrowList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 84;
        gridBagConstraints.ipady = 178;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(jScrollPane3, gridBagConstraints);

        nextdayList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("next_day"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        nextdayList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        nextdayList.setName("nextday"); // NOI18N
        nextdayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextdayListMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(nextdayList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 84;
        gridBagConstraints.ipady = 178;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        add(jScrollPane4, gridBagConstraints);

        editPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        editPanel.setLayout(new java.awt.GridBagLayout());

        calPanel.setLayout(new java.awt.GridBagLayout());

        schedTypeButtonGroup.add(dailyRadioButton);
        dailyRadioButton.setSelected(true);
        dailyRadioButton.setText(bundle.getString("daily")); // NOI18N
        dailyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dailyRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(dailyRadioButton, gridBagConstraints);

        schedTypeButtonGroup.add(singleRadioButton);
        singleRadioButton.setText(bundle.getString("single_event")); // NOI18N
        singleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(singleRadioButton, gridBagConstraints);

        schedTypeButtonGroup.add(weeklyRadioButton);
        weeklyRadioButton.setText(bundle.getString("weekly")); // NOI18N
        weeklyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklyRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(weeklyRadioButton, gridBagConstraints);

        schedTypeButtonGroup.add(monthlyRadioButton);
        monthlyRadioButton.setText(bundle.getString("monthly")); // NOI18N
        monthlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthlyRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(monthlyRadioButton, gridBagConstraints);

        schedTypeButtonGroup.add(yearlyRadioButton);
        yearlyRadioButton.setText(bundle.getString("yearly")); // NOI18N
        yearlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearlyRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(yearlyRadioButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        calPanel.add(schedDateChooser, gridBagConstraints);

        sunCheckBox.setText(bundle.getString("su")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        calPanel.add(sunCheckBox, gridBagConstraints);

        monCheckBox.setText(bundle.getString("mo")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        calPanel.add(monCheckBox, gridBagConstraints);

        tueCheckBox.setText(bundle.getString("tu")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        calPanel.add(tueCheckBox, gridBagConstraints);

        wedCheckBox.setText(bundle.getString("we")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        calPanel.add(wedCheckBox, gridBagConstraints);

        thuCheckBox.setText(bundle.getString("th")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        calPanel.add(thuCheckBox, gridBagConstraints);

        satCheckBox.setText(bundle.getString("sa")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        calPanel.add(satCheckBox, gridBagConstraints);

        friCheckBox.setText(bundle.getString("fr")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        calPanel.add(friCheckBox, gridBagConstraints);

        daySpinField.setMaximum(31);
        daySpinField.setMinimum(1);
        daySpinField.setValue(1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        calPanel.add(daySpinField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 12, 5);
        editPanel.add(calPanel, gridBagConstraints);

        fieldsPanel.setLayout(new java.awt.GridBagLayout());

        schedIdLabel.setText(bundle.getString("ID_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedIdLabel, gridBagConstraints);

        schedIdTextField.setEditable(false);
        schedIdTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        schedIdTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedIdTextField, gridBagConstraints);

        schedAquariumLabel.setText(bundle.getString("AQUARIUM")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedAquariumLabel, gridBagConstraints);

        schedAquariumComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 90;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedAquariumComboBox, gridBagConstraints);

        schedEventLabel.setText(bundle.getString("Ny.maintEventLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedEventLabel, gridBagConstraints);

        schedComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 90;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedComboBox, gridBagConstraints);

        schedNotesLabel.setText(bundle.getString("Notes_")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedNotesLabel, gridBagConstraints);

        schedNotesTextField.setMinimumSize(new java.awt.Dimension(30, 19));
        schedNotesTextField.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 90;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedNotesTextField, gridBagConstraints);

        schedStatusLabel.setText(bundle.getString("status")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(schedStatusLabel, gridBagConstraints);

        statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clock_16x.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        fieldsPanel.add(statusIconLabel, gridBagConstraints);

        statusHiddenLabel.setBackground(new java.awt.Color(255, 255, 0));
        statusHiddenLabel.setEnabled(false);
        statusHiddenLabel.setMaximumSize(new java.awt.Dimension(20, 20));
        statusHiddenLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        statusHiddenLabel.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        fieldsPanel.add(statusHiddenLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(19, 0, 19, 0);
        editPanel.add(fieldsPanel, gridBagConstraints);

        buttonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), bundle.getString("Edit"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        schedClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_clear.png"))); // NOI18N
        schedClearButton.setToolTipText(bundle.getString("Clear_Fields")); // NOI18N
        schedClearButton.setFocusable(false);
        schedClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedClearButton.setMaximumSize(new java.awt.Dimension(44, 44));
        schedClearButton.setMinimumSize(new java.awt.Dimension(44, 44));
        schedClearButton.setPreferredSize(new java.awt.Dimension(44, 44));
        schedClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        schedClearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedClearButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        buttonsPanel.add(schedClearButton, gridBagConstraints);

        schedSaveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_accept.png"))); // NOI18N
        schedSaveButton.setToolTipText(bundle.getString("Confirm_record")); // NOI18N
        schedSaveButton.setFocusable(false);
        schedSaveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedSaveButton.setMaximumSize(new java.awt.Dimension(44, 44));
        schedSaveButton.setMinimumSize(new java.awt.Dimension(44, 44));
        schedSaveButton.setPreferredSize(new java.awt.Dimension(44, 44));
        schedSaveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        schedSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedSaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        buttonsPanel.add(schedSaveButton, gridBagConstraints);

        schedDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/btn_delete.png"))); // NOI18N
        schedDeleteButton.setToolTipText(bundle.getString("Delete_record")); // NOI18N
        schedDeleteButton.setFocusable(false);
        schedDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedDeleteButton.setMaximumSize(new java.awt.Dimension(44, 44));
        schedDeleteButton.setMinimumSize(new java.awt.Dimension(44, 44));
        schedDeleteButton.setPreferredSize(new java.awt.Dimension(44, 44));
        schedDeleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        schedDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedDeleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        buttonsPanel.add(schedDeleteButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        editPanel.add(buttonsPanel, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(5, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        editPanel.add(jSeparator1, gridBagConstraints);

        actionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("Actions"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        actionsPanel.setLayout(new java.awt.GridBagLayout());

        schedDoneTaskButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/donetask_32x.png"))); // NOI18N
        schedDoneTaskButton.setToolTipText(bundle.getString("Close_task")); // NOI18N
        schedDoneTaskButton.setFocusable(false);
        schedDoneTaskButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedDoneTaskButton.setMaximumSize(new java.awt.Dimension(44, 44));
        schedDoneTaskButton.setMinimumSize(new java.awt.Dimension(44, 44));
        schedDoneTaskButton.setPreferredSize(new java.awt.Dimension(44, 44));
        schedDoneTaskButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        schedDoneTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedDoneTaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        actionsPanel.add(schedDoneTaskButton, gridBagConstraints);

        schedMarkTaskButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mark_tasks.png"))); // NOI18N
        schedMarkTaskButton.setToolTipText(bundle.getString("Save_completed_task")); // NOI18N
        schedMarkTaskButton.setFocusable(false);
        schedMarkTaskButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        schedMarkTaskButton.setMaximumSize(new java.awt.Dimension(44, 44));
        schedMarkTaskButton.setMinimumSize(new java.awt.Dimension(44, 44));
        schedMarkTaskButton.setPreferredSize(new java.awt.Dimension(44, 44));
        schedMarkTaskButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        schedMarkTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedMarkTaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        actionsPanel.add(schedMarkTaskButton, gridBagConstraints);

        cancelTaskButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancel_task_32x.png"))); // NOI18N
        cancelTaskButton.setToolTipText(bundle.getString("Cancel_task")); // NOI18N
        cancelTaskButton.setFocusable(false);
        cancelTaskButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelTaskButton.setMaximumSize(new java.awt.Dimension(44, 44));
        cancelTaskButton.setMinimumSize(new java.awt.Dimension(44, 44));
        cancelTaskButton.setPreferredSize(new java.awt.Dimension(44, 44));
        cancelTaskButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelTaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        actionsPanel.add(cancelTaskButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        editPanel.add(actionsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 13, 12, 12);
        add(editPanel, gridBagConstraints);

        hideClosedTasksCheckBox.setSelected(true);
        hideClosedTasksCheckBox.setText(bundle.getString("hide_closed_tasks")); // NOI18N
        hideClosedTasksCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideClosedTasksCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(hideClosedTasksCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void schedSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedSaveButtonActionPerformed
        // Save current data
        saveSchedule();        
    }//GEN-LAST:event_schedSaveButtonActionPerformed

    private void schedClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedClearButtonActionPerformed
        // Cleans all textFields on tab
        CleanAllFields();
    }//GEN-LAST:event_schedClearButtonActionPerformed

    private void schedDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedDeleteButtonActionPerformed
        // Delete selected item
        Schedule.deleteById(schedIdTextField.getText());
        PopulateList();  
        CleanAllFields();
        populateDaysLists(eventCalendar.getDate());
    }//GEN-LAST:event_schedDeleteButtonActionPerformed

    private void dailyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dailyRadioButtonActionPerformed
        changeRecursionType();
    }//GEN-LAST:event_dailyRadioButtonActionPerformed

    private void weeklyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklyRadioButtonActionPerformed
        changeRecursionType();
    }//GEN-LAST:event_weeklyRadioButtonActionPerformed

    private void monthlyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthlyRadioButtonActionPerformed
        changeRecursionType();
    }//GEN-LAST:event_monthlyRadioButtonActionPerformed

    private void yearlyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearlyRadioButtonActionPerformed
        changeRecursionType();
    }//GEN-LAST:event_yearlyRadioButtonActionPerformed

    private void singleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleRadioButtonActionPerformed
        changeRecursionType();
    }//GEN-LAST:event_singleRadioButtonActionPerformed

    private void eventCalendarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_eventCalendarPropertyChange
        //  a better management of this event
        if (evt.getPropertyName().matches("calendar")){
            getCalendarSelectedDate();
            populateDaysLists(eventCalendar.getDate());
            populateListsCaptions();
        }
    }//GEN-LAST:event_eventCalendarPropertyChange

    private void yesterdayListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yesterdayListMouseClicked
        // element selection
        todayList.clearSelection();
        tomorrowList.clearSelection();
        nextdayList.clearSelection();
        retrieveEvent(yesterdayList);
    }//GEN-LAST:event_yesterdayListMouseClicked

    private void todayListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_todayListMouseClicked
        //  element selection
        yesterdayList.clearSelection();
        tomorrowList.clearSelection();
        nextdayList.clearSelection();
        retrieveEvent(todayList);
    }//GEN-LAST:event_todayListMouseClicked

    private void tomorrowListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tomorrowListMouseClicked
        // element selection
        yesterdayList.clearSelection();
        todayList.clearSelection();
        nextdayList.clearSelection();
        retrieveEvent(tomorrowList);
    }//GEN-LAST:event_tomorrowListMouseClicked

    private void nextdayListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextdayListMouseClicked
        //  element selection
        yesterdayList.clearSelection();
        todayList.clearSelection();
        tomorrowList.clearSelection();
        retrieveEvent(nextdayList);
    }//GEN-LAST:event_nextdayListMouseClicked

    private void schedDoneTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedDoneTaskButtonActionPerformed
        // Mark schedule as done
        String recID=schedIdTextField.getText();
        if (recID.isEmpty() || recID.matches("")){// NOI18N
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_ITEM_SELECTED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"), JOptionPane.OK_OPTION);
                    return;
        }
        statusHiddenLabel.setText(Integer.toString(Schedule.STATUS_CLOSED));
        statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok_16x.png"))); // NOI18N 
        //check if this is single event
        if (singleRadioButton.isSelected()){
            saveSchedule(); 
        }else{
            if (askForSingle() == JOptionPane.YES_OPTION) { //close single event (copy
                saveClone();
            }else { //close all events
                saveSchedule(); 
            }    
        }        
    }//GEN-LAST:event_schedDoneTaskButtonActionPerformed

    private void schedMarkTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedMarkTaskButtonActionPerformed
        // Mark schedule as done and copy to maintenace
        String recID=schedIdTextField.getText();
        if (recID.isEmpty() || recID.matches("")){// NOI18N
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_ITEM_SELECTED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"), JOptionPane.OK_OPTION);
                    return;
        }
        statusHiddenLabel.setText(Integer.toString(Schedule.STATUS_CLOSED));
        statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok_16x.png"))); // NOI18N
        //check if this is single event
        if (singleRadioButton.isSelected()){
            copyToMaintenance();
            saveSchedule();
        }else{        
            if (askForSingle() == JOptionPane.YES_OPTION) { //close single event (copy
                copyToMaintenance();
                saveClone();
            }else { //close all events
                copyToMaintenance();
                saveSchedule(); 
            }    
        }    
    }//GEN-LAST:event_schedMarkTaskButtonActionPerformed

    private void hideClosedTasksCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideClosedTasksCheckBoxActionPerformed
        // set or reset filter for closed tasks
        Schedule.setHideClosedTasksFilter(hideClosedTasksCheckBox.isSelected());
        if (Application.isStarted()){
            populateDaysLists(eventCalendar.getDate());
        }
    }//GEN-LAST:event_hideClosedTasksCheckBoxActionPerformed

    private void cancelTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelTaskButtonActionPerformed
        // Mark schedule as cancel
        String recID=schedIdTextField.getText();
        if (recID.isEmpty() || recID.matches("")){// NOI18N
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("NO_ITEM_SELECTED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFORMATION"), JOptionPane.OK_OPTION);
                    return;
        }
        statusHiddenLabel.setText(Integer.toString(Schedule.STATUS_CANCELED));
        statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok_16x.png"))); // NOI18N        
        //check if this is single event
        if (singleRadioButton.isSelected()){
            saveSchedule(); 
        }else{
            if (askForSingle() == JOptionPane.YES_OPTION) { //close single event (copy
                saveClone();
            }else { //close all events
                saveSchedule(); 
            }    
        }    
    }//GEN-LAST:event_cancelTaskButtonActionPerformed

    /**
     * Dialog box to ask if operation involves one or all recurring events
     * 
     * @return YES_OPTION for single event
     */
    private int askForSingle(){
        String message=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("recurring_event")+"\n"// NOI18N 
                     + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("do_you_want_to_close")+"\n";//NOI18N 
        Object[] options = {java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("single"),// NOI18N 
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("All")};// NOI18N 
        int reply = JOptionPane.showOptionDialog(null, message, Application.NAME,
                JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
        return reply;
    }
    
    /**
     * This is a workaround for a bug in jcalendar when last day of month is selected
     * Fool but it works :-)
     * 
     * @return 
     */
    private Date getCalendarSelectedDate(){
        Calendar cal=eventCalendar.getCalendar();
        int day=eventCalendar.getDayChooser().getDay();
        int month=eventCalendar.getMonthChooser().getMonth();
        int year=eventCalendar.getYearChooser().getYear();
        cal.set(year, month, day);
        //Date d=new Date(cal.getTimeInMillis());
        //return d;
        return null;
    }
    
    /**
     * Copy current item to maintenance table
     */
    private void copyToMaintenance(){
        int [] aquariumId;                
        if (Global.AqID == 0) {//no aquarium selected
            String message=java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("No_selected_aquarium")+"\n"// NOI18N 
                     + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("do_you_want_to_copy")+"\n";//NOI18N 
            //Answers
            Object[] options = {java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("yes"),// NOI18N 
            java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("CANCEL")};// NOI18N 
            int reply = JOptionPane.showOptionDialog(null, message, Application.NAME,
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            if (reply == JOptionPane.YES_OPTION) {                  
              aquariumList=Aquarium.getAll();        
                int totElements=aquariumList.length;
                aquariumId=new int [totElements];
                for (int i=0; i<totElements; i++){
                    aquariumId[i]=aquariumList[i].getId();
                }                        
            } else { //undo all
                return;
            }            
        }else{  //aquarium selected
            aquariumId=new int [1];
            aquariumId[0]=Global.AqID;
        }
        //do copy
        for (int i=0; i<aquariumId.length;i++){
            int currID=aquariumId[i];
            Maintenance event=new Maintenance();
            event.setId(0);

            if (LocUtil.isValidDate(schedDateChooser.getDate())){
                if (schedDateChooser.getDate() == null){
                    event.setDate(LocUtil.delocalizeDate(LocUtil.getCurrentlocalizedDate()));
                }else {
                    event.setDate(LocUtil.delocalizeDate(schedDateChooser.getDate()));
                }

            } else {
                Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INVALID_DATE."));// NOI18N 
                schedDateChooser.requestFocus();   
                return;
            }
            event.setNotes(schedNotesTextField.getText());
            event.setEvent(schedComboBox.getSelectedItem().toString());    
            event.save(event,currID); 
        }
        Watched nyMessages=Watched.getInstance();
        nyMessages.Update(Watched.ADDED_MAINTENANCE_EVENT);
        
    }
    
    /**
     * Retrieve single event by clicked id 
     * 
     * @param clickedList list where click occour
     */
    private void retrieveEvent(JList clickedList){
        ListDEntry element=(ListDEntry) clickedList.getSelectedValue();
        int retrievedId=element.getValue();
        Schedule schedData= Schedule.getById(retrievedId);
        setRecursionType(schedData.getRecursionType());
        changeRecursionType();
        schedIdTextField.setText(Integer.toString(retrievedId));
        schedNotesTextField.setText(schedData.getNotes());                
        schedDateChooser.setDate(schedData.getDate());
        setRecursion(schedData.getRecursion(), schedData.getRecursionType());
        
        Schedule.populateCombo(schedComboBox);
        schedComboBox.setSelectedItem(schedData.getEvent());
        statusHiddenLabel.setText(Integer.toString(schedData.getStatus()));
        //Refresh icon:
        //System.out.println(clickedList.getName());
        //System.out.println(eventCalendar.getDate());
        switch (schedData.getStatus()){            
            case Schedule.STATUS_CLOSED:
                statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok_16x.png"))); // NOI18N
                break;
            case Schedule.STATUS_LATE:
                statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hourglass_16x.png"))); // NOI18N
                break;  
            case Schedule.STATUS_CANCELED:
                statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete_16x.png"))); // NOI18N
                break;
            default:
                statusIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clock_16x.png"))); // NOI18N
                //TODO verify date to show late icon              
                break;     
        }
        populateAquariums();
        for (Aquarium aquariumList1 : aquariumList) {
            if (schedData.getAqId() == aquariumList1.getId()) {
                schedAquariumComboBox.setSelectedItem(aquariumList1.getName());
            }                
        }
                
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionsPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel calPanel;
    private javax.swing.JButton cancelTaskButton;
    private javax.swing.JRadioButton dailyRadioButton;
    private com.toedter.components.JSpinField daySpinField;
    private javax.swing.JPanel editPanel;
    private com.toedter.calendar.JCalendar eventCalendar;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JCheckBox friCheckBox;
    private javax.swing.JCheckBox hideClosedTasksCheckBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox monCheckBox;
    private javax.swing.JRadioButton monthlyRadioButton;
    private javax.swing.JList nextdayList;
    private javax.swing.JCheckBox satCheckBox;
    private static javax.swing.JComboBox schedAquariumComboBox;
    private javax.swing.JLabel schedAquariumLabel;
    private javax.swing.JButton schedClearButton;
    private javax.swing.JComboBox schedComboBox;
    private com.toedter.calendar.JDateChooser schedDateChooser;
    private javax.swing.JButton schedDeleteButton;
    private javax.swing.JButton schedDoneTaskButton;
    private javax.swing.JLabel schedEventLabel;
    private javax.swing.JLabel schedIdLabel;
    private javax.swing.JTextField schedIdTextField;
    private javax.swing.JButton schedMarkTaskButton;
    private javax.swing.JLabel schedNotesLabel;
    private javax.swing.JTextField schedNotesTextField;
    private javax.swing.JButton schedSaveButton;
    private javax.swing.JLabel schedStatusLabel;
    private javax.swing.ButtonGroup schedTypeButtonGroup;
    private javax.swing.JRadioButton singleRadioButton;
    private javax.swing.JLabel statusHiddenLabel;
    private javax.swing.JLabel statusIconLabel;
    private javax.swing.JCheckBox sunCheckBox;
    private javax.swing.JCheckBox thuCheckBox;
    private javax.swing.JList todayList;
    private javax.swing.JList tomorrowList;
    private javax.swing.JCheckBox tueCheckBox;
    private javax.swing.JCheckBox wedCheckBox;
    private javax.swing.JRadioButton weeklyRadioButton;
    private javax.swing.JRadioButton yearlyRadioButton;
    private javax.swing.JList yesterdayList;
    // End of variables declaration//GEN-END:variables

/**
     * bind cutandpaste popup menu to text fields
     */
    private void initCutAndPaste(){
        schedNotesTextField.addMouseListener(new ContextMenuMouseListener());
    }
    
    
        
    /**
     * Enables or disables date fields 
     * 
     * @param enabled true=enable | false disable
     */
    private void enableDateFields(Boolean enabled){
        if (!enabled){
           monCheckBox.setSelected(enabled);
           tueCheckBox.setSelected(enabled);
           wedCheckBox.setSelected(enabled);
           thuCheckBox.setSelected(enabled);
           friCheckBox.setSelected(enabled);
           satCheckBox.setSelected(enabled);
           sunCheckBox.setSelected(enabled);
        }
        monCheckBox.setEnabled(enabled);
        tueCheckBox.setEnabled(enabled);
        wedCheckBox.setEnabled(enabled);
        thuCheckBox.setEnabled(enabled);
        friCheckBox.setEnabled(enabled);
        satCheckBox.setEnabled(enabled);
        sunCheckBox.setEnabled(enabled);        
    }
    
    /**
     * changes active fields depending on Recursion Type
     */
    private void changeRecursionType(){
        String recursionType=getRecursionType();
        switch (recursionType){
            case Schedule.RECURSION_DAILY:
                enableDateFields(false);
                daySpinField.setEnabled(false);
                schedDateChooser.setEnabled(false);
                break;
            case Schedule.RECURSION_WEEKLY:
                enableDateFields(true);
                daySpinField.setEnabled(false);
                schedDateChooser.setEnabled(false);
                break;  
            case Schedule.RECURSION_MONTHLY:
                enableDateFields(false);
                daySpinField.setEnabled(true);
                schedDateChooser.setEnabled(false);
                break;    
            default:
                enableDateFields(false);
                daySpinField.setEnabled(false);
                schedDateChooser.setEnabled(true);
        }
    }
    
    /**
     * Get recursion type string on option selected
     * 
     * @return recursion type string [ S | D | W | M | Y ] 
     */
    private String getRecursionType(){
        String type=Schedule.RECURSION_SINGLE; //default on single event
        if (dailyRadioButton.isSelected()) {
            type=Schedule.RECURSION_DAILY;
        }
        if (weeklyRadioButton.isSelected()) {
            type=Schedule.RECURSION_WEEKLY;
        }
        if (monthlyRadioButton.isSelected()) {
            type=Schedule.RECURSION_MONTHLY;
        }
        if (yearlyRadioButton.isSelected()) {
            type=Schedule.RECURSION_YEARLY;
        } //else back to default
        return type;
    }
    
    /**
     * Set the active option depending on recursion Type
     * 
     * @param type  A string representing recursion type  [ S | D | W | M | Y ] 
     */
    private void setRecursionType(String type){
        if (type.isEmpty()){
            type=Schedule.RECURSION_SINGLE; //default on single event
        }
        switch (type){
            case Schedule.RECURSION_SINGLE:
                singleRadioButton.setSelected(true);
                break;
            case Schedule.RECURSION_DAILY:
                dailyRadioButton.setSelected(true);
                break;
            case Schedule.RECURSION_WEEKLY:
                weeklyRadioButton.setSelected(true);
                break;
            case Schedule.RECURSION_MONTHLY:
                monthlyRadioButton.setSelected(true);
                break;
            case Schedule.RECURSION_YEARLY:
                yearlyRadioButton.setSelected(true);
                break;
        }        
    }
    
    /**
     * 
     * set recursion value correct field
     *      
     * @param recursion
     * @param rectype
     */
    private void setRecursion (int recursion, String recType){           
        switch (recType){
            case Schedule.RECURSION_SINGLE:
                break;
                
            case Schedule.RECURSION_DAILY:
                break;
                
            case Schedule.RECURSION_WEEKLY:
                String recurringOn=Integer.toBinaryString(recursion);                
                if (recurringOn.length()<7){    
                    String filler;
                    filler = "0000000";// NOI18N 
                    recurringOn=filler.substring(0, 7-recurringOn.length())+recurringOn;
                }
                char[] numbers = recurringOn.toCharArray();
                for(int i=0; i<numbers.length;i++){
                    if(numbers[i]=='1') {// NOI18N 
                        switch (i){
                            case 0:
                                sunCheckBox.setSelected(true);
                                break;
                            case 1:
                                monCheckBox.setSelected(true);
                                break;
                            case 2:
                                tueCheckBox.setSelected(true);
                                break;
                            case 3:
                                wedCheckBox.setSelected(true);
                                break;
                            case 4:
                                thuCheckBox.setSelected(true);
                                break;
                            case 5:
                                friCheckBox.setSelected(true);
                                break;
                            case 6:
                                satCheckBox.setSelected(true);
                                break;
                        }
                    }
                }
                break;     
                
            case Schedule.RECURSION_MONTHLY:
                daySpinField.setValue(recursion);
                break;   
                
            case Schedule.RECURSION_YEARLY:                
                ScheduledDate selday;
                selday  = new ScheduledDate(today); 
                break;
        }
    }
    
    /**
     * get recursion value from correct field
     * 
     * @return recursion value 
     */
    private int getRecursion (){
        int recursion=0;
        String recType=getRecursionType();
        switch (recType){
            case Schedule.RECURSION_SINGLE:
                recursion=0;
                break;
                
            case Schedule.RECURSION_DAILY:
                recursion=0;
                break;
                
            case Schedule.RECURSION_WEEKLY:
                String weekdays;
                if (sunCheckBox.isSelected()){
                    weekdays="1";// NOI18N 
                }else{
                    weekdays="0";// NOI18N 
                }
                if (monCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                if (tueCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                if (wedCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                if (thuCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                if (friCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                if (satCheckBox.isSelected()){
                    weekdays=weekdays+"1";// NOI18N 
                }else{
                    weekdays=weekdays+"0";// NOI18N 
                }
                recursion=Util.binaryToInteger(weekdays);
                break;     
                
            case Schedule.RECURSION_MONTHLY:
                recursion=daySpinField.getValue();
                break;   
                
            case Schedule.RECURSION_YEARLY:
                ScheduledDate selday;
                selday = new ScheduledDate(schedDateChooser.getDate());                         
                recursion=selday.getYearDay();
                break;
        }
        
        return recursion;
    }
    
    /**
     * Set the current selected aquarium on related combo
     */
    public void setCurrentAquarium(){
        populateAquariums();
        if (Global.AqID == 0){
            schedAquariumComboBox.setSelectedIndex(0);
            return;
        }
        for (Aquarium aquariumList1 : aquariumList) {
            if (Global.AqID == aquariumList1.getId()) {
                schedAquariumComboBox.setSelectedItem(aquariumList1.getName());
            }                
        }
    }
    
    /**
     * Add captions to day lists
     */
    private void populateListsCaptions(){
        Date selectedDate=eventCalendar.getDate(); 
        ScheduledDate sd=new ScheduledDate(selectedDate);
        if (LocUtil.delocalizeDate(today).matches(LocUtil.delocalizeDate(selectedDate))){//today
            TitledBorder tb=(TitledBorder) yesterdayList.getBorder();
            tb.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("yesterday"));// NOI18N 
            tb=(TitledBorder)todayList.getBorder();
            tb.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("today"));// NOI18N 
            tb=(TitledBorder)tomorrowList.getBorder();
            tb.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("tomorrow"));// NOI18N 
            tb=(TitledBorder)nextdayList.getBorder();
            tb.setTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("next_day"));  // NOI18N         
        }else{
            Date[] fourDays=sd.getFourDays();
            TitledBorder tb=(TitledBorder) yesterdayList.getBorder(); 
            tb.setTitle(LocUtil.localizeDate(LocUtil.delocalizeDate(fourDays[0])));
            tb=(TitledBorder) todayList.getBorder();
            tb.setTitle(LocUtil.localizeDate(LocUtil.delocalizeDate(fourDays[1])));
            tb=(TitledBorder) tomorrowList.getBorder();
            tb.setTitle(LocUtil.localizeDate(LocUtil.delocalizeDate(fourDays[2])));
            tb=(TitledBorder) nextdayList.getBorder();
            tb.setTitle(LocUtil.localizeDate(LocUtil.delocalizeDate(fourDays[3])));
        }       
        
    }
    
}


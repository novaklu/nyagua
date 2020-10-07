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
package nyagua;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util_panels.Converter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Application wide but specific
 * utilities and service routines
 *
 * @author Rudi Giacomini Pilon
 * @version 1.0
 */
public class AppUtil {
    
    /**
     * Show a message to select an aquarium id before to
     * execute any operation were this id is required
     */
    public static void msgSelectAquarium(){
        JOptionPane.showMessageDialog (
                null,
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "SELECT_AN_AQUARIUM_BEFORE..."),
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString(
                        "INFORMATION"),JOptionPane.WARNING_MESSAGE);
    }    
    
     /**
     * Imports xml file saved from fishbase.org
     *
     * @param file  the selected file
     * @param jtfList   the right fields where to import data
     * @throws ParserConfigurationException
     */
    @Deprecated
    public static void importXML(File file,JTextComponent [] jtfList) 
            throws ParserConfigurationException {
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc=null;
        try {
            doc = db.parse(file);
        } catch (SAXException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        parseXMLtoFields(doc, jtfList);
    }
    
   @Deprecated
   /**
    * Import xml stream from fishbase.org
    * 
    * @param docToParse     the xml to parse 
    * @param JTextComponent[] array of fields to fill with informations 
    */
   
    public static void webImportXML (
            String docToParse, JTextComponent [] jtfList) 
            throws ParserConfigurationException {
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try {            
            InputStream is;
            is = new java.io.ByteArrayInputStream(docToParse.getBytes());
            org.w3c.dom.Document doc = db.parse(is);   
            is.close();
            parseXMLtoFields(doc, jtfList);
        } catch (SAXException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * Import fish data from fishbase.org
    * 
    * @param docToParse the xml to parse 
    * @param jtfList    array of fields to fill with informations 
    * @throws java.lang.Exception 
    */
    public static void webImportHTML (
        Document docToParse, JTextComponent [] jtfList) 
            throws Exception {
        
        Element scinameDiv = docToParse.getElementById("ss-sciname");//NOI18N
        Elements nameParts = scinameDiv.getElementsByTag("a");//NOI18N
        String sciName = "";
        for (Element namePart : nameParts) {
            sciName = sciName + " " + namePart.text();
        }
        jtfList[SCI_NAME].setText(sciName); //scientific name
        
        Elements commonNames = scinameDiv.getElementsByClass("sheader2");//NOI18N
        if (commonNames!=null && commonNames.first().hasText()) {
            jtfList[COMMON_NAME].setText(commonNames.first().text()); //common name
        }
        
        Element dataDiv = docToParse.getElementById("ss-main");//NOI18N
        
        Elements infoBlocks = dataDiv.getElementsByClass("smallSpace");//NOI18N
        int total = infoBlocks.size() - 1;
        int x = 0;
        
        Element classBlock = infoBlocks.first();
        if (classBlock != null) {
            x = infoBlocks.indexOf(classBlock);
            
            Elements classNames = classBlock.getElementsByClass("slabel1");//NOI18N
            String classText = "";
            for (Element className : classNames) {
                if (classNames.indexOf(className) < (classNames.size() - 1)) {
                    classText = classText + className.text() + " > ";
                }
                else {
                    classText = classText + className.text();
                }                 
            }
            jtfList[CLASS].setText(classText);  //class          
        }
        
        if (x < total) {
            x = x + 1;
            Element basicInfos = infoBlocks.get(x);
            if (basicInfos != null) {
                
                if (basicInfos.text() != null) {
                    String [] basic = basicInfos.text().split(";");
                    int basicTotal = basic.length - 1 ;
                    int y = 0;
                    String tmp = "";//NOI18N
                    while  ((y < basicTotal) &&  (!basic[y].trim().startsWith("pH range:"))) { //NOI18N
                        tmp = tmp + basic [y]  + ";" ;
                        y = y + 1;
                    }
                    if (tmp.length() > 0) {
                        jtfList[ENVIRONMENT].setText(tmp.substring(0, tmp.length()-1));    //environment
                    }                    
                    
                    //PH
                    if (basic[y].trim().startsWith("pH range:")) {//NOI18N
                        String element = basic [y].trim();
                        int i = element.indexOf(":");//NOI18N
                        element=element.substring(i+1, element.length());                        
                        int z=element.indexOf("-");//NOI18N
                        String PHmin=element.substring(1,z-1);
                        String PHmax=element.substring(z+2,i);
                        jtfList[PH_MIN].setText(PHmin);
                        jtfList[PH_MAX].setText(PHmax);
                    }
                    if (y < basicTotal) {
                        y = y + 1;
                    }
                    
                    //DH / Climate
                    if (basic[y].trim().startsWith("dH range:")) {
                       String element = basic [y].trim();     
                       int i=element.indexOf("dH range:");//NOI18N
                       element=element.substring(i+1, element.length());
                       i=element.indexOf(":");//NOI18N
                       element=element.substring(i+1, element.length());
                       int z=element.indexOf("-");//NOI18N
                       if (z>2){                        
                           String dHmin=element.substring(1,z-1);                        
                           jtfList[DH_MIN].setText(dHmin);
                       }
                       int k = element.length();
                       if (k >= (z+2)) {
                           if (element.contains(".")) {//NOI18N
                               if (element.lastIndexOf(".") > z + 2) {
                                   k = element.lastIndexOf(".");//NOI18N
                                   if (element.lastIndexOf(" ") > k + 1) {//NOI18N
                                       k = element.lastIndexOf(" ");//NOI18N
                                   }
                                   
                               }
                           }
                           String dHmax=element.substring(z+2,k).trim();
                           jtfList[DH_MAX].setText(dHmax);
                       }
                       
                       if (k < element.length()) {
                           String climate = element.substring(
                                   k + 1, element.length());
                           climate = climate.trim();
                           jtfList[CLIMATE].setText(climate); //climate   
                       }
                    }
                    
                    //Temperature
                    if (y < basicTotal) {
                        y = y + 1;
                    }
                    if (basic[y].trim().contains("°C")) {//NOI18N
                        String element = basic [y].trim(); 
                        int z=element.indexOf("-");//NOI18N
                        if (z>2){                        
                           String tMin=element.substring(0,z-1);     
                           int k = tMin.lastIndexOf("°C");//NOI18N
                           tMin = tMin.substring(0,k);
                           jtfList[T_MIN].setText(tMin);
                       }
                       if (element.length() >= (z+2)) {
                           int k = element.lastIndexOf("°C");//NOI18N
                           if (k >= (z+2)) {
                                String tMax=element.substring(z+2,k);                           
                                jtfList[T_MAX].setText(tMax);
                           }                           
                       }
                    }
                }
            }
        }
        
        //Distribution
        if (x < total) {
            x = x + 1;
            Element infos = infoBlocks.get(x);
            if (infos != null) {                
                if (infos.text() != null) {
                    jtfList[DISTRIBUTION].setText(infos.text()); //Distribution
                }
            }
        }
        
        //Lenght
        if (x < total) {
            x = x + 1;
            Element infos = infoBlocks.get(x);
            if (infos != null) {   
                String element = infos.text().trim();
                if (element != null) {
                    int z = element.indexOf("Max length :");//NOI18N
                    if (z > 0) {
                        int k = element.length();
                        if (element.contains(";")) {//NOI18N
                            k = element.lastIndexOf(";");//NOI18N
                        }
                        if (k > z) {
                            jtfList[MAX_SIZE].setText(element.substring(z + 12, k)); //Lenght 
                        }
                    }
                }
            }
        }
        
        //Biology
        if (x < total) {
            x = x + 1;
            Element infos = infoBlocks.get(x);
            if (infos != null) {                
                if (infos.text() != null) {
                    jtfList[BIOLOGY].setText(infos.text()); //Biology
                }
            }
        }
        
        //Danger
        if (x < (total - 6)) {
            x = x + 6;
            Element infos = infoBlocks.get(x);
            if (infos != null) {                
                if (infos.text() != null) {
                    jtfList[DANGEROUS].setText(infos.text()); //danger
                }
            }
         }
        
            
    }   
    
    
    @Deprecated
    private static void parseXMLtoFields (
            org.w3c.dom.Document doc, JTextComponent [] jtfList) {
        
        doc.getDocumentElement().normalize();
        NodeList nodeLst = doc.getElementsByTagName("taxon");//NOI18N
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node fstNode = nodeLst.item(s);
            
            jtfList[CLASS].setText(AppUtil.getaNodeValue(fstNode, "dwc:Family"));//NOI18N
            jtfList[SCI_NAME].setText(AppUtil.getaNodeValue(fstNode, "dwc:ScientificName"));//NOI18N
                    //Util.getaNodeValue(fstNode, "dwc:Class"));//NOI18N
            NodeList innernodes = doc.getElementsByTagName("dataObject");// NOI18N
            for (int t = 0; t < innernodes.getLength(); t++) {
                Node nextNode = innernodes.item(t);
                String nv=AppUtil.getaNodeValue(nextNode, "dc:identifier");// NOI18N
                if (nv.startsWith("FB-Distribution-")){//NOI18N
                    jtfList[DISTRIBUTION].setText(AppUtil.getaNodeValue(nextNode, "dc:description"));//NOI18N
                } else if (nv.startsWith("FB-Uses-")){// NOI18N
                    jtfList[DANGEROUS].setText(AppUtil.getaNodeValue(nextNode, "dc:description"));//NOI18N
                } else if (nv.startsWith("FB-TrophicStrategy-")){// NOI18N
                    jtfList[BIOLOGY].setText(AppUtil.getaNodeValue(nextNode, "dc:description"));//NOI18N
                } else if (nv.startsWith("FB-Size-")){// NOI18N
                    jtfList[MAX_SIZE].setText(AppUtil.getaNodeValue(nextNode, "dc:description"));//NOI18N
                } else if (nv.startsWith("FB-Habitat-")){// NOI18N
                    //enviroment need to be parsed
                    String element=AppUtil.getaNodeValue(nextNode, "dc:description");//NOI18N
                    int i=element.indexOf(";");//NOI18N
                    jtfList[ENVIRONMENT].setText(element.substring(0, i));
                    //PH
                    element=element.substring(i+1, element.length());
                    i=element.indexOf("pH range:");//NOI18N
                    element=element.substring(i+1, element.length());
                    i=element.indexOf(":");//NOI18N
                    element=element.substring(i+1, element.length());
                    i=element.indexOf(";");//NOI18N
                    int y=element.indexOf("-");//NOI18N
                    String PHmin=element.substring(1,y-1);
                    String PHmax=element.substring(y+2,i);
                    jtfList[PH_MIN].setText(PHmin);
                    jtfList[PH_MAX].setText(PHmax);

                    //DH
                    i=element.indexOf("dH range:");//NOI18N
                    element=element.substring(i+1, element.length());
                    i=element.indexOf(":");//NOI18N
                    element=element.substring(i+1, element.length());
                    y=element.indexOf("-");//NOI18N
                    if (y>2){                        
                        String dHmin=element.substring(1,y-1);                        
                        jtfList[DH_MIN].setText(dHmin);
                        
                    }
                    if (element.length() >= (y+2)) {
                        
                        String dHmax=element.substring(y+2,element.length());
                        jtfList[DH_MAX].setText(dHmax);
                    }
                }

            }
            
        }
        org.w3c.dom.Element rootElement = doc.getDocumentElement();
            NodeList rowElements = rootElement.getElementsByTagName("commonName");// NOI18N
            for(int i=0; i<rowElements.getLength(); i++) {
          //Lettura dell'elemento
          org.w3c.dom.Element row = (org.w3c.dom.Element)rowElements.item(i);
          //Lettura degli attributi
          System.out.println(i);
          System.out.println(row.getNodeName());
          System.out.println(row.getAttribute("xml:lang"));// NOI18N
          if (row.getAttribute("xml:lang").trim().startsWith("eng")){// NOI18N
              jtfList[COMMON_NAME].setText(row.getTextContent());//NOI18N
              System.out.println(row.getTextContent());
          }
        }
    }

    /**
     * Retrivies a given XML node value
     * 
     * @param fstNode   the node 
     * @param item      the key
     * @return  the value for given key
     */
    public static String getaNodeValue( Node fstNode, String item ){
        String element="";
        if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
          org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
          NodeList fstNmElmntLst = fstElmnt.getElementsByTagName(item);
          org.w3c.dom.Element fstNmElmnt = (org.w3c.dom.Element) fstNmElmntLst.item(0);
          NodeList fstNm = fstNmElmnt.getChildNodes();
          if (fstNm.getLength()>0){
             element=((Node) fstNm.item(0)).getNodeValue(); 
          }          
        }
        return element;
    }
    
    /**
     * @return  nh3 alerts:
     * 
     * * NH3 level             PPM       PPM
     * safe                 0.000     0.020
     * alert                0.020     0.050
     * alarm                0.050     0.200
     * toxic                0.200     0.500
     * deadly               0.500+   
     */
    public static List<RangeValue> getNH3Ranges() {
        List<RangeValue> nh3Range = new ArrayList<>();
        
        nh3Range.add(new RangeValue(0, 0.020, Color.GREEN, ""));
        nh3Range.add(new RangeValue(0.020,0.050 , Color.YELLOW, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Alert")));
        nh3Range.add(new RangeValue(0.050, 0.200, Color.ORANGE, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Alarm")));       
        nh3Range.add(new RangeValue(0.200, 0.500, Color.RED, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Toxic")));       
        nh3Range.add(new RangeValue(0.500, 99999, Color.MAGENTA, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Deadly")));       
        
        return nh3Range;
    }
    
    /**
     * Calculates one of the values from the 
     * two others with the needed formula
     *
     * @param tfPH  the PH value text field
     * @param tfTemperature  the temperature value text field
     * @param tfTotalNH  the total NH value text field
     * @param tfNH3Result  field to write result     
     * 
     */
    public static void calcNh3( JTextField tfPH, JTextField tfTemperature, 
            JTextField tfTotalNH, JTextField tfNH3Result) {
        
        if (tfPH.getText().isEmpty()  || tfTemperature.getText().isEmpty()  || 
                tfTotalNH.getText().isEmpty()) {            
             JOptionPane.showMessageDialog (null, 
                     java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("PH_TEMP_NH_VALUES_NEEDED"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),JOptionPane.ERROR_MESSAGE );
            return;
        }
        double PH=Double.valueOf(LocUtil.delocalizeDouble(tfPH.getText()));
        double tempr=Double.valueOf(LocUtil.delocalizeDouble(tfTemperature.getText()));
        double NHtot=Double.valueOf(LocUtil.delocalizeDouble(tfTotalNH.getText()));        
        if (Global.temperatureunit.equalsIgnoreCase("F")) {
            tempr=(tempr*9)/5; //input in °F -> °C
        }
        double NH3 = NHtot/(1+Math.pow(10,(0.0902-PH+(2730/(273.2+tempr)))));
        
        //return result
        tfNH3Result.setText(LocUtil.localizeDouble(NH3)); 
        
    }
    
    public static void evaluateTextField(
            List<RangeValue> rangeValues, JTextField evalField, JLabel alertLabel) {
        
        if (evalField.getText().isEmpty() || evalField.getText().equalsIgnoreCase("")) {
            evalField.setBackground(Color.WHITE);
            if (alertLabel!=null) {
                alertLabel.setText("");                    
            }
            return;
        }
        double value = Double.valueOf(LocUtil.delocalizeDouble(evalField.getText()));        
        if (rangeValues!=null) {            
            for (RangeValue range : rangeValues) {
                if (value >= range.getMin() && value <  range.getMax()){
                    evalField.setBackground(range.getColor());
                    if (alertLabel!=null) {
                        alertLabel.setText(range.getText());                    
                    }
                    break;
                }            
            }
        }
    }
    
    /**
     * 
     * @return co2 alerts
     * 
     *   level   
     *   0  ->  12 mg/l	red [very low for plants - adjust]
     *   13 ->  20 mg/l	yellow [low for plants - adjust]
     *   21 ->  30 mg/l	green [optimum for plants & fish]
     *   31 ->  40 mg/l	yellow [high for fish - adjust]
     *   > 40 mg/l  red [toxic for fish - adjust immediately]
     * 
     */
    public static List<RangeValue>  getCO2Ranges() {
         List<RangeValue> co2Range = new ArrayList<>();
        
        co2Range.add(new RangeValue(0, 12, Color.pink, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("very_low_adjust")));
        co2Range.add(new RangeValue(12, 20, Color.yellow, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("low_adjust")));
        co2Range.add(new RangeValue(20, 30, Color.green, ""));
        co2Range.add(new RangeValue(30, 40, Color.yellow.brighter(), 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("alert_high")));
        co2Range.add(new RangeValue(40, 9999, Color.red, 
                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("Toxic")));
        
        return co2Range;
    }

    /**
     * Calculates one of the values from the 
     * two others with the needed formula
     * 
     * @param tfKH  the KH value text field
     * @param tfPH  the PH value text field
     * @param tfCO  the CO2 value text field
     */
    public static void calcCO2(JTextField tfKH, JTextField tfPH, JTextField tfCO){
        /*
         *    Formula 1: CO2 = 3 * KH * 10(7-pH) (KH in degrees)
         *    Formula 2: KH(degrees) = 0.056 * KH(ppm)
         *    Fromula 3: KH = CO2 / 3 / 10(7-PH)
         *    Fromula 4: pH=7,5+Log(KH)-Log(ppmCO2)
         */
         int numpar=3;
         if (tfKH.getText().isEmpty()){
             numpar-- ;
         }
         if (tfPH.getText().isEmpty()){
             numpar-- ;
         }
         if (tfCO.getText().isEmpty()){
             numpar-- ;
         }
         if (numpar!=2){
             JOptionPane.showMessageDialog (null,numpar+ " "
                     + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("_VALUES_IN_INPUT!__2_VALUES_NEEDED_FOR_CALCULATION"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),JOptionPane.ERROR_MESSAGE );
             return;
         }
         if (tfKH.getText().isEmpty()){ //need to calculate KH
             double PH=Double.valueOf(LocUtil.delocalizeDouble(tfPH.getText()));
             double CO=Double.valueOf(LocUtil.delocalizeDouble(tfCO.getText()));
             double KH;
             if (PH<=0 || CO<=0){
                 JOptionPane.showMessageDialog (null,
                         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("_PH_AND_CO2_MUST_BE_POSITIVE_VALUES"),
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),JOptionPane.ERROR_MESSAGE );
                return;
             }
             KH=CO/3;
             KH=KH/Math.pow(10, 7-PH);
             if (Global.khunit.matches("ppm")){//NOI18N
                 KH=KH/0.056;
             }
            tfKH.setText(LocUtil.localizeDouble(KH));
         }
         if (tfPH.getText().isEmpty()){ //need to calculate PH
             double PH;
             double CO=Double.valueOf(LocUtil.delocalizeDouble(tfCO.getText()));
             double KH=Double.valueOf(LocUtil.delocalizeDouble(tfKH.getText()));
             if (KH<=0 || CO<=0){
                 JOptionPane.showMessageDialog (null,
                         " KH and CO2 must be positive values",
                    "Error",JOptionPane.ERROR_MESSAGE );
                return;
             }
             if (Global.khunit.matches("ppm")){//NOI18N
                 KH=KH*0.056;
             }
             PH=7.5 + Math.log10(KH)-Math.log10(CO);
             tfPH.setText(LocUtil.localizeDouble(PH));
         }
         if (tfCO.getText().isEmpty()){ //need to calculate CO2
             double PH=Double.valueOf(LocUtil.delocalizeDouble(tfPH.getText()));
             double CO;
             double KH=Double.valueOf(LocUtil.delocalizeDouble(tfKH.getText()));
             if (KH<=0 || PH<=0){
                 JOptionPane.showMessageDialog (null,
                         " KH and PH must be positive values",
                    "Error",JOptionPane.ERROR_MESSAGE );
                return;
             }
             if (Global.khunit.matches("ppm")){//NOI18N
                 KH=KH*0.056;
             }
             CO=3 * KH * Math.pow(10, 7-PH);
             tfCO.setText(LocUtil.localizeDouble(CO));
        }
    }
    
    public static String calcAquariumVolume (String w, String l, String h){
        if (w.isEmpty()||w.matches("")){//NOI18N
            return "";//NOI18N
        }
        if (l.isEmpty()||l.matches("")){//NOI18N
            return "";//NOI18N
        }
        if (h.isEmpty()||h.matches("")){//NOI18N
            return "";//NOI18N
        }
        double width=Double.valueOf(LocUtil.delocalizeDouble(w));
        double len=Double.valueOf(LocUtil.delocalizeDouble(l));
        double height=Double.valueOf(LocUtil.delocalizeDouble(h));
        double vol;
        if (Global.lenghtunit.matches("cm")){
            vol=width*len*height; //cm3
            vol=vol/1000;//liters
        }else{//inch
            width=Converter.inch2cm(width);
            len=Converter.inch2cm(len);
            height=Converter.inch2cm(height);
            vol=width*len*height; //cm3
            vol=vol/1000;//liters
        }
        if (Global.volunit.matches("usGal")){
            vol=Converter.l2gal(vol);
        }        
        return LocUtil.localizeDouble(vol);
    }
    
    public static String calcAquariumVolume (
            String w, String l, String h, String gt){
        
        if (w.isEmpty()||w.matches("")){//NOI18N
            return "";//NOI18N
        }
        if (l.isEmpty()||l.matches("")){//NOI18N
            return "";//NOI18N
        }
        if (h.isEmpty()||h.matches("")){//NOI18N
            return "";//NOI18N
        }
         if (gt.isEmpty()||gt.matches("")){//NOI18N
            return "";//NOI18N
        }
        double width = Double.valueOf(LocUtil.delocalizeDouble(w));
        double len = Double.valueOf(LocUtil.delocalizeDouble(l));
        double height = Double.valueOf(LocUtil.delocalizeDouble(h));
        double glassThick = Double.valueOf(LocUtil.delocalizeDouble(gt));
        double vol;
        if (Global.lenghtunit.matches("cm")){
            vol=(width-glassThick)*(len-glassThick)*(height-glassThick); //cm3
            vol=vol/1000;//liters
        }else{//inch
            width=Converter.inch2cm(width);
            len=Converter.inch2cm(len);
            height=Converter.inch2cm(height);
            vol=width*len*height; //cm3
            vol=vol/1000;//liters
        }
        if (Global.volunit.matches("usGal")){
            vol=Converter.l2gal(vol);
        }        
        return LocUtil.localizeDouble(vol);
    }
    
    public static String calcWaterVolFromVol (String vol) {
        if (vol.isEmpty()||vol.matches("")){//NOI18N
            return "";//NOI18N
        }
        double volume = Double.valueOf(LocUtil.delocalizeDouble(vol));
        volume=0.85*volume; //estimated water volume is 85% of global volume
        return LocUtil.localizeDouble(volume);
    }
    
    /** Sets cursor for specified component to Wait cursor
     * @param component */
    public static void startWaitCursor(JComponent component) {
      RootPaneContainer root = 
        (RootPaneContainer)component.getTopLevelAncestor();
      root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      root.getGlassPane().setVisible(true);
    }

    /** Sets cursor for specified component to normal cursor
     * @param component */
    public static void stopWaitCursor(JComponent component) {
      RootPaneContainer root = 
        (RootPaneContainer)component.getTopLevelAncestor();
      root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      root.getGlassPane().setVisible(false);
    }
    
    private static final int COMMON_NAME = 1;
    private static final int CLASS = 2;
    private static final int SCI_NAME = 3;
    private static final int DISTRIBUTION = 4;
//    private static final int DIAGNOSIS = 5;
    private static final int BIOLOGY = 6;
    private static final int ENVIRONMENT = 7;
    private static final int MAX_SIZE = 8;
    private static final int CLIMATE = 9;
    private static final int DANGEROUS = 10;
    private static final int PH_MIN = 11;
    private static final int PH_MAX = 12;
    private static final int DH_MIN = 13;
    private static final int DH_MAX = 14;
    private static final int T_MIN = 15;
    private static final int T_MAX = 16;
//    private static final int SWIM_LEVEL = 17;
//    private static final int LIFE_SPAN = 18;
    
    private static final Logger _log=Logger.getLogger(AppUtil.class.getName());
      
}

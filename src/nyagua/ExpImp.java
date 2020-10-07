/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2016 Rudi Giacomini Pilon
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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import nyagua.data.FishBase;
import nyagua.data.InvBase;
import nyagua.data.PlantBase;

/**
 * Import Export service routines
 *
 * @author Rudi Giacomini Pilon
 * @version 1.0
 */
public class ExpImp {
    
    private final ResourceBundle res = ResourceBundle.getBundle("nyagua/Bundle");
    public final static  String BLANK = ""; //NOI18N
    private final static String EXPORT_ZIP = "export.zip"; //NOI18N
    public final static String FISH_TABLE = "FishBase";    //NOI18N
    public final static String INV_TABLE = "InvBase";      //NOI18N
    public final static String PLANTS_TABLE = "PlantsBase";  //NOI18N
    public final static  String VERSION_FILE="version.txt"; //NOI18N
    
    public static void Export () {
        BaseExport be=new BaseExport(null, true);        
        be.setVisible(true);      
        //if Cancel selected
        int result=be.getReturnStatus();
        if (result == DBSelector.RET_CANCEL) {
            return;
        }
        
        try {
            String exportFileName = be.getFullFileName();
            
            if (exportFileName == null || exportFileName.equalsIgnoreCase("")) {
                exportFileName=Global.WorkDir + Application.FS + EXPORT_ZIP; 
            }

            File f = new File(exportFileName);

            try (ZipOutputStream out = 
                    new ZipOutputStream(new FileOutputStream(f))) {
                
                if (be.isFishSelected()){
                    String tablename = FISH_TABLE;
                    ExpImp.saveToExport(out, tablename);
                }
                
                if (be.isInvertebratesSelected()){
                    String tablename = INV_TABLE;
                    ExpImp.saveToExport(out, tablename);
                }
                
                if (be.isPlantsSelected()){
                    String tablename = PLANTS_TABLE;
                    ExpImp.saveToExport(out, tablename);
                }
                
                saveVersion(out, DB.getVersion());
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(ExpImp.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

        } 
        catch (IOException ex) {
            Logger.getLogger(ExpImp.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        
    }
    
    public static void Import () {
        BaseImport bi=new BaseImport(null, true);        
        bi.setVisible(true);
        
        //if Cancel selected
        int result=bi.getReturnStatus();
        if (result == DBSelector.RET_CANCEL) {
            return;
        }
        
        String importFileName = bi.getFullFileName();
        String tableName = bi.getSelectedTable();
        
        importTable(importFileName, tableName);
            
    }
       
     public static void saveVersion (ZipOutputStream out, String Version) 
            throws FileNotFoundException, IOException {
         
        ZipEntry e = new ZipEntry(VERSION_FILE);  //NOI18N
        out.putNextEntry(e);
        byte[] data = Version.getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
               
    }
    
     public static void saveToExport (ZipOutputStream out, String tablename) 
            throws FileNotFoundException, IOException {
        
        
        ZipEntry e = new ZipEntry(
                tablename + Application.FS  + "data.txt");  //NOI18N
        out.putNextEntry(e);

        String exportedTable = DB.exportTable(tablename);
//        System.out.println("\nResult\n------\n" + exportedTable); //TODO
        
        byte[] data = exportedTable.getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
        
        List<String> imgList = DB.getImagesIds(tablename);
//        System.out.println("\n\nids\n" + imgList.toString());
        if (imgList.size() > 0) { 
            for (String id : imgList) {
                addImageFromDB(tablename, id, out);
            }
        }         
    }
     
     private static void addImageFromDB (
             String tableName, String id, ZipOutputStream out) {         
        
        //load the image
        BufferedImage img;
        try {
            img = DB.DBLoadImage(tableName, id); //NOI18N
            if (img!=null) {
                  
                ZipEntry e = new ZipEntry(
                        "images" + Application.FS  + 
                                tableName + "-" + id +".jpg");//NOI18N
                  
                    out.putNextEntry(e); 
                    ImageIO.write(img, "jpg", out);//NOI18N
                    out.closeEntry();
                
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
        
    }
  
    
    private static void importTable (String fullFilePath, String tablename) {
        try (ZipFile zipFile = new ZipFile(fullFilePath)) {
            
            Map<String, Integer> headerMap = new HashMap<>();
            Map<Integer,BufferedImage> imageMap = new HashMap<>();
            List <String> records = new ArrayList<>();
            
            ZipEntry  entry =  zipFile.getEntry(
                    tablename + Application.FS  + "data.txt");
            
            if (entry == null) {
                entry =  zipFile.getEntry(
                    tablename + "\\data.txt");
            }
             if (entry == null) {
                entry =  zipFile.getEntry(
                    tablename + "/data.txt");
            }
            
            
            if (entry!=null) {                  
                try (InputStream zipInput = zipFile.getInputStream(entry); 
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(zipInput, "UTF-8"))) {
                    String line;
                    int currentLine=0;
                    
                    String [] header = null;
                    String record="";
                    while((line = br.readLine()) != null) {
                        if (currentLine == 0) {
                            header = line.split(Pattern.quote(DB.FIELD_SEP));
                            for (int x = 0; x < header.length; x++) {
                                String value = header [x];
                                headerMap.put(value, x);
                            }
                        }
                        else {
                            if (header != null && header.length > 0) {
                                
                                record=record + line;
                                if (line.endsWith(DB.LINE_TERM + DB.NL) || 
                                        line.endsWith(DB.LINE_TERM) ) {
                                    
                                    record = 
                                            record.replace(DB.LINE_TERM, BLANK);
                                   
                                    records.add(record);
                                  
                                    String [] currentRecord = 
                                            record.split(
                                                    Pattern.quote(DB.FIELD_SEP));
                                    int fieldn = headerMap.get("id");
                                    String id = currentRecord[fieldn];
                                    BufferedImage img = 
                                            _getImage(zipFile, tablename,id);
                                    
                                    if (img != null) {
                                        imageMap.put(Integer.parseInt(id), img);
                                    }
//                                      System.out.println(record);
                                    //complete import                            
                                    record = ""; //RESET record
                                    
                                }
                            }
                        }
                        
                        
                        
                        currentLine ++;
                        
                    }
                }                                

            }
            zipFile.close();
            
            BaseImportSelector selector = new BaseImportSelector(
                    null, true, headerMap, records,imageMap,tablename);
            selector.setVisible(true);
            
            
         }
         catch (IOException e) {
            System.err.println("Unable to load zip file at location: " +
                    fullFilePath);
             
         }
    }
    
    private static BufferedImage _getImage(
            ZipFile zipFile,String tablename,String id) {
        
        BufferedImage img=null;
        ZipEntry entry =  zipFile.getEntry(
                    "images" + Application.FS  + tablename + "-" + id + ".jpg");
        if (entry == null) {
                entry =  zipFile.getEntry(
                    "images\\"  + tablename + "-" + id + ".jpg");
            }
             if (entry == null) {
                entry =  zipFile.getEntry(
                    "images/"  + tablename + "-" + id + ".jpg");
            }
        
        if (entry!=null) {   
            
            try (BufferedInputStream bis = 
                    new BufferedInputStream(zipFile.getInputStream(entry)))  {
                
                img = ImageIO.read(bis);
                
            } catch (IOException e) {
                System.err.println("No zip entry found at: " + 
                        "images" + Application.FS  + tablename + "-" + id);
                return null;
                
            }
        }
         if (img != null) return img;
        
        return null;
        
    }
    
    public static String getVersion (String fullFilePath) {
        try (ZipFile zipFile = new ZipFile(fullFilePath)) {
             Enumeration zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()){
                ZipEntry entry = (ZipEntry) zipEntries.nextElement();
//debug                System.out.println(entry.getName());
                
                String filename = entry.getName();
                if (filename.endsWith(VERSION_FILE)) {
                                   
                    try (InputStream zipInput = zipFile.getInputStream(entry); 
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(zipInput, "UTF-8"))) {
                        String line;
                        while((line = br.readLine()) != null) {
                            return line.trim();
                        }
                    }
                        
                }
            }    
        } catch (IOException e) {
            System.err.println("Unable to load zip file at location: " +
                    fullFilePath);
        }        
        return null;
    }                 

    public static List<String> getTablesNames (String fullFilePath) {
        List<String> tables = new ArrayList<>();
         try (ZipFile zipFile = new ZipFile(fullFilePath)) {
             Enumeration zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()){
                ZipEntry entry = (ZipEntry) zipEntries.nextElement();
//debug                System.out.println(entry.getName());
                
                String filename = entry.getName();
                if (filename.startsWith(FISH_TABLE)) {
                    tables.add(FISH_TABLE);
                }
                if ( filename.startsWith(INV_TABLE)){
                    tables.add(INV_TABLE);
                }
                if (filename.startsWith(PLANTS_TABLE))  {                   
                    tables.add(PLANTS_TABLE);                
                }
                
            }
            zipFile.close();
            
        } catch (IOException e) {
            System.err.println("Unable to load zip file at location: " +
                    fullFilePath);
        }
        
        return tables;
    }
    
    public static boolean checkZipFile (String fullFilePath) {
        try {
            ZipFile  zipFile = new ZipFile(fullFilePath);
        } catch (IOException e) {
            System.err.println("Unable to load zip file at location: " +
                    fullFilePath);
            
            return false;
        }
        return true;
    }   
    
    private static String _getFieldByName (
            Map<String, Integer> headerMap, String [] currentRecord , 
            String fieldName) {        
        
        int fldIndex = 0;
        try  {            
            fldIndex = headerMap.get(fieldName);
        }
        catch (Exception e) {
            
        }
        if (fldIndex > 0) {
            try {
                String result = currentRecord[fldIndex];
                if ( result != null) {
                    result = result.replace(DB.FIELD_SEP, BLANK);
                    result = result.replace(DB.LINE_TERM, BLANK);
                    if (result.equalsIgnoreCase("null")) result = BLANK;
                    if (result.length() > 0) {
                        return currentRecord[fldIndex];
                    }
                }    
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                _log.log(
                        Level.INFO, 
                        "Num field not matching for element:{0}in record [{1}]", 
                        new Object[]{fieldName, currentRecord});
            }
        }
        
        return BLANK;        
        
    }
    
    public static void importFishFromRecord(
            Map<String, Integer> headerMap, String record, BufferedImage img ) {
        
        String [] currentRecord = record.split(Pattern.quote(DB.FIELD_SEP));
               
        FishBase specData=new FishBase();
        specData.setId(0);
        
        specData.setCommonName(
                _getFieldByName(headerMap,currentRecord,"CommonName"));
        
        specData.setType(
                _getFieldByName(headerMap,currentRecord,"Class"));
        
        specData.setName(
                _getFieldByName(headerMap,currentRecord,"Name"));
        
        specData.setDistribution(
                _getFieldByName(headerMap,currentRecord,"Distribution"));
        
        specData.setDiagnosis(
                _getFieldByName(headerMap,currentRecord,"Diagnosis"));
        
        specData.setBiology(_getFieldByName(headerMap,currentRecord,"Biology"));
      
        
        specData.setMaxSize(_getFieldByName(headerMap,currentRecord,"Maxsize"));
        
        specData.setEnvironment(
                _getFieldByName(headerMap,currentRecord,"Environment"));
        
        specData.setClimate(
                _getFieldByName(headerMap,currentRecord,"Climate"));
    
        specData.setDangerous(
                _getFieldByName(headerMap,currentRecord,"Dangerous"));
        
        specData.setPhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMin")));
        
        specData.setPhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMax")));
        
        specData.setDhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMin")));
        
        specData.setDhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMax")));
        
        specData.setTempMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Min")));
        
        specData.setTempMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Max")));
        
        specData.setSwimLevel(
                _getFieldByName(headerMap,currentRecord,"swimLevel"));    
        
        specData.setLifeSpam(
                _getFieldByName(headerMap,currentRecord,"lifeSpan"));    
        
        specData.setAka(_getFieldByName(headerMap,currentRecord,"Aka")); 

        //save image       
        if (img != null) { 
            specData.setImage(img);
        }
       
        FishBase.save(specData);
    }
    
    public static void importInvertsFromRecord(
            Map<String, Integer> headerMap, String record, BufferedImage img ) {
        
        String [] currentRecord = record.split(Pattern.quote(DB.FIELD_SEP));
        
        InvBase specData=new InvBase();
        specData.setId(0);
        
        specData.setCommonName(
                _getFieldByName(headerMap,currentRecord,"CommonName"));
        
        specData.setType(
                _getFieldByName(headerMap,currentRecord,"Class"));
        
        specData.setName(
                _getFieldByName(headerMap,currentRecord,"Name"));
        
        specData.setDistribution(
                _getFieldByName(headerMap,currentRecord,"Distribution"));
        
        specData.setDiagnosis(
                _getFieldByName(headerMap,currentRecord,"Diagnosis"));
        
        specData.setBiology(_getFieldByName(headerMap,currentRecord,"Biology"));
      
        
        specData.setMaxSize(_getFieldByName(headerMap,currentRecord,"Maxsize"));
        
        specData.setEnvironment(
                _getFieldByName(headerMap,currentRecord,"Environment"));
        
        specData.setClimate(
                _getFieldByName(headerMap,currentRecord,"Climate"));
    
        specData.setDangerous(
                _getFieldByName(headerMap,currentRecord,"Dangerous"));
        
        specData.setPhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMin")));
        
        specData.setPhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMax")));
        
        specData.setDhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMin")));
        
        specData.setDhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMax")));
        
        specData.setTempMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Min")));
        
        specData.setTempMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Max")));
        
        specData.setSwimLevel(
                _getFieldByName(headerMap,currentRecord,"swimLevel"));    
        
        specData.setLifeSpam(
                _getFieldByName(headerMap,currentRecord,"lifeSpan"));    
        
        specData.setAka(_getFieldByName(headerMap,currentRecord,"Aka")); 
        
        specData.setTdsMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"TDSmin")));
        
        specData.setTdsMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"TDSmax")));
        
        //save image       
        if (img != null) { 
            specData.setImage(img);
        }
       
        InvBase.save(specData);
    }
   
    public static void importPlantsFromRecord(
            Map<String, Integer> headerMap, String record, BufferedImage img ) {
        
        String [] currentRecord = record.split(Pattern.quote(DB.FIELD_SEP));
        
        PlantBase specData=new PlantBase();
        specData.setId(0);
       
        specData.setFamily(_getFieldByName(headerMap,currentRecord,"Family")); 
        
        specData.setName(_getFieldByName(headerMap,currentRecord,"Name"));  
        
        specData.setDistribution(
                _getFieldByName(headerMap,currentRecord,"Distribution"));   
        
        specData.setHeight(_getFieldByName(headerMap,currentRecord,"Height"));  
       
        specData.setWidth(_getFieldByName(headerMap,currentRecord,"Width"));
        
        specData.setLight(_getFieldByName(headerMap,currentRecord,"Light"));
       
        specData.setGrowth(_getFieldByName(headerMap,currentRecord,"Growth"));
        
        specData.setDemands(_getFieldByName(headerMap,currentRecord,"Demands"));
        
        specData.setPhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMin")));
        
        specData.setPhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"PHMax")));
        
        specData.setDhMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMin")));
        
        specData.setDhMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"DHMax")));
        
        specData.setTempMin(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Min")));
        
        specData.setTempMax(LocUtil.delocalizeDouble(
                _getFieldByName(headerMap,currentRecord,"t_Max")));
        
        specData.setAquatic(Boolean.getBoolean(
                _getFieldByName(headerMap,currentRecord,"Aquatic"))); 
        
        specData.setNote(_getFieldByName(headerMap,currentRecord,"Note")); 
        
        specData.setAka(_getFieldByName(headerMap,currentRecord,"Aka")); 
        
        specData.setCO2Required(Boolean.getBoolean(
                _getFieldByName(headerMap,currentRecord,"CO2")));  
        
        specData.setPlacement(
                _getFieldByName(headerMap,currentRecord,"Placement")); 
        
        //save image       
        if (img != null) { 
            specData.setImage(img);
        }
       
        PlantBase.save(specData);
    }
    
    static final Logger _log = Logger.getLogger(Util.class.getName());      
    
}

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

import components.ImageFileView;
import components.ImageFilter;
import components.ImagePreview;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import nyagua.data.Setting;

/**
 *Utility and service routines
 *
 * @author Rudi Giacomini Pilon
 * @version 1.0
 */
public class Util {

    /**
     * calculates exponent in base n
     * 
     * @param base
     * @param exp
     * @return exponent
     */
    public static int integerPower (int base, int exp){
        int x = 1;
        for(int i=0;i<exp;i++){
        x*=base;
        }
        return x;
    }

    /**
     * Check a keytyped event 
     * if non numeric key is pressed the event is discarded
     * 
     * @param evt
     * @return the event if numeric
     */
    public static java.awt.event.KeyEvent checkNumericKey(java.awt.event.KeyEvent evt){
        // Allow only numbers
        char key = evt.getKeyChar();
        if (Character.isDigit(key) || key ==',' || key =='.' || key =='-') {
            return evt;
        } else if (evt.isActionKey()) {
            return evt;
        } else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
            return evt;
        } else if (evt.getKeyCode() == KeyEvent.VK_DELETE){
            return evt;
        } else {
            evt.consume();
            return evt;
       }

    }

    /**
     * GUI to chose an image file
     *
     * @param parent
     * @return the file or null if aborted
     */
    public static BufferedImage LoadImage(JLabel parent){
         BufferedImage image;
        // show a chooser 
        JFileChooser fc=null;
        if (fc == null) {
            fc = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);

	    //Add custom icons for file types.
            fc.setFileView(new ImageFileView());

	    //Add the preview pane.
            fc.setAccessory(new ImagePreview(fc));
        }

        //Show it.
        int result = fc.showDialog(parent,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("OK"));

        //Process the results.
        if (result == JFileChooser.APPROVE_OPTION) {
         try {
                image = ImageIO.read(fc.getSelectedFile());
                fc.setSelectedFile(null);
                return image;
            } catch (IOException ex) {
                fc.setSelectedFile(null);
                _log.log(Level.SEVERE, null, ex);
                return null;
            }            
        } else {
            fc.setSelectedFile(null);
            return null;
        }
        //Reset the file chooser for the next time it's shown.
        

    }

    /**
     * Save image as jpg
     * 
     * @param bi    image to save
     * @param fileName dest file
     */
    public static void SaveImage(BufferedImage bi, String fileName){
        try {
            File outputfile = new File(fileName);
            ImageIO.write(bi, "jpg", outputfile);//NOI18N
        } catch (IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sabe image as PNG
     * 
     * @param bi   image to save
     * @param fileName dest file
     */
    public static void SaveImagePNG(BufferedImage bi, String fileName){
        try {
            File outputfile = new File(fileName);
            ImageIO.write(bi, "PNG", outputfile);//NOI18N
        } catch (IOException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads an image from file, resize it if too big 
     * and then place it in the container
     * 
     * @param container a JLabel were to place the image
     * @param sizeLimit
     */
    public static void ImageLoadResize (JLabel container, int sizeLimit ){
        BufferedImage image;
        image=Util.LoadImage(container);
        if (image != null){
                 int imgw=image.getWidth();
                 int imgh=image.getHeight();
                 double imgRatio=(double) imgw/(double) imgh;
                 if (imgh>container.getHeight() ){
                     image=Util.resize(image, (int) (imgRatio*container.getHeight()),
                             container.getHeight());
                }
                 if (image.getWidth() > container.getWidth() ){
                     if (container.getWidth() > sizeLimit){
                         image=Util.resize(image, sizeLimit ,
                             (int) (sizeLimit/imgRatio));
                     }else{
                        image=Util.resize(image, container.getWidth() ,
                             (int) (container.getWidth()/imgRatio)); 
                     }
                     
                 }
                 if (imgh>container.getHeight() ){
                     image=Util.resize(image, (int) (imgRatio*container.getHeight()),
                             container.getHeight());
                }
                 container.setIcon(new javax.swing.ImageIcon(image));
        }
    }
    
    
    /**
     * Resize an imageit if too big 
     * and then place it in the container
     * 
     * @param image      The image to be displayed
     * @param container  a JLabel were to place the image
     * @param sizeLimit  a maximum size (width) for image 
     *                   (only to avoid too big images in db)
     */
    public static void ImageDisplayResize (BufferedImage image, JLabel container, int sizeLimit ){
        //BufferedImage image = null;
        //image=Util.LoadImage(container);
        if (image != null){
                 int imgw=image.getWidth();
                 int imgh=image.getHeight();
                 double imgRatio=(double) imgw/(double) imgh;
                 if (imgh>container.getHeight() ){
                     image=Util.resize(image, (int) (imgRatio*container.getHeight()),
                             container.getHeight());
                }
                 if (image.getWidth() > container.getWidth() ){
                     if (container.getWidth() > sizeLimit){
                         image=Util.resize(image, sizeLimit ,
                             (int) (sizeLimit/imgRatio));
                     }else{
                        image=Util.resize(image, container.getWidth() ,
                             (int) (container.getWidth()/imgRatio)); 
                     }
                     
                 }
                 if (imgh>container.getHeight() ){
                     image=Util.resize(image, (int) (imgRatio*container.getHeight()),
                             container.getHeight());
                }
                 container.setIcon(new javax.swing.ImageIcon(image));
        }
    }
    
    /**
    * Converts a given Image into a BufferedImage
    *
    * @param img The Image to be converted
    * @return The converted BufferedImage
    */
   public static BufferedImage toBufferedImage(Image img) {
       if (img instanceof BufferedImage)
       {
           return (BufferedImage) img;
       }

       // Create a buffered image with transparency
       BufferedImage bimage = new BufferedImage(
               img.getWidth(null), 
               img.getHeight(null), 
               BufferedImage.TYPE_INT_ARGB);

       // Draw the image on to the buffered image
       Graphics2D bGr = bimage.createGraphics();
       bGr.drawImage(img, 0, 0, null);
       bGr.dispose();

       // Return the buffered image
       return bimage;
   }

    /**
     * Converts an immage into a byte array
     * 
     * @param image the image to convert
     * @return a byte array of image
     */
    public static byte[] image_byte_data(BufferedImage image){
        /*WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
        return buffer.getData();       */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", out);//NOI18N
        } catch (IOException ex) {
            Logger.getLogger(ImageFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out.toByteArray();

    }

    /**
     * Converts a byte array into an image
     * 
     * @param buffer the byte array to convert
     * @return the image 
     * @throws IOException
     */
    public static BufferedImage byte_image_data (byte[] buffer) throws IOException{
        ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        return ImageIO.read(in);
    }

    /**
     * Resize an image
     * @param img   The image to be resized
     * @param newW  new width
     * @param newH  new height
     * @return  resized image
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg;
        dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }


    /**
     * Clean an array of text fields
     * setting all text values to  ""
     * (empty string)
     *
     * @param jtfList the list of text fields
     */
    public static void CleanTextFields (JTextField [] jtfList){
        for (JTextField jtfList1 : jtfList) {
            jtfList1.setText("");
            jtfList1.setBackground(Color.WHITE);
        }
    }
    
    /**
     *  Clean an array of text Areas
     * setting all text values to  ""
     * (empty string)
     * 
     * @param jtfList the list of text area
     */
     static void CleanTextFields(JTextComponent[] jtaList) {
        for (JTextComponent jtaList1 : jtaList) {
            jtaList1.setText("");
        }
    }
     
     /**
      * Check an array of text fields and return false 
      * if any is empty or zero
      * 
      * @param jtfList  [array of text fields to check]
      * @return [false if empty or zero otherwise true]
      */
     public static boolean CheckTestFields (JTextField [] jtfList){
        for (JTextField jtfList1 : jtfList) {
            if (jtfList1.getText().isEmpty()) {
                //if any is empty
                return false;
            } else if (Double.parseDouble(LocUtil.delocalizeDouble(jtfList1.getText())) == 0) {
                //if any is zero
                return false;
            }
        }
        return true; 
     }
     
      /**
      * Check a  text fields and return false 
      * if it is empty or zero
      * 
      * @param jtf  [array of text fields to check]
      * @return [false if empty or zero otherwise true]
      */
     public static boolean CheckTestFields (JTextField  jtf){         
        if (jtf.getText().isEmpty()){//if  is empty
            return false;
        }else if(Double.parseDouble(LocUtil.delocalizeDouble( jtf.getText()))==0){//if  is zero
            return false;
        }        
        return true; 
     }
    
    /**
     * Detects the Operative System
     * 
     * @return a standard string that identifies the OS (not the version)     * 
     */
    public static String OS_Detect (){
        String os = System.getProperty("os.name").toLowerCase();//NOI18N
         if (os.contains("win")) {//NOI18N
             return "win";//NOI18N
         }  else if (os.contains("mac")) {//NOI18N
             return "mac"; //NOI18N
         } else if (os.contains("nix")) {//NOI18N
             return "unix"; //NOI18N
         } else if (os.contains("nux")){//NOI18N
             return "linux";//NOI18N
         } else {
            return "n/a";//NOI18N
         }
    }

    

    /**
     * gets the current date
     * 
     * @return a string formatted date with format yyyy/MM/dd HH:mm:ss
     */
    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//NOI18N
        Date d = new java.util.Date();
        String datetime = dateFormat.format(d);
        return datetime;
    }

    /**
     * Call the browser defined in settings 
     * (default for firefox) and show the given file
     * 
     * @param fileName  the file to be shown
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     * 
     * @deprecated 
     */
    public static void BrowseDocument(String fileName)
           throws ClassNotFoundException, SQLException, IOException{
        /* String cmd = null;
        Setting s=Setting.getInstance();
        String browser = s.getBrowser();
        //String browser = DB.getSettings("def_browser","firefox");//NOI18N
        cmd=browser + " " + fileName;
        Process p = Runtime.getRuntime().exec(cmd);*/
    }

    /**
     * Plots the given document
     * (works only in GNU/Linux)
     * [requires gnuplot installed]
     * 
     * @param fileName  the file to be plotted
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static void PlotDocument(String fileName)
            throws ClassNotFoundException, SQLException, IOException{
        String cmd = "gnuplot -persist " + fileName;//NOI18N
        if (Util.OS_Detect().matches("win")) {//NOI18N  //different sintax for windows
            cmd = "gnuplot  \"" + fileName + "\" -";//NOI18N
        }
        Process p = Runtime.getRuntime().exec(cmd);
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            _log.log(Level.SEVERE, null, ex);
        }
    }

    
   
    
    public static long DateDiff (Date start, Date end){
        long diff = end.getTime() - start.getTime();    //date diff
        diff = Math.abs(diff);  //get absolute val
        return Math.round(diff/86400000);  //convert ms in day (1000 * 60 * 60 * 24)
    }
    
    /**
     * Gets two dates from the related fields via
     * two global vars and creates a subquery for
     * the date interval
     *
     * @return a subquery for given dates
     */
    public static String getPeriod () {
        String qry = "";
        if (Global.dFrom.isEmpty()) { //from = null
            if (Global.dTo.isEmpty()){ //to = null
                return qry;
            } else { //to have value
                qry = " AND Date <= '" + Global.dTo + "' ";//NOI18N
                return qry;
            }
        } else { //from have value
            if (Global.dTo.isEmpty()){ //to = null
                qry = " AND Date >= '" + Global.dFrom + "' ";//NOI18N
                return qry;
            } else { //to have value
                qry = " AND Date >= '" + Global.dFrom + "' ";//NOI18N
                qry = qry + " AND Date <= '" + Global.dTo + "' ";//NOI18N
                return qry;
            }
        } 
    }

    /**
     * Gets two dates from the related fields via
     * two global vars and creates a subquery for
     * the date interval considering only year and month
     * (not day)
     * 
     * @return  a subquery for given dates
     */
    public static String getMidPeriod () {
        String qry = "";
        if (Global.dFrom.isEmpty()) { //from = null
            if (Global.dTo.isEmpty()){ //to = null
                return qry;
            } else { //to have value
                String y = Global.dTo.substring(0,4);
                String m = Global.dTo.substring(5, 7);
                qry = " AND y <= '" + y + "' ";//NOI18N
                qry=qry+ " AND m <= '" + m + "' ";//NOI18N
                return qry;
            }
        } else { //from have value
            if (Global.dTo.isEmpty()){ //to = null
                String y = Global.dFrom.substring(0,4);
                String m = Global.dFrom.substring(5, 7);
                qry = " AND y >= '" + y + "' ";//NOI18N
                qry=qry+ " AND m >= '" + m + "' ";//NOI18N
                return qry;
            } else { //to have value
                String y = Global.dFrom.substring(0,4);
                String m = Global.dFrom.substring(5, 7);
                qry = " AND y >= '" + y + "' ";//NOI18N
                qry = qry+ " AND m >= '" + m + "' ";//NOI18N
                y = Global.dTo.substring(0,4);
                m = Global.dTo.substring(5, 7);
                qry = qry + " AND y <= '" + y + "' ";//NOI18N
                qry = qry + " AND m <= '" + m + "' ";//NOI18N
                return qry;
            }
        }
    }

     /**
     * Show an error message box
     *
     * @param sMsg error text
     */
     public static void showErrorMsg( String sMsg ) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog( null, sMsg,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INPUT_ERROR"),
                 JOptionPane.ERROR_MESSAGE );
    }

/**
     * Show an info message box
     *
     * @param sMsg error text
     */
     public static void showInfoMsg( String sMsg ) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog( null, sMsg,java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INPUT_ERROR"),
                 JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Show a dialog box to allow directory selection
     *
     * @param theForm from which the dialog is called
     * @return  the directory selected
     */
    public static File directorySelector(JPanel theForm){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));//NOI18N
        chooser.setDialogTitle(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("SELECT_BACKUP_DIRECTORY"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(theForm) == JFileChooser.APPROVE_OPTION) {
             return chooser.getSelectedFile();
         } else {
            return null;
         }

     }

    /**
     * Copy one file
     *
     * @param source    source file
     * @param dest      destination file
     * @throws IOException
     */
     public static void fileCopy(File source, File dest) throws IOException {
         FileChannel in = null, out = null;
         try {
              in = new FileInputStream(source).getChannel();
              out = new FileOutputStream(dest).getChannel();

              long size = in.size();
              MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

              out.write(buf);

         } finally {
              if (in != null) {
                 in.close();
             }
              if (out != null) {
                 out.close();
             }
         }
    }

    /**
     * Execute the backup of application data file
     * 
     * @param jp the pane to bind the window
     */
     public static void backupFile (JPanel jp) {
        File backupDir =Util.directorySelector(jp);
        if (backupDir != null){
            if (backupDir.exists()){
                String backupFileName=Application.NAME;
                if (!(DB.getCurrent().isEmpty())){
                    backupFileName=DB.getCurrent();
                }
                int backupNum=0;
                File backupFile= null;
                String backupFullFileName;
                do  {
                    backupFullFileName=backupDir.getAbsolutePath() +
                            Application.FS +
                            backupFileName + backupNum + ".bak";//NOI18N
                    backupFile= new File(backupFullFileName);
                    backupNum++;
                } while (backupFile.exists());
                File source=new File (Global.FullFileName);
                    try {
                        Util.fileCopy(source, backupFile);
                        JOptionPane.showMessageDialog( null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("BACKUP_COMPLETED."),java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("INFO"),
                                JOptionPane.INFORMATION_MESSAGE );
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog( null, java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("AN_ERROR_OCCURRED_") +
                                java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("DURING_BACKUP."),java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR"),
                                JOptionPane.ERROR_MESSAGE );
                    }
            } else {
                Util.showErrorMsg(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERROR_INVALID_DIRECTORY"));
            }
        }
    }
     
         /**
     * save a window position and size
     * 
     * @param w the window
     */
    public static void savePosition(Window w) {		
		Rectangle bounds = w.getBounds();
                Setting s= Setting.getInstance();
                s.saveWinPosition(bounds.x, bounds.y, bounds.width, bounds.height);
	}

    /**
     * Load a window position and size
     * 
     * @param w the window
     */
    public static void loadPosition(Window w) {
		Rectangle bounds = w.getBounds();
                Setting s= Setting.getInstance();
                int [] position =new int[4];
                position=s.loadWinPosition();
		if((w instanceof Frame) && ((Frame)w).isResizable()) {
			bounds.height = position[3];
			bounds.width = position[2];
		}
		bounds.x = position[0];
		bounds.y = position[1];
		w.setBounds(bounds);		
	}
    
    /**
     * Return an array of column sizez from a given table
     * 
     * @param jt    the table to evaluate
     * @return      an array of int with column widths
     */
    public static int[] getColSizes(JTable jt){
        int cols=jt.getColumnCount();
        int[] cw =new int[cols];
        for (int x=0; x< cols; x++){
            TableColumn col = jt.getColumnModel().getColumn(x);
            if (col.getWidth()>39){
                cw[x]=col.getWidth();
            }else{
                cw [x]=40; //min size
            }
            
        }
        return cw;
    }
    
    /**
     * Applies an array of column widths to a given table
     * 
     * @param jt        the table to manipulate
     * @param colWidth  the array of widths
     */
    public static void setColSizes (JTable jt, int [] colWidth){
        int cols=jt.getColumnCount();
        if (cols > 1){              //check to avoid changes in a new undefined table
            for (int x=0; x< cols; x++){
                TableColumn col = jt.getColumnModel().getColumn(x);
                int y=colWidth[x];
                if (y<40){y=40;}
                col.setPreferredWidth(y);
            }
        }
        
    }
    
    /**
     * Return an empty string instead of null
     * 
     * @param stc
     * @return 
     */
    public static String notNullString (String stc){
        if (stc == null){
            return "";
        }
        return stc;
    }
    
    /**
     * Gets an image out of a component
     * 
     * @param c the component
     * @return a BufferedImage
     */
    public static BufferedImage imageFromComponent(Component c) {
        BufferedImage bi;
        try {
            bi = new BufferedImage(c.getWidth(),c.getHeight(), BufferedImage.TYPE_INT_ARGB);             
            Graphics2D g2d =bi.createGraphics();
            c.print(g2d);
            g2d.dispose();
        } catch (Exception e) {
            return null;
        }
        return bi;
    }
    
    /**
     * Converts a binary string into integer
     * 
     * @param binary    String to convert (should be all 1/0)
     * @return  an integer representing the binary
     */
    public static Integer binaryToInteger(String binary){
        char[] numbers = binary.toCharArray();
        Integer result = 0;
        for(int i=numbers.length;i>0;i--){
            if(numbers[i-1]=='1') {
                result+=integerPower(2,numbers.length-i);
            }
        }
        return result;
    }
    
    /**
     * Check if a file exists
     * 
     * @param filename
     * @return true=exist || false not exist
     */
    public static boolean checkFileExistence(String filename) {
        File f = new File(filename);
        return f.exists();    
    }

    
    static final Logger _log = Logger.getLogger(Util.class.getName());
    
}

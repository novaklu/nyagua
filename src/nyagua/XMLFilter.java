/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nyagua;


import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;



/**
 * This class implements images files operations
 * derivated from SUN filechoser2 demo
 *
 * @author giacomini
 * @version 1.0
 *
 */
public class XMLFilter extends FileFilter {
    public final static String xml = "xml";//NOI18N
    public final static String XML = "XML";//NOI18N
    public final static String htm = "htm";//NOI18N
    public final static String html = "html";//NOI18N
    
    /**
     * Returns a given file extension
     * @param f the given file 
     * @return  the file extension
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * Verify if a file extension is in the list
     *
     * @param f the file
     * @return true if file extension accettable
     */
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            return extension.equals(XMLFilter.xml) ||
                    extension.equals(XMLFilter.XML) ||
                    extension.equals(XMLFilter.htm) ||
                    extension.equals(XMLFilter.html);
        }
        return false;
    }

    /**
     * The description of this filter
     * @return A description
     */
    @Override
    public String getDescription() {
        return java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("XML_OR_HTML_FILE");
    }


    /**
     * Allow to choose an XML file
     *
     * @return  the file pointer
     */
    public static File getXMLFile (){
        File file = null;
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(new XMLFilter());
        int result = jfc.showOpenDialog(null);
        if (result==JFileChooser.APPROVE_OPTION ) {
            file = (jfc.getSelectedFile());
        }
        return file;
    }

}



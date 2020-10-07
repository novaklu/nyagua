/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon *
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
 */

package nyagua;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import nyagua.data.Setting;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author rgi
 */
public class FBConnector extends javax.swing.JDialog {
    //fishbase.org | fishbase.us | fishbase.de | fishbase.fr | fishbase.se | fishbase.tw | fishbase.cn | fishbase.sa | fishbase.ca
    
    private final String USER_AGENT = "Mozilla/5.0";       
  
    String protocol= "http://";
    String host="www.fishbase.org/";    //default fishbase host
//    String xmlHost="www.fishbase.us/";  //xml is hosted only on this host
    
    int timeout=30;
    
    //Parsing fields
    private final int IMAGE_FIELD=1;
    private final int NAME_FIELD=2;
    private final int LANG_FIELD=3;
    private final int SCINAME_FIELD=5;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | 
                javax.swing.UnsupportedLookAndFeelException ex) {
            _log.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FBConnector dialog = new FBConnector(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    /**
     * Creates new form FBConnector
     * @param parent    parent window
     * @param modal     true=modal mode; false=non modal mode;
     */
    public FBConnector(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /**
     * 
     * Get the XML document as string
     * 
     * @return xml doc
     * 
     */
//    public String getReturnedDoc() {
//        return retString;
//    }
    
    /**
     * Get the HTML document
     * @return Jsoup Document retDoc
     * 
     */
    public Document getReturnedDoc() {
        return retDoc;
    }
    
    /**
     * Get fish image
     * 
     * @return image
     */
    public Image getImage() {
        return fishImage;
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

        queryButtonGroup = new javax.swing.ButtonGroup();
        searchLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        searchTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        rowsPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        errLabel = new javax.swing.JLabel();
        containsRadioButton = new javax.swing.JRadioButton();
        isRadioButton = new javax.swing.JRadioButton();
        beginsRadioButton = new javax.swing.JRadioButton();
        endRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        setTitle(bundle.getString("FBCONNECTOR")); // NOI18N
        setName(bundle.getString("FBCONNECTOR")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        searchLabel.setText(bundle.getString("SEARCH_ON_FB")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 0, 10);
        getContentPane().add(searchLabel, gridBagConstraints);

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Search_16.png"))); // NOI18N
        searchButton.setText(bundle.getString("Search")); // NOI18N
        searchButton.setSelected(true);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 5, 20);
        getContentPane().add(searchButton, gridBagConstraints);

        searchTextField.setMargin(new java.awt.Insets(0, 10, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(searchTextField, gridBagConstraints);

        rowsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rowsPanel.setAlignmentY(0.0F);
        rowsPanel.setMinimumSize(new java.awt.Dimension(400, 400));
        rowsPanel.setLayout(new javax.swing.BoxLayout(rowsPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(rowsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 700;
        gridBagConstraints.ipady = 400;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Exit_16.png"))); // NOI18N
        closeButton.setText(bundle.getString("Close")); // NOI18N
        closeButton.setDefaultCapable(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 0);
        getContentPane().add(closeButton, gridBagConstraints);

        errLabel.setForeground(new java.awt.Color(255, 51, 51));
        errLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(errLabel, gridBagConstraints);

        queryButtonGroup.add(containsRadioButton);
        containsRadioButton.setSelected(true);
        containsRadioButton.setText(bundle.getString("Contains")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(containsRadioButton, gridBagConstraints);

        queryButtonGroup.add(isRadioButton);
        isRadioButton.setText(bundle.getString("Is")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(isRadioButton, gridBagConstraints);

        queryButtonGroup.add(beginsRadioButton);
        beginsRadioButton.setText(bundle.getString("Begins_with")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(beginsRadioButton, gridBagConstraints);

        queryButtonGroup.add(endRadioButton);
        endRadioButton.setText(bundle.getString("Ends_with")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        getContentPane().add(endRadioButton, gridBagConstraints);

        jLabel1.setText(bundle.getString("Ny.fbCommonNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        setSize(new java.awt.Dimension(751, 649));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Get xml fishbase file corresponding to chosen button
     * 
     * @param id    fishbase fish id
     */
    private void chosenButtonID(String id, String imgURL) { 
        errLabel.setText("");//NOI18N
        errLabel.repaint();
        StringBuilder pageReq = new StringBuilder();
//        pageReq.append("maintenance/FB/showXML.php?identifier=FB-");
//        pageReq.append(id);
//        pageReq.append("&ProviderDbase=03");
        
        pageReq.append("Summary/SpeciesSummary.php?ID=");
        pageReq.append(id);
        
//        String url=protocol + xmlHost + pageReq.toString();
        String url=protocol + host + pageReq.toString();
        AppUtil.startWaitCursor(rootPane);
        try {
            Document doc = Jsoup.connect(url)
            .timeout(timeout * 1000)
            .get();
//             String doc = sendGet(url);
            
            if (doc != null) {
//                retString = doc;
                retDoc = doc;    

                if (imgURL != null && !imgURL.isEmpty()) {
                    fishImage = downloadImage(imgURL);                
                }
                
                setVisible(false);
                dispose();
            }
            
            
        }
         catch (Exception ex) {
            if (ex.getClass() == java.net.SocketTimeoutException.class) {
                errLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERR_TIMEOUT") + " [url=" + url + "]");
            }
            if (ex.getClass() == java.net.UnknownHostException.class) {
                errLabel.setText(java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("ERR_HOST") + " [url=" + url + "]");
            }
            else {
                 errLabel.setText(ex.getLocalizedMessage() + " [url=" + url + "]");
            }
        }
        finally {
            AppUtil.stopWaitCursor(rootPane);
        }            
     }
     
     	

/**
 *    HTTP GET request
 * 
 */
private String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();       

        // optional default is GET
        con.setRequestMethod("GET");
        
        con.setConnectTimeout(timeout * 1000);

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            
            }
        }
        
        return response.toString();
        
}

//// HTTP POST request
//private void sendPost(String url,String urlParameters) throws Exception {
//
//
//        URL obj = new URL(url);
//        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//
//        //add reuqest header
////		con.setRequestMethod("POST");
////		con.setRequestProperty("User-Agent", USER_AGENT);
////		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
////
////		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
//
//        // Send post request
//        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();
//
//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
//        System.out.println("Response Code : " + responseCode);
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuilder response = new StringBuilder();
//
//        while ((inputLine = in.readLine()) != null) {
//                
//                    response.append(inputLine);
//        }
//        in.close();
//
//        //print result
//        System.out.println(response.toString());
//
//}

/**
 * downoad image from url
 * 
 * @param url   uri of the image
 * 
 * @return an Image from the URL
 */
private Image downloadImage(String url) {
        Image image = null;
    try {
        URL imgUrl = new URL(url);

        try {
            image = ImageIO.read(imgUrl);
        } catch (IOException e1) {
             errLabel.setText(e1.getLocalizedMessage());
        }
    } catch (MalformedURLException ex) {
        _log.log(Level.SEVERE, null, ex);
    }
    return image;
}
    /**
     * Search action
     * 
     * @param evt 
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed

        //Operators: 
        final String EQUAL="EQUAL";
        final String CONTAINS="CONTAINS";
        final String BEGINS_WITH="BEGINS_WITH";
        final String ENDS_WITH="ENDS_WITH";
        String operator = CONTAINS;
        if (isRadioButton.isSelected()) {
            operator = EQUAL;
        }
        else if (beginsRadioButton.isSelected()) {
            operator = BEGINS_WITH;
        }
        else if (endRadioButton.isSelected()) {
            operator = ENDS_WITH;
        }
        
        rowsPanel.removeAll();
        errLabel.setText("");//NOI18N
        
        // Search on FishBase        
        String searched = searchTextField.getText();
        if (searched.trim().isEmpty() || searched.length()<3) {
            errLabel.setText(
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").
                            getString("ERR_TOO_SHORT"));
            return;
        }     //nothing to search

        StringBuilder pageReq = new StringBuilder();
        pageReq.append("ComNames/CommonNameSearchList.php?");
        pageReq.append("resultPage=1&CommonName=");
        pageReq.append(searched);
        pageReq.append("&sp=y&sortby=nametype&crit1_operator=");
        pageReq.append(operator);
        String url=protocol + host + pageReq.toString();
        int counter=0;
        AppUtil.startWaitCursor(rootPane);
        try {
            Document doc = Jsoup.connect(url)
            .timeout(timeout * 1000)
            .get();
            Elements contents = doc.getElementsByClass("commonTable");
            Element table=null;
            
            if (contents.size()>0)table=contents.first();
            if (table !=null) {
                Elements rows = table.getElementsByTag("tr");
                for (Element row : rows) {
                    Elements cols = row.getElementsByTag("td");
                    int colIndex=0;
                    FishBaseRow fbRow = new FishBaseRow();
                    String name="";
                    String lang="";
                    String sciName="";
                    String imgUrl=null;
                    String id="";
                    for (Element col : cols) {
                        colIndex++;
                        switch (colIndex) {
                            case IMAGE_FIELD: //get image from html: col.html()
                            String imgTag= col.html();
                            int start = imgTag.indexOf("\"");
                            if (start > 0) {
                                imgTag=imgTag.substring(start+1);
                                int end = imgTag.indexOf("\"");
                                if (end > 0) {
                                    imgUrl = imgTag.substring(0, end);
                                    System.out.print("Image:" + imgUrl + " | ");

                                }
                            }
                            break;

                            case NAME_FIELD:
                            System.out.print("Name:" + col.text() + " | ");
                            name=col.text();
                            String tmpId =  col.html();
                            int startid = tmpId.indexOf("ID=");
                            if (startid > 0 && tmpId.length()> startid+3) {
                                tmpId=tmpId.substring(startid+3);
                                int endId=tmpId.indexOf("&");
                                if (endId > 0) {
                                    id=tmpId.substring(0,endId);
                                }
                                System.out.print("Id:" + id + " | ");
                            }
                            break;

                            case LANG_FIELD:
                            System.out.print("Lang:" + col.text() + " | ");
                            lang=" [" + col.text() + "]"  ;
                            break;

                            case SCINAME_FIELD:
                            System.out.println("Scientific Name:" + col.text() );
                            sciName= "(" + col.text() + ")";
                            break;

                        }
                    }
                    if (imgUrl != null) {
                        fbRow.setImage(downloadImage(protocol + host +imgUrl));
                        String fishBigImage = 
                                imgUrl.replaceAll("tnn_files/tnn_Jpg", "thumbnails/jpg");
                        fishBigImage = fishBigImage.replaceAll("tnn_", "tn_");
                        fbRow.setImageUrl(protocol + host + fishBigImage);
                    } else {
                        fbRow.setImage(null);
                    }
                    if (name == null || name.isEmpty()) continue;
                    
                    fbRow.setNameLabel(name + lang + sciName);
                    fbRow.setAlignmentX(0.0F);
                    fbRow.setAlignmentY(0.0F);
                    
                    fbRow.setID(id);
                    
                    fbRow.setChoseActionListener(
                        new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                String btnId = (String)
                                        ((JButton)evt.getSource()).
                                                getClientProperty( "id" );
                                
                                String imgURL = (String)
                                        ((JButton)evt.getSource()).
                                                getClientProperty( "imgURL" );

                                chosenButtonID(btnId, imgURL);
                            }
                        }
                    );
                    fbRow.setAlignmentY(LEFT_ALIGNMENT);
                        GridBagConstraints gridBagConstraints = 
                                new java.awt.GridBagConstraints();
                        add(fbRow, gridBagConstraints);            
                        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                    
                    rowsPanel.add(fbRow);
                    counter ++;
                }
            }
            rowsPanel.revalidate();
            rowsPanel.repaint();

        }
        catch (Exception ex) {
            if (ex.getClass() == java.net.SocketTimeoutException.class) {
                 errLabel.setText(ex.getLocalizedMessage()  + " [url=" + url + "]");
            }
            _log.log(Level.SEVERE, null, ex);
        }
        finally {
            AppUtil.stopWaitCursor(rootPane);
        }
        
        if (counter == 0) {
            errLabel.setText(
                    java.util.ResourceBundle.getBundle("nyagua/Bundle").
                            getString("ERR_NOT_FOUND"));
        }

    }//GEN-LAST:event_searchButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Close
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // set default button
        getRootPane().setDefaultButton(searchButton);
        Setting s= Setting.getInstance();
        timeout =s.getTimeout();
        host = s.getFishbaseSite() + "/";
        if (!host.endsWith("/")) {
            host = host + "/";
        }
    }//GEN-LAST:event_formWindowOpened
       

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton beginsRadioButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JRadioButton containsRadioButton;
    private javax.swing.JRadioButton endRadioButton;
    private javax.swing.JLabel errLabel;
    private javax.swing.JRadioButton isRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup queryButtonGroup;
    private javax.swing.JPanel rowsPanel;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables

    private Document retDoc = null;
    private String retString = null;
    private Image fishImage = null;
    
    static final Logger _log = Logger.getLogger(FBConnector.class.getName());

}
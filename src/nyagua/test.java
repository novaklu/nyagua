/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nyagua;

/**
 *
 * @author giacomini
 */
public class test extends javax.swing.JPanel {
    
    
       
    /**
     * Creates new form test
     */
    public test() {
        initComponents();
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

        searchLabel = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        searchLabel.setText("This is the Test Panel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(searchLabel, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("nyagua/Bundle"); // NOI18N
        searchButton.setText(bundle.getString("Search")); // NOI18N
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
        });
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 5, 20);
        add(searchButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
             
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchButtonMouseClicked
        
            // TODO add your handling code here:
//            UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
//            for (int x=0; x<lafInfo.length; x++) {
//                System.out.println(lafInfo[x].getName());
//            }
           
    }//GEN-LAST:event_searchButtonMouseClicked
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchLabel;
    // End of variables declaration//GEN-END:variables
}

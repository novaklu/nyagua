/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nyagua;

import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author rgi
 */
public class TablesUtil {
    public static int getIdFromTable (JTable table, int row) {
        
        if (table == null) return 0;
        
        Enumeration<TableColumn> tableCols = table.getColumnModel().getColumns();
        int i=0;
        while (tableCols.hasMoreElements()){
            TableColumn column = tableCols.nextElement();
            int idCol =column.getModelIndex();
            if (idCol == 0){
                String tmp = table.getValueAt(row, i).toString();
                return Integer.parseInt(tmp); 
            }
            i++;
        }
        String tmp = table.getValueAt(row,0).toString();
        return Integer.parseInt(tmp); 
    }
    
    public static int[] getOrderList (JTable displayData) {
        
        TableColumnModel colMod = displayData.getColumnModel();
        Enumeration<TableColumn> tableCols = colMod.getColumns();

        int[] orderList = new int[colMod.getColumnCount()];
        int i = 0;
        while (tableCols.hasMoreElements()){
            TableColumn column = tableCols.nextElement();
            orderList[i] = column.getModelIndex();                
            i++;
        }
        
        return orderList;
    }
    
    public static void sortColumns (
            int[] orderList, JTable displayData, TableColumnModel colMod) {

        if ((orderList != null) && 
                    (orderList.length == displayData.getColumnCount())) {
            
            int i = 0;
            Enumeration<TableColumn> ordCols = colMod.getColumns();
            
            while (ordCols.hasMoreElements()){
                TableColumn column = ordCols.nextElement();
                column.setModelIndex(orderList[i]);
                i++;
            }
           
        } 
    }
    
    public static String[] sortHeader (
            int[] orderList, String[] captions) {
        
        int size = captions.length;
        String[] headerCaptions = new String[size];
        
        headerCaptions=Arrays.copyOf(captions, size);
        for (int a = 0; a < size; a++) {
            if (orderList.length < size) {
                break;
            }

            headerCaptions[a]=captions[orderList[a]];
        }
        return headerCaptions;
    }
}

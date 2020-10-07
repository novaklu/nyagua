/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nyagua.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import nyagua.LocUtil;
import nyagua.data.Solutions;

/**
 *
 * @author rgi
 */
public class ElementRenderer extends DefaultTableCellRenderer { 
    
    private static final long serialVersionUID = 6703872492730589499L;
    
        
    private static final  Color LIGHT_BLUE = new Color(0, 153, 255);
    private static final  Color LIGHT_GREEN = new Color(204, 255, 204);
    
    private static int _method = 0;
        

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        
        Component cellComponent = super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
        
        String deLocalizedVal = LocUtil.delocalizeDouble(((String) value));
        double numValue = Double.parseDouble(deLocalizedVal) ;
      
        int evaluated = evaluateElement(column, numValue);
        
        if(evaluated < 0){
            cellComponent.setBackground(LIGHT_BLUE);
        } else if ( evaluated > 0){
            cellComponent.setBackground(Color.RED);
        } else {
            cellComponent.setBackground(LIGHT_GREEN);
        }
        return cellComponent;
    }
    
    public static void setFertMethod (int method) {
        _method = method;
    }
    
    private int evaluateElement(int elementNumber, double elementValue) {
        if (elementNumber > 6) {
            return 0; //NOT EVALUATED
        }
        
        Solutions sol = new Solutions();        
        double [][][] methods_parameters = sol.getMethodParameters();
        double max = methods_parameters[elementNumber][_method][Solutions.PAR_HIGH];
        double min = methods_parameters[elementNumber][_method][Solutions.PAR_LOW];
        if (elementValue < min) {
            return -1; //LOW
        }
        else if (elementValue > max) {
            return 1; //HIGH
        }
        else {
            return 0; //RIGHT OR NOT CARE
        }
    }
    
}

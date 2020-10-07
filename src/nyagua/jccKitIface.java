/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2010 Rudi Giacomini Pilon
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

import java.util.Properties;
import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

/**
 * Interface for jccKit 
 * 
 * @author Rudi Giacomini Pilon
 */
public class jccKitIface {
    private boolean grid;
    private boolean legend;
    private String legendCaption="";
    private String xLabel="x";
    private String yLabel="y";
    private double xMin=0;
    private double yMin=0;
    private double xMax=1;
    private double yMax=1;
    private int gridStep=10;
    private String [] xMap;
    private String [] yMap;
    private final int MAX_DATASET = 19; 

    // <editor-fold defaultstate="collapsed" desc="Private Variables Access Methods"> 
    /**
     * Set grid on=true off=false
     * 
     * @param state [boolean]
     */
    public void setGrid (boolean state){        
        this.grid=state;
    }
    
    /**
     * Set legend on=true off=false
     * 
     * @param state [boolean]
     * @param caption
     */
    public void setLegend (boolean state, String caption){        
        this.legend=state;
        this.legendCaption=caption;
    }
    
    /**
     * Set the x axis label
     * 
     * @param caption The label
     */
    public void setXLabel (String caption){
        this.xLabel=caption;
    }
    
    /**
     * Set the y axis label
     * 
     * @param caption The label
     */
    public void setYLabel (String caption){
        this.yLabel=caption;
    }
    
    /**
     * Set x axis min value
     * 
     * @param min 
     */
    public void setXMin (double min){
        this.xMin=min;
    }
    
    /**
     * Set x axis max value
     * 
     * @param max 
     */
    public void setXMax (double max){
        this.xMax=max;
    }
    
    /**
     * Set y axis min value
     * 
     * @param min 
     */
    public void setYMin (double min){
        this.yMin=min;
    }
    
    /**
     * Set y axis max value
     * 
     * @param max 
     */
    public void setYMax (double max){
        this.yMax=max;
    }
    
    /**
     * Set the grid tics frequency in units
     * 
     * @param gridUnits 
     */
    public void setGridStep (double gridUnits){
        this.gridStep=(int) (((yMax+1)/gridUnits));
    }
    
    /**
     * Set the hash map for x axis tics labels
     * 
     * @param map an array containing ordered captions
     */
    public void setXmap (String[] map ){
        this.xMap=map;
    }
    
    /**
     * Set the hash map for y axis tics labels
     * 
     * @param map an array containing ordered captions
     */
    public void setYmap (String[] map ){
        this.yMap=map;
    }
    
  // </editor-fold>  
        
    /**
     * Creates a canvas to plot lines 
     * with related parameters
     * 
     * @param numCurves
     * @return the canvas
     */
    public GraphicsPlotCanvas createLinesPlotCanvas(int numCurves) { //numCurves <20       
        Properties props = new Properties();
        ConfigParameters config = new ConfigParameters(new PropertiesBasedConfigData(props));
        
        if (numCurves > MAX_DATASET) {
            System.out.println("too much curves ("+ Integer.toString(numCurves) +  
                    ") to plot: not so much colors");
        }      
       
        //preparing colors for up to 18 curves
        String [] colors = new String [] {"0xff0000","0x00ff00","0x0000ff","0xff00ff",
            "0x00ffff","0x000000","0xff4800","0xff00ff","0x00c000","0xd7d700","0x00c0c0",
            "0x252289","0xa0a0a0","0xffc0ff","0x4800f3","0xf3b0ff","0x0ec00b","0xa7a708",
            "0xff9c6a","0xa494ff","0xff4ab7"}; 
        //preparing curves definition
        String param = "";
        for (int i = 1; i <= MAX_DATASET; i++){
            param= param + "curve" + Integer.toString(i)+" ";
        }
        props.put("data/curves" ,param);
        for (int i = 1; i <= MAX_DATASET; i++){
            props.put("data/curve" + Integer.toString(i)+"/title","curve "+ Integer.toString(i));
        }
        props.put("paper" ,"0 0 1.15 0.6");
        props.put("background" ,"0xffffff");    //white background
        props.put("plot/initialHintForNextCurve/className" ,"jcckit.plot.PositionHint");
        props.put("plot/initialHintForNextCurve/position" ,"0 0.1");
        props.put("plot/coordinateSystem/origin" ,"0.099 0.1");
        
            //x axis settings
        props.put("plot/coordinateSystem/xAxis/axisLabel" ,this.xLabel); 
        props.put("plot/coordinateSystem/xAxis/gridAttributes/lineColor" ,"0xdcdcdc");
        props.put("plot/coordinateSystem/xAxis/grid" , Boolean.toString(this.grid));
        //props.put("plot/coordinateSystem/xAxis/automaticTicCalculation" ,"false");
        props.put("plot/coordinateSystem/xAxis/automaticTicCalculation" ,"true");
        props.put("plot/coordinateSystem/xAxis/numberOfTics" ,"10");
        props.put("plot/coordinateSystem/xAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/fontSize" ,"0.030");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/textColor" ,"0xaa");
        props.put("plot/coordinateSystem/xAxis/minimum" ,Double.toString(xMin));
        props.put("plot/coordinateSystem/xAxis/maximum" ,Double.toString(xMax));
        if (this.xMap!=null){
            if (this.xMap.length > 0){
                String map="";
                for (int i=0; i < this.xMap.length; i++){
                    map=map + Integer.toString(i) + "=" + this.xMap[i] + " ";
                }
                props.put("plot/coordinateSystem/xAxis/ticLabelFormat/className",
                "jcckit.plot.TicLabelMap");
                props.put("plot/coordinateSystem/xAxis/ticLabelFormat/map",map);
            }
        }
            
      
            //y axis settings        
        props.put("plot/coordinateSystem/yAxis/gridAttributes/lineColor" ,"0xdcdcdc");        
        props.put("plot/coordinateSystem/yAxis/grid" , Boolean.toString(this.grid));
        
        props.put("plot/coordinateSystem/yAxis/axisLabel" ,this.yLabel); 
        props.put("plot/coordinateSystem/yAxis/axisLabelPosition" ,"-0.06 0.05");       
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/fontSize" ,"0.030");
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/textColor" ,"0xee");
         props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/yAxis/ticLength" ,"0.006");
        //props.put("plot/coordinateSystem/yAxis/ticLabelPosition" ,"0.05 0");
        
        //props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/fontStyle" ,"bold");
        props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/horizontalAnchor" ,"right");
        props.put("plot/coordinateSystem/yAxis/automaticTicCalculation" ,"false");        
        props.put("plot/coordinateSystem/yAxis/minimumTic",Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximumTic",Double.toString(yMax));        
        props.put("plot/coordinateSystem/yAxis/numberOfTics",Integer.toString(gridStep));
        
        
        props.put("plot/coordinateSystem/yAxis/minimum" ,Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximum" ,Double.toString(yMax));
            //simbol settings
        props.put("defaultDefinition/symbolFactory/className" ,"jcckit.plot.CircleSymbolFactory");
        props.put("defaultDefinition/symbolFactory/size" ,"0.008");
        props.put("defaultDefinition/symbolFactory/attributes/className" ,"jcckit.graphic.BasicGraphicAttributes");
        props.put("defaultDefinition/symbolFactory/attributes/lineColor" ,"0");
        props.put("defaultDefinition/symbolFactory/attributes/lineThickness" ,"0.002");
            //Lines settings
        props.put("defaultDefinition/lineAttributes/className" ,"jcckit.graphic.ShapeAttributes");
        props.put("defaultDefinition/lineAttributes/linePattern" ,"0.01 0.005");
        props.put("defaultDefinition/lineAttributes/lineThickness" ,"0.005");
        props.put("defaultDefinition/lineAttributes/lineColor" ,"0xca");
            //appliing curves definitions and colors
        param = "";
        for (int i = 1; i <= MAX_DATASET; i++){
            param= param + "def" + Integer.toString(i)+" ";
        }
        props.put("plot/curveFactory/definitions" ,param);
        for (int i = 1; i <= MAX_DATASET; i++){
              props.put("plot/curveFactory/def" + Integer.toString(i)+"/","defaultDefinition/");
              props.put("plot/curveFactory/def"+ Integer.toString(i)+"/lineAttributes/lineColor" ,
                      colors[i-1]);
          }
            //Legend section
        props.put("plot/legend/upperRightCorner","1.145 0.65");
        props.put("plot/legend/boxAttributes/fillColor" ,"0xeeeeee");
        props.put("plot/legend/lineLength" ,"0.035");
        props.put("plot/legendVisible", Boolean.toString(this.legend));
        props.put("plot/legend/title",this.legendCaption);
        double boxHeight=0.032*numCurves+0.060;
        props.put("plot/legend/boxHeight", String.valueOf(boxHeight));
        props.put("plot/legend/boxWidth","0.235");
                
        return new GraphicsPlotCanvas(config);
    }
    
    /**
     * Creates a canvas to plot bars 
     * with related parameters
     * 
     * @param numCurves
     * @return the canvas
     */
    public GraphicsPlotCanvas createBarsPlotCanvas (int numCurves) {
        Properties props = new Properties();
        ConfigParameters config = new ConfigParameters(new PropertiesBasedConfigData(props));
        if (numCurves > 13) {
            System.out.println("too much curves ("+ Integer.toString(numCurves) +  
                    ") to plot: not so much colors");
        }        
        //preparing colors for up to 14 curves
        String [] colors = new String [] {
            "0xff0000","0x00ff00","0x0000ff","0xff00ff", "0x00ffff","0x000000",
            "0xff4800","0xff00ff","0x00c000","0xd7d700","0x00c0c0", "0x252289",
            "0xa0a0a0","0xffc0ff"}; 
        //preparing curves definition
        String param = "";        
        for (int i = 1; i <= 13; i++){            
            param= param + "curve" + Integer.toString(i)+" ";
            param= param + "bar" + Integer.toString(i)+" ";
        }
        props.put("data/curves" ,param);
        for (int i = 1; i <= 13; i++){
            props.put("data/curve" + Integer.toString(i)+"/title","curve "+ Integer.toString(i));
            props.put("data/bar" + Integer.toString(i)+"/title","bar "+ Integer.toString(i));
        }        
        
        props.put("paper" ,"0 0 1 0.6");
        props.put("background" ,"0xffffff");    //white background
        props.put("defaultCoordinateSystem/ticLabelAttributes/fontSize" ,"0.03");
        props.put("defaultCoordinateSystem/axisLabelAttributes/fontSize" ,"0.04");
        props.put("defaultCoordinateSystem/axisLabelAttributes/fontStyle" ,"bold");
        props.put("plot/coordinateSystem/xAxis/" ,"defaultCoordinateSystem/");
        props.put("plot/coordinateSystem/yAxis/" ,"defaultCoordinateSystem/");
        props.put("plot/initialHintForNextCurve/className" ,"jcckit.plot.PositionHint");
        props.put("plot/initialHintForNextCurve/origin" ,"0.16 0.1");
        
        
            //x axis settings
        props.put("plot/coordinateSystem/xAxis/axisLabel" ,this.xLabel); 
        props.put("plot/coordinateSystem/xAxis/gridAttributes/lineColor" ,"0xdcdcdc");
        props.put("plot/coordinateSystem/xAxis/grid" , Boolean.toString(this.grid));
        props.put("plot/coordinateSystem/xAxis/automaticTicCalculation" ,"false");
        props.put("plot/coordinateSystem/xAxis/numberOfTics" ,"10");
        props.put("plot/coordinateSystem/xAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/fontSize" ,"0.035");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/textColor" ,"0xaa");
        props.put("plot/coordinateSystem/xAxis/minimum" ,Double.toString(xMin));
        props.put("plot/coordinateSystem/xAxis/maximum" ,Double.toString(xMax));
        
            //y axis settings        
        props.put("plot/coordinateSystem/yAxis/gridAttributes/lineColor" ,"0xdcdcdc");        
        props.put("plot/coordinateSystem/yAxis/grid" , Boolean.toString(this.grid));
        
        props.put("plot/coordinateSystem/yAxis/axisLabel" ,this.yLabel); 
        props.put("plot/coordinateSystem/yAxis/axisLabelPosition" ,"0.85 0.1");       
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/fontSize" ,"0.035");
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/textColor" ,"0xee");
         props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/yAxis/ticLength" ,"0.006");props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/horizontalAnchor" ,"right");
        props.put("plot/coordinateSystem/yAxis/automaticTicCalculation" ,"false");        
        props.put("plot/coordinateSystem/yAxis/minimumTic",Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximumTic",Double.toString(yMax));        
        props.put("plot/coordinateSystem/yAxis/numberOfTics",Integer.toString(gridStep));
        props.put("plot/coordinateSystem/yAxis/minimum" ,Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximum" ,Double.toString(yMax));
        if (this.yMap!=null){
            if (this.yMap.length > 0){
                String map="";
                for (int i=0; i < this.yMap.length; i++){
                    map=map + Integer.toString(i) + "=";                    
                    
                    if (this.yMap[i].length()>13){
                        map=map+ this.yMap[i].substring(0, 13)+".";
                    }else{
                        map=map + this.yMap[i];
                    }
                    map=map + "; ";
                }
                props.put("plot/coordinateSystem/yAxis/ticLabelFormat/className",
                "jcckit.plot.TicLabelMap");
                props.put("plot/coordinateSystem/yAxis/ticLabelFormat/map",map);
            }
        }
        
        
            //appliing curves definitions and colors
        param = "";
        for (int i = 1; i <= 13; i++){
            param= param + "defc" + Integer.toString(i)+" "
                    + "defb" + Integer.toString(i)+" "
                    + "defa" + Integer.toString(i)+" ";
        }
        props.put("plot/curveFactory/definitions" ,param);
        for (int i = 1; i <= 13; i++){
              props.put("plot/curveFactory/defc" + Integer.toString(i)+"/","defaultDefinition/");
              props.put("plot/curveFactory/defc" + Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.ErrorBarFactory");
              //props.put("plot/curveFactory/defc"+ Integer.toString(i)+"/lineAttributes/lineColor" ,colors[i-1]);
               props.put("plot/curveFactory/defc" + Integer.toString(i)+"/withLine", "false");
               
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/","defaultDefinition/");
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.ErrorBarFactory");
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/symbolFactory/attributes/className" ,"jcckit.graphic.ShapeAttributes");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/attributes/fillColor" ,
                      colors[i-1]);
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/attributes/lineColor" ,"0");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/size" ,"0.01");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/withLine" ,"false");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/softClipping" ,"false");
              //vertical Lines settings
              props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.CircleSymbolFactory");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/size" ,"0.010");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/className" ,"jcckit.graphic.BasicGraphicAttributes");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/lineColor" ,colors[i-1]);
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/lineThickness" ,"0.002");   
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/className" ,"jcckit.graphic.ShapeAttributes");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/linePattern" ,"0.01 0.005");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/lineThickness" ,"0.005");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/lineColor" ,colors[i-1]);
          }
        
        //Legend section
        props.put("plot/legend/upperRightCorner","0.6 0.65");
        props.put("plot/legend/boxAttributes/fillColor" ,"0xeeeeee");
        props.put("plot/legend/lineLength" ,"0.035");
        props.put("plot/legendVisible", Boolean.toString(this.legend));
        props.put("plot/legend/title",this.legendCaption);                
        //props.put("plot/legend/boxHeight", "0.13");
        double boxHeight=0.032*numCurves+0.030;
        props.put("plot/legend/boxHeight", String.valueOf(boxHeight));
        props.put("plot/legend/boxWidth","0.42");
        
        return new GraphicsPlotCanvas(config);
    }
    
    public void doBarsGraph (DataPlot _dp,double _data [] [] [] [], String _capt[]){
        
        int totSeries = _data.length; // total series of data = num of curves
                
        for (int curve=0; curve < totSeries; curve++){
            int med=0;
            int bar=1;
            int line=2;
            for (int gt=med; gt<=line; gt++){
                int numPoints = _data[curve][gt][0].length;
                //DataCurve curve = new DataCurve("curve" + Integer.toString(curves));
                String caption="";
                if (gt==line) {caption=_capt[curve];}
                DataCurve curveDef = new DataCurve(caption);
                for (int i = 0; i < numPoints; i++) {
                    int x=0;    //x axis data serie for this curve
                    int y=1;    //y axis data serie for this curve
                    curveDef.addElement(new DataPoint(_data[curve][gt][x][i],_data[curve][gt][y][i]));
                }
                _dp.addElement(curveDef);
            } 
         }  
    }
    
    
    public void doLinesGraph(DataPlot _dp, double _data [] [] [], String _capt[]) {
        /* //debug point
        System.out.println();
        System.out.println(_data.length);
        System.out.println(_data[0].length);
        System.out.println(_data[0][0].length);
        System.out.println(_data.length);
        System.out.println(_data[0].length);
        System.out.println(_data[0][1].length);*/
        
        int totSeries = _data.length; // total series of data = num of curves
                
        for (int curve=0; curve < totSeries; curve++){
            int numPoints = _data[curve][0].length;
            //DataCurve curve = new DataCurve("curve" + Integer.toString(curves));
            DataCurve curveDef = new DataCurve(_capt[curve]);
            for (int i = 0; i < numPoints; i++) {
                int x=0;    //x axis data serie for this curve
                int y=1;    //y axis data serie for this curve
                if (!(_data[curve][x][i]==0 && _data[curve][y][i]==0)){
                    curveDef.addElement(new DataPoint(_data[curve][x][i],_data[curve][y][i]));
                }                
            }
            _dp.addElement(curveDef);
        }
            
    }
    
    /**
     * This routine has been cloned from compatibility chart to get solution chart
     * It has only some adjustements to better view the graph
     * 
     * @param numCurves number of curves to plot
     * @return  a PlotCanvas with graphs
     */
    public GraphicsPlotCanvas createSolBarsPlotCanvas (int numCurves) {
        Properties props = new Properties();
        ConfigParameters config = new ConfigParameters(new PropertiesBasedConfigData(props));
        int maxCurves=6;
        if (numCurves > maxCurves) {
            System.out.println("too much curves ("+ Integer.toString(numCurves) +  
                    ") to plot: not so much colors");
        }        
        //preparing colors for up to 7 curves
        String [] colors = new String [] {"0xff0000","0x00ff00","0x0000ff","0xff00ff",
            "0x638DFF","0x000000","0x00ffff"};
        //preparing curves definition
        String param = "";        
        for (int i = 1; i <= maxCurves; i++){            
            param= param + "curve" + Integer.toString(i)+" ";
            param= param + "bar" + Integer.toString(i)+" ";
        }
        props.put("data/curves" ,param);
        for (int i = 1; i <= maxCurves; i++){
            props.put("data/curve" + Integer.toString(i)+"/title","curve "+ Integer.toString(i));
            props.put("data/bar" + Integer.toString(i)+"/title","bar "+ Integer.toString(i));
        }        
        
        props.put("paper" ,"0 0 1.5 0.6");
        props.put("background" ,"0xffffff");    //white background
        props.put("defaultCoordinateSystem/ticLabelAttributes/fontSize" ,"0.03");
        props.put("defaultCoordinateSystem/axisLabelAttributes/fontSize" ,"0.04");
        props.put("defaultCoordinateSystem/axisLabelAttributes/fontStyle" ,"bold");
        props.put("plot/coordinateSystem/xAxis/" ,"defaultCoordinateSystem/");
        props.put("plot/coordinateSystem/yAxis/" ,"defaultCoordinateSystem/");
        props.put("plot/initialHintForNextCurve/className" ,"jcckit.plot.PositionHint");
        props.put("plot/initialHintForNextCurve/origin" ,"0.1 0.1");
        
        
            //x axis settings
        props.put("plot/coordinateSystem/xAxis/axisLabel" ,this.xLabel); 
        props.put("plot/coordinateSystem/xAxis/gridAttributes/lineColor" ,"0xdcdcdc");
        props.put("plot/coordinateSystem/xAxis/grid" , Boolean.toString(this.grid));
        props.put("plot/coordinateSystem/xAxis/automaticTicCalculation" ,"false");
        props.put("plot/coordinateSystem/xAxis/numberOfTics" ,"10");
        props.put("plot/coordinateSystem/xAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/fontSize" ,"0.035");
        props.put("plot/coordinateSystem/xAxis/axisLabelAttributes/textColor" ,"0xaa");
        props.put("plot/coordinateSystem/xAxis/minimum" ,Double.toString(xMin));
        props.put("plot/coordinateSystem/xAxis/maximum" ,Double.toString(xMax));
        
            //y axis settings        
        props.put("plot/coordinateSystem/yAxis/gridAttributes/lineColor" ,"0xdcdcdc");        
        props.put("plot/coordinateSystem/yAxis/grid" , Boolean.toString(this.grid));
        
        props.put("plot/coordinateSystem/yAxis/axisLabel" ,this.yLabel); 
        props.put("plot/coordinateSystem/yAxis/axisLabelPosition" ,"0.85 0.1");       
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/fontSize" ,"0.035");
        props.put("plot/coordinateSystem/yAxis/axisLabelAttributes/textColor" ,"0xee");
         props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/fontSize" ,"0.02");
        props.put("plot/coordinateSystem/yAxis/ticLength" ,"0.006");props.put("plot/coordinateSystem/yAxis/ticLabelAttributes/horizontalAnchor" ,"right");
        props.put("plot/coordinateSystem/yAxis/automaticTicCalculation" ,"false");        
        props.put("plot/coordinateSystem/yAxis/minimumTic",Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximumTic",Double.toString(yMax));        
        props.put("plot/coordinateSystem/yAxis/numberOfTics",Integer.toString(gridStep));
        props.put("plot/coordinateSystem/yAxis/minimum" ,Double.toString(yMin));
        props.put("plot/coordinateSystem/yAxis/maximum" ,Double.toString(yMax));
        if (this.yMap!=null){
            if (this.yMap.length > 0){
                String map="";
                for (int i=0; i < this.yMap.length; i++){
                    map=map + Integer.toString(i) + "=";                    
                    
                    if (this.yMap[i].length()>maxCurves){
                        map=map+ this.yMap[i].substring(0, maxCurves)+".";
                    }else{
                        map=map + this.yMap[i];
                    }
                    map=map + "; ";
                }
                props.put("plot/coordinateSystem/yAxis/ticLabelFormat/className",
                "jcckit.plot.TicLabelMap");
                props.put("plot/coordinateSystem/yAxis/ticLabelFormat/map",map);
            }
        }
        
        
            //appliing curves definitions and colors
        param = "";
        for (int i = 1; i <= maxCurves; i++){
            param= param + "defc" + Integer.toString(i)+" "
                    + "defb" + Integer.toString(i)+" "
                    + "defa" + Integer.toString(i)+" ";
        }
        props.put("plot/curveFactory/definitions" ,param);
        for (int i = 1; i <= maxCurves; i++){
              props.put("plot/curveFactory/defc" + Integer.toString(i)+"/","defaultDefinition/");
              props.put("plot/curveFactory/defc" + Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.ErrorBarFactory");
              //props.put("plot/curveFactory/defc"+ Integer.toString(i)+"/lineAttributes/lineColor" ,colors[i-1]);
               props.put("plot/curveFactory/defc" + Integer.toString(i)+"/withLine", "false");
               
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/","defaultDefinition/");
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.ErrorBarFactory");
              props.put("plot/curveFactory/defb" + Integer.toString(i)+"/symbolFactory/attributes/className" ,"jcckit.graphic.ShapeAttributes");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/attributes/fillColor" ,
                      colors[i-1]);
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/attributes/lineColor" ,"0");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/symbolFactory/size" ,"0.01");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/withLine" ,"false");
              props.put("plot/curveFactory/defb"+ Integer.toString(i)+"/softClipping" ,"false");
              //vertical Lines settings
              props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/className" ,"jcckit.plot.CircleSymbolFactory");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/size" ,"0.010");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/className" ,"jcckit.graphic.BasicGraphicAttributes");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/lineColor" ,colors[i-1]);
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/symbolFactory/attributes/lineThickness" ,"0.002");   
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/className" ,"jcckit.graphic.ShapeAttributes");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/linePattern" ,"0.01 0.005");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/lineThickness" ,"0.008");
            props.put("plot/curveFactory/defa"+ Integer.toString(i)+"/lineAttributes/lineColor" ,colors[i-1]);
            
          }
        
        //Legend section
        props.put("plot/legend/upperRightCorner","1.35 0.55");
        props.put("plot/legend/boxAttributes/fillColor" ,"0xeeeeee");
        props.put("plot/legend/lineLength" ,"0.025");
        props.put("plot/legendVisible", Boolean.toString(this.legend));
        props.put("plot/legend/title",this.legendCaption);                
        //props.put("plot/legend/boxHeight", "0.13");
        double boxHeight=0.032*numCurves+0.070;
        props.put("plot/legend/boxHeight", String.valueOf(boxHeight));
        props.put("plot/legend/boxWidth","0.32");
        
        return new GraphicsPlotCanvas(config);
    }
    
}

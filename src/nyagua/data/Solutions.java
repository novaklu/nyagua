/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nyagua.data;

import java.util.HashMap;

/**
 *
 * @author rgi
 */
public class Solutions {
    
    //elements constants
    public static final int NO3=0;
    public static final int PO4=1;
    public static final int K=2;
    public static final int Ca=3;
    public static final int Mg=4;
    public static final int Fe=5;
    public static final int Mn=6;
    public static final int B=7;
    public static final int Cu=8;
    public static final int Mo=9;
    public static final int Cl=10;
    public static final int S=11;
    public static final int Zn=12;
    public static final int gH=13;
    public static final int Solubility=14;  
    public static final int Target=15;
    
    // Methods
    public static final int METHOD_TARGET=0;
    public static final int METHOD_RESULT=1;
    public static final int METHOD_EI=2;
    public static final int METHOD_EID=3;
    public static final int METHOD_EIW=4;
    public static final int METHOD_PPS=5;
    public static final int METHOD_PMDD=6;
    public static final int METHOD_WALSTAD=1;//used in graphs
    public static final int METHOD_ADA=7; 
    
    /*
    # nutrient 
    # method (EI = Tom Barr's Estimative Index, PPS = Edward's Perpetual Preservation System, Walstad = Diana Walstad's Natural Aquariums, PMDD = Poor Man's Dupla Drops)
    # method: recommended ppm by author
    # low and high ppm (or +/- 20% if author uses exact value) for given nutrient
    # margin: high - low*/
    
    //For each method and for each element a table of values is needed
    //Constant for values column:
    public static final int PAR_METHOD=0;  //Method Target
    public static final int PAR_LOW=1;     //Lowest value
    public static final int PAR_HIGH=2;     //Highest value
    public static final int PAR_MARGIN=3;   //margin=High-Low
    
   
    
    HashMap solute = new HashMap();
    HashMap soluteElementsValues= new HashMap();
    HashMap soluteElementsLabels= new HashMap();
    HashMap soluteElementsName= new HashMap();
    
    public Solutions() {
        createSolutionsMap();
    }
    
    public String[] getElements() {
        return ELEMENTS;
    }
    
    public String[] getMethods() {
        return METHODS;
    }
    
    public double [][][] getMethodParameters() {
        return METHOD_PARAMETERS;
    }
    
    public String [] getShortMethodsCaptions() {
        return SHORT_METHODS_CAPTIONS;
    }
    
    public HashMap getSolute() {
        return solute;
    }
    
    public HashMap getSoluteElementsValues() {
        return soluteElementsValues;
    }
    
    public HashMap getSoluteElementsLabels() {
        return soluteElementsLabels;
    }
    
    public HashMap getSoluteElementsName() {
        return soluteElementsName;
    }
    
    /**
      * Calculate the amount of each element that will be dosed at selected conditions
      * 
      * @param compound the introduced compound
      * @param x    solute in mg/l = amount of powder (or liquid) to put in water to get the solution
      * @param AV   Aquarium Water total volume in liters
      * @param SV   Solution Water volume in ml
      * @param DV   Dose Volume (the minimal dose used to reach targ in ml
      * @return     An array with the amount for each element
      */
     public double [] calcElements(String compound, double x, double AV, 
             double SV, double DV){
         /*for each element the formula is:
         * x=  solute in mg/l = amount of powder (or liquid) to put in water to get the solution
          * targ = target quantity in ppm or mg/l for the element to reach in tank
          * K=  value of target lement contained in a unit of the initial compount
          * AV= Aquarium Water total volume in liters
          * SV= Solution Water volume in ml
          * DV= Dose Volume (the minimal dose used to reach targ in ml
          * 
          * x * K * {1/ [AV * (SV/DV)]} =targ 
          */ 
         double [] elementsDosed=new double [14];
         double [] values = (double[]) solute.get(compound);
         for (int y=0; y<14;y++){
             double k=values[y];
             elementsDosed[y]= x*k*(1 / (AV * (SV/DV)));
             
         }
         
         return elementsDosed;
     }
     
     /**
     * Calc the amount of compound to be dissolved in water to obtain a solution 
     * that will bring the target element to the desired value
     * 
     * All required fields need to be non zero values checked before calling function
     * 
     * @param compound
     * @param targ
     * @param AV
     * @param SV
     * @param DV
     * 
     * @return  amount of compound to put in water 
     */public double calcSolute(String compound, double targ, double AV, double SV,
             double DV){
        /*used formula is:
          * x=  solute in mg/l = amount of powder (or liquid) to put in water to get the solution
          * targ = target quantity in ppm or mg/l for the element to reach in tank
          * K=  value of target lement contained in a unit of the initial compount
          * AV= Aquarium Water total volume in liters
          * SV= Solution Water volume in ml
          * DV= Dose Volume (the minimal dose used to reach targ in ml
          * 
          * x=targ * (1 / k) * AV * (SV/DV)
          */
         double [] values=(double[]) solute.get(compound);         
         //calc solute
         int targetElement=(int) values[Target];
         double k=values[targetElement];
         double x=targ * (1 / k) * AV * (SV/DV); //x=
            
         return x; 
    } 
     
     /**
      * Add a warning for toxic Cu concentration
      * 
      * @param cuDose   Amount of Cu in compound
      * @return hint     A suggestion or warning message
      */
     public String checkCu(double cuDose){
         String hint="";
         if (cuDose > 0.072){
             int toxic=(int) ((cuDose/0.072)*100);
             
              hint=hint +  "<html>";//NOI18N
             hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.cu")+"<br>";//NOI18N
             hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.cu2");//NOI18N
             hint=hint + " " + String.valueOf(toxic) + "% </html>";//NOI18N
             
         }
         return hint;
     }
     
     /**
      * Add a warning for EDDHA FE
     * @return hint     A suggestion or warning message
      */
     public String checkK3PO4(){
        String hint="<html>";//NOI18N
        hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.K3PO4");//NOI18N
        hint=hint + " </html>";//NOI18N
        return hint;
     }
     
     /**
      * Add a warning for EDDHA FE
     * @return hint     A suggestion or warning message
      */
     public String checkEDDHA(){
        String hint="<html>";//NOI18N
        hint=hint + java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.hints.EDDHA");//NOI18N
        hint=hint + " </html>";//NOI18N
        return hint;
     }
     
     
     /** 
     * Creates an HashMap of elements amount for each solution   
     */ 
    private void createSolutionsMap(){
        //HashMap solute = new HashMap();
        solute.put("(CaSO4)2.H2O",new double [] {0,0,0,0.2761079352,0,0,0,0,0,0,0,0,0,0,2.4,Ca});//NOI18N
        solute.put("Ca(NO3)2.4(H2O)",new double [] {0.5251123221,0,0,0.1697000101,0,0,0,0,0,0,0,0,0,0,121,Ca});//NOI18N
        solute.put("CaCl2",new double [] {0,0,0,0.3611282838,0,0,0,0,0,0,0,0,0,0,750,Ca});//NOI18N
        solute.put("CaCl2.2H2O",new double [] {0,0,0,0.2725875023,0,0,0,0,0,0,0.48232,0,0,0.03808,750,Ca});//NOI18N
        solute.put("CaCl2.6H2O",new double [] {0,0,0,0.1829489513,0,0,0,0,0,0,0,0,0,0,750,Ca});//NOI18N
        solute.put("CaCO3",new double [] {0,0,0,0.400383004,0,0,0,0,0,0,0,0,0,0,0.007,Ca});//NOI18N
        solute.put("CaMg(CO3)2",new double [] {0,0,0,0.2172826362,0.1318080382,0,0,0,0,0,0,0,0,0,1000000000,Ca});//NOI18N
        solute.put("CSM+B",new double [] {0,0,0,0,0.0140060236,0.0652983532,0.0187,0.008,0.0009,0.0005,0,0,0.0037,0,1000000000,Fe});//NOI18N
        solute.put("Fleet Enema",new double [] {0,0.1313537888,0,0,0,0,0,0,0,0,0,0,0,0,1000000000,PO4});//NOI18N
        solute.put("Iron (Fe) 07% DPTA",new double [] {0,0,0,0,0,0.07,0,0,0,0,0,0,0,0,1000000000,Fe});//NOI18N
        solute.put("Iron (Fe) 10% DPTA",new double [] {0,0,0,0,0,0.1000105792,0,0,0,0,0,0,0,0,200,Fe});//NOI18N
        solute.put("Iron (Fe) 13% EDTA",new double [] {0,0,0,0,0,0.130013753,0,0,0,0,0,0,0,0,90,Fe});//NOI18N
        solute.put("Iron (Fe) Gluconate",new double [] {0,0,0,0,0,0.123404424,0,0,0,0,0,0,0,0,1000000000,Fe});//NOI18N
        solute.put("K2CO3",new double [] {0,0,0.5658054988,0,0,0,0,0,0,0,0,0,0,0,1120,K});//NOI18N
        solute.put("K2HPO4",new double [] {0,0.5452507128,0.4489498371,0,0,0,0,0,0,0,0,0,0,0,1492,PO4});//NOI18N
        solute.put("K2SO4",new double [] {0,0,0.4486848583,0,0,0,0,0,0,0,0,0.18401,0,0,120,K});//NOI18N
        solute.put("K3PO4",new double [] {0,0.447416,0.552596,0,0,0,0,0,0,0,0,0,0,0,900,PO4});//NOI18N
        solute.put("KCl",new double [] {0,0,0.5244688021,0,0,0,0,0,0,0,0.18401,0,0,0,344,K});//NOI18N
        solute.put("KH2PO4",new double [] {0,0.6979163699,0.2873127541,0,0,0,0,0,0,0,0,0,0,0,220,PO4});//NOI18N
        solute.put("KNO3",new double [] {0.6133124166,0,0.3867176674,0,0,0,0,0,0,0,0,0,0,0,360,NO3});//NOI18N
        solute.put("MgCO3",new double [] {0,0,0,0,0.288259107,0,0,0,0,0,0,0,0,0,0.4,Mg});//NOI18N
        solute.put("MgSO4.7H2O",new double [] {0,0,0,0,0.0986099769,0,0,0,0,0,0,0.1301,0,0.02255,710,Mg});//NOI18N
        solute.put("MnSO4",new double [] {0,0,0,0,0,0,0.3250597919,0,0,0,0,0.2123262543,0,0,393,Mn});//NOI18N
        solute.put("Na2HPO4",new double [] {0,0.6689958239,0,0,0,0,0,0,0,0,0,0,0,0,1000000000,PO4});//NOI18N
        solute.put("NaH2PO3",new double [] {0,0.7915674573,0,0,0,0,0,0,0,0,0,0,0,0,1000000000,PO4});//NOI18N
        
        soluteElementsName .put("(CaSO4)2.H2O","Calcium Sulfate Dihydrate");//NOI18N
        soluteElementsName.put("Ca(NO3)2.4(H2O)","Calcium Nitrate Tetrahydrate");//NOI18N
        soluteElementsName.put("CaCl2","Calcium Chloride");//NOI18N
        soluteElementsName.put("CaCl2.2H2O","Calcium Chloride Dihydrate");//NOI18N
        soluteElementsName.put("CaCl2.6H2O","Calcium Chloride Hexahydrate");//NOI18N
        soluteElementsName.put("CaCO3","Calcium Carbonate");//NOI18N
        soluteElementsName.put("CaMg(CO3)2","Calcium Magnesium Dicarbonate");//NOI18N
        soluteElementsName.put("CSM+B","Plantex CSM + Boron (micro)");//NOI18N
        soluteElementsName.put("Fleet Enema","Sodium Phosphate");//NOI18N
        soluteElementsName.put("Iron (Fe) 07% DPTA","DPTA, ferric disodium complex");//NOI18N
        soluteElementsName.put("Iron (Fe) 10% DPTA","DPTA, ferric sodium salt ");//NOI18N
        soluteElementsName.put("Iron (Fe) 13% EDTA","EDTA, ferric-sodium");//NOI18N
        soluteElementsName.put("Iron (Fe) Gluconate","Iron pentahydroxyhexanoic acid");//NOI18N
        soluteElementsName.put("K2CO3","Potassium Carbonate");//NOI18N
        soluteElementsName.put("K2HPO4","Dipotassium Phosphate");//NOI18N
        soluteElementsName.put("K2SO4","Potassium Sulfate");//NOI18N
        soluteElementsName.put("K3PO4","Tripotassium Phosphate");//NOI18N
        soluteElementsName.put("KCl","Potassium Chloride");//NOI18N
        soluteElementsName.put("KH2PO4","Potassium phosphate monobasic");//NOI18N
        soluteElementsName.put("KNO3","Potassium Nitrate");//NOI18N
        soluteElementsName.put("MgCO3","Magnesium Carbonate");//NOI18N
        soluteElementsName.put("MgSO4.7H2O","Magnesium Sulfate Heptahydrate");//NOI18N
        soluteElementsName.put("MnSO4","Manganese Sulfate");//NOI18N
        soluteElementsName.put("Na2HPO4","Disodium Hydrogen Phosphate");//NOI18N
        soluteElementsName.put("NaH2PO3","Sodium Dihydrogen Phosphite");//NOI18N
        
        
        //"NO3","PO4","K","Ca","Mg","Fe","Mn","B","Cu","Mo","Cl","S","Zn","gH"
        //
        soluteElementsLabels.put("",new String [] {""});//NOI18N
        soluteElementsValues.put("(CaSO4)2.H2O",new double [] {0.6944,49.6023,27.6118,22.0914});//NOI18N
        soluteElementsLabels.put("(CaSO4)2.H2O",new String []{"H","O","Ca","S"});//NOI18N
        soluteElementsValues.put("Ca(NO3)2.4(H2O)",new double [] {16.9716, 3.4146, 67.7512, 11.8626});//NOI18N
        soluteElementsLabels.put("Ca(NO3)2.4(H2O)",new String [] {"Ca","H","O","N"});//NOI18N
        soluteElementsValues.put("CaCl2",new double [] { 36.1116, 63.8884});//NOI18N
        soluteElementsLabels.put("CaCl2",new String [] {"Ca","Cl"});//NOI18N      soluteElementsLabels.put("",new String [] {""});//NOI18N
        soluteElementsValues.put("CaCl2.2H2O",new double [] {27.2614, 48.2306, 2.7424, 21.7656});//NOI18N
        soluteElementsLabels.put("CaCl2.2H2O",new String [] {"Ca","Cl","H","O"});//NOI18N
        soluteElementsValues.put("CaCl2.6H2O",new double [] {18.2942, 32.3660, 5.5211, 43.8187});//NOI18N
        soluteElementsLabels.put("CaCl2.6H2O",new String [] {"Ca","Cl","H","O"});//NOI18N
        soluteElementsValues.put("CaCO3",new double [] {40.0434, 12.0003, 47.9563});//NOI18N
        soluteElementsLabels.put("CaCO3",new String [] {"Ca","C","O"});//NOI18N
        soluteElementsValues.put("CaMg(CO3)2",new double [] {21.7343, 13.1805, 13.0268, 52.0584});//NOI18N
        soluteElementsLabels.put("CaMg(CO3)2",new String [] {"Ca", "Mg","C","O"});//NOI18N
        soluteElementsValues.put("CSM+B",new double [] {0.277, 0.79, 33.3,8.74, 5.2,4.3 });//NOI18N
        soluteElementsLabels.put("CSM+B",new String [] {"B","Cu","Fe","Mn","Mo","Zn"});//NOI18N 
        soluteElementsValues.put("Fleet Enema",new double [] {25.0238, 14.1924, 19.8239, 40.9599});//NOI18N
        soluteElementsLabels.put("Fleet Enema",new String [] {"K", "H", "P", "O"});//NOI18N
        soluteElementsValues.put("Iron (Fe) 07% DPTA",new double [] {34.3072, 3.7017, 8.5732,  32.6430, 11.3939, 9.3810});//NOI18N
        soluteElementsLabels.put("Iron (Fe) 07% DPTA",new String [] {"C","H","N","O","Fe","Na"});//NOI18N
        soluteElementsValues.put("Iron (Fe) 10% DPTA",new double [] {35.9956, 3.8838, 8.9951, 34.2495, 11.9546, 4.9214});//NOI18N
        soluteElementsLabels.put("Iron (Fe) 10% DPTA",new String [] {"C","H","N","O","Fe","Na"});//NOI18N
        soluteElementsValues.put("Iron (Fe) 13% EDTA",new double [] {32.7227, 3.2953, 7.6321, 34.8717, 15.2147, 6.2634});//NOI18N
        soluteElementsLabels.put("Iron (Fe) 13% EDTA",new String [] {"C","H","N","O","Fe","Na"});//NOI18N         
        soluteElementsValues.put("Iron (Fe) Gluconate",new double [] {32.1604, 5.3978, 12.4611, 49.9807});//NOI18N
        soluteElementsLabels.put("Iron (Fe) Gluconate",new String [] {"C","H","Fe","O"});//NOI18N
        soluteElementsValues.put("K2CO3",new double [] {56.5799, 8.6905, 34.7296 });//NOI18N
        soluteElementsLabels.put("K2CO3",new String [] {"K", "C", "O"});//NOI18N
        soluteElementsValues.put("K2HPO4",new double [] {44.8952, 0.5787, 17.7830, 36.7431});//NOI18N
        soluteElementsLabels.put("K2HPO4",new String [] {"K", "H", "P", "O"});//NOI18N
        soluteElementsValues.put("K2SO4",new double [] {44.8736, 18.4010, 36.7255});//NOI18N
        soluteElementsLabels.put("K2SO4",new String [] {"K", "S", "O"});//NOI18N
        soluteElementsValues.put("K3PO4",new double [] {55.2584 , 14.5919 , 30.1497});//NOI18N
        soluteElementsLabels.put("K3PO4",new String [] {"K", "P", "O"});//NOI18N
        soluteElementsValues.put("KCl",new double [] {52.4447, 47.5553});//NOI18N
        soluteElementsLabels.put("KCl",new String [] {"K", "Cl"});//NOI18N
        soluteElementsValues.put("KH2PO4",new double [] {28.7307, 1.4813, 22.7605, 47.0275});//NOI18N
        soluteElementsLabels.put("KH2PO4",new String [] {"K", "H", "P", "O"});//NOI18N
        soluteElementsValues.put("KNO3",new double [] {38.6716, 13.8539, 47.4745});//NOI18N
        soluteElementsLabels.put("KNO3",new String [] {"K", "N", "O"});//NOI18N
        soluteElementsValues.put("MgCO3",new double [] {28.8268, 14.2453, 56.9279});//NOI18N
        soluteElementsLabels.put("MgCO3",new String [] {"Mg","C","O"});//NOI18N
        soluteElementsValues.put("MgSO4.7H2O",new double [] {9.8610,13.0096 , 71.4041,5.7252 });//NOI18N
        soluteElementsLabels.put("MgSO4.7H2O",new String [] {"Mg","S","O","H"});//NOI18N
        soluteElementsValues.put("MnSO4",new double [] {36.3825, 21.2353, 42.3822});//NOI18N
        soluteElementsLabels.put("MnSO4",new String [] {"Mn","S","O"});//NOI18N
        soluteElementsValues.put("Na2HPO4",new double [] {32.389, 0.7100, 21.8188, 45.0818});//NOI18N
        soluteElementsLabels.put("Na2HPO4",new String [] {"Na","H","P","O"});//NOI18N
        soluteElementsValues.put("NaH2PO3",new double [] {22.1103,1.9388, 29.7888, 46.1621 });//NOI18N
        soluteElementsLabels.put("NaH2PO3",new String [] {"Na","H","P","O"});//NOI18N
        
        
    }
        
    //Elements names constants array   
    private static final String[] ELEMENTS =new String [] {
        "NO3","PO4","K","Ca","Mg","Fe","Mn","B","Cu","Mo","Cl","S","Zn","gH"};//NOI18N
    
   //Types of operations we can do with this form
    private static final String[] METHODS=new String [] {
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.1.target"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.2.result"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.3.ei"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.4.eid"),//NOI18N            
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.5.eiw"),//NOI18N 
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.6.pps"),//NOI18N 
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.7.pmdd"),//NOI18N
        java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.8.ada")//NOI18N
    };
    private static final String [] SHORT_METHODS_CAPTIONS = new String []{
         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.short.2.walstad"),//NOI18N
         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.short.3.EI"),//NOI18N
         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.short.4.PPS"),//NOI18N
         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.short.5.PMDD"),//NOI18N
         java.util.ResourceBundle.getBundle("nyagua/Bundle").getString("solutions.method.short.6.ADA") 
    };
    
     /* method parameter array is a double with following dimensions:
     * [Element: NO3=0 to Mn=6]    - Elements over Mn are not in graphs by now
     *   [Method: EI=2 to  PMDD=6] - Wet and Walstad not used by now
     *      [value: Target=0 Low=1, High=2, Margin=3]   
      */
    private static final double [][][] METHOD_PARAMETERS=new double [][][] {
        {  //NO3: 
            {3,1,30,29},//Wet:
            {0,0.443,0.553,0.11},//Walstad:
            {7.5 , 5 , 30, 25},//EI:
            {3.2,5 , 30, 25},//  EI_daily:
            {10,5 , 20, 20},//  EI_low:   
            {1,5,10,9}, //PPS:            
            {1.4,1,5,4},//PMDD:
            {7 , 6 , 24, 18},//ADA: 
        },
        {//PO4:
            {1,1,5,4},//  Wet:
            {0,0.061,0.073,0.012},//Walstad:
            {1.3,1,3,2},//  EI:
            {0.6,1,3,2},//  EI_daily:
            {1,1,2,1},// EI_low:
            {0.1,0.1,1,0.9},//  PPS:            
            {0,0,0,0},//  PMDD:
            {1.2,1.4,5,3.6},//  ADA:    
        },
        {//K:
            {3, 1, 20, 19},//  Wet:
            {0, 2, 0.4, 2.4},//  Walstad:
            {7.5, 10, 30, 20}, //EI:
            {3.2, 10, 30, 20},//  EI_daily:
            {10, 3, 20, 17},//  EI_low:
            {1.33, 1,20, 15},//PPS:            
            {3, 2.4, 3.6, 1.2},//  PMDD: 
            {7, 10, 24, 14}, //ADA  
        },
        {//Ca:
            {2,0,0,0},//  Wet:
            {0, 18, 34, 6},//  Walstad:
            {15, 15, 30, 15},//  EI:
            {6.4, 15, 30, 15},//  EI_daily:
            {15, 15, 25, 10},//  EI_low:   
            {0.4, 20, 30, 10},//PPS:            
            {0,0,0,0},//  PMDD:
            {0, 0, 0, 0},//  ADA:   
        },
        {//Mg:
            {0.5,0,0,0},//  Wet:
            {0, 2, 2.4, 0.4},//Walstad:
            {5,5,10,5},//EI:
            {2, 5,10,5},//  EI_daily:
            {5, 3, 8, 5},//  EI_low:    
            {0.1, 2, 5, 3},//PPS:
            {0.2, 0.16, 0.24, 0.08},//PMDD:   
            {0,0,0,0},//ADA:    
        },
        {// Fe:
            {0.2,0.1,0.5,0.4},//  Wet:
            {0, 0.06, 0.072, 0.012},//Walstad:
            {0.15, 0.1, 1.0, 0.9},//  EI:
            {0.2, 0.1, 1.0, 0.9},//  EI_daily:
            {0.2, 0.08, 0.9, 0.82},//  EI_low:
            {0.1, 0.01, 0.1, 0.09},//PPS:
            {0.1, 0.08, 0.12, 0.04},//PMDD: 
            {0.1, 0.03, 0.6, 0.5},//  ADA:  
        },
        {//Mn:
            {0.2,0.5,0.1,0.4},//  Wet:
            {0, 0.06, 0.072, 0.012},//Walstad:
            {0.1, 0.1, 0.5, 0.4},//  EI:
            {0.04, 0.1, 0.5, 0.4},//  EI_daily:
            {0.1, 0.05, 0.4, 0.35},//  EI_low:            
            {0.1, 0.01, 0.1, 0.09},//PPS:            
            {0.029, 0.023, 0.035, 0.012},//PMDD:  
            {0.1, 0, 0, 0},//  ADA:     
        }
    };
    
}

/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LH_Hum
Created Date     : 03/04/2022
Function         : This is helper class for the controller QuickStart_C_HUM Test
Test Class       : 
** Modification Log
* Developer Name          Code Review #         Date                       Description
* Pooja Kumbhar             -----               03/04/2022                 Original Version
* Disha Dole 									20/01/2023 	   US:4085171 - T1PRJ0865978 - C06, Lightning-Case Management- Quick Start-Associate to Policy, alignment & display issue fixes
*************************************************************************************************************************************************************************************/
public with sharing class QuickStart_LH_Hum {
    static final String SELECTBOX = 'Selectbox';
    static final String CHECKBOX = 'Checkbox';
    static final String NONE = 'None';
    static final String OTHER = 'Other';
    static final String DEFAULT_EMPTY = '';
    static final Integer DEFAULT_LENGTH_SIZE = 3;
    static final Integer DEFAULT_DISPLAY_ORDER = 0;
    static final Integer DEFAULT_DISPLAY_POSITION = 0;
	
	/*
    * Method Name : isHPClassificationIntent
    * Description : This method is to determine if the C&I pair on Quick Start Screen is HP.
    * Return type : Boolean 
    * Paramater   : N/A    
    */
    
    public static boolean isHPClassificationIntent(Id idClassification, Id idIntent, Id idClassificationType)
    {              
         List<CTCI_Junction__c> objCTC = [SELECT Id,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c,isActive__c FROM CTCI_Junction__c WHERE Classification__c = :idClassification AND Intent__c =:idIntent AND Classification_Type__c =:idClassificationType AND isActive__c = true AND Service_Model_Type__c =: HumConstants_LWC.SERVICE_CENTER_HUMANA_PHARMACY];
            
            if (objCTC!=null && !objCTC.isEmpty() ){ 
            return true;
            }else{
                return false;
            }               
            
    }
	
    /*
     * Process pretext config data to json format so that JS file can read it
     * and render components accordingly.
     * 
     * @return map<String,String> where key will be section name(AR,CIC,AT) and
     * value will be json strings coninting all the component details
     * 
     * @Param list of Quick start pretext section record
     */
    public static String parsePreTextData(List<Quick_Start_Pretext_Section__c> listQSSections){
        String jsonMap;
        Map<String, QSSectionWrapper> mapQSPretext = new Map<String, QSSectionWrapper>();
        if(listQSSections.size()>0){
            for(Quick_Start_Pretext_Section__c qs_section : listQSSections) {
                mapQSPretext.put(qs_section.Case_Comment_Section__c, new QSSectionWrapper(qs_section));
            }
            if(!mapQSPretext.isEmpty()) {
                jsonMap = JSON.serialize(mapQSPretext);
            }
        } 
        return jsonMap;
    }

    /**
     * wrapper class to represent each section and its line items records.
     */
    private class QSSectionWrapper {
        String sTransferLabel;
        List<String> listTransferValues = new List<String>();
        List<String> listTransferValuesWithInput = new List<String>();
        List<QSLineItemsWrapper> listLineItem = new List<QSLineItemsWrapper>();

        private QSSectionWrapper(Quick_Start_Pretext_Section__c qs_section){
            this.sTransferLabel = qs_section.Transfer_Label__c != null ? qs_section.Transfer_Label__c : DEFAULT_EMPTY;   
            if(String.isNotBlank(this.sTransferLabel)) {
                String sTransferValues = qs_section.Transfer_Values__c;
                this.listTransferValues.add(NONE);
                if(String.isNotBlank(sTransferValues)) {
                    this.listTransferValues = sTransferValues.split(';');
                    this.listTransferValues.sort();
                    this.listTransferValues.add(0, NONE);
                }
                this.listTransferValues.add(OTHER);
                
                String sTransferValuesWithInput = qs_section.Transfer_Values_with_Input__c;
                if(String.isNotBlank(sTransferValuesWithInput)) {
                    this.listTransferValuesWithInput = sTransferValuesWithInput.split(';');
                }
                this.listTransferValuesWithInput.add(OTHER);    
            }
            
            for(Quick_Start_Pretext_Line_Item__c qs_lineitem : qs_section.Quick_Start_Pretext_Line_Items__r) {
                this.listLineItem.add(new QSLineItemsWrapper(qs_lineitem));
            }
        }
    }

    /**
     * wrapper class to represent each section line items records.
     */
    private class QSLineItemsWrapper {
        String sPreLabel;
        String sInputFieldType;
        String sGhostText;
        Integer sFieldLength;
        Integer sFieldDisplaySize;
        Integer iDisplayOrder;
        Integer iDisplayPosition;
        Boolean bIsRequiredLineItem;
        List<String> listInputFieldLabel = new List<String>();
        Map<String, List<QSLineItemsWrapper>> mapAdditionalLineitem = new Map<String, List<QSLineItemsWrapper>>();

        private QSLineItemsWrapper(Quick_Start_Pretext_Line_Item__c qs_lineitem){
            this.sPreLabel = qs_lineitem.Pre_Label__c != null ? qs_lineitem.Pre_Label__c : DEFAULT_EMPTY;
            this.sInputFieldType = qs_lineitem.Input_Field_Type__c != null ? qs_lineitem.Input_Field_Type__c : DEFAULT_EMPTY;
            this.sGhostText = qs_lineitem.Ghost_Text__c != null ? qs_lineitem.Ghost_Text__c : DEFAULT_EMPTY;
            this.sFieldLength = qs_lineitem.Field_Length__c != null ? qs_lineitem.Field_Length__c.intValue() : DEFAULT_LENGTH_SIZE;
            this.sFieldDisplaySize = qs_lineitem.Field_Display_Size__c != null ? qs_lineitem.Field_Display_Size__c.intValue() : DEFAULT_LENGTH_SIZE;
            this.iDisplayOrder = qs_lineitem.Display_Order__c != null ? qs_lineitem.Display_Order__c.intValue() : DEFAULT_DISPLAY_ORDER;
            this.iDisplayPosition = qs_lineitem.Display_Position__c != null ? qs_lineitem.Display_Position__c.intValue() : DEFAULT_DISPLAY_POSITION;
            this.bIsRequiredLineItem = qs_lineitem.Is_Required_Line_Item__c;
            String sLabels = qs_lineitem.Input_Field_Labels__c;
            if(String.isNotBlank(sLabels)) {
                this.listInputFieldLabel = sLabels.split(';');
            }
            
            if(this.sInputFieldType == SELECTBOX || this.sInputFieldType == CHECKBOX) {
                List<QSLineItemsWrapper> listAdditionalLineitem;
                for(Quick_Start_Pretext_Line_Item__c ad_lineitem : [Select Pre_Label__c, Input_Field_Type__c, Input_Field_Labels__c, Ghost_Text__c, Field_Length__c, Field_Display_Size__c, Is_Required_Line_Item__c, Dependent_Input_Value__c, Display_Order__c, Display_Position__c from Quick_Start_Pretext_Line_Item__c 
                                                                                    where Additional_Line_Item__c =:qs_lineitem.Id ORDER BY Dependent_Input_Value__c, Display_Order__c, Display_Position__c asc]) {
                    String sDependentInputValue = ad_lineitem.Dependent_Input_Value__c != null ? ad_lineitem.Dependent_Input_Value__c : DEFAULT_EMPTY;
                    
                    if(this.mapAdditionalLineitem.get(sDependentInputValue) == null) {
                        listAdditionalLineitem = new List<QSLineItemsWrapper>();
                        listAdditionalLineitem.add(new QSLineItemsWrapper(ad_lineitem));
                        this.mapAdditionalLineitem.put(sDependentInputValue, listAdditionalLineitem);
                    } else {
                        listAdditionalLineitem = this.mapAdditionalLineitem.get(sDependentInputValue);
                        listAdditionalLineitem.add(new QSLineItemsWrapper(ad_lineitem));
                    }
                }
            }
        }
    }

}
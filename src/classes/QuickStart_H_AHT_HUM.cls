/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_H_AHT_HUM 
Created Date     : January 20, 2017
Function         : This is helper class for the controller QuickStart_C_AHT_HUM Test
Test Class       : QuickStart_T_AHT_HUM
 * Modification Log
 * Developer Name          Code Review #         Date                       Description
 * Ashish Behera           -----                 10/24/2017                 Original Version
 * Subhamay Mandal           -----               10/24/2017                 Fixed Defect#340403 for Observation 20  
 * Subhamay Mandal				                 03/18/2020		            QS 3.0 Changes 938164|938619
 * Subhamay Mandal                               03/17/2020		            938197 QS 3.0 - Quality - Ability to make pretext line item mandatory in quick start (RF) 
 * Amar Gadewar                                  07/05/2020                 User Story 1247438: PR00091574 - MF 1 - SF - TECH - QS 3.0 Code Improvement and Knowledge cleanup (RF)
 * Samantha Tennyson                             01/22/2021                 Adding With Sharing to fix Checkmarx issue
 * Subhamay Mandal                               06/17/2021                 User Story 2368471: T1PRJ0001827 - IR 5050 - [Tech] - Check Marx violation fix (CRM)
 *************************************************************************************************************************************************************************************/
public with sharing class QuickStart_H_AHT_HUM {
	static final String SELECTBOX = 'Selectbox';
    static final String CHECKBOX = 'Checkbox';
	static final String NONE = 'None';
	static final String OTHER = 'Other';
	static final String DEFAULT_EMPTY = '';
	static final Integer DEFAULT_LENGTH_SIZE = 3;
	static final Integer DEFAULT_DISPLAY_ORDER = 0;
	static final Integer DEFAULT_DISPLAY_POSITION = 0;
    /*
     * Process pretext config data to json format so that JS file can read it
     * and render components accordingly.
     * 
     * @return map<String,String> where key will be section name(AR,CIC,AT) and
     * value will be json strings coninting all the component details
     * 
     * @Param classfication and Intenet value
     */
    public static String parsePreTextData(List<Quick_Start_Pretext_Section__c> listQSSections){
        String jsonMap;
        Map<String, QSSectionWrapper> mapQSPretext = new Map<String, QSSectionWrapper>();
        try{
            for(Quick_Start_Pretext_Section__c qs_section : listQSSections) {
            	mapQSPretext.put(qs_section.Case_Comment_Section__c, new QSSectionWrapper(qs_section));
            }
            if(!mapQSPretext.isEmpty()) {
            	jsonMap = JSON.serialize(mapQSPretext);
            }
        } catch(Exception e) {
        	HUMExceptionHelper.logErrors(e, 'QuickStart_H_AHT_HUM', 'parsePreTextData');
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
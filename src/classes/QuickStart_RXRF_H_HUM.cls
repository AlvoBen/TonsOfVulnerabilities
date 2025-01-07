/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_RXRF_H_HUM.cls 
Created Date     : May 05,2020
Function         : This is helper class for the controller QuickStart_RXRF_C_HUM Test
Test Class       : QuickStart_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Himalay Patel							        05/13/2020				   REQ - 1076034 Quick Start File Isloation (CRMS)
* Aaron Speakman                                01/22/2021                 Checkmarx Sharing Rule Fix - 2/12 Release
* Kiran Kumar Bhuvanagiri						05/06/2022				   User Story 3123903: T1PRJ0200895 - MF 1 - Humana Pharmacy Quick Start+ - Ability to Make Pretext Line Items Mandatory (CRMS) (ID# 24)
************************************************************************************************************************************************************************************/
public with sharing class QuickStart_RXRF_H_HUM {
    static final String SELECTBOX = 'Selectbox';
    static final String CHECKBOX = 'Checkbox';
	static final String NONE = 'None';
	static final String OTHER = 'Other';
	static final String DEFAULT_EMPTY = '';
	static final Integer DEFAULT_LENGTH_SIZE = 3;
	static final Integer DEFAULT_DISPLAY_ORDER = 0;
	static final Integer DEFAULT_DISPLAY_POSITION = 0;
    private static Boolean bSwitchPretext = false;
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
        bSwitchPretext = HUMUtilityHelper.isCRMFunctionalityON('3123903');
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
        	HUMExceptionHelper.logErrors(e, 'QuickStart_RXRF_H_HUM', 'parsePreTextData');
        }
        return jsonMap;
	}

    /**
     * wrapper class to represent each section and its line items records.
     */
    private with sharing class QSSectionWrapper {
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
    private with sharing class QSLineItemsWrapper {
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
            if(bSwitchPretext){
                this.bIsRequiredLineItem = qs_lineitem.Is_Required_Line_Item__c;
            } else{
                this.bIsRequiredLineItem = false;
            }
			  
			String sLabels = qs_lineitem.Input_Field_Labels__c;
			if(String.isNotBlank(sLabels)) {
				this.listInputFieldLabel = sLabels.split(';');
			}
			
			if(this.sInputFieldType == SELECTBOX || this.sInputFieldType == CHECKBOX) {
				List<QSLineItemsWrapper> listAdditionalLineitem;
				for(Quick_Start_Pretext_Line_Item__c ad_lineitem : [Select Pre_Label__c, Input_Field_Type__c, Input_Field_Labels__c, Ghost_Text__c, Field_Length__c, Field_Display_Size__c, Dependent_Input_Value__c, Display_Order__c, Display_Position__c, Is_Required_Line_Item__c from Quick_Start_Pretext_Line_Item__c 
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
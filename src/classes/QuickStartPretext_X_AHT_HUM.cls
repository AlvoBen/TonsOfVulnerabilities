/******************************************************************************************************************
Apex class Name    : QuickStartPretext_X_AHT_HUM
Version            : 1.0
Function           : Create Quick Start Pretext
Created On         : 8/12/2019
Test Class         : QuickStartPretext_T_AHT_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
*-------------------------------------------------------------------------------------------------------------------------------
*   1.0             Subhamay Mandal                                	07/05/2019 	    Original Version REQ - 406018
*   1.1		    Subhamay Mandal                                     08/26/2019          REQ - 406018: added Method to get object's prefix.
*   1.2		    Subhamay Mandal					01/28/2020	    731580|729526|729566 PR00091574 - Code merged for 2/28
**********************************************************************************************************************/
public with sharing class QuickStartPretext_X_AHT_HUM {
	public String sClassificationTypeIdSelect{get; set;}
	public String sClassificationIdSelect{get; set;}
	public String sIntentIdSelect{get; set;}
	
    public Quick_Start_Pretext__c quickStartPretext{get; set;}
    public List<SelectOption> lstClassficationType{get; set;}
    public List<SelectOption> lstClassficationOption{get; set;}
    public List<SelectOption> lstIntentOption{get; set;}
    
    private String sClassificationLabel = '';
    private String sCurrentPage = '/apex/QuickStartPretext_VF_AHT_HUM';
    
    public QuickStartPretext_X_AHT_HUM(ApexPages.StandardController controller) {
    	this.quickStartPretext = new Quick_Start_Pretext__c();
    	this.sClassificationTypeIdSelect = null;
    	this.sClassificationIdSelect = null;
    	this.sIntentIdSelect = null;
    }
	
	/**
	* Reset Current page.
	* @return PageReference of new page
    * @see Quick Start Pretext UI refreshes
	*/
	public PageReference reset() {
		PageReference oNewPage = new PageReference(sCurrentPage);
        oNewPage.setRedirect(true);
        return oNewPage;
	}
	
	/**
	* Reset Current page.
	* @return PageReference of new page
    * @see Quick Start Pretext UI refreshes
	*/
	public PageReference cancel() {
		String keyPrefix = UtilityHelper_H_AHT_HUM.getObjectPrefix(Constants_C_AHT_HUM.OBJ_QUICKSTARTPRETEXT);
		PageReference oNewPage = new PageReference('/'+keyPrefix+'/o');
        oNewPage.setRedirect(true);
        return oNewPage;
	}
    
    /**
    * Load initial data classification type
    */
    public void init(){
		this.lstClassficationType = QuickStartPretext_S_AHT_HUM.allClassfiicationTypeOptions();
		
		this.lstClassficationOption = new List<SelectOption>();
		this.lstClassficationOption.add(new SelectOption(HUMConstants.EMPTY_STRING, HUMConstants.HUMNoneDisplay));
		
		this.lstIntentOption = new List<SelectOption>();
		this.lstIntentOption.add(new SelectOption(HUMConstants.EMPTY_STRING, HUMConstants.HUMNoneDisplay));
    }
    
    /**
    * Create Quick Start Pretext item if not present
    */
    public PageReference save() {
        PageReference pageRef;
        
    	try {
    		if(this.sClassificationTypeIdSelect == null) {
    			ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,'Classification Type: You must enter a value'));
    			return null;
    		}
	        String sCTId = getClassificationTypeId(this.sClassificationTypeIdSelect);

			String sNextPage = QuickStartPretext_S_AHT_HUM.createQuickStartPretext(this.quickStartPretext, sCTId, this.sClassificationIdSelect, this.sIntentIdSelect, this.sClassificationLabel);
			if(String.isNotBlank(sNextPage)) {
				pageRef = new PageReference(sNextPage);	
			} else {
				ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO,'Quick Start Pretext record(s) exists.'));
				return null;	
			}
	        
	        	
    	} catch(DMLException de) {
            Apexpages.addMessage(new ApexPages.Message(ApexPages.SEVERITY.ERROR, de.getDmlMessage(0)));
            return null;
	} catch(Exception e) {
    	    HUMExceptionHelper.logErrors(e, 'QuickStartPretext_X_AHT_HUM', 'save');
	    return null;
    	}
    	
        return pageRef;
    }
    
   
    
    /**
    * Get all Classfication from active in CTCI for selected classfication type
    */
    public void getClassifactions() {
    	try {
	    	String sCTId = getClassificationTypeId(this.sClassificationTypeIdSelect);
	    	
	    	this.sClassificationIdSelect = null;
	    	resetDropdownOption(this.lstClassficationOption);
	    	
	    	this.lstClassficationOption.addAll(QuickStartPretext_S_AHT_HUM.getClassifactions(sCTId));
	        
	        this.sIntentIdSelect = null;
	        resetDropdownOption(this.lstIntentOption);
    	} catch(Exception e) {
    		HUMExceptionHelper.logErrors(e, 'QuickStartPretext_S_AHT_HUM', 'getClassifactions');
    	}
    }
    
    /**
    * Get all Classfication from active in CTCI for selected classfication type and classification
    */
	public void getIntents() {
		try {
			String sCTId = getClassificationTypeId(this.sClassificationTypeIdSelect);
			
	    	this.sIntentIdSelect = null;
	    	resetDropdownOption(this.lstIntentOption);
	    	
	    	this.lstIntentOption.addAll(QuickStartPretext_S_AHT_HUM.getIntents(sCTId, this.sClassificationIdSelect));
    	} catch(Exception e) {
    		HUMExceptionHelper.logErrors(e, 'QuickStartPretext_S_AHT_HUM', 'getIntents');
    	}
    }
    
    /**
    * reset all drop down option
    * @param List<SelectOption> lstOption reference
    */ 
    private void resetDropdownOption(List<SelectOption> lstOption) {
    	lstOption.clear();
        lstOption.add(new SelectOption(HUMConstants.EMPTY_STRING, HUMConstants.HUMNoneDisplay));
    }
    
    /**
    *
    */
    private String getClassificationTypeId(String sClassificationTypeSelected) {
    	String sId = '';
    	if(String.isNotBlank(sClassificationTypeSelected)) {
    		String[] arCTIdName = this.sClassificationTypeIdSelect.split(HUMConstants.HUM_PCP_DATESEPERATOR);
    		sId = arCTIdName[0];
    		this.sClassificationLabel = arCTIdName[1];
    	}
    	
    	return sId;
    }
}
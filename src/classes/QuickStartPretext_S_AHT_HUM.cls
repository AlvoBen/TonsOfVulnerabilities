/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStartPretext_S_AHT_HUM 
Created Date     : 8/12/2019
Function         : Executes business logic and assignment functions for the Quick Start Console Component
Test Class       : QuickStartPretext_T_AHT_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Subhamay Mandal            -----              08/12/2019                 Original Version REQ - 406018
* Subhamay Mandal            -----              08/22/2019                 REQ - 406018, Fix 435354.0001
* Subhamay Mandal                               08/26/2019                 REQ - 406018: added Method to get object's prefix.
* Samantha Tennyson                             01/22/2021                 Adding With Sharing to fix Checkmarx issue
* Subhamay Mandal                               03/23/2021                 User Story 1946416: T1PRJ0001827- IR 5050- CheckMarx violation fixes (High items)
* Subhamay Mandal                               06/16/2021                 User Story 2368471: T1PRJ0001827 - IR 5050 - [Tech] - Check Marx violation fix (CRM)
************************************************************************************************************************************************************************************/
public with sharing class QuickStartPretext_S_AHT_HUM {
	
	
	/**
    * Get all Classfication Type from active in CTCI
    * @return List<SelectOption>
    */
	public static List<SelectOption> allClassfiicationTypeOptions() {
		List<SelectOption> lstOption = new List<SelectOption>();
		
    	try {
    		List<AggregateResult> lstAggregateResult = [select classification_type__c, classification_type__r.name ClassifcationType from CTCI_Junction__c 
											where IsActive__c = true 
											group by classification_type__c, classification_type__r.name 
											order by classification_type__r.name];
												
        	lstOption.add(new SelectOption(HUMConstants.EMPTY_STRING, HUMConstants.HUMNoneDisplay));
        	for(AggregateResult ar : lstAggregateResult) {
        		String sId = (String)ar.get('classification_type__c');
        		String sLabel = (String)ar.get('ClassifcationType');
                lstOption.add(new SelectOption(sId +HUMConstants.HUM_PCP_DATESEPERATOR+ sLabel, sLabel));
            }
            
    	} catch(Exception e) {
    		HUMExceptionHelper.logErrors(e, 'QuickStartPretext_S_AHT_HUM', 'allClassfiicationTypeOptions');
    	}
    	
    	return lstOption;
	}
	
	/**
    * Get all Classfication from active in CTCI for selected classfication type
    * @return List<SelectOption>
    */
    public static List<SelectOption> getClassifactions(String sCTId) {
    	List<SelectOption> lstOption = new List<SelectOption>();
    	
    	List<AggregateResult> lstAggregateResult = [select classification__c, classification__r.name classification from CTCI_Junction__c 
    						where IsActive__c = true and classification_type__c =: sCTId  
    						group by classification__c, classification__r.name 
    						order by classification__r.name];
    	
    	for(AggregateResult ar : lstAggregateResult) {
    		String sId = (String)ar.get('classification__c');
    		String sLabel = (String)ar.get('classification');
            lstOption.add(new SelectOption(sId, sLabel));
        }
        
        return lstOption;
    }
    
    /**
    * Get all Classfication from active in CTCI for selected classfication type and classification
    * @return List<SelectOption>
    */
	public static List<SelectOption> getIntents(String sCTId, String sClassificationIdSelect) {
		List<SelectOption> lstOption = new List<SelectOption>();
		
    	List<AggregateResult> lstAggregateResult = [select Intent__c, Intent__r.name intent from CTCI_Junction__c 
    						where IsActive__c = true and classification_type__c =:sCTId and classification__c =: sClassificationIdSelect 
    						group by Intent__c, Intent__r.name  
    						order by Intent__r.name];
    	   	
    	for(AggregateResult ar : lstAggregateResult) {
    		String sId = (String)ar.get('Intent__c');
    		String sLabel = (String)ar.get('intent');
            lstOption.add(new SelectOption(sId, sLabel));
        }
        
        return lstOption;
    }
    
    /**
    * Create Quick Start Pretext item if not present
    * @return String
    */
    public static String createQuickStartPretext(Quick_Start_Pretext__c quickStartPretext, String sCTId, String sClassificationIdSelect, String sIntentIdSelect, String sClassificationLabel) {
    	String sNextpage = '';
    	Boolean isSelectedClassificationType = false;
        Boolean isSelectedClassification = false;
        
    	String sSOQL_CTCI = 'select Id from CTCI_Junction__c where IsActive__c = true ';
    	if(String.isNotBlank(sCTId)) {
    		sSOQL_CTCI += 'and classification_type__c =\''+ String.escapeSingleQuotes(sCTId) +'\' ';
    		isSelectedClassificationType = true;
    	}
    	if(isSelectedClassificationType && String.isNotBlank(sClassificationIdSelect)) {
    		sSOQL_CTCI += 'and classification__c =\''+ String.escapeSingleQuotes(sClassificationIdSelect) +'\' ';
    		isSelectedClassification = true;
    	}
    	if(isSelectedClassification && String.isNotBlank(sIntentIdSelect)) {
    		sSOQL_CTCI += 'and Intent__c =\''+ String.escapeSingleQuotes(sIntentIdSelect)  +'\' ';
    	}
    	sSOQL_CTCI += 'order by Id';
    	
    	Set<Id> setID_CTCI = new Set<Id>();    	
    	if(isSelectedClassificationType) {
    		for(CTCI_Junction__c ctci : (List<CTCI_Junction__c>)Database.query(sSOQL_CTCI)) {
    			setID_CTCI.add(ctci.Id);
    		}
    	}    	
    	
    	Set<Id> setID_QSPCTCI = new Set<Id>();
    	for(Quick_Start_Pretext__c qsp : [select Id, CTCI_List_lkp__c from Quick_Start_Pretext__c where CTCI_List_lkp__c in : setID_CTCI]) {
    		setID_QSPCTCI.add(qsp.CTCI_List_lkp__c);
    	}
    	
    	if(!setID_CTCI.isEmpty()) {
    		setID_CTCI.removeAll(setID_QSPCTCI);
    	}
    	
    	List<Quick_Start_Pretext__c> listQSP = new List<Quick_Start_Pretext__c>(); 
    	for(Id id_ctci : setID_CTCI) {
    		Quick_Start_Pretext__c recQSP = quickStartPretext.clone();
    		if(Schema.sObjectType.Quick_Start_Pretext__c.fields.CTCI_List_lkp__c.isCreateable()) {
				recQSP.CTCI_List_lkp__c = id_ctci;
			}
    		recQSP.Business_Grp_Class_Type__c = recQSP.Business_Groups__c +' '+ sClassificationLabel;
    		listQSP.add(recQSP);
    	}
    	
		SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE, listQSP);
		listQSP = securityDecision.getRecords();
		
    	if(!listQSP.isEmpty()) {
    		insert listQSP;
    		createQSPS(listQSP);
	    	recordForReference(listQSP.get(0).Business_Groups__c, listQSP.get(0).Business_Grp_Class_Type__c);
    	}
    	
    	if(listQSP.size() == 1) {
        	sNextpage = '/'+listQSP.get(0).Id;
        }  else if(listQSP.size() > 1) {
        	String sId = listQSP.get(0).Id; 
        	sNextpage = '/'+UtilityHelper_H_AHT_HUM.getObjectPrefix(Constants_C_AHT_HUM.OBJ_QUICKSTARTPRETEXT)+'/o';
        }
        
        return sNextpage;  
    }

	/**
	* Create Quick Start pretext section
	*/
 	private static void createQSPS(List<Quick_Start_Pretext__c> listQSP) {
    	List<Quick_Start_Pretext_Section__c> listPSPS = new List<Quick_Start_Pretext_Section__c>();
    	
    	for(Quick_Start_Pretext__c oQSP : listQSP) {
    		listPSPS.add(new Quick_Start_Pretext_Section__c(Case_Comment_Section__c='DIV_CEO', Quick_Start_Pretext__c = oQSP.Id));
    		listPSPS.add(new Quick_Start_Pretext_Section__c(Case_Comment_Section__c='DIV_AT', Quick_Start_Pretext__c = oQSP.Id));
    		listPSPS.add(new Quick_Start_Pretext_Section__c(Case_Comment_Section__c='DIV_AR', Quick_Start_Pretext__c = oQSP.Id));
    	}
    	
    	if(!listPSPS.isEmpty()) {
    		insert listPSPS;
    	}
    }
    
    /**
    * Create Quick Start Pretrext for Reference document
    * @param String
    * @param String
    * @return void
    */
    private static void recordForReference(String sBusinessGrp, String BusinessGrpCT) {
		String sTempBusinessGrp = sBusinessGrp;
		String sTempBusinessGrpCT = BusinessGrpCT;
		
		List<Quick_Start_Pretext__c> listQSReference = [select id from Quick_Start_Pretext__c 
									where Business_Groups__c =:sTempBusinessGrp and Business_Grp_Class_Type__c =:sTempBusinessGrpCT and CTCI_List_lkp__c=null];
		if(listQSReference.isEmpty()) {
			Quick_Start_Pretext__c qspReference = new Quick_Start_Pretext__c();
			if(Schema.sObjectType.Quick_Start_Pretext__c.fields.Business_Groups__c.isCreateable()) {
				qspReference.Business_Groups__c = sTempBusinessGrp;
			}
			if(Schema.sObjectType.Quick_Start_Pretext__c.fields.Business_Grp_Class_Type__c.isCreateable()) {
				qspReference.Business_Grp_Class_Type__c=sTempBusinessGrpCT;
			}
			insert qspReference; 
		}    	
    }
}
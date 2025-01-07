/*****************************************************************************************************
 Apex Class Name : QuickStartPretext_H_AHT_HUM
 Version         : 1.0 
 Created Date    : 01/18/2018
 Function        : Holds the logic for trigger events on Quick Start Pretext Object
 Test Class      : QuickStartPretext_T_AHT_HUM
 Modification Log :

* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
*  Subhamay Mandal                              01/18/2016                  REQ - 350392 Ability to use Business Configuration model for Quick Start pretext for making changes within Quick Start pre-text (RF)
*  Subhamay Mandal                              06/27/2018                  REQ - 367101 AHT - Add Frequently Used Classification and Intent to Classification Dropdown (RF)
*  Asish Behera                                 08/27/2018                  REQ - 374677 Update QS pretext handler class to handle frequently used classfication and intent issue.
*  Darshan Sharma			                    10/01/2018		            REQ - 374820: CTCI Model Update (RF+CRM)
*  Asish Behera                                 10/05/2018                  REQ - 374820: Added null check for map to handle attempt to deference null object issue while loadinf opretext.
*  Asish Behera                                 10/08/2018                  REQ - 374677 Update QS pretext handler class to fix defects 386356.0001
*  Samantha Tennyson                            01/22/2021                  Adding With Sharing to fix Checkmarx issue
*******************************************************************************************************/
public with sharing class QuickStartPretext_H_AHT_HUM {
	private static Map<String, String> mapCurrentCINameId = new Map<String, String>();
    private static Map<String, Integer> mapCountFrequentlyUsedCI = new Map<String, Integer>();
    private static Map<Id, CTCI_Junction__c> mapCTCI = new Map<Id, CTCI_Junction__c>();
	
	/**
    *  Duplicate Pretext combination check during insert
    *  @param List<Quick_Start_Pretext__c> listNew
    *  @return void
    *  @throws NA
    */
    public static void duplicatePretextCI(List<Quick_Start_Pretext__c> listNew) {
    	loadCurrectPretext();
    	
    	for(Quick_Start_Pretext__c qp : listNew) {
    		String mapKeyDuplicate = qp.CTCI_List_lkp__c;
    		String mapKeyCount;
    		if(mapCTCI !=null && mapCTCI.containskey(qp.CTCI_List_lkp__c)){
    			mapKeyCount = mapCTCI.get(qp.CTCI_List_lkp__c).Classification_Type__c;
    		}
            	
    		if(qp.Is_Active__c == true && mapCurrentCINameId !=null && mapCurrentCINameId.containskey(mapKeyDuplicate) && mapCurrentCINameId.get(mapKeyDuplicate) != null) {
	            qp.addError(Label.DuplicateCIErrorMessage);
	            break;
	        }
	        if(qp.Is_Active__c && qp.is_Frequently_Used__c && mapCountFrequentlyUsedCI.containskey(mapKeyCount) && mapCountFrequentlyUsedCI.get(mapKeyCount) == HUMConstants.VALUE_FIVE) {
	        	qp.addError(Label.MaxFrequentlyCIErrorMessge);
	            break;
	        }
	    }
    }
    
	/**
    *  Duplicate Pretext combination check during update
    *  @param List<Quick_Start_Pretext__c> listNew
    *  @param List<Quick_Start_Pretext__c> listOld
    *  @return void
    *  @throws NA
    */
    public static void duplicatePretextCI(List<Quick_Start_Pretext__c> listNew, List<Quick_Start_Pretext__c> listOld) {
    	loadCurrectPretext();
        
        for(Integer i = 0; i < listNew.size(); i++) {
    		Quick_Start_Pretext__c newRec = listNew.get(i);
    		Quick_Start_Pretext__c oldRec = listOld.get(i);
            String mapKeyDuplicate = newRec.CTCI_List_lkp__c;
            String mapKeyCount;
            if(mapCTCI.containskey(newRec.CTCI_List_lkp__c)){
            	mapKeyCount = mapCTCI.get(newRec.CTCI_List_lkp__c).Classification_Type__c;
            }
            if((newRec.CTCI_List_lkp__c != oldRec.CTCI_List_lkp__c) || (newRec.Is_Active__c != oldRec.Is_Active__c) && newRec.Is_Active__c == true) {
    			if(newRec.Is_Active__c == true && mapCurrentCINameId !=null && mapCurrentCINameId.containskey(mapKeyDuplicate) && mapCurrentCINameId.get(mapKeyDuplicate) != null) {
		            newRec.addError(Label.DuplicateCIErrorMessage);
		            break;
		        }
    		}
            if(newRec.Is_Active__c && newRec.is_Frequently_Used__c && newRec.is_Frequently_Used__c != oldRec.is_Frequently_Used__c && mapCountFrequentlyUsedCI.get(mapKeyCount) == HUMConstants.VALUE_FIVE) {
	        	newRec.addError(Label.MaxFrequentlyCIErrorMessge);
	            break;
	        }
    	}
    }
    
    private static void loadCurrectPretext() {
    	for(Quick_Start_Pretext__c qsp : [select Id, CTCI_List_lkp__c,is_Frequently_Used__c,CTCI_List_lkp__r.Classification_Type__c from Quick_Start_Pretext__c where Is_Active__c= true]) {
            
            if(qsp.CTCI_List_lkp__c !=null){
            	mapCurrentCINameId.put(qsp.CTCI_List_lkp__c, qsp.Id);
            }
            if(qsp.is_Frequently_Used__c)
            {
                if(mapCountFrequentlyUsedCI.containskey(qsp.CTCI_List_lkp__r.Classification_Type__c))
                {
                    Integer iCount =  mapCountFrequentlyUsedCI.get(qsp.CTCI_List_lkp__r.Classification_Type__c);
                    iCount = iCount + 1;
                    mapCountFrequentlyUsedCI.put(qsp.CTCI_List_lkp__r.Classification_Type__c,iCount);
                }
                else
                {
                    mapCountFrequentlyUsedCI.put(qsp.CTCI_List_lkp__r.Classification_Type__c,1);
                }
            }
        }
        mapCTCI = new map<Id,CTCI_Junction__c>([Select Id, Name, Classification_Type__c from CTCI_Junction__c where IsActive__c = true]);
        
    }
}
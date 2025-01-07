/*****************************************************************************************************
 Apex Class Name  : CheckDuplicateCTCI_H_HUM
 Version   : 1.0 
 Created Date  : 03/23/2017
 Function  : Holds the logic for trigger events on CTCI Object
 Test Class  :   CheckDuplicateCTCI_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
*  Ranjeeth Nagishetty                           03/23/2017                    REQ - 304967:SF - TECH - Validation rule for CTCI List Record
*  Ranjeeth Nagishetty        95927              05/24/2017                    Defect- 286941 
*  Archana Das			     237454              05/31/2017                    Changed the logic to compare with Type, classification and Intent combination while checking for duplicates
*  Ashok Nutalapati			                     01/07/2020                    Add Humana Pharmacy Log Code to CTCI List Pages in Business Configuration App (CRMS)
*  Ashok Nutalapati                              01/18/2021                    REQ - 1598487 - Add 'Service Model Type' Picklist to CTCI List Pages in Business Configuration App (CRMS)
*  Samantha Tennyson                             12/28/2020                    User Story 1745934: Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
*  Aaron Speakman                                01/22/2021                    Checkmarx Sharing Rule Fix - 2/12 Release   
*  Aaron Speakman                                04/28/2021                    DF-2978 Fix - When editing a CTCI List, making changes and clicking save delivers an error
*  Atia Uzma                                     06/21/2021                    User Story 2348383: T1PRJ0001827 - MF 1 - Case Transfer Assist - Business Configuration enhancements (CRM)
*******************************************************************************************************/

public with sharing class CheckDuplicateCTCI_H_HUM
{
	
 public Static Boolean isTriggerExecuted = false;
	
    /**
    *  Checks for duplicate CTCI records with same classifcation,Intent and clasifcationtype
    *  @name checkForCTCIDuplicateonInsert
    *  @param lstCTCIRecords Holds the list of all CTCI records
    *  @return void
    *  @throws NA
    */
 public static void checkForCTCIDuplicateonInsert(List<CTCI_Junction__c > lstNewCTCIRec)
    {
	  Map<string, CTCI_Junction__c> mapCTCI = new Map<string,CTCI_Junction__c >();
      for(CTCI_Junction__c  oCICTnew: lstNewCTCIRec)
      {
                string mapKey = ''+oCICTnew.Classification_Type__c+oCICTnew.Classification__c+oCICTnew.Intent__c;
                mapCTCI.put(mapKey,oCICTnew);
      }
      fetchvalue(mapCTCI);
   
    }
    
   /**
    *  Checks for duplicate CTCI records with same classifcation,Intent and clasifcationtype on Update
    *  @name checkForDuplicateAssocitedCICTUpdate
    *  @param lstCTCIRecords Holds the list of all CTCI records,Map<id,CTCI_Junction__c >
    *  @return void
    *  @throws NA
    */ 
    
 public static void checkForDuplicateAssocitedCICTUpdate(List<CTCI_Junction__c > lstNewCICTnRec , Map<id,CTCI_Junction__c > mapOldCICTRec)
    {	
		boolean isCTCIUpdated = false;
        Map<string,CTCI_Junction__c > mapCTCI = new Map<string,CTCI_Junction__c >();
        for (CTCI_Junction__c  oCICTnew : lstNewCICTnRec )
        {
			if((oCICTnew.Classification_Type__c != mapOldCICTRec.get(oCICTnew.Id).Classification_Type__c) || (oCICTnew.Classification__c != mapOldCICTRec.get(oCICTnew.Id).Classification__c) || (oCICTnew.Intent__c != mapOldCICTRec.get(oCICTnew.Id).Intent__c))  isCTCIUpdated = true;
			string mapKey = '';
			if(isCTCIUpdated){
				mapKey = ''+oCICTnew.Classification_Type__c+oCICTnew.Classification__c+oCICTnew.Intent__c;
			}
			else {
				mapKey = ''+oCICTnew.Classification_Type__c+oCICTnew.Classification__c+oCICTnew.Intent__c+oCICTnew.Classification_Type_Log_Code__c+oCICTnew.Humana_Pharmacy_Log_Code__c+oCICTnew.Service_Model_Type__c;
			}
			mapCTCI.put(mapKey,oCICTnew);
        }
        if(isCTCIUpdated){
            fetchvalue(mapCTCI);
        }
        else {
            fetchvalueHP(mapCTCI);
        }
      }   

      /**
    *  After - Update Check if the Never a Complaint or Never a G&A Given Rights is true and the QuickStart Pretext setup is cleared accordingly.
    *  @name emptyComplaintGASetup
    *  @param Map<Id,CTCI_Junction__c > 
    *  @return void
    *  @throws NA
    */   
    public static void emptyComplaintGASetup(List<CTCI_Junction__c> lstNewCICTnRec, Map<Id,CTCI_Junction__c> mapOldCICTRec){ 
        
        Map<id,CTCI_Junction__c> ctciMap = new Map<id,CTCI_Junction__c>();
        
        for(CTCI_Junction__c oCICTnew : lstNewCICTnRec){
            CTCI_Junction__c oCTCIold = mapOldCICTRec.get(oCICTnew.id);
            if((oCICTnew.Never_a_Complaint__c && (oCICTnew.Never_a_Complaint__c != oCTCIold.Never_a_Complaint__c)) || (oCICTnew.Never_a_G_A_Rights_Given__c && (oCICTnew.Never_a_G_A_Rights_Given__c != oCTCIold.Never_a_G_A_Rights_Given__c))){
                ctciMap.put(oCICTnew.id,oCICTnew);
            }
        }
        
        if(!ctciMap.isEmpty()){
            List<Quick_Start_Pretext__c> updateQSPlst = new List<Quick_Start_Pretext__c>();
            List<Quick_Start_Pretext__c> qspList = [select id, CTCI_List_lkp__c, Complaint_GA_Setup__c from Quick_Start_Pretext__c where Complaint_GA_Setup__c <> null AND CTCI_List_lkp__c IN: ctciMap.keyset()];
            if(!qspList.isEmpty()){
                for(Quick_Start_Pretext__c qsp : qspList){
                    if((ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_Complaint__c == true && ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_G_A_Rights_Given__c == false && qsp.Complaint_GA_Setup__c.contains('Complaint')) ||
                       (ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_G_A_Rights_Given__c == true && ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_Complaint__c == false && qsp.Complaint_GA_Setup__c.contains('GnA')) ||
                       (ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_G_A_Rights_Given__c == true && ctciMap.get(qsp.CTCI_List_lkp__c).Never_a_Complaint__c == true)){
                           qsp.Complaint_GA_Setup__c = null;
                       }
                    updateQSPlst.add(qsp);
                }
                if(!updateQSPlst.isEmpty()){
                    update updateQSPlst;
                }
            }
        }
    }
  
     
    /**
    *  Checks for Fetch value of the records of task and if it matchs the task name and Casesubtype then throws error
    *  @name fetchvalue
    *  @param Map<String,CTCI_Junction__c > 
    *  @return void
    *  @throws NA
    */
    
  //private static void fetchvalue( Map<id,CTCI_Junction__c > mapclassification , Map<id,CTCI_Junction__c > mapclassificationType ,Map<id,CTCI_Junction__c > mapIntent ,Map<boolean,CTCI_Junction__c > mapIsactive )
    private static void fetchvalue( Map<string,CTCI_Junction__c > mapCTCI)
	{
		string ctcQuery = 'SELECT Classification__c,Classification__r.name,Classification_Type__r.name, Intent__r.name,Classification_Type__c ,Intent__c ,IsActive__c FROM CTCI_Junction__c';
		boolean isWhereReq=false;
		boolean isOrReq=false;
		string whereQuery = '';
		isTriggerExecuted = true;
		//Build CTCI query to check if CTCI combination passed in parameter already exists or not
		for(CTCI_Junction__c sCTC : mapCTCI.values())	
		{
			//if(isOrReq)
			if(String.IsNotBlank(sCTC.Classification_Type__c) && String.IsNotBlank(sCTC.Classification__c) && String.IsNotBlank(sCTC.Intent__c))
			{
				
				if(isOrReq)
					whereQuery = whereQuery+ ' OR ';
				if (sCTC.Classification_Type_Log_Code__c == null) sCTC.Classification_Type_Log_Code__c ='';
                if (sCTC.Humana_Pharmacy_Log_Code__c == null) sCTC.Humana_Pharmacy_Log_Code__c ='';
				if (sCTC.Service_Model_Type__c == null) sCTC.Service_Model_Type__c ='';
				whereQuery = whereQuery +' (IsActive__c=true and Classification_Type__c=' + '\''  + sCTC.Classification_Type__c +  '\'' + ' and Classification__c='+ '\''  +sCTC.Classification__c+ '\'' +' and Intent__c=' + '\''  + sCTC.Intent__c + '\'' +')';
				isWhereReq=true;
				isOrReq=true;
			}
		}
		if(isWhereReq)
			ctcQuery = ctcQuery+' Where (' + whereQuery + ')';
		
		CTCI_Junction__c[] objCTCIList = Database.query(ctcQuery);
		//If CTCI combination exists in database then add error
		if(!objCTCIList.isEmpty())  
		{
			//If any of the CTCI already exists in DB then add to error list and prevent records from inserting
			for (CTCI_Junction__c oCICTQ:objCTCIList)
			{	   		
				string mapKey = ''+oCICTQ.Classification_Type__c+oCICTQ.Classification__c+oCICTQ.Intent__c;
				CTCI_Junction__c   ocictshow =mapCTCI.get(mapKey);
				ocictshow.addError(System.Label.UNIQUERECORDCTCI);
			}
		}
    }
	
	
    /**
    *  Checks for Fetch value of the records of task and if it matchs the task name and Casesubtype then throws error only if new HP fields are modified
    *  @name fetchvalue
    *  @param Map<String,CTCI_Junction__c > 
    *  @return void
    *  @throws NA
    */
    
  //private static void fetchvalue( Map<id,CTCI_Junction__c > mapclassification , Map<id,CTCI_Junction__c > mapclassificationType ,Map<id,CTCI_Junction__c > mapIntent ,Map<boolean,CTCI_Junction__c > mapIsactive )
    private static void fetchvalueHP( Map<string,CTCI_Junction__c > mapCTCI)
    {
        string ctcQuery = 'SELECT Classification__c,Classification__r.name,Classification_Type__r.name, Intent__r.name,Classification_Type__c ,Intent__c ,IsActive__c,Classification_Type_Log_Code__c,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c FROM CTCI_Junction__c';
        boolean isWhereReq=false;
        boolean isOrReq=false;
        string whereQuery = '';
        isTriggerExecuted = true;
        //Build CTCI query to check if CTCI combination passed in parameter already exists or not
        for(CTCI_Junction__c sCTC : mapCTCI.values())   
        {
            //if(isOrReq)
            if(String.IsNotBlank(sCTC.Classification_Type__c) && String.IsNotBlank(sCTC.Classification__c) && String.IsNotBlank(sCTC.Intent__c))
            {
                
                if(isOrReq)
                    whereQuery = whereQuery+ ' OR ';
                
                whereQuery = whereQuery +' (IsActive__c=true and Classification_Type__c=' + '\''  + sCTC.Classification_Type__c +  '\'' + ' and Classification__c='+ '\''  +sCTC.Classification__c+ '\'' +' and Intent__c=' + '\''  + sCTC.Intent__c + '\'' +' and Classification_Type_Log_Code__c=' + '\''  + sCTC.Classification_Type_Log_Code__c + '\'' +' and Humana_Pharmacy_Log_Code__c=' + '\''  + sCTC.Humana_Pharmacy_Log_Code__c + '\'' +' and Service_Model_Type__c=' + '\''  + sCTC.Service_Model_Type__c + '\'' +')';
                isWhereReq=true;
                isOrReq=true;
            }
        }
        if(isWhereReq)
            ctcQuery = ctcQuery+' Where (' + whereQuery + ')';
        
        CTCI_Junction__c[] objCTCIList = Database.query(ctcQuery);
        //If CTCI combination exists in database then add error
        if(!objCTCIList.isEmpty())  
        {
            //If any of the CTCI already exists in DB then add to error list and prevent records from inserting
            for (CTCI_Junction__c oCICTQ:objCTCIList)
            {           
                string mapKey = ''+oCICTQ.Classification_Type__c+oCICTQ.Classification__c+oCICTQ.Intent__c+oCICTQ.Classification_Type_Log_Code__c+oCICTQ.Humana_Pharmacy_Log_Code__c;
                CTCI_Junction__c   ocictshow =mapCTCI.get(mapKey);
                ocictshow.addError(System.Label.UNIQUERECORDCTCI);
            }
        }
    }
    
/**
*  Checks for inactivate CTCI records and make the associted Case_Transfer_Assist__c inactive 
*  @name inactivateCTARecords
*  @param List<CTCI_Junction__c>,Map<id,CTCI_Junction__c >
*  @return void
*  @throws NA
*/ 
    public static void inactivateCTARecords(List<CTCI_Junction__c> TriggerNew, Map<Id,CTCI_Junction__c> oldMap){
        Map<id,CTCI_Junction__c> ctciMap = new Map<id,CTCI_Junction__c>();
        for(CTCI_Junction__c ctci : TriggerNew){
            CTCI_Junction__c oldRecord = oldMap.get(ctci.id);
            if(oldRecord.IsActive__c && oldRecord.IsActive__c!=ctci.IsActive__c){
                ctciMap.put(ctci.id,ctci);
            }
        }
        if(!ctciMap.isEmpty()){
            List<Case_Transfer_Assist__c> lstUpdate = new List<Case_Transfer_Assist__c>();
            for(Case_Transfer_Assist__c cta : [SELECT id,Is_Active__c FROM Case_Transfer_Assist__c WHERE CTCI_List__c IN :ctciMap.keyset() AND Is_Active__c =: True]){
                cta.Is_Active__c = false;
                lstUpdate.add(cta);
            }
            if(!lstUpdate.isEmpty()){
             	update lstUpdate;   
            }

            
            
        }
    }
}